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

package org.cleverbus.core.common.asynch.msg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.Message;
import org.cleverbus.common.log.Log;

import org.apache.commons.lang.StringUtils;

/**
 * Helper class for message manipulation.
 */
//TODO (juza) applicant for moving to API
public final class MessageHelper {

    private MessageHelper() {
    }

    /**
     * Checks the properties for those that end with {@link AsynchConstants#BUSINESS_ERROR_PROP_SUFFIX}
     * and moves the found exceptions to the Message's business error list, subsequently returning it.
     * <p/>
     * After this operation succeeds, there will be no more properties with the specified suffix,
     * and the Message will contain all the exception messages that were previously in these properties.
     *
     * @param msg the message to collect and set business errors for
     * @param properties the exchange properties, possibly with some new business errors
     */
    public static void updateBusinessErrors(Message msg, Map<String, Object> properties) {
        List<String> errorList = new ArrayList<String>();
        errorList.addAll(msg.getBusinessErrorList());

        List<String> errorProps = new LinkedList<String>();
        for (String property : properties.keySet()) {
            if (property.endsWith(AsynchConstants.BUSINESS_ERROR_PROP_SUFFIX)) {
                errorProps.add(property);
            }
        }

        for (String errorProp : errorProps) {
            Object businessError = properties.remove(errorProp);
            if (businessError instanceof Exception) {
                Exception ex = (Exception) businessError;
                errorList.add(ex.getMessage());
            } else {
                Log.error("Property with suffix " + AsynchConstants.BUSINESS_ERROR_PROP_SUFFIX
                        + " isn't of type Exception");
            }
        }

        String businessErrors = StringUtils.join(errorList, Message.ERR_DESC_SEPARATOR);
        msg.setBusinessError(businessErrors);
    }
}
