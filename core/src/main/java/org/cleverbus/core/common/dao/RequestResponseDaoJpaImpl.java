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

package org.cleverbus.core.common.dao;

import static org.springframework.util.StringUtils.hasText;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.cleverbus.api.entity.Request;
import org.cleverbus.api.entity.Response;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * JPA implementation of {@link RequestResponseDao} interface.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @since 0.4
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class RequestResponseDaoJpaImpl implements RequestResponseDao {

    public static final int MAX_REQUESTS_IN_ONE_QUERY = 50;

    @PersistenceContext(unitName = DbConst.UNIT_NAME)
    private EntityManager em;

    @Override
    public void insertRequest(Request request) {
        em.persist(request);
    }

    @Override
    public void insertResponse(Response response) {
        em.persist(response);
    }

    @Nullable
    @Override
    public Request findLastRequest(String uri, String responseJoinId) {
        Assert.hasText(uri, "the uri must not be empty");
        Assert.hasText(responseJoinId, "the responseJoinId must not be empty");

        String jSql = "SELECT r " +
                "FROM " + Request.class.getName() + " r " +
                "WHERE r.responseJoinId = :responseJoinId AND r.uri = :uri " +
                "ORDER BY r.reqTimestamp";

        TypedQuery<Request> q = em.createQuery(jSql, Request.class);
        q.setParameter("responseJoinId", responseJoinId);
        q.setParameter("uri", uri);

        // we search by unique key - it's not possible to have more records
        List<Request> requests = q.getResultList();
        if (requests.isEmpty()) {
            return null;
        } else {
            return requests.get(0); // if find more items then return first one only
        }
    }

    @Override
    public List<Request> findByCriteria(Date from, Date to, String subUri, String subRequest) {
        Assert.notNull(from, "the from must not be null");
        Assert.notNull(to, "the to must not be null");

        String jSql = "SELECT r "
                + "         FROM " + Request.class.getName() + " r " +
                "           WHERE r.reqTimestamp >= :from " +
                "               AND r.reqTimestamp <= :to ";

        if (hasText(subUri)) {
            jSql += "           AND r.uri like :subUri)";
        }
        if (hasText(subRequest)) {
            jSql += "           AND r.request like :subRequest)";
        }

        jSql += "           ORDER BY r.reqTimestamp";

        TypedQuery<Request> q = em.createQuery(jSql, Request.class);
        q.setParameter("from", new Timestamp(from.getTime()));
        q.setParameter("to", new Timestamp(to.getTime()));
        if (hasText(subUri)) {
            q.setParameter("subUri", "%" + subUri + "%");
        }
        if (hasText(subRequest)) {
            q.setParameter("subRequest", "%" + subRequest + "%");
        }
        q.setMaxResults(MAX_REQUESTS_IN_ONE_QUERY);

        return q.getResultList();
    }
}
