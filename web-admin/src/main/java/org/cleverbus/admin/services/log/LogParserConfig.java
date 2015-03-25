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

package org.cleverbus.admin.services.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 * Configuration holder for {@link LogParser}
 */
public class LogParserConfig {

    private DateTime fromDate;
    private Integer limit;
    private Map<String, String> filter;
    private List<String> groupBy;
    private Integer groupLimit;
    private String msg;

    public LogParserConfig() {
        setFromDate(DateTime.now());
        setLimit(LogParserConstants.MAX_RESULT_LIMIT);
        setFilter(Collections.<String, String>emptyMap());
        setGroupBy(Collections.<String>emptySet());
        setGroupLimit(null);
        setMsg(null);
    }

    public LogEvent createLogEvent() {
        return new LogEvent(this);
    }

    /** the date to find log lines after */
    public DateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(DateTime fromDate) {
        this.fromDate = fromDate;
    }

    /** the limit of how many lines should be returned */
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * The Property=Value sets that the specified log event should have in order to not be ignored.
     */
    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        if (filter == null) {
            this.filter = Collections.emptyMap();
        } else {
            this.filter = new LinkedHashMap<String, String>(filter);
            this.filter.keySet().retainAll(getPropertyNames());
        }
    }

    /**
     * The property names that results will be grouped by.
     */
    public List<String> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(Collection<String> groupBy) {
        if (groupBy == null) {
            this.groupBy = Collections.emptyList();
        } else {
            this.groupBy = new ArrayList<String>(getPropertyNames());
            this.groupBy.retainAll(groupBy);
        }
    }

    /** max number of lines to return for each group */
    public Integer getGroupLimit() {
        return groupLimit;
    }

    public void setGroupLimit(Integer groupLimit) {
        this.groupLimit = groupLimit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fromDate", fromDate)
                .append("limit", limit)
                .append("filter", filter)
                .append("groupBy", groupBy)
                .append("groupLimit", groupLimit)
                .toString();
    }

    public String describe() {
        return String.format("first %s log lines with up to %s per group, after %s, grouped by %s, with properties %s",
                limit, groupLimit, fromDate, groupBy, filter);
    }

    public Pattern getDatePattern() {
        return LogParserConstants.LOG_LINE_DATE_PATTERN;
    }

    public DateTimeFormatter getDateFormat() {
        return LogParserConstants.LOGBACK_ISO8601_OPTIONAL_TIME_FORMAT;
    }

    public Pattern getPropertiesPattern() {
        return LogParserConstants.LOG_LINE_PROPERTIES_PATTERN;
    }

    public int getPropertyCount() {
        return getPropertyNames().size();
    }

    public List<String> getPropertyNames() {
        return LogParserConstants.LOG_LINE_PROPERTIES;
    }

    public boolean isMatchesFilter(String propertyValue, int propertyIndex) {
        String expectedValue = filter.get(getPropertyNames().get(propertyIndex));
        return expectedValue == null || StringUtils.containsIgnoreCase(propertyValue, expectedValue);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
