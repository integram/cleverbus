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

package org.cleverbus.core.common.version.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.cleverbus.core.common.version.VersionInfo;


/**
 * Comparator for {@link VersionInfo} objects.
 *
 * @author <a href="mailto:michal.palicka@cleverlance.com">Michal Palicka</a>
 * @version $Id: VersionInfoComparator.java 5073 2011-01-23 12:54:01Z mbenda@CLANCE.LOCAL $
 */
public class VersionInfoComparator implements Comparator<VersionInfo>, Serializable {

    //----------------------------------------------------------------------
    // instance fields
    //----------------------------------------------------------------------

    /**
     * The name of the {@link VersionInfo} property used for ordering.
     */
    private String propertyName;

    /**
     * Determines the order direction (ascending/descending).
     */
    private boolean ascending = true;

    /**
     * The comparator used for comparisons.
     */
    private Comparator<VersionInfo> comparator;

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    /**
     * Constructs a new {@code VersionInfoComparator} instance
     * that uses the natural ordering of {@link VersionInfo} objects.
     *
     * @param ascending specifies the order direction (ascending/descending).
     */
    public VersionInfoComparator(boolean ascending) {
        this(null, ascending);
    }

    /**
     * Constructs a new {@code VersionInfoComparator} instance, that compares objects
     * by the specified property.
     *
     * @param propertyName the name of the property to order by. If the parameter is set to {@code null},
     *        then the natural ordering is used.
     * @param ascending specifies the order direction (ascending/descending).
     */
    public VersionInfoComparator(String propertyName, boolean ascending) {
        this.propertyName = propertyName;
        this.ascending = ascending;

        // initialize the comparator
        if ("title".equalsIgnoreCase(propertyName)) {
            this.comparator = new VersionInfoTitleComparator(ascending);
        } else if ("vendorId".equalsIgnoreCase(propertyName)) {
            this.comparator = new VersionInfoVendorIdComparator(ascending);
        } else if ("version".equalsIgnoreCase(propertyName)) {
            this.comparator = new VersionInfoVersionComparator(ascending);
        } else if ("revision".equalsIgnoreCase(propertyName)) {
            this.comparator = new VersionInfoRevisionComparator(ascending);
        } else if ("timestamp".equalsIgnoreCase(propertyName)) {
            this.comparator = new VersionInfoTimestampComparator(ascending);
        } else {
            this.comparator = null;
        }
    }

    //----------------------------------------------------------------------
    // public methods
    //----------------------------------------------------------------------

    @Override
    public int compare(VersionInfo o1, VersionInfo o2) {
        if (comparator == null) {
            // natural order
            int result = o1.compareTo(o2);
            return (ascending) ? result : -result;
        } else {
            return this.comparator.compare(o1, o2);
        }
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isAscending() {
        return ascending;
    }

    //----------------------------------------------------------------------
    // equality
    //----------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        VersionInfoComparator that = (VersionInfoComparator) o;

        return (comparator != null) ? comparator.equals(that.comparator) : that.comparator == null;
    }

    @Override
    public int hashCode() {
        return (comparator != null ? comparator.hashCode() : 0);
    }
}
