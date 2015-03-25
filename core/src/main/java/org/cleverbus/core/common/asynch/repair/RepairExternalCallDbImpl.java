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

package org.cleverbus.core.common.asynch.repair;

import static java.lang.Math.min;

import java.util.List;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalCallStateEnum;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.common.dao.ExternalCallDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

/**
 * Implementation that uses DB to find external calls
 * and also uses DB to write them back after they're repaired.
 */
public class RepairExternalCallDbImpl implements RepairExternalCallService {

    private static final int BATCH_SIZE = 10;

    private TransactionTemplate transactionTemplate;

    @Autowired
    private ExternalCallDao externalCallDao;

    /**
     * How often to run repair process (in seconds).
     */
    @Value("${asynch.repairRepeatTime}")
    private int repeatInterval;

    @Override
    public void repairProcessingExternalCalls() {
        // find external calls in PROCESSING state
        List<ExternalCall> extCalls = findProcessingExternalCalls();

        Log.debug("Found {} external call(s) for repairing ...", extCalls.size());

        // repair external calls in batches
        int batchStartIncl = 0;
        int batchEndExcl;
        while (batchStartIncl < extCalls.size()) {
            batchEndExcl = min(batchStartIncl + BATCH_SIZE, extCalls.size());
            updateExternalCallsInDB(extCalls.subList(batchStartIncl, batchEndExcl));
            batchStartIncl = batchEndExcl;
        }

    }

    private List<ExternalCall> findProcessingExternalCalls() {
        return transactionTemplate.execute(new TransactionCallback<List<ExternalCall>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<ExternalCall> doInTransaction(TransactionStatus status) {
                return externalCallDao.findProcessingExternalCalls(repeatInterval);
            }
        });
    }

    private void updateExternalCallsInDB(final List<ExternalCall> externalCalls) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (ExternalCall extCall : externalCalls) {
                    Log.warn("The extCall {} is in {} state and is being changed to {}.",
                            extCall.toHumanString(), extCall.getState(), ExternalCallStateEnum.FAILED);

                    extCall.setState(ExternalCallStateEnum.FAILED);
                    externalCallDao.update(extCall);
                }
            }
        });
    }

    @Required
    public void setTransactionManager(JpaTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "the transactionManager must not be null");

        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void setExternalCallDao(ExternalCallDao externalCallDao) {
        this.externalCallDao = externalCallDao;
    }
}
