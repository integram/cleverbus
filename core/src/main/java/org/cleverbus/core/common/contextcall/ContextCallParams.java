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

package org.cleverbus.core.common.contextcall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.util.Assert;


/**
 * Encapsulates context call parameters.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see ContextCall
 */
public class ContextCallParams {

    private Class<?> targetType;
    private String methodName;
    private List<Object> methodArgs;

    // technical parameter
    private DateTime creationTimestamp;

    public ContextCallParams(Class<?> targetType, String methodName, Object... methodArgs) {
        Assert.notNull(targetType, "the targetType must not be null");
        Assert.hasText(methodName, "the methodName must be defined");

        this.targetType = targetType;
        this.methodName = methodName;
        this.methodArgs = new ArrayList<Object>(Arrays.asList(methodArgs));
        this.creationTimestamp = DateTime.now();
    }

    /**
     * Gets class of target service.
     *
     * @return the class of target service
     */
    public Class<?> getTargetType() {
        return targetType;
    }

    /**
     * Gets name of calling method on target service.
     *
     * @return name of calling method on target service
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Gets method arguments (if any).
     *
     * @return method arguments, can be empty
     */
    public List<Object> getMethodArgs() {
        return Collections.unmodifiableList(methodArgs);
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
                .append("targetType", targetType)
                .append("methodName", methodName)
                .toString();
    }
}
