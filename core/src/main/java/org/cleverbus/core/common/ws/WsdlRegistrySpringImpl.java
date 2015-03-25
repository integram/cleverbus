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

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;


/**
 * Implementation of {@link WsdlRegistry} based on Spring capabilities that supposes the following prerequisites:
 * <ul>
 *     <li>all WSDLs are defined/published via {@link SimpleWsdl11Definition}
 *     <li>Spring bean IDs will contain the WSDL name
 * </ul>
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class WsdlRegistrySpringImpl implements WsdlRegistry {

    @Autowired
    private Map<String, SimpleWsdl11Definition> wsdls;

    @Override
    public Collection<String> getWsdls() {
        return wsdls.keySet();
    }
}
