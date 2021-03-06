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

package org.cleverbus.core.common.version;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Nullable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;

/**
 * Holds information about versions that correspond to application modules (JAR, WAR, etc.).
 *
 * @author <a href="mailto:michal.palicka@cleverlance.com">Michal Palicka</a>
 * @version $Id: VersionInfo.java 10803 2012-10-25 11:56:15Z jloose@CLANCE.LOCAL $
 */
public class VersionInfo implements Comparable<VersionInfo>, Serializable {

    //----------------------------------------------------------------------
    // instance fields
    //----------------------------------------------------------------------

    /**
     * The implementation title.
     * In modules that were built by Maven, this field usually contains the <em>artifactId</em>.
     */
    private String title;

    /**
     * The implementation vendor identifier.
     * In modules that were built by Maven, this field usually contains the <em>groupId</em>.
     */
    private String vendorId;

    /**
     * The implementation version.
     * In modules that were built by Maven, this field usually contains the <em>version</em>.
     */
    private String version;

    /**
     * The implementation revision.
     * In modules that were built by Maven, this field usually contains the <em>revision</em>.
     */
    private String revision;

    /**
     * The implementation timestamp.
     * In modules that were built by Maven, this field usually contains the <em>timestamp</em>.
     */
    private String timestamp;

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    /**
     * Constructs a new {@code VersionInfo} instance.
     *
     * @param title the implementation title
     * @param vendorId the implementation vendor identifier
     * @param version the implementation version
     * @param revision the implementation version
     * @param timestamp the implementation timestamp
     */
    public VersionInfo(@Nullable String title, @Nullable String vendorId, @Nullable String version,
            @Nullable String revision, @Nullable String timestamp) {
        setTitle(title);
        setVendorId(vendorId);
        setVersion(version);
        setRevision(revision);
        setTimestamp(timestamp);
    }

    //----------------------------------------------------------------------
    // public methods
    //----------------------------------------------------------------------

    public String getTitle() {
        return title;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getVersion() {
        return version;
    }

    public String getRevision() {
        return revision;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getFullVersion() {
        return getVersion() + (isBlank(getRevision()) ? EMPTY : (".r" + getRevision()));
    }

    public String getDate() {
        try {
            return DateFormat.getDateTimeInstance().format(new Date(Long.valueOf(getTimestamp())));
        } catch (Exception e) {
            return EMPTY;
        }
    }

    /**
     * Determines whether this {@code VersionInfo} instance matches the specified filter.
     * <p>
     * The filter is also an instance of {@code VersionInfo}, but its fields are expected
     * to contain regular expressions instead of plain values.
     * <p>
     * If the <em>filter</em> is {@code null}, then this method returns {@code true}.
     * <p>
     * If a field in the filter is set to {@code null}, then all values are allowed.
     *
     * @param filter the filter to use for pattern matching
     * @return true if this version info matches the pattern
     * @throws PatternSyntaxException if the filter contains an invalid regular expression
     */
    public boolean matches(@Nullable VersionInfo filter) throws PatternSyntaxException {
        return (filter == null)
                || (matches(this.title, filter.title) && matches(this.vendorId, filter.vendorId)
                        && matches(this.version, filter.version) && matches(this.revision, filter.revision) && matches(
                        this.timestamp, filter.timestamp));
    }

    //----------------------------------------------------------------------
    // equality & comparison
    //----------------------------------------------------------------------

    @Override
    public int compareTo(VersionInfo obj) {
        if (obj == null) {
            return 1;
        }
        return new CompareToBuilder().append(this.vendorId, obj.vendorId).append(this.title, obj.title)
                .append(this.version, obj.version).append(revision, obj.revision).append(timestamp, obj.timestamp)
                .toComparison();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VersionInfo)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        VersionInfo info = (VersionInfo) obj;
        return new EqualsBuilder().append(title, info.title).append(vendorId, info.vendorId)
                .append(version, info.version).append(revision, info.revision).append(timestamp, info.timestamp)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(title).append(vendorId).append(version).append(revision)
                .append(timestamp).toHashCode();
    }

    //----------------------------------------------------------------------
    // text output
    //----------------------------------------------------------------------

    @Override
    public String toString() {
        return new ToStringCreator(this).append("title", title).append("vendorId", vendorId).append("version", version)
                .toString();
    }

    //----------------------------------------------------------------------
    // private methods
    //----------------------------------------------------------------------

    private void setTitle(String title) {
        this.title = title;
    }

    private void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    private void setVersion(String version) {
        this.version = version;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private boolean matches(String value, String regex) throws PatternSyntaxException {
        return (regex == null) || ((value != null) && value.matches(regex));
    }
}
