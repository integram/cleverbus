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

/**
 * Interface for classes that can be used as sources of application version data.
 *
 * @author <a href="mailto:michal.palicka@cleverlance.com">Michal Palicka</a>
 * @version $Id: VersionInfoSource.java 5073 2011-01-23 12:54:01Z mbenda@CLANCE.LOCAL $
 */
public interface VersionInfoSource {

    /**
     * Retrieves version information from available application modules.
     * The method allows to specify a filter that can be used to remove invalid entries from the result.
     * <p>
     * The filter is also an instance of {@code VersionInfo}, but its fields are expected to contain regular
     * expressions instead of plain values.
     * Each available version entry is matched against patterns in the filter (field-by-field).
     * If any of the fields does not match, the version entry is excluded from the result.
     * <p>
     * If the <em>filter</em> is {@code null}, all entries are returned.
     * <p>
     * If a field in the filter is set to {@code null}, then all values are allowed.
     *
     * @param filter the filter used to remove invalid or unwanted entries.
     * @return an array of version entries (in ascending order).
     */
    VersionInfo[] getVersionInformation(VersionInfo filter);
}
