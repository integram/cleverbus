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

package org.cleverbus.core.throttling;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import org.cleverbus.spi.throttling.ThrottleScope;

import org.junit.Test;


/**
 * Test suite for {@link ThrottleCounterMemoryImpl}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottleCountingMemoryImplTest {

    @Test
    public void testCounting() throws Exception {
        ThrottleCounterMemoryImpl counter = new ThrottleCounterMemoryImpl();

        ThrottleScope scope1 = new ThrottleScope("crm", "op1");
        int count = counter.count(scope1, 10);
        assertThat(count, is(1));

        ThrottleScope scope2 = new ThrottleScope("crm", "op2");
        count = counter.count(scope2, 10);
        assertThat(count, is(1));
        count = counter.count(scope2, 10);
        assertThat(count, is(2));

        ThrottleScope scope3 = new ThrottleScope("erp", "op1");
        count = counter.count(scope3, 10);
        assertThat(count, is(1));

        count = counter.count(scope1, 10);
        assertThat(count, is(2));

        ThrottleScope scope4 = new ThrottleScope("crm", "op4");
        count = counter.count(scope4, 1);
        assertThat(count, is(1));

        Thread.sleep(1500);

        count = counter.count(scope4, 1);
        assertThat(count, is(1));

        // test dump
        counter.dumpMemory();
    }

    @Test
    public void testMultiThreadCounting() throws Exception {
        final ThrottleCounterMemoryImpl counter = new ThrottleCounterMemoryImpl();

        // prepare threads
        int threads = 5;
        final CountDownLatch latch = new CountDownLatch(threads);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    // new instance for each thread
                    ThrottleScope scope1 = new ThrottleScope("crm", "op1");
                    ThrottleScope scope2 = new ThrottleScope("crm", "op2");

                    counter.count(scope1, 10);
                    counter.count(scope2, 10);
                } finally {
                    latch.countDown();
                }
            }
        };

        // start processing and waits for result
        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
        }

        latch.await();

        // verify counters
        ThrottleScope scope1 = new ThrottleScope("crm", "op1");
        ThrottleScope scope2 = new ThrottleScope("crm", "op2");

        int count = counter.count(scope1, 10);
        assertThat(count, is(threads + 1));

        count = counter.count(scope2, 10);
        assertThat(count, is(threads + 1));
    }
}
