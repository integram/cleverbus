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

package org.cleverbus.core.common.validator;

import org.cleverbus.api.asynch.model.TraceIdentifier;

/**
 * Validator that validates contents of the {@link TraceIdentifier} according list of allowed values.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 * @since 0.4
 */
public interface TraceIdentifierValidator {

    /**
     * Checks that {@link TraceIdentifier} contains values which are valid.
     *
     * @param traceIdentifier the {@link TraceIdentifier}
     * @return true if the {@link TraceIdentifier} contains allowed values
     */
    boolean isValid(TraceIdentifier traceIdentifier);

}
