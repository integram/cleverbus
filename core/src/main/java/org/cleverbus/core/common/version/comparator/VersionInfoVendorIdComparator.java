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

import org.apache.commons.lang.builder.CompareToBuilder;


/**
 * Compares {@link VersionInfo} objects by vendor ID.
 *
 * @author <a href="mailto:michal.palicka@cleverlance.com">Michal Palicka</a>
 * @version $Id: VersionInfoVendorIdComparator.java 8499 2012-04-16 17:10:32Z jloose@CLANCE.LOCAL $
 */
public class VersionInfoVendorIdComparator implements Comparator<VersionInfo>, Serializable {

    //----------------------------------------------------------------------
    // instance fields
    //----------------------------------------------------------------------

    /**
     * Determines the order direction (ascending/descending).
     */
    private boolean ascending = true;

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    /**
     * Constructs a new {@code VersionInfoVendorIdComparator} instance with the specified
     * order direction.
     *
     * @param ascending the order direction.
     */
    public VersionInfoVendorIdComparator(boolean ascending) {
        this.ascending = ascending;
    }

    //----------------------------------------------------------------------
    // public methods
    //----------------------------------------------------------------------

    @Override
    public int compare(VersionInfo o1, VersionInfo o2) {
        int value;

        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            value = -1;
        } else if (o2 == null) {
            value = +1;
        } else {
            value = new CompareToBuilder().append(o1.getVendorId(), o2.getVendorId()).toComparison();
        }
        return (ascending) ? value : -value;
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

        VersionInfoVendorIdComparator that = (VersionInfoVendorIdComparator) o;

        return (ascending == that.ascending);
    }

    @Override
    public int hashCode() {
        return (ascending ? 1 : 0);
    }
}
