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

package org.cleverbus.core.monitoring;

import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.asynch.msg.MessageOperationService;
import org.cleverbus.core.common.asynch.queue.JobStarterForMessagePooling;
import org.cleverbus.core.common.asynch.repair.RepairMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;


/**
 * JMX exporter of message administration operations.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ManagedResource(description = "Message Administration")
public class MessageAdminOperations {

    @Autowired
    private MessageOperationService messageOperationService;

    @Autowired
    private JobStarterForMessagePooling messagePooling;

    @Autowired
    private RepairMessageService repairMessageService;


    @ManagedOperation(description = "Restarts message for next processing. "
            + "totalRestart parameter determines if message should start from scratch again (true) or "
            + "if message should continue when it failed (false).")
    public void restartMessage(long msgId, boolean totalRestart) {
        Log.debug("Restart message by JMX ...");

        messageOperationService.restartMessage(msgId, totalRestart);
    }

    @ManagedOperation(description = "Cancels next message processing, sets message to CANCEL state.")
    public void cancelMessage(long msgId) {
        Log.debug("Cancel message (id = " + msgId + ") by JMX ...");

        messageOperationService.cancelMessage(msgId);
    }

    @ManagedOperation(description = "Starts next processing of PARTLY_FAILED and POSTPONED messages.")
    public void startNextProcessing() throws Exception {
        Log.debug("Start next processing by JMX ...");

        messagePooling.start();
    }

    @ManagedOperation(description = "Starts repairing processing messages.")
    public void repairProcessingMessages() throws Exception {
        Log.debug("Starts repairing processing messages by JMX ...");

        repairMessageService.repairProcessingMessages();
    }
}
