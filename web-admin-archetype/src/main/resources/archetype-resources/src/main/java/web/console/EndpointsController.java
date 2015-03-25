#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.web.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.cleverbus.core.common.contextcall.ContextCall;
import org.cleverbus.core.common.route.EndpointRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for displaying overview of endpoints.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
@Controller
public class EndpointsController {

    public static final String VIEW_NAME = "endpoints";

    @Autowired
    private ContextCall contextCall;

    /**
     * Pattern for filtering endpoints URI - only whose URIs will match specified pattern will be returned.
     */
    @Value("${symbol_dollar}{endpoints.includePattern}")
    private String endpointsIncludePattern;


    @RequestMapping("/" + VIEW_NAME)
    @SuppressWarnings("unchecked")
    public String getEndpoints(@ModelAttribute("model") ModelMap model) {
        Collection<String> endpoints = contextCall.makeCall(EndpointRegistry.class, "getEndpointURIs", Collection.class,
                endpointsIncludePattern);

        // note: endpoints will be always != null
        if (endpoints != null) {
            List<String> sortedEndpoints = new ArrayList<String>(endpoints);

            // group same URIs together
            Collections.sort(sortedEndpoints);

            model.addAttribute("endpoints", sortedEndpoints);
        }

        return VIEW_NAME;
    }
}
