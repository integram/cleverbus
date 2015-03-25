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

package org.cleverbus.api.event;

import java.lang.reflect.ParameterizedType;
import java.util.EventObject;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.util.Assert;



/**
 * Base class for implementing {@link EventNotifier Camel event notifiers}.
 * <p/>
 * Implements only one direct inherited child, no more inheritance levels.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public abstract class EventNotifierBase<T extends EventObject> extends EventNotifierSupport implements CamelContextAware {

    private Class<T> eventClass;

    private CamelContext camelContext;

    @SuppressWarnings("unchecked")
    public EventNotifierBase() {
        // valid only if there is only one inherited child of this class,
        //  see http://stackoverflow.com/questions/3403909/get-generic-type-of-class-at-runtime
        this.eventClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    @SuppressWarnings("unchecked")
	public final void notify(EventObject event) throws Exception {
		doNotify((T)event);
	}

    /**
     * Calls notification implementation.
     *
     * @param event the event
     */
    protected abstract void doNotify(T event) throws Exception;

    /**
     * {@inheritDoc}
     *
     * @param event the event
     * @return {@code true} if {@link EventObject} is instance of generic T, otherwise {@code false}
     */
    @Override
	public boolean isEnabled(EventObject event) {
        return eventClass.isAssignableFrom(event.getClass());
	}

    @Override
    public final void setCamelContext(CamelContext camelContext) {
        Assert.notNull(camelContext, "camelContext must not be null");

        this.camelContext = camelContext;
    }

    @Override
    public final CamelContext getCamelContext() {
        return camelContext;
    }

    /**
     * Gets {@link ProducerTemplate} default instance from Camel Context.
     *
     * @return ProducerTemplate
     * @throws IllegalStateException when there is no ProducerTemplate
     */
    public ProducerTemplate getProducerTemplate() {
        if (!isStarted() && !isStarting()) {
            throw new IllegalStateException(getClass().getName() + " is not started so far!");
        }

        Set<ProducerTemplate> templates = camelContext.getRegistry().findByType(ProducerTemplate.class);
        Assert.state(templates.size() >= 1, "ProducerTemplate must be at least one.");

        return templates.iterator().next();
    }
}
