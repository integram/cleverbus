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


/**
 * Service for manipulation with {@link Request requests} and {@link Response responses}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
public interface RequestResponseService {

    /**
     * Inserts new request.
     *
     * @param request the request
     */
    void insertRequest(Request request);


    /**
     * Inserts new response.
     *
     * @param response the response
     */
    void insertResponse(Response response);


    /**
     * Gets last request specified by target URI and response-join ID.
     * <p/>
     * Note: there can be more requests for one message and external system because of reprocessing the message.
     * Therefore last request is used.
     *
     * @param uri the target URI
     * @param responseJoinId the identifier for pairing/joining request and response together
     * @return request
     */
    @Nullable
    Request findLastRequest(String uri, String responseJoinId);


    /**
     * Finds request which matches the criteria filter.
     *
     * @param from       the timestamp from
     * @param to         the timestamp to
     * @param subUri     the substring of URI
     * @param subRequest the substring of request content
     * @return list of {@link Request}
     */
    List<Request> findByCriteria(Date from, Date to, String subUri, String subRequest);
}
