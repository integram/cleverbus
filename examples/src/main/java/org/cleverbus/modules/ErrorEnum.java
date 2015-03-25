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

package org.cleverbus.modules;

import org.cleverbus.api.exception.ErrorExtEnum;

import org.springframework.util.Assert;

/**
 * Catalog of custom error codes.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public enum ErrorEnum implements ErrorExtEnum {

    ;

    private String errDesc;

    /**
     * Creates new error code with specified description.
     *
     * @param errDesc the error description
     */
    private ErrorEnum(String errDesc) {
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
