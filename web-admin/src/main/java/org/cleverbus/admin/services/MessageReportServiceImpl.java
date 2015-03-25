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

import org.cleverbus.admin.dao.MessageReportDao;
import org.cleverbus.admin.dao.dto.MessageReportDto;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


/**
 * Implementation of {@link MessageReportService}.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
@Service
public class MessageReportServiceImpl implements MessageReportService {

    @Autowired
    private MessageReportDao dao;

    @Override
    public List<MessageReportDto> getMessageStateSummary(Date startDate, Date endDate) {
        Assert.notNull(startDate, "startDate mustn't be null");
        Assert.notNull(endDate, "startDate mustn't be null");

        // adjust dates to start from 0.00 and end 23.59
        DateTime from = new DateTime(startDate).withTimeAtStartOfDay();
        DateTime to = new DateTime(endDate).plusDays(1).withTimeAtStartOfDay();

        return dao.getMessageStateSummary(from.toDate(), to.toDate());
    }
}
