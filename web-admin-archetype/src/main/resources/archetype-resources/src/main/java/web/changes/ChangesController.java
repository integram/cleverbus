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

package ${package}.web.changes;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for displaying changes.txt (aka release notes).
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Controller
public class ChangesController {

    public static final String CHANGES_URI = "/changes";

    @RequestMapping(value = CHANGES_URI, method = RequestMethod.GET)
    @ResponseBody
    public String getChangesContent() throws IOException {
        ClassPathResource resource = new ClassPathResource("changes.txt");

        // add end of lines
        String resStr = "";
        List<String> lines = IOUtils.readLines(resource.getInputStream(), "utf-8");
        for (String line : lines) {
            resStr += line;
            resStr += "<br/>${symbol_escape}n";
        }

        return resStr;
    }
}
