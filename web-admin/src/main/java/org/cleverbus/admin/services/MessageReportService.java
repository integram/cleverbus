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

package org.cleverbus.admin.services;

import java.util.Date;
import java.util.List;

import org.cleverbus.admin.dao.dto.MessageReportDto;


/**
 * Service for retrieving data for reports about messages.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
public interface MessageReportService {

    /**
     * Returns list of aggregated values about messages in specified time interval.
     * Messages are aggregated and also ordered by service, operation name, source system and state.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return list of {@link MessageReportDto aggregated DTOs}
     */
    public List<MessageReportDto> getMessageStateSummary(Date startDate, Date endDate);
}
