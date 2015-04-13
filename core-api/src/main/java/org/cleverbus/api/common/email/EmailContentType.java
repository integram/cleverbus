package org.cleverbus.api.common.email;

import org.springframework.util.Assert;

/**
 * All enums of content type of body {@link Email}.
 *
 * @author Radek Čermák [<a href="mailto:radek.cermak@cleverlance.com">radek.cermak@cleverlance.com</a>]
 * @since 13.4.15
 */
public enum EmailContentType {

    /**
     * Body of email will be send as plain text.
     */
    PLAIN_TEXT("text/plain"),

    /**
     * Body of email will be send as html.
     */
    HTML("text/html");

    /**
     * Content type.
     */
    private final String contentType;

    /**
     * New instance.
     * @param contentType content type
     */
    EmailContentType(final String contentType) {
        Assert.hasText(contentType);

        this.contentType = contentType;
    }

    //------------------------------------------------- SET / GET ------------------------------------------------------

    /**
     * Return content type of body email.
     * @return content type
     */
    public String getContentType() {
        return contentType;
    }
}
