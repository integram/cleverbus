package org.cleverbus.core.common.converter;

import org.apache.camel.CamelContext;
import org.apache.camel.component.spring.ws.SpringWebserviceProducer;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.apache.camel.management.event.CamelContextStartedEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.cleverbus.common.log.Log;

import javax.xml.transform.stax.StAXSource;
import java.io.File;
import java.io.InputStream;
import java.util.EventObject;

/**
 * This class remove all converters which convert some objects into {@link StAXSource} after initialize
 * {@link CamelContext}.
 * It is necessary when we use JAVA8. When it is used {@link StAXSource} in JAVA8 and XML has no header with
 * xml version, than {@link NullPointerException} is thrown.
 * It is bug in JDK - <a href="https://bugs.openjdk.java.net/browse/JDK-8016914">
 *     https://bugs.openjdk.java.net/browse/JDK-8016914</a>.
 *
 * @author Radek Čermák [<a href="mailto:radek.cermak@cleverlance.com">radek.cermak@cleverlance.com</a>]
 * @see XmlConverter
 * @see SpringWebserviceProducer
 * @since 4.5.15
 */
public class RemoveStAXConverters extends EventNotifierSupport {

    /**
     * Remove all converters to {@link StAXSource}.
     *
     * @param event event about finished start of {@link CamelContext}
     * @throws Exception all errors
     */
    @Override
    public void notify(final EventObject event) throws Exception {
        if (event instanceof CamelContextStartedEvent) {
            CamelContext camelContext = ((CamelContextStartedEvent) event).getContext();

            if (camelContext.getTypeConverterRegistry().removeTypeConverter(StAXSource.class, byte[].class)) {
                Log.debug("Remove converter from '{}' to '{}'.", byte[].class.getSimpleName(),
                        StAXSource.class.getSimpleName());
            }
            if (camelContext.getTypeConverterRegistry().removeTypeConverter(StAXSource.class, File.class)) {
                Log.debug("Remove converter from '{}' to '{}'.", File.class.getSimpleName(),
                        StAXSource.class.getSimpleName());
            }
            if (camelContext.getTypeConverterRegistry().removeTypeConverter(StAXSource.class, InputStream.class)) {
                Log.debug("Remove converter from '{}' to '{}'.", InputStream.class.getSimpleName(),
                        StAXSource.class.getSimpleName());
            }
            if (camelContext.getTypeConverterRegistry().removeTypeConverter(StAXSource.class, String.class)) {
                Log.debug("Remove converter from '{}' to '{}'.", String.class.getSimpleName(),
                        StAXSource.class.getSimpleName());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled(final EventObject event) {
        return event instanceof CamelContextStartedEvent;
    }
}
