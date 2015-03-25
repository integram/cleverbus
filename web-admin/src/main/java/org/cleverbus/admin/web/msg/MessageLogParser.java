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

package org.cleverbus.admin.web.msg;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nullable;

import org.cleverbus.common.log.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Parses log file and returns log lines which corresponds with specified correlation ID.
 * <p/>
 * Prerequisites/known limitations:
 * <ul>
 *     <li>application logs in DEBUG level
 *     <li>known log format
 *     <li>there is only one log file (no increment parts)
 * </ul>
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 */
@Component
public class MessageLogParser {

    // log file format: logFile_%d{yyyy-MM-dd}_%i.log
    private SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final String BASE_FILE_EXTENSION = "log";
    public static final String GZIP_FILE_EXTENSION = "gz";

    @Autowired
    private IOFileFilter logNameFilter;

    /**
     * Absolute path to the folder with log files.
     */
    @Value("${log.folder.path}")
    private String logFolderPath;

    /**
     * Gets lines from the log file which corresponds with specified correlation ID.
     *
     * @param correlationId the correlation ID
     * @param logDate which date to search log files for
     * @return log lines
     * @throws IOException when error occurred during file reading
     */
    List<String> getLogLines(String correlationId, Date logDate) throws IOException {
        File logFolder = new File(logFolderPath);
        if (!logFolder.exists() || !logFolder.canRead()) {
            throw new FileNotFoundException("there is no readable log folder - " + logFolderPath);
        }

        final String logDateFormatted = fileFormat.format(logDate);

        // filter log files for current date
        IOFileFilter nameFilter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return logNameFilter.accept(file) && (StringUtils.contains(file.getName(), logDateFormatted)
                        || file.getName().endsWith(BASE_FILE_EXTENSION));
            }

            @Override
            public boolean accept(File dir, String name) {
                return StringUtils.contains(name, logDateFormatted)
                        || name.endsWith(BASE_FILE_EXTENSION);

            }
        };

        List<File> logFiles = new ArrayList<File>(FileUtils.listFiles(logFolder, nameFilter, null));
        Collections.sort(logFiles, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);

        // go through all log files
        List<String> logLines = new ArrayList<String>();
        for (File logFile : logFiles) {
            logLines.addAll(getLogLines(logFile, correlationId));
        }

        return logLines;
    }

    /**
     * Gets lines which corresponds with specified correlation ID from the specified log file.
     *
     * @param logFile the log file
     * @param correlationId the correlation ID
     * @return log lines
     * @throws IOException when error occurred during file reading
     */
    private List<String> getLogLines(File logFile, String correlationId) throws IOException {
        List<String> logLines = new ArrayList<String>();

        Log.debug("Go through the following log file: " + logFile);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        String[] possibleYears = new String[] {String.valueOf(year-1), String.valueOf(year)};

        InputStream stream = null;
        try {
            if (logFile.getName().endsWith(GZIP_FILE_EXTENSION)) {
                stream = new GZIPInputStream(new BufferedInputStream(new FileInputStream(logFile)));
            } else {
                stream = new BufferedInputStream(new FileInputStream(logFile));
            }

            LineIterator it = IOUtils.lineIterator(stream, Charset.defaultCharset());

            String requestId = null;
            boolean lastCorrectLine = false; // if previous log line belongs to requestId
            while (it.hasNext()) {
                String line = it.nextLine();

                if (requestId == null) {
                    if (StringUtils.contains(line, correlationId)) {
                        logLines.add(formatLogLine(line));

                        // finds requestID
                        requestId = getRequestId(line);

                        if (requestId != null) {
                            Log.debug("correlationId (" + correlationId + ") => requestId (" + requestId + ")");
                        }
                    }
                } else {
                    // adds lines with requestID and lines that belongs to previous log record (e.g. XML request)
                    //  it's better to check also correlationID because it's not one request ID
                    //  for all repeated scheduled jobs for processing of partly failed messages

                    // 2013-05-23 20:22:36,754 [MACHINE_IS_UNDEFINED, ajp-bio-8009-exec-19, /esb/ws/account/v1, ...
                    // <checkCustomerCreditRequest xmlns="http://cleverbus.org/ws/AccountService-v1">
                    //    <firstName>csd</firstName>
                    //    <lastName>acs</lastName>
                    //    <birthNumber>111111/1111</birthNumber>
                    // </checkCustomerCreditRequest>

                    if (StringUtils.contains(line, requestId)
                            || (StringUtils.contains(line, correlationId))
                            || (lastCorrectLine && !StringUtils.startsWithAny(line, possibleYears))) {
                        logLines.add(formatLogLine(line));
                        lastCorrectLine = true;
                    } else {
                        lastCorrectLine = false;
                    }
                }
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return logLines;
    }


    @Nullable
    private String getRequestId(String line) {
        // 2013-05-23 20:22:36,754 [MACHINE_IS_UNDEFINED, ajp-bio-8009-exec-19, /esb/ws/account/v1, 10.10.0.95:72cab819:13ecdbd371c:-7eff, ] DEBUG

        String logHeader = StringUtils.substringBetween(line, "[", "]");
        if (logHeader == null) {
            // no match - the line doesn't contain []
            return null;
        }
        String[] headerParts = StringUtils.split(logHeader, ",");
        String requestId = StringUtils.trim(headerParts[3]);

        // note: if request starts from scheduled job, then there is request ID information
        // 2013-05-27 16:37:25,633 [MACHINE_IS_UNDEFINED, DefaultQuartzScheduler-camelContext_Worker-8, , , ]
        //  WARN  c.c.c.i.c.a.d.RepairMessageServiceDbImpl$2 - The message (msg_id = 372, correlationId = ...

        return StringUtils.trimToNull(requestId);
    }

    private String formatLogLine(String line) {
        String resLine = StringEscapeUtils.escapeHtml(line);

        // highlight ERROR log line
        if (StringUtils.contains(line, " ERROR ")
                || StringUtils.contains(line, "StackTrace:")
                || StringUtils.contains(line, "AbstractSoapExceptionFilter - get new exception")) {
            resLine = "<span style=\"font-weight: bold; color: red;\">" + resLine + "</span>";

        // highlight log lines which contains sent/receive SOAP messages
        } else if (StringUtils.contains(line, "MessageTracing.sent")
                || StringUtils.contains(line, "MessageTracing.received")) {
            resLine = "<span class=\".soapMessage\">" + resLine + "</span>";
        }

        return resLine;
    }
}
