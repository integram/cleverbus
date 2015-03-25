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


/**
 * Custom {@link TypeFilter} for using with {@code context:component-scan: exclude-filter} Spring element.
 * This filter checks system and environment property "{@value #PATTERN_PROP_NAME}" and if defined then property value
 * is used for {@link Pattern} compilation.
 * System property has higher priority.
 * <p/>
 * Example:
 * {@code &lt;context:exclude-filter type="custom" expression="org.cleverbus.core.common.spring.SystemExcludeRegexPatternTypeFilter"/>}
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see SystemIncludeRegexPatternTypeFilter
 */
public class SystemExcludeRegexPatternTypeFilter implements TypeFilter {

    public static final String PATTERN_PROP_NAME = "springExcludePattern";

    @Nullable
    private Pattern pattern;

    public SystemExcludeRegexPatternTypeFilter() {
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
        return pattern != null && pattern.matcher(metadataReader.getClassMetadata().getClassName()).matches();
    }
}
