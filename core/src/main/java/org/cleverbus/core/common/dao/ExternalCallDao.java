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

import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.PersistenceException;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalCallStateEnum;


/**
 * DAO for {@link ExternalCall} entity.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public interface ExternalCallDao {

    /**
     * Inserts new call.
     *
     * @param externalCall the external call
     */
    void insert(ExternalCall externalCall);

    /**
     * Updates call.
     *
     * @param externalCall the external call
     */
    void update(ExternalCall externalCall);

    /**
     * Finds an existing external call for the specified operation and entityId.
     *
     * @param operationName the operation name (uri) to find the call for
     * @param entityId      the entity id (operation key) to find the call for
     * @return the found external call or null, if no such call is registered
     */
    @Nullable
    ExternalCall getExternalCall(String operationName, String entityId);

    /**
     * Updates the specified attached external call entity to
     * start processing a new external call (set state to PROCESSING).
     *
     * @param extCall the external call that is attached to local EntityManager
     *                (e.g., acquired via {@link #getExternalCall(String, String)} in the same transaction)
     * @throws PersistenceException e.g., if the lock fails
     */
    void lockExternalCall(ExternalCall extCall) throws PersistenceException;

    /**
     * Finds ONE confirmation in state {@link ExternalCallStateEnum#FAILED}.
     *
     * @param interval Interval (in seconds) between two tries of failed confirmations.
     * @return external call or {@code null} if there is no any confirmation
     */
    @Nullable
    ExternalCall findConfirmation(int interval);

    /**
     * Updates confirmation (set state to PROCESSING) - gets lock for this confirmation.
     *
     * @param extCall the external call
     * @return the locked external call, if lock is successful; throws an exception, if the lock failed
     * @throws PersistenceException e.g., if the lock fails
     */
    ExternalCall lockConfirmation(ExternalCall extCall);

    /**
     * Finds "processing" external calls in specified interval.
     *
     * @param interval the interval
     * @return list of calls
     */
    List<ExternalCall> findProcessingExternalCalls(int interval);
}
