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

package org.cleverbus.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * This class provides access to logging facilities.
 *
 * @author <a href="mailto:michal.palicka@cleverlance.com">Michal Palicka</a>
 * @author <a href="mailto:jan.loose@cleverlance.com">Jan Loose</a>
 * @version $Id: Log.java 8499 2012-04-16 17:10:32Z jloose@CLANCE.LOCAL $
 */
public final class Log {

    // ----------------------------------------------------------------------
    // util
    // ----------------------------------------------------------------------

    private static String getLogger() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String logger = Logger.ROOT_LOGGER_NAME;
        int i = 3;
        if (stackTrace.length >= i) {
            logger = stackTrace[i++].getClassName();
            while (logger.startsWith(Log.class.getName()) && (i <= stackTrace.length) && (i <= 7)) {
                logger = stackTrace[i++].getClassName();
            }
        }
        return logger;
    }

    // ----------------------------------------------------------------------
    // logging level status
    // ----------------------------------------------------------------------

    public static boolean isDebugEnabled() {
        return LoggerFactory.getLogger(getLogger()).isDebugEnabled();
    }

    public static boolean isDebugEnabled(String loggerName) {
        return LoggerFactory.getLogger(loggerName).isDebugEnabled();
    }

    public static boolean isInfoEnabled() {
        return LoggerFactory.getLogger(getLogger()).isInfoEnabled();
    }

    public static boolean isInfoEnabled(String loggerName) {
        return LoggerFactory.getLogger(loggerName).isInfoEnabled();
    }

    public static boolean isWarnEnabled() {
        return LoggerFactory.getLogger(getLogger()).isWarnEnabled();
    }

    public static boolean isWarnEnabled(String loggerName) {
        return LoggerFactory.getLogger(loggerName).isWarnEnabled();
    }

    public static boolean isErrorEnabled() {
        return LoggerFactory.getLogger(getLogger()).isErrorEnabled();
    }

    public static boolean isErrorEnabled(String loggerName) {
        return LoggerFactory.getLogger(loggerName).isErrorEnabled();
    }

    // ----------------------------------------------------------------------
    // debug
    // ----------------------------------------------------------------------

    public static void debug(String message) {
        LoggerFactory.getLogger(getLogger()).debug(message);
    }

    public static void debug(String message, Object... values) {
        LoggerFactory.getLogger(getLogger()).debug(message, values);
    }

    public static void debug(String message, Throwable t) {
        LoggerFactory.getLogger(getLogger()).debug(message, t);
    }

    public static void debug(String message, Throwable t, Object... values) {
        if (isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(message, values);
            LoggerFactory.getLogger(getLogger()).debug(ft.getMessage(), t);
        }
    }

    public static void debugTo(String loggerName, String message) {
        LoggerFactory.getLogger(loggerName).debug(message);
    }

    public static void debugTo(String loggerName, String message, Object... values) {
        LoggerFactory.getLogger(loggerName).debug(message, values);
    }

    public static void debugTo(String loggerName, String message, Throwable t) {
        LoggerFactory.getLogger(loggerName).debug(message, t);
    }

    public static void debugTo(String loggerName, String message, Throwable t, Object... values) {
        if (isDebugEnabled(loggerName)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(message, values);
            LoggerFactory.getLogger(loggerName).debug(ft.getMessage(), t);
        }
    }

    // ----------------------------------------------------------------------
    // info
    // ----------------------------------------------------------------------

    public static void info(String message) {
        LoggerFactory.getLogger(getLogger()).info(message);
    }

    public static void info(String message, Throwable t) {
        LoggerFactory.getLogger(getLogger()).info(message, t);
    }

    public static void info(String message, Object... values) {
        LoggerFactory.getLogger(getLogger()).info(message, values);
    }

    public static void info(String message, Throwable t, Object... values) {
        if (isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(message, values);
            LoggerFactory.getLogger(getLogger()).info(ft.getMessage(), t);
        }
    }

    public static void infoTo(String loggerName, String message) {
        LoggerFactory.getLogger(loggerName).info(message);
    }

    public static void infoTo(String loggerName, String message, Object... values) {
        LoggerFactory.getLogger(loggerName).info(message, values);
    }

    public static void infoTo(String loggerName, String message, Throwable t) {
        LoggerFactory.getLogger(loggerName).info(message, t);
    }

    public static void infoTo(String loggerName, String message, Throwable t, Object... values) {
        if (isInfoEnabled(loggerName)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(message, values);
            LoggerFactory.getLogger(loggerName).info(ft.getMessage(), t);
        }
    }

    // ----------------------------------------------------------------------
    // warn
    // ----------------------------------------------------------------------

    public static void warn(String message) {
        LoggerFactory.getLogger(getLogger()).warn(message);
    }

    public static void warn(String message, Object... values) {
        LoggerFactory.getLogger(getLogger()).warn(message, values);
    }

    public static void warn(String message, Throwable t) {
        LoggerFactory.getLogger(getLogger()).warn(message, t);
    }

    public static void warn(String message, Throwable t, Object... values) {
        if (isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(message, values);
            LoggerFactory.getLogger(getLogger()).warn(ft.getMessage(), t);
        }
    }

    public static void warnTo(String loggerName, String message) {
        LoggerFactory.getLogger(loggerName).warn(message);
    }

    public static void warnTo(String loggerName, String message, Object... values) {
        LoggerFactory.getLogger(loggerName).warn(message, values);
    }

    public static void warnTo(String loggerName, String message, Throwable t) {
        LoggerFactory.getLogger(loggerName).warn(message, t);
    }

    public static void warnTo(String loggerName, String message, Throwable t, Object... values) {
        if (isWarnEnabled(loggerName)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(message, values);
            LoggerFactory.getLogger(loggerName).warn(ft.getMessage(), t);
        }
    }

    // ----------------------------------------------------------------------
    // error
    // ----------------------------------------------------------------------

    public static void error(String message) {
        LoggerFactory.getLogger(getLogger()).error(message);
    }

    public static void error(String message, Object... values) {
        LoggerFactory.getLogger(getLogger()).error(message, values);
    }

    public static void error(String message, Throwable t) {
        LoggerFactory.getLogger(getLogger()).error(message, t);
    }

    public static void error(String message, Throwable t, Object... values) {
        if (isErrorEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(message, values);
            LoggerFactory.getLogger(getLogger()).error(ft.getMessage(), t);
        }
    }

    public static void errorTo(String loggerName, String message) {
        LoggerFactory.getLogger(loggerName).error(message);
    }

    public static void errorTo(String loggerName, String message, Object... values) {
        LoggerFactory.getLogger(loggerName).error(message, values);
    }

    public static void errorTo(String loggerName, String message, Throwable t) {
        LoggerFactory.getLogger(loggerName).error(message, t);
    }

    public static void errorTo(String loggerName, String message, Throwable t, Object... values) {
        if (isErrorEnabled(loggerName)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(message, values);
            LoggerFactory.getLogger(loggerName).error(ft.getMessage(), t);
        }
    }

    // ----------------------------------------------------------------------
    // context
    // ----------------------------------------------------------------------

    public static String getContextValue(String key) {
        return MDC.get(key);
    }

    public static void setContextValue(String key, String value) {
        MDC.put(key, value);
    }

    public static void removeContextValue(String key) {
        MDC.remove(key);
    }

    public static void clearContext() {
        MDC.clear();
    }

    // ----------------------------------------------------------------------
    // constructors
    // ----------------------------------------------------------------------

    /**
     * Prevents instantiation.
     */
    private Log() {
        // empty
    }
}
