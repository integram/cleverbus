# HTTP Message Sender

    From version 0.2

## Description

CleverBus provides own customized solution as *CloseableHttpComponentsMessageSender*, which uses the [Apache HttpComponents HttpClient](http://hc.apache.org/httpcomponents-client-ga). Use that if you need more advanced and easy-to-use functionality (such as authentication, HTTP connection pooling, and so forth).

The following example shows how to configure this component, and to use Apache HttpClient to authenticate using HTTP authentication with connection pooling per host:

``` xml
<bean id="billingSender" class="org.cleverbus.core.common.ws.transport.http.CloseableHttpComponentsMessageSender">
 <constructor-arg index="0" value="true"/> <!-- use Preemptive Auth-->
 <property name="credentials">
  <bean class="org.apache.http.auth.UsernamePasswordCredentials">
   <constructor-arg index="0" value="${billing.user}"/>
   <constructor-arg index="1" value="${billing.password}"/>
  </bean>
 </property>
 <property name="maxTotalConnections" value="${billing.maxTotalConnections}"/>
 <property name="defaultMaxPerHost" value="${billing.maxTotalConnections}"/>
 <property name="maxConnectionsPerHost">
  <props>
   <prop key="${billing.uri}">${billing.maxTotalConnections}</prop>
  </props>
 </property>
 <property name="connectionTimeout" value="${billing.connectionTimeout}"/>
 <property name="readTimeout" value="${billing.readTimeout}"/>
</bean>
```

 

*CloseableHttpComponentsMessageSender* has several type of implementation: 

-   *NtlmCloseableHttpComponentsMessageSender* for NTLM authentication support
