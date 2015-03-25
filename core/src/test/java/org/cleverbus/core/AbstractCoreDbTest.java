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

package org.cleverbus.core;

import java.util.HashMap;
import java.util.Map;

import org.cleverbus.api.asynch.model.TraceHeader;
import org.cleverbus.api.asynch.model.TraceIdentifier;
import org.cleverbus.core.common.asynch.AsynchInMessageRoute;
import org.cleverbus.core.common.asynch.TraceHeaderProcessor;
import org.cleverbus.test.AbstractDbTest;
import org.cleverbus.test.ActiveRoutes;

import org.joda.time.DateTime;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;


/**
 * Parent class for all tests with database in core module.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ContextConfiguration(locations = {"classpath:/META-INF/test_core_db_conf.xml", "classpath:/META-INF/sp_async.xml"})
@ActiveRoutes(classes = {AsynchInMessageRoute.class})
public abstract class AbstractCoreDbTest extends AbstractDbTest {

    private TraceHeader traceHeader;

    private Map<String, Object> headers;

    /**
     * Prepares {@link TraceHeaderProcessor#TRACE_HEADER trace header} data.
     */
    @Before
    public void prepareTraceHeaderData() {
        headers = new HashMap<String, Object>();

        traceHeader = new TraceHeader();

        TraceIdentifier traceId = new TraceIdentifier();
        traceId.setCorrelationID("123-456-789");
        traceId.setApplicationID("crm");
        traceId.setTimestamp(DateTime.now());

        getTraceHeader().setTraceIdentifier(traceId);

        getHeaders().put(TraceHeaderProcessor.TRACE_HEADER, getTraceHeader());
    }

    protected TraceHeader getTraceHeader() {
        return traceHeader;
    }

    protected Map<String, Object> getHeaders() {
        return headers;
    }
}
