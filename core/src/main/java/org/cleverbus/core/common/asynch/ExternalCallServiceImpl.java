/*
 * Copyright (C) 2015
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cleverbus.core.common.asynch;

import java.util.regex.Pattern;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalCallStateEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.exception.LockFailureException;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.dao.ExternalCallDao;
import org.cleverbus.spi.extcall.ExternalCallService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * Implementation of {@link ExternalCallService} interface.
 * Supports specifying a RegEx pattern to skip matched operation URIs.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ExternalCallServiceImpl implements ExternalCallService {

    @Value("${asynch.externalCall.skipUriPattern}")
    private Pattern skipOperationUriList;

    @Autowired
    private ExternalCallDao extCallDao;

    @Override
    @Transactional
    public ExternalCall prepare(String operationUri, String operationKey, Message message) {
        if (skipOperationUriList != null && skipOperationUriList.matcher(operationUri).matches()) {
            Log.warn("Not allowing an external call for ignored operation URI: [{}] matches pattern [{}]",
                    operationUri, skipOperationUriList);
            return null;
        }

        ExternalCall extCall = extCallDao.getExternalCall(operationUri, operationKey);
        Log.debug("Locking msgId={}, extCall={}", message.getMsgId(), extCall);

        if (extCall == null) {
            // create a new call in the Processing state, if none registered so far
            extCall = ExternalCall.createProcessingCall(operationUri, operationKey, message);
            extCallDao.insert(extCall);
            Log.debug("Locked msgId={}, extCall={}", message.getMsgId(), extCall);
            return extCall;
        }

        long extCallAge = message.getMsgTimestamp().getTime() - extCall.getMsgTimestamp().getTime();

        switch (extCall.getState()) {
            case PROCESSING:
                // don't allow two concurrent external calls to be made, even if this one is newer
                throw new LockFailureException(String.format(
                        "Another external call is currently being processed for uri=[%s] key=[%s] msgId=[%s]",
                        operationUri, operationKey, extCall.getMsgId()));
            case OK:
                if (extCallAge < 0) {
                    // the existing external call is younger/newer and is OK, skip this new call
                    Log.warn("Not allowing an external call, since it's older than a successful call: {}", extCall);
                    return null;
                } else if (extCallAge == 0) {
                    // this external call already happened with OK result, skip it
                    Log.info("Not allowing an external call, since it's a duplicate of a successful call: {}", extCall);
                    return null;
                }
                break;
            default:
                Log.error("Unexpected ExternalCall State: " + extCall);
                // log error, but otherwise behave as if it was FAILED:
            case FAILED:
            case FAILED_END:
                if (extCallAge < 0) {
                    // the existing external call is younger/newer, skip this new call
                    Log.warn("Not allowing an external call, since it's older than a failed call: {}", extCall);
                    return null;
                }
                break;
        }
        // mark the found external call as taken for processing by this message:
        extCallDao.lockExternalCall(extCall);
        extCall.setMessage(message);
        extCall.setMsgId(message.getMsgId());
        extCall.setMsgTimestamp(message.getMsgTimestamp());
        Log.debug("Locked msgId={}, extCall={}", message.getMsgId(), extCall);
        return extCall;
    }

    @Override
    @Transactional
    public void complete(ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");
        Assert.isTrue(extCall.getState() == ExternalCallStateEnum.PROCESSING,
                "the external call must be in PROCESSING state, but state is " + extCall.getState());
        extCall.setState(ExternalCallStateEnum.OK);
        extCallDao.update(extCall);
        Log.debug("External call " + extCall.toHumanString() + " changed state to " + ExternalCallStateEnum.OK);
    }

    @Override
    @Transactional
    public void failed(ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");
        Assert.isTrue(extCall.getState() == ExternalCallStateEnum.PROCESSING,
                "the external call must be in PROCESSING state, but state is " + extCall.getState());
        extCall.setState(ExternalCallStateEnum.FAILED);
        extCallDao.update(extCall);
        Log.debug("External call " + extCall.toHumanString() + " changed state to " + ExternalCallStateEnum.FAILED);
    }

    public Pattern getSkipOperationUriList() {
        return skipOperationUriList;
    }

    public void setSkipOperationUriList(Pattern skipOperationUriList) {
        this.skipOperationUriList = skipOperationUriList;
    }
}
