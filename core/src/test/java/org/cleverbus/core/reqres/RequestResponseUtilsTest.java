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
        msg.setBody(null);
        // null
        assertThat(transformBody(msg), is(""));

        // text
        msg = new DefaultMessage();
        msg.setBody("text");
        assertThat(transformBody(msg), is("text"));

        // byte array, unknown format
        byte[] byteBody = {(byte) 0xe0, 0x4f, (byte) 0xd0, 0x20};
        msg.setBody(byteBody);
        assertThat(transformBody(msg), is(Hex.encodeHexString(byteBody)));

        // message
        org.cleverbus.api.entity.Message entity = new org.cleverbus.api.entity.Message();
        entity.setMsgId(1L);
        entity.setCorrelationId(UUID.randomUUID().toString());
        msg.setBody(entity);
        assertThat(transformBody(msg), is(entity.toHumanString()));

        // XML
        byteBody = "<test>valueč</test>".getBytes();
        msg.setBody(byteBody);
        assertThat(transformBody(msg), is("<test>valueč</test>"));
    }
}
