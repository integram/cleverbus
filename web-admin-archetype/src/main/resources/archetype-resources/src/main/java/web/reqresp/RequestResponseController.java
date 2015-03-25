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

package ${package}.web.reqresp;

import java.util.List;

import ${package}.services.log.LogParserConstants;
import ${package}.web.common.editor.DateTimeEditor;
import org.cleverbus.api.entity.Request;
import org.cleverbus.core.reqres.RequestResponseService;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller that encapsulates actions around request/response tracking.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 * @since 0.4
 */
@Controller
@RequestMapping("/reqResp")
public class RequestResponseController {

    @Autowired
    private RequestResponseService requestResponseService;

    @RequestMapping(value = "/search", method = { RequestMethod.GET, RequestMethod.POST })
    public String searchRequests(
                @RequestParam(value = "fromDate", required = false) DateTime fromDate,
                @RequestParam(value = "toDate", required = false) DateTime toDate,
                @RequestParam(value = "uri", required = false) String uri,
                @RequestParam(value = "content", required = false) String content,
                @ModelAttribute("model") ModelMap model) {

        if (fromDate != null && toDate != null) {
            List<Request> requestList =
                    requestResponseService.findByCriteria(fromDate.toDate(), toDate.toDate(), uri, content);

            model.addAttribute("fromDate",
                    LogParserConstants.LOGBACK_ISO8601_FORMAT.print(
                            fromDate));
            model.addAttribute("toDate",
                    LogParserConstants.LOGBACK_ISO8601_FORMAT.print(
                            toDate));

            if (!requestList.isEmpty()) {
                model.addAttribute("requestList", requestList);
            } else {
                model.addAttribute("emptyList", Boolean.TRUE);
            }
        } else {

            model.addAttribute("fromDate",
                    LogParserConstants.LOGBACK_ISO8601_FORMAT.print(
                            DateTime.now()
                                    .minusHours(1)
                                    .withMinuteOfHour(0)
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0)));
            model.addAttribute("toDate",
                    LogParserConstants.LOGBACK_ISO8601_FORMAT.print(
                            DateTime.now()
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0)));
        }

        model.addAttribute("uri", uri);
        model.addAttribute("content", content);

        return "reqRespSearch";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(DateTime.class, new DateTimeEditor());
        binder.registerCustomEditor(DateMidnight.class, new DateTimeEditor());
    }

}
