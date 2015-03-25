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

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for displaying admin console directory of actions
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 */
@Controller
public class ConsoleController {

    public static final String JAVAMELODY_DISABLED = "javamelody.disabled";

    public static final String VIEW_NAME = "console";

    @RequestMapping("/console")
    public String showConsole(@ModelAttribute("model") ModelMap model) {

        // this variable is set only if monitoring is switched on
        final String state = System.getProperty(JAVAMELODY_DISABLED);

        final Boolean monitoring = StringUtils.isEmpty(state) || Boolean.valueOf(state).equals(Boolean.TRUE);
        model.addAttribute("javamelody", monitoring);

        return VIEW_NAME;
    }
}
