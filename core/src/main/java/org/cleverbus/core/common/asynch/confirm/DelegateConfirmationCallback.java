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

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.cleverbus.api.asynch.confirm.ConfirmationCallback;
import org.cleverbus.api.asynch.confirm.ExternalSystemConfirmation;
import org.cleverbus.api.entity.ExternalSystemExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.common.log.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;


/**
 * Implementation of {@link ConfirmationCallback} that delegates confirmation
 * to {@link ExternalSystemConfirmation} implementation.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class DelegateConfirmationCallback implements ConfirmationCallback {

    private static final Set<MsgStateEnum> ALLOWED_STATES =
            Collections.unmodifiableSet(EnumSet.of(MsgStateEnum.OK, MsgStateEnum.FAILED));

    @Autowired(required = false)
    private List<ExternalSystemConfirmation> msgConfirmations;

    @Override
    public void confirm(Message msg) {
        Assert.notNull(msg, "Message must not be null");
        Assert.isTrue(ALLOWED_STATES.contains(msg.getState()), "Message must be in a final state to be confirmed");

        ExternalSystemConfirmation impl = getImplementation(msg.getSourceSystem());
        if (impl != null) {
            impl.confirm(msg);
        } else {
            Log.debug("Confirmation {} - no suitable ExternalSystemConfirmation implementation "
                    + "for the following external system: {}", msg.getState(), msg.getSourceSystem());
        }
    }

    /**
     * Gets (first) implementation for specified external system.
     *
     * @param externalSystem the source system
     * @return implementation
     */
    @Nullable
    protected ExternalSystemConfirmation getImplementation(ExternalSystemExtEnum externalSystem) {
        if (msgConfirmations == null) {
            return null;
        }

        // select the supported implementation for the source external system
        for (ExternalSystemConfirmation msgConfirmation : msgConfirmations) {
            final Set<ExternalSystemExtEnum> systems = msgConfirmation.getExternalSystems();

            List<String> supportedSystems = new LinkedList<String>();
            for (ExternalSystemExtEnum system : systems) {
                supportedSystems.add(system.getSystemName());
            }
            if (supportedSystems.contains(externalSystem.getSystemName())) {
                return msgConfirmation;
            }
        }

        return null;
    }
}
