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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ReflectionUtils;


/**
 * Helper class for calling target service via reflection.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public final class ReflectionCallUtils {

    private ReflectionCallUtils() {
    }

    /**
     * Invokes target method.
     *
     * @param params the parameters of the call
     * @param beanFactory the Spring bean factory
     * @return response
     */
    public static Object invokeMethod(ContextCallParams params, BeanFactory beanFactory) {
        // find target service
        Object targetService = beanFactory.getBean(params.getTargetType());

        // determine method's argument types
        List<Class> argTypes = new ArrayList<Class>();
        for (Object arg : params.getMethodArgs()) {
            argTypes.add(arg.getClass());
        }

        // exist method?
        Method method = ReflectionUtils.findMethod(params.getTargetType(), params.getMethodName(),
                argTypes.toArray(new Class[]{}));
        if (method == null) {
            throw new IllegalStateException("there is no method '" + params.getMethodName()
                    + "' on target type '" + params.getTargetType().getSimpleName() + "'");
        }

        // invoke method
        return ReflectionUtils.invokeMethod(method, targetService, params.getMethodArgs().toArray());
    }
}
