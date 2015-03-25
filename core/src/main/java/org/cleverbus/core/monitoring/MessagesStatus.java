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


import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.spi.msg.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * JMX exporter of message processing statistics.
 *
 * @author <a href="mailto:Jaromir.Stradej@cleverlance.com">Jaromir Stradej</a>
 * @version $Id: MessagesStatus.java 418 2014-10-27 14:15:09Z pjuza@CLANCE.LOCAL $
 */
@ManagedResource(description = "Message State Information")
public class MessagesStatus {

    @Autowired
    private MessageService messageService;


    @ManagedAttribute(description = "Count of messages in state FAILED")
    public int getCountOfFailed() {
        return messageService.getCountMessages(MsgStateEnum.FAILED, null);
    }

    @ManagedAttribute(description = "Count of messages in state PROCESSING")
    public int getCountOfProcessing() {
        return messageService.getCountMessages(MsgStateEnum.PROCESSING, null);
    }

    @ManagedAttribute(description = "Count of messages in state CANCEL")
    public int getCountOfCanceled() {
        return messageService.getCountMessages(MsgStateEnum.CANCEL, null);
    }

    @ManagedAttribute(description = "Count of messages in state NEW")
    public int getCountOfNew() {
        return messageService.getCountMessages(MsgStateEnum.NEW, null);
    }

    @ManagedAttribute(description = "Count of messages in state OK")
    public int getCountOfOk() {
        return messageService.getCountMessages(MsgStateEnum.OK, null);
    }

    @ManagedAttribute(description = "Count of messages in state PARTLY_FAILED")
    public int getCountOfPartlyFailed() {
        return messageService.getCountMessages(MsgStateEnum.PARTLY_FAILED, null);
    }

    @ManagedAttribute(description = "Count of messages in state WAITING")
    public int getCountOfWaiting() {
        return messageService.getCountMessages(MsgStateEnum.WAITING, null);
    }

    @ManagedAttribute(description = "Count of messages in state WAITING_FOR_RES")
    public int getCountOfWaitingForResponse() {
        return messageService.getCountMessages(MsgStateEnum.WAITING_FOR_RES, null);
    }

    @ManagedAttribute(description = "Count of messages in state POSTPONED")
    public int getCountOfPostponed() {
        return messageService.getCountMessages(MsgStateEnum.POSTPONED, null);
    }

    @ManagedAttribute(description = "Count of messages in state CANCEL")
    public int getCountOfCancel() {
        return messageService.getCountMessages(MsgStateEnum.CANCEL, null);
    }

    @ManagedOperation(description = "Count of messages in state FAILED and after interval")
    public int getCountOfFailedAfterInterval(int interval) {
        return messageService.getCountMessages(MsgStateEnum.FAILED, interval);
    }

    @ManagedOperation(description = "Count of messages in state PROCESSING and after interval")
    public int getCountOfProcessingAfterInterval(int interval) {
        return messageService.getCountMessages(MsgStateEnum.PROCESSING, interval);
    }

    @ManagedOperation(description = "Count of messages in state WAITING and after interval")
    public int getCountOfWaitingAfterInterval(int interval) {
        return messageService.getCountMessages(MsgStateEnum.WAITING, interval);
    }

    @ManagedOperation(description = "Count of messages in state OK and after interval")
    public int getCountOfOkAfterInterval(int interval) {
        return messageService.getCountMessages(MsgStateEnum.OK, interval);
    }

    @ManagedOperation(description = "Count of messages in state NEW and after interval")
    public int getCountOfNewAfterInterval(int interval) {
        return messageService.getCountMessages(MsgStateEnum.NEW, interval);
    }

    @ManagedOperation(description = "Count of messages in state PARTLY_FAILED and after interval")
    public int getCountOfPartlyFailedAfterInterval(int interval) {
        return messageService.getCountMessages(MsgStateEnum.PARTLY_FAILED, interval);
    }

    @ManagedOperation(description = "Count of messages in state CANCEL and after interval")
    public int getCountOfCancelAfterInterval(int interval) {
        return messageService.getCountMessages(MsgStateEnum.CANCEL, interval);
    }

    @ManagedOperation(description = "Count of messages in state WAITING_FOR_RES and after interval")
    public int getCountOfWaitingForResponseAfterInterval(int interval) {
        return messageService.getCountMessages(MsgStateEnum.WAITING_FOR_RES, interval);
    }

    @ManagedOperation(description = "Count of messages in state POSTPONED and after interval")
    public int getCountOfPostponedAfterInterval(int interval) {
        return messageService.getCountMessages(MsgStateEnum.POSTPONED, interval);
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}
