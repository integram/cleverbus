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

package org.cleverbus.core.common.asynch.confirm;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalCallStateEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.dao.ExternalCallDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * Implementation of {@link ConfirmationService} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ConfirmationServiceImpl implements ConfirmationService {

    /**
     * Maximum count of confirmation fails when will finish further processing.
     */
    @Value("${asynch.confirmation.failedLimit}")
    private int failedCountLimit;

    @Autowired
    private ExternalCallDao extCallDao;

    @Transactional
    @Override
    public ExternalCall insertFailedConfirmation(Message msg) {
        Assert.notNull(msg, "the msg must not be null");
        Assert.notNull(msg.getState() == MsgStateEnum.OK || msg.getState() == MsgStateEnum.FAILED,
                "the msg must in state OK or FAILED, but state is " + msg.getState());

        ExternalCall extCall = ExternalCall.createFailedConfirmation(msg);

        extCallDao.insert(extCall);

        Log.debug("Inserted confirmation failed call " + msg.toHumanString());

        return extCall;
    }

    @Transactional
    @Override
    public void confirmationComplete(ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");
        Assert.isTrue(ExternalCall.CONFIRM_OPERATION.equals(extCall.getOperationName()),
                "the extCall must be a " + ExternalCall.CONFIRM_OPERATION + ", but is " + extCall.getOperationName());
        Assert.isTrue(extCall.getState() == ExternalCallStateEnum.PROCESSING,
                "the confirmation must be in PROCESSING state, but state is " + extCall.getState());

        extCall.setState(ExternalCallStateEnum.OK);

        extCallDao.update(extCall);

        Log.debug("Confirmation call " + extCall.toHumanString() + " changed state to " + ExternalCallStateEnum.OK);
    }

    @Transactional
    @Override
    public void confirmationFailed(ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");
        Assert.isTrue(ExternalCall.CONFIRM_OPERATION.equals(extCall.getOperationName()),
                "the extCall must be a " + ExternalCall.CONFIRM_OPERATION + ", but is " + extCall.getOperationName());
        Assert.isTrue(extCall.getState() == ExternalCallStateEnum.PROCESSING,
                "the confirmation must be PROCESSING state, but is in " + extCall.getState());

        int failedCount = extCall.getFailedCount() + 1;
        extCall.setFailedCount(failedCount);

        ExternalCallStateEnum state = ExternalCallStateEnum.FAILED;
        if (failedCount > failedCountLimit) {
            state = ExternalCallStateEnum.FAILED_END;
        }

        extCall.setState(state);

        extCallDao.update(extCall);

        Log.debug("Confirmation call " + extCall.toHumanString() + " changed state to " + state);
    }
}
