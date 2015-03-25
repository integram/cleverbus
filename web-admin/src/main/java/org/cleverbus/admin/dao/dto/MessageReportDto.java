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

package org.cleverbus.admin.dao.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * DTO with main message attributes used for message reports.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
public class MessageReportDto {

    private int messageId; // SQL "msg_id"
    private String serviceName;
    private String operationName;
    private String sourceSystem;
    private String state; // state CONSTANTs
    private int stateCount; // SQL: Count(*) as pocet
    private Date messageReceiveTimestamp; // SQL "recieve_timestamp"

    /* Default Constructor */

    public MessageReportDto() {
    }

    /* Getters & Setters */

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Date getMessageReceiveTimestamp() {
        return messageReceiveTimestamp;
    }

    public void setMessageReceiveTimestamp(Date messageReceiveTimestamp) {
        this.messageReceiveTimestamp = messageReceiveTimestamp;
    }

    public int getStateCount() {
        return stateCount;
    }

    public void setStateCount(int stateCount) {
        this.stateCount = stateCount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("MsgID:",messageId)
                .append("MsgRecieveDateTime", messageReceiveTimestamp)
                .append("ServiceName",serviceName)
                .append("MsgOpName",operationName)
                .append("MsgSrcSys",sourceSystem)
                .append("MsgState",state)
                .append("StateCount",stateCount)
                .toString();
    }

}
