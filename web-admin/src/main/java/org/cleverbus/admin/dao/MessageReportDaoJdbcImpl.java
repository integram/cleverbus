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

package org.cleverbus.admin.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.cleverbus.admin.dao.dto.MessageReportDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;


/**
 * JDBC implementation of {@link MessageReportDao}.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
@Repository
public class MessageReportDaoJdbcImpl implements MessageReportDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier(value = "dataSource")
    public void setDataSource(DataSource dataSource) {
        Assert.notNull(dataSource, "the dataSource must not be null");

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<MessageReportDto> getMessageStateSummary(Date startDate, Date endDate) {

        String sql = "SELECT service, operation_name, source_system, state, COUNT(*) as state_count " +
                "FROM message m " +
                "     WHERE receive_timestamp >= ? AND receive_timestamp <= ? " +
                "GROUP BY service, operation_name, source_system, state " +
                "ORDER BY service, operation_name, source_system, state;";

        List<MessageReportDto> raw = jdbcTemplate.query(sql, new RowMapper<MessageReportDto>() {

            public MessageReportDto mapRow(ResultSet rs, int i) throws SQLException {
                MessageReportDto mdto = new MessageReportDto();
                mdto.setServiceName(rs.getString("service"));
                mdto.setOperationName(rs.getString("operation_name"));
                mdto.setSourceSystem(rs.getString("source_system"));
                mdto.setState(rs.getString("state"));
                mdto.setStateCount(rs.getInt("state_count"));
                return mdto;
            }
        }, startDate, endDate);

        return raw;
    }
}
