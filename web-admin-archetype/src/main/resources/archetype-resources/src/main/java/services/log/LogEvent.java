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

package ${package}.services.log;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.util.Assert;

public class LogEvent {

    private final LogParserConfig config;

    private DateTime date;
    private String message;
    private final Object[] properties;

    public LogEvent(LogParserConfig config) {
        Assert.notNull(config);
        this.config = config;
        this.properties = new Object[config.getPropertyCount()];
    }

    public LogParserConfig getConfig() {
        return config;
    }

    public List<String> getPropertyNames() {
        return config.getPropertyNames();
    }

    public int getPropertyCount() {
        return getPropertyNames().size();
    }

    public Object[] getProperties() {
        return properties;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void appendMessage(String message) {
        this.message += message;
    }

}
