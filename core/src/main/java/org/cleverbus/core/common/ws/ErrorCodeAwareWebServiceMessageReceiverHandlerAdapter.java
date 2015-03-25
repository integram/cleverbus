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

package org.cleverbus.core.common.ws;

import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.core.common.exception.ExceptionTranslator;
import org.cleverbus.core.common.ws.component.ErrorAwareWebServiceMessageReceiverHandlerAdapter;

import org.springframework.ws.InvalidXmlException;


/**
 * Extension error handler that adds {@link InternalErrorEnum} into fault message.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ErrorCodeAwareWebServiceMessageReceiverHandlerAdapter
        extends ErrorAwareWebServiceMessageReceiverHandlerAdapter {

    @Override
    protected String getFaultString(InvalidXmlException ex) {
        return ExceptionTranslator.composeErrorMessage(InternalErrorEnum.E111, ex);
    }
}
