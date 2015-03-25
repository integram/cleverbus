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

package org.cleverbus.core.common.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.cleverbus.api.exception.IllegalDataException;
import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.test.ErrorTestEnum;

import org.junit.Test;


/**
 * Test suite for {@link ExceptionTranslator}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ExceptionTranslatorTest {

    @Test
    public void testComposeErrorMessage() {
        Exception ex = new IllegalDataException("wrong data", new IllegalArgumentException("wrong number format"));

        String errMsg = ExceptionTranslator.composeErrorMessage(ErrorTestEnum.E200, ex);

        assertThat(errMsg, is("E200: error in billing (IllegalArgumentException: wrong number format)"));

        // unspecified error code
        errMsg = ExceptionTranslator.composeErrorMessage(InternalErrorEnum.E100, ex);

        assertThat(errMsg, is("E100: unspecified error (IllegalArgumentException: wrong number format)"));
    }
}
