package org.cleverbus.api.common.email;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.cleverbus.api.common.EmailService;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Email that can be send with method {@link EmailService#sendEmail(Email)}.
 * <p>
 * Default content type of body email is {@link EmailContentType#HTML}. If you need another content type,
 * you can change it with call method {@link #setContentType(EmailContentType)}.
 * </p>
 *
 * @author Radek Čermák [<a href="mailto:radek.cermak@cleverlance.com">radek.cermak@cleverlance.com</a>]
 * @see EmailService#sendEmail(Email)
 * @see EmailAttachment
 * @see EmailContentType
 * @since 22.2.15
 */
public class Email {

    /**
     * Address who send email.
     */
    private final String from;

    /**
     * Recipients email.
     */
    private final List<String> recipients = new ArrayList<String>();

    /**
     * Subject.
     */
    private final String subject;

    /**
     * Body.
     */
    private final String body;

    /**
     * Attachments.
     */
    private final List<EmailAttachment> emailAttachments = new ArrayList<EmailAttachment>();

    /**
     * Content type of email body.
     */
    private EmailContentType contentType = EmailContentType.HTML;

    /**
     * New instance for send email to one recipient.
     *
     * @param from      address who send email, NULL - default address will be used
     * @param recipient recipient
     * @param subject   subject
     * @param body      body
     */
    public Email(@Nullable final String from, final String recipient, final String subject, final String body) {
        Assert.hasText(recipient, "recipient can not be empty");
        Assert.hasText(subject, "subject can not be empty");
        Assert.hasText(body, "body can not be empty");

        this.from = from;
        this.subject = subject;
        this.body = body;
        this.recipients.add(recipient);
    }

    /**
     * New instance for send email to multiple recipients
     *
     * @param from       address who send email, NULL - default address will be used
     * @param recipients recipients
     * @param subject    subject
     * @param body       body
     */
    public Email(@Nullable final String from, final Collection<String> recipients, final String subject
            , final String body) {
        Assert.notEmpty(recipients, "recipient can not be empty");
        Assert.hasText(subject, "subject can not be empty");
        Assert.hasText(body, "body can not be empty");

        this.from = from;
        this.subject = subject;
        this.body = body;
        this.recipients.addAll(recipients);
    }

    //----------------------------------------------- ATTACHMENT -------------------------------------------------------

    /**
     * Add attachment to email.
     *
     * @param attachment attachment
     */
    public void addAttachment(final EmailAttachment attachment) {
        Assert.notNull(attachment, "attachment can not be null");

        emailAttachments.add(attachment);
    }

    /**
     * Add attachments to email.
     *
     * @param attachments attachments
     */
    public void addAttachments(final Collection<EmailAttachment> attachments) {
        Assert.notNull(attachments, "attachments can not be null");

        emailAttachments.addAll(attachments);
    }

    /**
     * Remove all attachments from email.
     */
    public void removeAllAttachments() {
        emailAttachments.clear();
    }

    /**
     * Get all attachments.
     * Returned list can not be changed.
     *
     * @return attachments
     */
    public List<EmailAttachment> getAllAtachments() {
        return Collections.unmodifiableList(emailAttachments);
    }

    //------------------------------------------------- SET / GET ------------------------------------------------------

    /**
     * Return sender email.
     *
     * @return sender email, NULL - default sender will be used
     */
    @Nullable
    public String getFrom() {
        return from;
    }

    /**
     * Get all recipients.
     * Returned list can not be changed.
     *
     * @return recipients
     */
    public List<String> getRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    /**
     * Return subject of email.
     *
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Return body of email.
     *
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     * Return content type of email body.
     *
     * @return content type
     */
    public EmailContentType getContentType() {
        return contentType;
    }

    /**
     * Set content type of email body.
     *
     * @param contentType content type
     */
    public void setContentType(final EmailContentType contentType) {
        Assert.notNull(contentType, "contentType can not be null");

        this.contentType = contentType;
    }

    //------------------------------------------- HASH / EQUAL / TOSTRING ----------------------------------------------

    @Override
    public boolean equals(final Object o) {
        return EqualsBuilder.reflectionEquals(o, this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("from", from)
                .append("recipients", recipients)
                .append("subject", subject)
                .append("body", body)
                .append("emailAttachments", emailAttachments)
                .toString();
    }
}
