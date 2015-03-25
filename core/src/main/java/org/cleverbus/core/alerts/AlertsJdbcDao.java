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

package org.cleverbus.core.alerts;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;


/**
 * Spring JDBC implementation of {@link AlertsDao}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
@Repository
public class AlertsJdbcDao implements AlertsDao {

    private JdbcTemplate template;

    @Autowired
    @Qualifier(value = "dataSource")
    public void setDataSource(DataSource dataSource) {
        Assert.notNull(dataSource, "dataSource must not be empty!");

        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public long runQuery(String sql) {
        Assert.hasText(sql, "the sql must not be empty");

        return template.queryForObject(sql, Long.class);
    }
}
