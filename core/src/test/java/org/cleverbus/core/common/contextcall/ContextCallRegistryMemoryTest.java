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

package org.cleverbus.core.common.contextcall;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.cleverbus.api.exception.NoDataFoundException;

import org.junit.Test;


/**
 * Test suite for {@link ContextCallRegistryMemoryImpl}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ContextCallRegistryMemoryTest {

    @Test
    public void testBasicScenarios() {
        ContextCallRegistry callRegistry = new ContextCallRegistryMemoryImpl();

        String callId = UUID.randomUUID().toString();

        // params
        ContextCallParams params = new ContextCallParams(String.class, "indexOf");

        callRegistry.addParams(callId, params);

        assertThat(callRegistry.getParams(callId), is(params));
        assertThat(callRegistry.getParams(callId), is(params));

        // response
        String res = "response";

        callRegistry.addResponse(callId, res);

        assertThat(callRegistry.getResponse(callId, String.class), is(res));
        assertThat(callRegistry.getResponse(callId, String.class), is(res));

        // clear all
        callRegistry.clearCall(callId);

        try {
            callRegistry.getParams(callId);
            fail("there is no params with call ID=" + callId);
        } catch (NoDataFoundException ex) {
            // everything OK
        }

        try {
            callRegistry.getResponse(callId, String.class);
            fail("there is no response with call ID=" + callId);
        } catch (NoDataFoundException ex) {
            // everything OK
        }
    }
}
