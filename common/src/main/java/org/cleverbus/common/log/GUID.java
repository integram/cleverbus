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

package org.cleverbus.common.log;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.UID;

/**
 * Represents a globally unique ID by combining the UID (which is unique
 * with respect to the host) and the host's Internet address.
 *
 * @author <a href="mailto:michal.palicka@cleverlance.com">Michal Palicka</a>
 * @version $Id: GUID.java 9536 2012-08-09 12:18:09Z pjuza@CLANCE.LOCAL $
 */
public class GUID implements Serializable {

    //----------------------------------------------------------------------
    // class (static) fields
    //----------------------------------------------------------------------

    /**
     * A dummy address that is used when the InternetAddress
     * of the local host cannot be found. In this case, the
     * global uniqueness of the identifier may be compromised.
     */
    private static final String UNKNOWN_HOST = "0.0.0.0";

    /**
     * This field is used as a cache for the internet address
     * of the local host.
     */
    private static String LOCAL_HOST;

    //----------------------------------------------------------------------
    // class (static) methods
    //----------------------------------------------------------------------

    static {
        try {
            LOCAL_HOST = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOCAL_HOST = UNKNOWN_HOST;
        }
    }

    //----------------------------------------------------------------------
    // instance fields
    //----------------------------------------------------------------------

    /**
     * The internet address of the host from which this instance originates.
     */
    private String host;

    /**
     * An identifier (unique to host).
     */
    private UID uid;

    //----------------------------------------------------------------------
    // constructors
    //----------------------------------------------------------------------

    /**
     * Creates a globally unique ID.
     */
    public GUID() {
        host = LOCAL_HOST;
        uid = new UID();
    }

    //----------------------------------------------------------------------
    // comparing
    //----------------------------------------------------------------------

    /**
     * Indicates whether some other object is equal to this GUID.
     *
     * @param obj the object to compare with.
     * @return true if the two objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GUID)) {
            return false;
        }
        final GUID guid = (GUID) obj;
        return ((this.host.equals(guid.host)) && (this.uid.equals((guid.uid))));
    }

    /**
     * Returns a hashcode for this GUID.
     *
     * @return a hashcode for this GUID.
     */
    @Override
    public int hashCode() {
        int result;
        result = host.hashCode();
        result = (29 * result) + uid.hashCode();
        return result;
    }

    //----------------------------------------------------------------------
    // string representation
    //----------------------------------------------------------------------

    /**
     * Returns the string representation of this GUID.
     *
     * @return the string representation of this GUID.
     */
    @Override
    public String toString() {
        return host + ":" + uid.toString();
    }
}
