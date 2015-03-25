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

package org.cleverbus.core.common.asynch.notification;

import org.cleverbus.api.common.EmailService;
import org.cleverbus.common.Strings;
import org.cleverbus.common.log.Log;

import org.springframework.beans.factory.annotation.Value;


/**
 * Dummy implementation of {@link EmailService} interface, mainly for testing purposes.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class DummyEmailServiceImpl implements EmailService {

    /**
     * Administrator email address.
     */
    @Value("${mail.admin}")
    private String recipients;

    @Override
    public void sendEmailToAdmins(String subject, String body) {
        Log.debug("Sending email:"
            + "\nrecipients: " + recipients
            + "\nsubject: " + subject
            + "\nbody: " + body);
    }

    @Override
    public void sendFormattedEmail(String recipients, String subject, String body, Object... values) {
        Log.debug("Sending email:"
                + "\nrecipients: " + recipients
                + "\nsubject: " + subject
                + "\nbody: " + Strings.fm(body, values));
    }
}
