#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.web.msg;


import ${package}.dao.dto.MessageReportDto;
import org.cleverbus.api.entity.MsgStateEnum;


/**
 * DTO for message report view.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
public class TransformedMessageDto {

    private String serviceName; //  as service
    private String operationName; // as operation_name
    private String sourceSystem; // asd source_system

    /* STATES fields obtain their value from SQL -> Column State, default 0 */
    private int stateOK = 0;
    private int stateProcessing = 0;
    private int statePartlyFailed = 0;
    private int stateFailed = 0;
    private int stateWaiting = 0;
    private int stateWaitingForRes = 0;
    private int stateCancel = 0;

    public String getServiceName() {
        return serviceName;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public int getStateOK() {
        return stateOK;
    }

    public int getStateProcessing() {
        return stateProcessing;
    }

    public int getStatePartlyFailed() {
        return statePartlyFailed;
    }

    public int getStateFailed() {
        return stateFailed;
    }

    public int getStateWaiting() {
        return stateWaiting;
    }

    public int getStateWaitingForRes() {
        return stateWaitingForRes;
    }

    public int getStateCancel() {
        return stateCancel;
    }

    // Atomic Setters
    void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    // Setters for stateFields
    void addStatesCount(int stateCount, String state) {

        if (MsgStateEnum.OK.name().equals(state)) {
            this.stateOK += stateCount;
        } else if (MsgStateEnum.PROCESSING.name().equals(state)) {
            this.stateProcessing += stateCount;
        } else if (MsgStateEnum.PARTLY_FAILED.name().equals(state)) {
            this.statePartlyFailed += stateCount;
        } else if (MsgStateEnum.FAILED.name().equals(state)) {
            this.stateFailed += stateCount;
        } else if (MsgStateEnum.WAITING.name().equals(state)) {
            this.stateWaiting += stateCount;
        } else if (MsgStateEnum.WAITING_FOR_RES.name().equals(state)) {
            this.stateWaitingForRes += stateCount;
        } else if (MsgStateEnum.CANCEL.name().equals(state)) {
            this.stateCancel += stateCount;
        }
    }

    /**
     * Method used for comparison of the last item's fields in the transformed list and the next item's fields.
     *
     * @param item object is the next object {@link MessageReportDto} in the result list
     * @return true if compared fields don't mach, if not return is false
     */
    public boolean differs(MessageReportDto item) {
        return !serviceName.equals(item.getServiceName())
                || !operationName.equals(item.getOperationName())
                || !sourceSystem.equals(item.getSourceSystem());
    }

    /**
     * Class fields setters.
     *
     * @param item the next object to the last object in the result list of type {@link MessageReportDto}
     */
    public void fill(MessageReportDto item) {
        this.setServiceName(item.getServiceName());
        this.setOperationName(item.getOperationName());
        this.setSourceSystem(item.getSourceSystem());
        this.addStatesCount(item.getStateCount(), item.getState());
    }

    /**
     * Custom setter method for the case that item and last object in the result list match
     * in all fields except the state or statesCount.
     *
     * @param item the next object to the last object in the result list of type {@link MessageReportDto}
     */
    public void add(MessageReportDto item) {
        addStatesCount(item.getStateCount(), item.getState());
    }
}
