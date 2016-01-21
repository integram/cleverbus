# Dynamic extension loading

## Description

Dynamic extensions loading allows to add selected extension to CleverBus application independently by each other.

There is *extensions.cfg* configuration file (in *web-admin* module by default) with extension configuration parameters. 

If you want to add one specific extension to CleverBus application then follow the following steps:

### 0) Prerequisite - extension configuration loader must be initialized

``` xml
    <bean class="org.cleverbus.core.common.extension.PropertiesExtensionConfigurationLoader" depends-on="camelContext">
        <constructor-arg ref="confProperties"/>
    </bean>
```

### 1) add Maven dependency to specific extension

For example add dependency to ARES extenion:

``` xml
        <groupId>org.cleverbus.extensions</groupId>
        <artifactId>ares</artifactId>
```

### 2) add Spring configuration of specific extension

*extensions.cfg *(defined directly in CleverBus *sc-web-admin* module) or *extensions0.cfg* (has higher priority) defines references to Spring root configuration files for each extension. Each property has to starts with *context.ext* prefix.

#### Example

``` java
context.ext1 = classpath:/META-INF/sp_ext_ares.xml
```

*sp\_ext\_ares.xml* file can contain "unlimited" another Spring configuration specific for ARES extension, imports other configurations for the extension, choose between XML, annotation or Java config style etc.

For each item in the previous configuration file is new Spring child context created. This context is child context of Spring Camel (Web Service) context.

Child Spring context is type of <a href='http://docs.spring.io/spring/docs/3.2.8.RELEASE/javadoc-api/org/springframework/context/support/ClassPathXmlApplicationContext.html'>ClassPathXmlApplicationContext</a><br/><br/>See <i>PropertiesExtensionConfigurationLoader</i> for more details.

There is the following Spring context hierarchy:

-   root context
    -   Camel (Spring Web Service) context
        -   extension1 context
        -   extension2 context
    -   Spring MVC (admin) context
