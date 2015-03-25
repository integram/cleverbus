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

import static junit.framework.Assert.assertNotNull;

import java.util.Collections;
import java.util.Set;

import org.cleverbus.api.asynch.confirm.ExternalSystemConfirmation;
import org.cleverbus.api.entity.ExternalSystemExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.core.AbstractCoreTest;

import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link DelegateConfirmationCallback}.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 */
public class DelegateConfirmationCallbackTest extends AbstractCoreTest {

    private DelegateConfirmationCallback confirmation;

    @Before
    public void initDelegateConfirmationCallback() {
        confirmation = new DelegateConfirmationCallback();
        setPrivateField(confirmation, "msgConfirmations", Collections.singletonList(getDefaultImplementation("CRM")));
    }

    @Test
    public void testGetImplementation() throws Exception {
        assertNotNull(confirmation.getImplementation(getSourceSystem("CRM")));
    }

    private static ExternalSystemConfirmation getDefaultImplementation(final String sourceSystem) {
        return new ExternalSystemConfirmation() {
            @Override
            public void confirm(Message msg) {
                //nothing
            }

            @Override
            public Set<ExternalSystemExtEnum> getExternalSystems() {
                return Collections.singleton(getSourceSystem(sourceSystem));
            }
        };
    }

    private static ExternalSystemExtEnum getSourceSystem(final String sourceSystem) {
        return new ExternalSystemExtEnum() {
            @Override
            public String getSystemName() {
                return sourceSystem;
            }
        };
    }

}
