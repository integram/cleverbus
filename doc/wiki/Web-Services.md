# Web Services

## Description

CleverBus provides several tools and components for communication with external systems.

### Components

-   [CloseableHttpComponentsMessageSender](HTTP-Message-Sender)

## SOAP 1.2

Since version 0.4

CleverBus supports SOAP 1.1 and 1.2 for outgoing web service communication. There are *getOutWsUri* and *getOutWsSoap12Uri* methods in *org.cleverbus.api.route.AbstractBasicRoute*.

There is the following configuration prerequisite (*sp\_ws.xml*):

``` xml
    <bean id="messageFactorySOAP11" class="org.springframework.ws.soap.saaj.SaajSoapMessageFactory">
        <property name="soapVersion">
            <util:constant static-field="org.springframework.ws.soap.SoapVersion.SOAP_11"/>
        </property>
    </bean>

    <bean id="messageFactorySOAP12" class="org.springframework.ws.soap.saaj.SaajSoapMessageFactory">
        <property name="soapVersion">
            <util:constant static-field="org.springframework.ws.soap.SoapVersion.SOAP_12"/>
        </property>
    </bean>
```
