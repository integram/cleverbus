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

package org.cleverbus.core.common.route;

import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.Nullable;


/**
 * Contract for getting endpoints overview.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface EndpointRegistry {

    /**
     * Gets endpoint URIs which match specified pattern.
     * <p/>
     * Example: method with the following pattern {@code ^(spring-ws|servlet).*$} will return URIs which starts
     * by spring-ws or servlet.
     *
     * @param includePattern {@link Pattern pattern} for filtering endpoints URI - only whose URIs will
     *                       match specified pattern will be returned
     * @return collection of endpoint URIs
     */
    Collection<String> getEndpointURIs(@Nullable String includePattern);

}
