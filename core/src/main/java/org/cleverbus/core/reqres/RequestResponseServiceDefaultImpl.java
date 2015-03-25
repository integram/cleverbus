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

package org.cleverbus.core.reqres;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import org.cleverbus.api.entity.Request;
import org.cleverbus.api.entity.Response;
import org.cleverbus.core.common.dao.RequestResponseDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link RequestResponseService} interface.
 * <p/>
 * Implementation saves directly requests/responses into database in synchronous manner.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
@Transactional
public class RequestResponseServiceDefaultImpl implements RequestResponseService {

    @Autowired
    private RequestResponseDao requestResponseDao;

    @Override
    public void insertRequest(Request request) {
        Assert.notNull(request, "the request must not be null");

        requestResponseDao.insertRequest(request);
    }

    @Override
    public void insertResponse(Response response) {
        Assert.notNull(response, "the response must not be null");

        requestResponseDao.insertResponse(response);
    }

    @Nullable
    @Override
    public Request findLastRequest(String uri, String responseJoinId) {
        return requestResponseDao.findLastRequest(uri, responseJoinId);
    }

    @Override
    public List<Request> findByCriteria(Date from, Date to, String subUri, String subRequest) {
        return requestResponseDao.findByCriteria(from, to, subUri, subRequest);
    }
}
