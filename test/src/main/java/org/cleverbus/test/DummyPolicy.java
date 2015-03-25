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

package org.cleverbus.test;

import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.Policy;
import org.apache.camel.spi.RouteContext;


/**
 * Dummy policy implementation that nothing to do.
 * This class is handy for testing routes with reference to any policy, e.g. authorization policy.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class DummyPolicy implements Policy {

    @Override
    public void beforeWrap(RouteContext routeContext, ProcessorDefinition<?> processorDefinition) {
        // nothing to do
    }

    @Override
    public Processor wrap(RouteContext routeContext, Processor processor) {
        return processor;
    }
}
