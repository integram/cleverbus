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

package org.cleverbus.core.common.directcall;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.util.Assert;


/**
 * Encapsulates parameters for direct call.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see DirectCallParams
 */
public class DirectCallParams {

    private Object header;
    private Object body;
    private String uri;
    private String senderRef;
    private String soapAction;
    private DateTime creationTimestamp;

    public DirectCallParams(Object body, String uri, String senderRef, @Nullable String soapAction) {
        this(body, uri, senderRef, soapAction, null);
    }

    public DirectCallParams(Object body, String uri, String senderRef, @Nullable String soapAction, @Nullable String header) {
        Assert.notNull(body, "the body must not be null");
        Assert.hasText(uri, "the uri must not be empty");
        Assert.hasText(senderRef, "the senderRef must not be empty");

        this.body = body;
        this.uri = uri;
        this.senderRef = senderRef;
        this.soapAction = soapAction;
        this.header = header;
        this.creationTimestamp = DateTime.now();
    }

    /**
     * Gets call body.
     *
     * @return body
     */
    public Object getBody() {
        return body;
    }

    /**
     * Gets call header.
     *
     * @return header
     */
    @Nullable
    public Object getHeader() {
        return header;
    }

    /**
     * Sets call header.
     *
     * @param header header
     */
    public void setHeader(@Nullable Object header) {
        this.header = header;
    }

    /**
     * Gets external system URI.
     *
     * @return external system URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Gets reference (= Spring bean name) to sender.
     *
     * @return sender reference
     */
    public String getSenderRef() {
        return senderRef;
    }

    /**
     * Gets SOAP action.
     *
     * @return SOAP action
     */
    @Nullable
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * Gets timestamp when these params were created.
     *
     * @return timestamp
     */
    public DateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("uri", uri)
                .append("senderRef", senderRef)
                .append("soapAction", soapAction)
                .append("header", header != null ? StringUtils.abbreviate(header.toString(), 100) : "")
                .append("body", StringUtils.abbreviate(body.toString(), 100))
                .toString();
    }
}
