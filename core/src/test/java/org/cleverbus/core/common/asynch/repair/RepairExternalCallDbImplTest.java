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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.cleverbus.api.entity.ExternalCall;
import org.cleverbus.api.entity.ExternalCallStateEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.common.log.Log;
import org.cleverbus.core.AbstractCoreDbTest;
import org.cleverbus.core.common.dao.ExternalCallDao;
import org.cleverbus.test.ExternalSystemTestEnum;
import org.cleverbus.test.ServiceTestEnum;

import org.joda.time.DateTime;
import org.junit.Test;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Tests {@link RepairExternalCallDbImpl}
 */
@Transactional
@ContextConfiguration(loader = SpringockitoContextLoader.class)
public class RepairExternalCallDbImplTest extends AbstractCoreDbTest {

    @Autowired
    private ExternalCallDao externalCallDao;

    @Autowired
    private RepairExternalCallDbImpl externalCallService;

    @Value("${asynch.repairRepeatTime}")
    private int repeatInterval;


    @Test
    public void testRepairProcessingExternalCallsMany() throws Exception {
        ExternalCall[] externalCalls = createAndSaveExternalCalls(119);

        // 3 times to because MAX_MESSAGES_IN_ONE_QUERY=50
        externalCallService.repairProcessingExternalCalls();
        externalCallService.repairProcessingExternalCalls();
        externalCallService.repairProcessingExternalCalls();

        for (ExternalCall externalCall : externalCalls) {
            Log.info("Verifying external call {}", externalCall);
            ExternalCall found = externalCallDao.getExternalCall(
                    externalCall.getOperationName(), externalCall.getEntityId());
            assertThat(found, notNullValue());
            assertThat(found.getState(), is(ExternalCallStateEnum.FAILED));
        }
    }

    @Test
    public void testRepairProcessingExternalCallsOne() throws Exception {
        ExternalCall externalCall = createAndSaveExternalCalls(1)[0];

        externalCallService.repairProcessingExternalCalls();
        externalCallService.repairProcessingExternalCalls();

        ExternalCall found = externalCallDao.getExternalCall(
                externalCall.getOperationName(), externalCall.getEntityId());
        assertThat(found, notNullValue());
        assertThat(found.getState(), is(ExternalCallStateEnum.FAILED));
    }

    private ExternalCall[] createAndSaveExternalCalls(final int quantity) {
        TransactionTemplate tx = new TransactionTemplate(jpaTransactionManager);
        return tx.execute(new TransactionCallback<ExternalCall[]>() {
            @Override
            public ExternalCall[] doInTransaction(TransactionStatus status) {
                ExternalCall[] extCalls = new ExternalCall[quantity];
                for (int i = 0; i < extCalls.length; i++) {
                    Message message = createMessage(ExternalSystemTestEnum.CRM, ServiceTestEnum.CUSTOMER,
                            "someOperation", "some payload");

                    extCalls[i] = ExternalCall.createProcessingCall(
                            "direct:someOperation", UUID.randomUUID().toString(), message);
                    extCalls[i].setLastUpdateTimestamp(DateTime.now().minusHours(1).toDate());
                    em.persist(message);
                    em.persist(extCalls[i]);
                }
                em.flush();
                return extCalls;
            }
        });
    }
}
