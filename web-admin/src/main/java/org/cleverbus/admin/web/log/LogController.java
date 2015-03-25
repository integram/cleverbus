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

package org.cleverbus.admin.web.log;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.cleverbus.admin.services.log.LogEvent;
import org.cleverbus.admin.services.log.LogParser;
import org.cleverbus.admin.services.log.LogParserConfig;
import org.cleverbus.admin.services.log.LogParserConstants;
import org.cleverbus.admin.web.common.editor.DateTimeEditor;
import org.cleverbus.common.log.Log;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;


/**
 * Controller that encapsulates actions around logs.
 */
@Controller
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogParser logParser;

    @RequestMapping("/")
    public String getLogSearch(@RequestParam(value = "fromDate", required = false) DateTime fromDate,
                               @RequestParam MultiValueMap<String, String> params,
                               Model model) throws UnsupportedEncodingException {
        if (fromDate != null) {
            params.remove("fromDate");
            // remove empty values:
            for (List<String> valueList : params.values()) {
                ListIterator<String> values = valueList.listIterator();
                while (values.hasNext()) {
                    if (!StringUtils.hasText(values.next())) {
                        values.remove();
                    }
                }
            }
            model.mergeAttributes(params);
            return "redirect:" + UriUtils.encodePath(
                    LogParserConstants.LOGBACK_ISO8601_FORMAT.print(fromDate), "UTF-8");
        }

        model.addAttribute("fromDate",
                LogParserConstants.LOGBACK_ISO8601_FORMAT.print(
                        DateTime.now()
                                .minusHours(2)
                                .withMinuteOfHour(0)
                                .withSecondOfMinute(0)
                                .withMillisOfSecond(0)));

        LogParserConfig logParserConfig = new LogParserConfig();
        logParserConfig.setGroupBy(LogParserConstants.DEFAULT_GROUP_BY_PROPERTY);
        logParserConfig.setGroupLimit(LogParserConstants.DEFAULT_GROUP_SIZE);
        model.addAttribute("config", logParserConfig);

        return "logSearch";
    }

    @RequestMapping("/{fromDate}")
    public String getLogOverview(
            @PathVariable("fromDate") DateTime fromDate,
            @RequestParam(value = LogParserConstants.VIEW_REQUEST_PARAMETER, required = false) String view,
            @RequestParam(value = LogParserConstants.GROUP_BY_REQUEST_PARAMETER, required = false) Set<String> groupBy,
            @RequestParam(value = LogParserConstants.GROUP_SIZE_REQUEST_PARAMETER, required = false) Integer groupSize,
            @RequestParam Map<String, String> params,
            Model model) {
        try {
            LogParserConfig logParserConfig = new LogParserConfig();
            logParserConfig.setFromDate(fromDate);
            logParserConfig.setGroupBy(groupBy);
            logParserConfig.setGroupLimit(groupSize);
            logParserConfig.setFilter(getSubProperties(params, "filter."));
            logParserConfig.setMsg(params.get("msg"));

            Log.info("Looking for {}", logParserConfig.describe());

            File[] logFiles = logParser.getLogFiles(logParserConfig.getFromDate());
            Iterator<LogEvent> logEvents = (logFiles.length > 0)
                    ? logParser.getLogEventIterator(logParserConfig, Arrays.asList(logFiles))
                    : Collections.<LogEvent>emptyList().iterator();

            model.addAttribute("fromDate", fromDate);
            model.addAttribute("config", logParserConfig);
            model.addAttribute("logEvents", logEvents);
            model.addAttribute("view", view);
        } catch (IOException exc) {
            model.addAttribute("logErr", "Error occurred during reading log files:\n" + exc);
        }
        return "logByDate";
    }

    private Map<String, String> getSubProperties(Map<String, String> properties, String prefix) {
        Map<String, String> subProperties = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            if (property.getKey().startsWith(prefix)) {
                subProperties.put(property.getKey().substring(prefix.length()), property.getValue());
            }
        }
        return subProperties;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(DateTime.class, new DateTimeEditor());
        binder.registerCustomEditor(DateMidnight.class, new DateTimeEditor());
    }
}
