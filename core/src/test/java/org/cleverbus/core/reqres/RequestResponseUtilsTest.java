/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cleverbus.core.reqres;

import static org.cleverbus.core.reqres.RequestResponseUtils.transformBody;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

/**
 * Test suite for {@link RequestResponseUtils}.
 *
 * @author <a href="mailto:hanusto@gmail.com">Tomas Hanus</a>
 */
public class RequestResponseUtilsTest {

    @Test
    public void testTransformBody() {
        Message msg = new DefaultMessage();
        msg.setBody("text");
        assertThat(transformBody(msg), is("text"));

        final byte[] byteBody = {(byte) 0xe0, 0x4f, (byte) 0xd0, 0x20};
        msg.setBody(byteBody);
        assertThat(transformBody(msg), is(Hex.encodeHexString(byteBody)));

        org.cleverbus.api.entity.Message entity = new org.cleverbus.api.entity.Message();
        entity.setMsgId(1L);
        entity.setCorrelationId(UUID.randomUUID().toString());
        msg.setBody(entity);
        assertThat(transformBody(msg), is(entity.toHumanString()));
    }
}
