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

package org.cleverbus.common.jaxb;

import javax.annotation.Nullable;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


/**
 * JAXB adapter for automatic conversion between {@link XMLGregorianCalendar} to {@link DateTime}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">ISO_8601</a>
 */
public class JaxbDateAdapter {

    private static final DateTimeFormatter DATE_PRINT_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-ddZZ");

    private JaxbDateAdapter() {
    }

    @Nullable
    public static DateTime parseDate(@Nullable String dateStr) {
        if (dateStr == null) {
            return null;
        }

        return new DateTime(DatatypeConverter.parseDate(dateStr), DateTimeZone.getDefault());
    }

    @Nullable
    public static String printDate(@Nullable DateTime dt) {
        if (dt == null) {
            return null;
        }

        return DATE_PRINT_FORMATTER.print(dt);
    }

    @Nullable
    public static DateTime parseDateTime(@Nullable String dtStr) {
        if (dtStr == null) {
            return null;
        }

        return DateTime.parse(dtStr).withZone(DateTimeZone.getDefault());
    }

    @Nullable
    public static String printDateTime(@Nullable DateTime dt) {
        if (dt == null) {
            return null;
        }

        return dt.withZone(DateTimeZone.getDefault()).toString(ISODateTimeFormat.dateTime());
    }
}