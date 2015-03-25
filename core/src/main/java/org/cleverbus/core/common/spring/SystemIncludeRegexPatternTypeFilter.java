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

package org.cleverbus.core.common.spring;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;


/**
 * Custom {@link TypeFilter} for using with {@code context:component-scan: include-filter} Spring element.
 * This filter checks system and environment property "{@value #PATTERN_PROP_NAME}" and if defined then property value
 * is used for {@link Pattern} compilation and only classes which match the pattern are included.
 * If there is no property defined then all Spring {@link Component} beans are included.
 * System property has higher priority.
 * <p/>
 * Example:
    <pre>
 &lt;context:component-scan base-package="org.cleverbus.core" use-default-filters="false">
     &lt;context:include-filter type="custom" expression="org.cleverbus.core.common.spring.SystemIncludeRegexPatternTypeFilter"/>
 &lt;/context:component-scan>
    </pre>
 *
 * <p/>
 * Remember: include filters are applied after exclude filters.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see SystemExcludeRegexPatternTypeFilter
 */
public class SystemIncludeRegexPatternTypeFilter implements TypeFilter {

    public static final String PATTERN_PROP_NAME = "springIncludePattern";

    @Nullable
    private Pattern pattern;

    public SystemIncludeRegexPatternTypeFilter() {
        String regex = System.getenv(PATTERN_PROP_NAME);

        if (System.getProperty(PATTERN_PROP_NAME) != null) {
            regex = System.getProperty(PATTERN_PROP_NAME);
        }

        // compile pattern
        if (regex != null) {
            this.pattern = Pattern.compile(regex);
        }
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return (pattern == null && metadataReader.getAnnotationMetadata().hasMetaAnnotation(Component.class.getName()))
               || (pattern != null && pattern.matcher(metadataReader.getClassMetadata().getClassName()).matches());
    }
}
