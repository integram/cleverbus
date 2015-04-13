/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cleverbus.core.common.asynch.notification;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.cleverbus.api.common.EmailService;
import org.cleverbus.api.common.email.Email;
import org.cleverbus.api.common.email.EmailAttachment;
import org.cleverbus.common.Strings;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;


/**
 * Mail (SMTP) implementation of {@link EmailService} interface that uses Apache Camel SMTP component.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class EmailServiceCamelSmtpImpl implements EmailService {

    @Autowired
    private ProducerTemplate producerTemplate;

    /**
     * Administrator email address.
     */
    @Value("${mail.admin}")
    private String recipients;

    /**
     * Email address FROM for sending emails.
     */
    @Value("${mail.from}")
    private String from;

    /**
     * SMTP server.
     */
    @Value("${mail.smtp.server}")
    private String smtp;

    @Override
    public void sendEmailToAdmins(String subject, String body) {
        sendFormattedEmail(recipients, subject, body);
    }

    @Override
    public void sendFormattedEmail(String recipients, String subject, String body, Object... values) {
        Assert.hasText(subject, "the subject must not be empty");
        Assert.hasText(body, "the body must not be empty");

        if (StringUtils.isNotEmpty(recipients)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("To", recipients);
            map.put("From", from);
            map.put("Subject", subject);

            producerTemplate.sendBodyAndHeaders("smtp://" + smtp, Strings.fm(body, values), map);
        }
    }

    @Override
    public void sendEmail(final Email email) {
        Assert.notNull(email, "email can not be null");
        Assert.notEmpty(email.getRecipients(), "email must have at least one recipients");
        Assert.hasText(email.getSubject(), "subject in email can not be empty");
        Assert.hasText(email.getBody(), "body in email can not be empty");

        producerTemplate.send("smtp://" + smtp, new Processor() {
            @Override
            public void process(final Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setHeader("To", StringUtils.join(email.getRecipients(), ","));
                in.setHeader("From", StringUtils.isBlank(email.getFrom()) ? from : email.getFrom());
                in.setHeader("Subject", email.getSubject());
                in.setHeader("contentType", email.getContentType().getContentType());
                in.setBody(email.getBody());
                if (email.getAllAtachments() != null && !email.getAllAtachments().isEmpty()) {
                    for (EmailAttachment attachment : email.getAllAtachments()) {
                        in.addAttachment(attachment.getFileName(), new DataHandler(new ByteArrayDataSource(
                                attachment.getContent(), "*/*")));
                    }
                }
            }
        });
    }
}
