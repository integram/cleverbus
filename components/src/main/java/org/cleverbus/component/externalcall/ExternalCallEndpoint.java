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

package org.cleverbus.component.externalcall;

import org.cleverbus.spi.extcall.ExternalCallService;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultEndpoint;


/**
 * See {@link ExternalCallComponent}
 */
public class ExternalCallEndpoint extends DefaultEndpoint {

    private ExternalCallKeyType keyType;
    private String targetURI;

    public ExternalCallEndpoint(String uri, ExternalCallComponent externalCallComponent, ExternalCallKeyType keyType, String targetURI) {
        super(uri, externalCallComponent);
        this.keyType = keyType;
        this.targetURI = targetURI;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new ExternalCallProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException(
                ExternalCallEndpoint.class.getSimpleName() + " doesn't support consuming from it");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    ProducerTemplate getProducerTemplate() {
        return ((ExternalCallComponent)getComponent()).getProducerTemplate();
    }

    ExternalCallService getService() {
        return ((ExternalCallComponent)getComponent()).getService();
    }

    public ExternalCallKeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(ExternalCallKeyType keyType) {
        this.keyType = keyType;
    }

    public String getTargetURI() {
        return targetURI;
    }

    public void setTargetURI(String targetURI) {
        this.targetURI = targetURI;
    }
}
