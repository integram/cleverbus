package org.cleverbus.api.common.email;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.cleverbus.api.common.EmailService;
import org.springframework.util.Assert;

/**
 * Class represented attachment of email.
 * For adding attachment into email we can use {@link Email#addAttachment(EmailAttachment)}.
 *
 * @author Radek Čermák [<a href="mailto:radek.cermak@cleverlance.com">radek.cermak@cleverlance.com</a>]
 * @see EmailService#sendEmail(Email)
 * @see Email
 * @see EmailContentType
 * @since 22.2.15
 */
public class EmailAttachment {

    /**
     * File name of attachment.
     */
    private final String fileName;

    /**
     * Content (data) of attachment.
     */
    private final byte[] content;

    /**
     * New instance.
     *
     * @param fileName file name of attachment
     * @param content  content of attachment
     */
    public EmailAttachment(final String fileName, final byte[] content) {
        Assert.hasText(fileName);
        Assert.notNull(content);

        this.fileName = fileName;
        this.content = content;
    }

    //--------------------------------------------- SET / GET ----------------------------------------------------------

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
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
                .append("fileName", fileName)
                .toString();
    }
}
