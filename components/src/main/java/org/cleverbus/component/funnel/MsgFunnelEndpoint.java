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

package org.cleverbus.component.funnel;

import org.cleverbus.api.entity.Message;
import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.spi.AsyncEventNotifier;
import org.cleverbus.spi.msg.MessageService;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

import javax.annotation.Nullable;


/**
 * Endpoint for {@link MsgFunnelComponent msg-funnel} component.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class MsgFunnelEndpoint extends DefaultEndpoint {

    public static final int DEFAULT_IDLE_INTERVAL = 600;

    /**
     * Interval (in seconds) that determines how long can be message processing.
     */
    private int idleInterval = DEFAULT_IDLE_INTERVAL;

    /**
     * {@code true} if funnel component should guaranteed order of processing messages.
     * By default funnel works with running messages (PROCESSING, WAITING, WAITING_FOR_RES) only
     * and if it's necessary to guarantee processing order then also PARTLY_FAILED, POSTPONED and FAILED
     * messages should be involved.
     * <p/>
     * Use {@link #isExcludeFailedState()} to exclude FAILED state from searching messages.
     */
    private boolean guaranteedOrder;

    /**
     * {@link MsgStateEnum#FAILED FAILED} state is used for guaranteed order by default;
     * {@code true} if you want to exclude FAILED state.
     * <p/>
     * This option has influence only if {@link #isGuaranteedOrder() guaranteed processing order} is enabled.
     */
    private boolean excludeFailedState;

    /**
     * Funnel component identifier.
     */
    private String id;

    /**
     * Funnel value.
     * <p>
     * If this funnelValue is blank, then will be used funnelValue on {@link Message}
     * ({@link Message#getFunnelValue()}).
     * </p>
     */
    private String funnelValue;

    /**
     * Creates new endpoint.
     *
     * @param endpointUri the URI
     * @param component the "msg-funnel" component
     */
    public MsgFunnelEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new MsgFunnelProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("you cannot send messages to this endpoint:" + getEndpointUri());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    AsyncEventNotifier getAsyncEventNotifier() {
        return ((MsgFunnelComponent)getComponent()).getAsyncEventNotifier();
    }

    MessageService getMessageService() {
        return ((MsgFunnelComponent)getComponent()).getMessageService();
    }

    /**
     * Gets interval (in seconds) that determines how long can be message processing.
     *
     * @return interval
     */
    public int getIdleInterval() {
        return idleInterval;
    }

    public void setIdleInterval(int idleInterval) {
        this.idleInterval = idleInterval;
    }

    /**
     * Gets {@code true} if funnel component should guaranteed order of processing messages.
     *
     * @return {@code true} if funnel component should guaranteed order of processing messages otherwise {@code false}
     */
    public boolean isGuaranteedOrder() {
        return guaranteedOrder;
    }

    public void setGuaranteedOrder(boolean guaranteedOrder) {
        this.guaranteedOrder = guaranteedOrder;
    }

    public boolean isExcludeFailedState() {
        return excludeFailedState;
    }

    /**
     * Sets flag whether you want to exclude FAILED state from searching messages for guaranteed order.
     * <p/>
     * This option has influence only if {@link #isGuaranteedOrder() guaranteed processing order} is enabled.
     *
     * @param excludeFailedState {@code true} if you want to exclude FAILED state
     */
    public void setExcludeFailedState(boolean excludeFailedState) {
        this.excludeFailedState = excludeFailedState;
    }

    /**
     * Gets funnel component identifier.
     *
     * @return funnel component identifier
     */
    public String getId() {
        return id;
    }

    //TODO (juza) check unique ID

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets funnel value.
     *
     * @return funnel value, {@code NULL} - no funnel value (funnelValue on {@link Message} will be used)
     */
    @Nullable
    public String getFunnelValue() {
        return funnelValue;
    }

    /**
     * Sets funnel value.
     *
     * @param funnelValue funnel value, {@code NULL} - no funnel value (funnelValue on {@link Message} will be used)
     */
    public void setFunnelValue(@Nullable String funnelValue) {
        this.funnelValue = funnelValue;
    }
}
