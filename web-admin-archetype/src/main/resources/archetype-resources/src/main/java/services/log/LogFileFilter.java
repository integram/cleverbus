#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package}.services.log;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


/**
 * Lop file filter based on regular expression pattern.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 * @since 0.4
 */
@Component
public class LogFileFilter implements IOFileFilter, InitializingBean, FileFilter {

    private String formatPattern;
    private Pattern pattern;

    @Override
    public boolean accept(File file) {
        Matcher matcher = pattern.matcher(file.getName());
        return matcher.matches();
    }

    @Override
    public boolean accept(File file, String s) {
        Matcher matcher = pattern.matcher(file.getName());
        return matcher.matches();
    }

    public String getFormatPattern() {
        return formatPattern;
    }

    public void setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.pattern = Pattern.compile(getFormatPattern());
    }
}
