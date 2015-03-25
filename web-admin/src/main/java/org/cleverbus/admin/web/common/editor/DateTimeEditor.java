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

package org.cleverbus.admin.web.common.editor;

import java.beans.PropertyEditorSupport;

import org.cleverbus.admin.services.log.LogParserConstants;

import org.joda.time.DateTime;

/**
 * Custom property editor for {@link org.joda.time.DateTime} of Joda-Time format conversion.
 */
public class DateTimeEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        return LogParserConstants.LOGBACK_ISO8601_OPTIONAL_TIME_FORMAT.print((DateTime) getValue());
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(LogParserConstants.LOGBACK_ISO8601_OPTIONAL_TIME_FORMAT.parseDateTime(text));
    }
}
