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

package org.cleverbus.core.common.route;

import java.beans.Introspector;
import java.util.Set;

import org.cleverbus.api.route.CamelConfiguration;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;


/**
 * {@link BeanNameGenerator} implementation for bean classes annotated with the
 * {@link CamelConfiguration @CamelConfiguration} annotation.
 *
 * <p/>
 * Derives a default bean name from the given bean definition.
 * The default implementation simply builds a decapitalized version of the short class name
 * plus add constant for input/output module and finally adds suffix "{@value #BEAN_SUFFIX}":
 * e.g. <pre class="code">"com.cleverlance.tutan.modules.in.account.CreateCustomerAccountRoute" -> "createCustomerAccountRouteInBean".</pre>
 *
 * <p/>
 * If final bean name (defined or generated) is not unique then exception is thrown.
 * See {@code context:component-scan} how to use it.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 1.1
 */
public class RouteBeanNameGenerator extends AnnotationBeanNameGenerator {

    private static final String CAMEL_CONF_CLASSNAME = "org.cleverbus.api.route.CamelConfiguration";

    public static final String BEAN_SUFFIX = "Bean";

    public static final String MODULES_IN = "In";

    public static final String MODULES_PACKAGE_IN = "modules.in";

    public static final String MODULES_OUT = "Out";

    public static final String MODULES_PACKAGE_OUT = "modules.out";

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String beanName = null;

        if (definition instanceof AnnotatedBeanDefinition) {
            // bean name is from annotation
            beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);

            if (StringUtils.isEmpty(beanName) && isRouteAnnotation((AnnotatedBeanDefinition) definition)) {
                // generate bean name for routes
                beanName = buildRouteBeanName(definition);
            }
        }

        if (StringUtils.isEmpty(beanName)) {
            // generate default name
            beanName = buildDefaultBeanName(definition);
        }

        // check uniqueness
        if (registry.containsBeanDefinition(beanName)) {
            throw new IllegalStateException("Bean name '" + beanName + "' already exists, please change "
                    + "class name or explicitly define bean name");
        }

        return beanName;
    }

    protected boolean isRouteAnnotation(AnnotatedBeanDefinition annotatedDef) {
        AnnotationMetadata amd = annotatedDef.getMetadata();
        Set<String> types = amd.getAnnotationTypes();
        for (String type : types) {
            if (type.equals(CAMEL_CONF_CLASSNAME)) {
                return true;
            }
        }

        return false;
    }

    protected String buildRouteBeanName(BeanDefinition definition) {
        String shortClassName = ClassUtils.getShortName(definition.getBeanClassName());
        String beanName = Introspector.decapitalize(shortClassName);

        if (StringUtils.contains(definition.getBeanClassName(), MODULES_PACKAGE_IN)) {
            beanName += MODULES_IN;
        } else if (StringUtils.contains(definition.getBeanClassName(), MODULES_PACKAGE_OUT)) {
            beanName += MODULES_OUT;
        }

        beanName += BEAN_SUFFIX;

        return beanName;
    }
}
