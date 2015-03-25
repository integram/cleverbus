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

package org.cleverbus.api.common;

/**
 * Service for sending emails.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface EmailService {

    public static final String BEAN = "emailService";

    /**
     * Sends email to administrators.
     *
     * @param subject the subject
     * @param body the body
     */
    void sendEmailToAdmins(String subject, String body);

    /**
     * Sends formatted email to recipients.
     * <p>
     * For example, message &quot;Hi {}. My name is {}.&quot;, &quot;Alice&quot;, &quot;Bob&quot;
     * will return the string "Hi Alice. My name is Bob.".
     *
     * @param recipients the comma separated recipients; if empty then email won't be send
     * @param subject the subject
     * @param body the body with possible placeholders {@code {}}
     * @param values the values for placeholders; count of values have to correspond with count of placeholders
     */
    void sendFormattedEmail(String recipients, String subject, String body, Object... values);

}
