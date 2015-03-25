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

package org.cleverbus.admin.web.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.cleverbus.core.common.contextcall.ContextCall;
import org.cleverbus.core.common.ws.WsdlRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for displaying overview of WSDLs.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
@Controller
public class WsdlController {

    public static final String VIEW_NAME = "wsdl";

    @Autowired
    private ContextCall contextCall;


    @RequestMapping("/" + VIEW_NAME)
    @SuppressWarnings("unchecked")
    public String getEndpoints(@ModelAttribute("model") ModelMap model) {
        Collection<String> wsdls = contextCall.makeCall(WsdlRegistry.class, "getWsdls", Collection.class);

        // note: wsdls will be always != null
        if (wsdls != null) {
            List<String> sortedWsdls = new ArrayList<String>(wsdls);

            Collections.sort(sortedWsdls);

            model.addAttribute("wsdls", sortedWsdls);
        }

        return VIEW_NAME;
    }
}
