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

package org.cleverbus.api.exception;

import org.springframework.util.Assert;


/**
 * Catalog of internal error codes.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public enum InternalErrorEnum implements ErrorExtEnum {

    // ------------------------------------------------------------------------
    // internal errors
    // ------------------------------------------------------------------------

    /**
     * unspecified error
     */
    E100("unspecified error"),

    /**
     * the request message is not valid against to XSD schema
     */
    E101("the request message is not valid against to XSD schema"),

    /**
     * the validation error
     */
    E102("the validation error"),

    /**
     * I/O error during communication with target system
     */
    E103("I/O error during communication with target system"),

    /**
     * the request does not contain trace header
     */
    E104("the request does not contain trace header"),

    /**
     * the trace header does not contain all mandatory parameters
     */
    E105("the trace header does not contain all mandatory parameters"),

    /**
     * error during saving asynchronous message into storage
     */
    E106("error during saving asynchronous message into storage"),

    /**
     * no data found
     */
    E107("no data found"),

    /**
     * multiple results found
     */
    E108("multiple results found"),

    /**
     * the validation error - invalid data
     */
    E109("the validation error - invalid data"),

    /**
     * the validation error - there are no mandatory elements
     */
    E110("the validation error - there are no mandatory elements in XML request"),

    /**
     * the request message is not valid XML
     */
    E111("the request message is not valid XML"),

    /**
     * Locking exception - unsuccessful getting lock for the DB record.
     */
    E112("locking exception - unsuccessful getting lock for the DB record."),

    /**
     * There is no requested invoice in the repository.
     */
    E113("there is no requested invoice in the repository"),

    /**
     * Request is rejected because of throttling rules.
     */
    E114("request is rejected because of throttling rules"),

    /**
     * I/O error during saving file
     */
    E115("I/O error during saving file"),

    /**
     * Message stays repeatedly in PROCESSING state, probably because of some error.
     */
    E116("Message stays repeatedly in PROCESSING state, probably because of some error"),

    /**
     * Access is denied - there is no required authorization role.
     */
    E117("Access is denied - there is no required authorization role"),

    /**
     * Error occurred during extension loading, configuration error.
     */
    E118("Error occurred during extension loading, configuration error."),

    /**
     * Asynchronous request was rejected because ESB was stopping.
     */
    E119("Asynchronous request was rejected because ESB was stopping."),

    /**
     * the trace identifier does not contain allowed values
     */
    E120("the trace identifier does not contain allowed values"),

    /**
     * Message changed to POSTPONED state repeatedly and max. limit for starting processing was exceeded.
     */
    E121("message changed to POSTPONED state repeatedly and max. limit for starting processing was exceeded");


    private String errDesc;

    /**
     * Creates new error code with specified description.
     *
     * @param errDesc the error description
     */
    private InternalErrorEnum(String errDesc) {
        Assert.hasText(errDesc, "the errDesc must not be empty");

        this.errDesc = errDesc;
    }

    @Override
    public String getErrorCode() {
        return name();
    }

    @Override
    public String getErrDesc() {
        return errDesc;
    }
}
