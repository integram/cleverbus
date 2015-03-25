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

import javax.annotation.Nullable;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalCallStateEnum;
import org.cleverbus.api.exception.LockFailureException;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.dao.ExternalCallDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * Polls confirmations in the {@link ExternalCallStateEnum#FAILED} state.
 * If there is this confirmation available then try to get and lock it for further processing.

 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ConfirmationPoolDbImpl implements ConfirmationPool {

    @Autowired
    private ExternalCallDao extCallDao;

    /**
     * Interval (in seconds) between two tries of failed confirmations.
     */
    @Value("${asynch.confirmation.interval}")
    private int interval;

    @Nullable
    @Override
    @Transactional
    public ExternalCall getNextConfirmation() {
        // -- is there next confirmation for processing?
        final ExternalCall extCall = extCallDao.findConfirmation(interval);

        if (extCall == null) {
            Log.debug("There is no FAILED confirmation for processing.");
            return null;
        }

        // try to get lock for the confirmation
        return lockConfirmation(extCall);
    }

    private ExternalCall lockConfirmation(final ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");

        try {
            ExternalCall lockedCall = extCallDao.lockConfirmation(extCall);
            Log.debug("Success in getting lock for confirmation " + lockedCall.toHumanString());
            return lockedCall;
        } catch (Exception ex) {
            throw new LockFailureException("Not success in getting lock for confirmation " + extCall.toHumanString(), ex);
        }
    }
}
