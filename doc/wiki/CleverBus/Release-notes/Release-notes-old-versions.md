# Release notes old versions

## Description

This page contains old releases to keep history of CleverBus development.<br/><br/>Look at <a href='https://github.com/integram/cleverbus/releases'>GitHub for new releases</a>

## 1.1-snapshot

### Incompatible changes

-   [[CLVBUS-155](https://jira.cleverlance.com/jira/browse/CLVBUS-155)] Odmazani @Deprecated veci
-   [[CLVBUS-164](https://jira.cleverlance.com/jira/browse/CLVBUS-164)] Prejmenovat packages na org.cleverbus a zmenit licenci
    -   packages *com.cleverlance -\> org.cleverbus*
    -   prejmenovani Maven modulu
        -   groupId: *com.cleverlance -\> org.cleverbus* (see [Maven and Spring](../Architecture/Maven-and-Spring))

            ```xml
            <dependency>
             <groupId>org.cleverbus</groupId>
             <artifactId>integration</artifactId>
            </dependency> 
            ```
        -   artefactIds: *sc-core -\> core, sc-test -\> test, ...*
    
    -   core-api: */com/cleverlance/cleverbus/core/modules/in/common/commonTypes-v1.0.xsd -\> /org/cleverbus/api/modules/in/common/commonTypes-v1.0.xsd*
       
    -   configuration parameter renamed: *directCall.localhostUri* =\> *contextCall.localhostUri*

### Highlights

### Other tasks

-   [[CLVBUS-162](https://jira.cleverlance.com/jira/browse/CLVBUS-162)] Vice entity manageru na projektu s CleverBusem
-   [[CLVBUS-117](https://jira.cleverlance.com/jira/browse/CLVBUS-117)] Automaticke generovani jmena beany - ROUTE BEAN
-   [[CLVBUS-161](https://jira.cleverlance.com/jira/browse/CLVBUS-161)] Zavislost v jinych Maven projektech na stejne verzi Camelu
-   [[CLVBUS-132](https://jira.cleverlance.com/jira/browse/CLVBUS-132)] Moznost vlastni implementace kontroly pri inicializaci aplikace
-   [[CLVBUS-166](https://jira.cleverlance.com/jira/browse/CLVBUS-166)] Garantovane poradi nechodi jak by melo - kontrola postponedIntervalWhenFailed

## 1.0.1-snapshot

### Bug fixes

-   [[CLVBUS-166](https://jira.cleverlance.com/jira/browse/CLVBUS-166)] Garantovane poradi nechodi jak by melo - kontrola postponedIntervalWhenFailed

## 0.4 (=1.0 release, 15.10.2014)

### Incompatible changes

-   API changes
    -   [[CLVBUS-115](https://jira.cleverlance.com/jira/browse/CLVBUS-115)] - zpětně nekompatibilní *TraceHeaderProcessor*
-   DB changes - necessary to run SQL script (*db\_schema\_postgreSql\_0\_4.sql*) for upgrading table structures
    -   [[CLVBUS-52](https://jira.cleverlance.com/jira/browse/CLVBUS-52)] - new tables *request* and *response*
    -   [[CLVBUS-105](https://jira.cleverlance.com/jira/browse/CLVBUS-105)] *-* changes in *message* table, added new column *parent\_binding\_type*
    -   [[CLVBUS-87](https://jira.cleverlance.com/jira/browse/CLVBUS-87)] - changes in *message* table, added new columns *guaranteed\_order* and *exclude\_failed\_state*
    -   [[CLVBUS-152](https://jira.cleverlance.com/jira/browse/CLVBUS-152)] - changes in *message *table, added new column *funnel\_component\_id*
    -   [[CLVBUS-113](https://jira.cleverlance.com/jira/browse/CLVBUS-113)] Pridat DB proceduru na archivaci zaznamu - *db\_schema\_postgreSql\_archive\_0\_4.sql*

### Highlights

-   [[CLVBUS-131](https://jira.cleverlance.com/jira/browse/CLVBUS-131)] Zprávy ve stavu PARTLY\_FAILED se nezpracovaji i kdyz ESB neni ve stop modu
-   [[CLVBUS-106](https://jira.cleverlance.com/jira/browse/CLVBUS-106)] Přidat podporu pro volani WS pomoci SOAP 1.2
-   [[CLVBUS-127](https://jira.cleverlance.com/jira/browse/CLVBUS-127)] Pridat kontrolni Spring beanu pro overovani konfigurace
-   [[CLVBUS-105](https://jira.cleverlance.com/jira/browse/CLVBUS-105)] Evidence type vazby parent-child
-   [[CLVBUS-93](https://jira.cleverlance.com/jira/browse/CLVBUS-93)] Prehled vstupnich endpointu a WSDL
-   [[CLVBUS-89](https://jira.cleverlance.com/jira/browse/CLVBUS-89)] Cekajici PROCESSING zprava ve fronte
-   [[CLVBUS-88](https://jira.cleverlance.com/jira/browse/CLVBUS-88)] Vylepseni trychtyre - zohledneni casu
-   [[CLVBUS-66](https://jira.cleverlance.com/jira/browse/CLVBUS-66)] legalni stop ESB
-   [[CLVBUS-52](https://jira.cleverlance.com/jira/browse/CLVBUS-52)] Ukladani volani externich systemu
-   [[CLVBUS-134](https://jira.cleverlance.com/jira/browse/CLVBUS-134)] Kontrola stavu zprav a odeslani notifikace pri jejich prekroceni
-   [[CLVBUS-87](https://jira.cleverlance.com/jira/browse/CLVBUS-87)] Garantované pořadí zpracování více asynchronních zpráv v rámci jednoho business procesu
-   [[CLVBUS-50](https://jira.cleverlance.com/jira/browse/CLVBUS-50)] Integrační logy - high overview
    -   new configuration parameter added (l*og.file.pattern*)
    -   logs can be compressed into gzip format now
    -   MDC context was changed - added source system of caller (*SOURCE\_SYSTEM*) if process is asynchronous and therefore changed *logback.xml*

### Other tasks:

-   [[CLVBUS-128](https://jira.cleverlance.com/jira/browse/CLVBUS-128)] Nespravne vyhodnocovani podporovaneho externiho systemu v DelegateConfirmationCallback
-   [[CLVBUS-139](https://jira.cleverlance.com/jira/browse/CLVBUS-139)] Pridani copyright
-   [[CLVBUS-137](https://jira.cleverlance.com/jira/browse/CLVBUS-137)] Chybne nacitani sloupce failedErrorCode pres failedErrorCodeInternal v entite Message
-   [[CLVBUS-118](https://jira.cleverlance.com/jira/browse/CLVBUS-118)] Konsolidace web provided knihoven
-   [[CLVBUS-119](https://jira.cleverlance.com/jira/browse/CLVBUS-119)] Zamenit basic auth ve web konzoli na form based (session) auth
-   [[CLVBUS-123](https://jira.cleverlance.com/jira/browse/CLVBUS-123)] Presun GUI popisku do properties
-   [[CLVBUS-91](https://jira.cleverlance.com/jira/browse/CLVBUS-91)] Prechod plne na JPA
-   [[CLVBUS-90](https://jira.cleverlance.com/jira/browse/CLVBUS-90)] Upgrade na Camel 2.13
-   [[CLVBUS-124](https://jira.cleverlance.com/jira/browse/CLVBUS-124)] Vytvorit "deployable" test pro overeni nasaditelnosti na server
-   [[CLVBUS-110](https://jira.cleverlance.com/jira/browse/CLVBUS-110)] pridani indexu na funnel\_value v tabulce messages
-   [[CLVBUS-113](https://jira.cleverlance.com/jira/browse/CLVBUS-113)] Pridat DB proceduru na archivaci zaznamu
    -   *db\_schema\_postgreSql\_archive\_0\_4.sql*
-   [[CLVBUS-152](https://jira.cleverlance.com/jira/browse/CLVBUS-152)] Oprava trychtyre - nutne zohlednit aktualni stav zpracovani zprav
-   [[CLVBUS-115](https://jira.cleverlance.com/jira/browse/CLVBUS-115)] Validace applicationID dle zadaneho enumu (zpětně nekompatibilní použítí *TraceHeaderProcessor*, nutné implementovat parametrický konstruktor)
-   [[CLVBUS-153](https://jira.cleverlance.com/jira/browse/CLVBUS-153)] Odmazani mock serveru
-   [[CLVBUS-154](https://jira.cleverlance.com/jira/browse/CLVBUS-154)] Upgrade archetype
-   [[CLVBUS-141](https://jira.cleverlance.com/jira/browse/CLVBUS-141)] Umozneni zretezeni validace pomoci vice validatoru AsynchRouteBuilder\#withValidator()

 

## 0.3.1-snapshot

### Bug fixes

-   [[CLVBUS-116](https://jira.cleverlance.com/jira/browse/CLVBUS-116)] TraceHeaderProcessor, ktery neni mandatory.

## 0.3 (25.6.2014)

### Incompatible changes

-   [[CLVBUS-65](https://jira.cleverlance.com/jira/browse/CLVBUS-65)] sp\_monitoring.xml renamed to sp\_jmx.xml + moved from sc-core module to sc-web-admin
-   [[CLVBUS-95](https://jira.cleverlance.com/jira/browse/CLVBUS-95)] changes in sc-web-admin module - [new default Spring profiles are "dev, h2"](../Architecture/Maven-and-Spring) + "db\_schema\_test.sql" removal
-   [[CLVBUS-81](https://jira.cleverlance.com/jira/browse/CLVBUS-81)] removed sc-addons module, new project was created for CleverBus extensions, see [CleverBus extensions](../CleverBus-extensions)
-   [[CLVBUS-84](https://jira.cleverlance.com/jira/browse/CLVBUS-84)] new API and SPI were created
    -   **There is really lot of incompatible changes, the following list presents major ones only. Version 0.3 is not backward compatible to version 0.1 but it was necessary to made these changes before more projects will use CleverBus.**
    -   Maven modules were reorganized, see [Maven and Spring](../Architecture/Maven-and-Spring)
    -   new API (module sc-core-api) was created, package com.cleverlance.cleverbus.api
        -   *AsynchConstants* - new class for route URIs

            ```java
            AsynchMessageRoute.URI_ERROR_HANDLING >> AsynchConstants.URI_ERROR_HANDLING
            AsynchMessageRoute.MSG_HEADER >> AsynchConstants.MSG_HEADER
            AsynchMessageRoute.URI_ERROR_FATAL >> AsynchConstants.URI_ERROR_FATAL
            AsynchMessageRoute.BUSINESS_ERROR_PROP_SUFFIX >> AsynchConstants.BUSINESS_ERROR_PROP_SUFFIX
            AsynchMessageRoute.ASYNCH_MSG_HEADER >> AsynchConstants.ASYNCH_MSG_HEADER
            AsynchInMessageRoute.OBJECT_ID_HEADER >> AsynchConstants.OBJECT_ID_HEADER
            AsynchInMessageRoute.SERVICE_HEADER >> AsynchConstants.SERVICE_HEADER
            AsynchInMessageRoute.OPERATION_HEADER >> AsynchConstants.OPERATION_HEADER
            AsynchInMessageRoute.URI_ASYNCH_IN_MSG >> AsynchConstants.URI_ASYNCH_IN_MSG
            ... 
            ```
            
        -   change in package name of CleverBus entities for persistence - *com.cleverlance.cleverbus.api.entity*
        -   *com.cleverlance.cleverbus.api.extcall.ExtCallComponentParams* - new class with params for extcall component, before  *ExternalCallComponent*

    -   new SPI (module sc-core-spi) was created, package *com.cleverlance.cleverbus.spi*
    -   changes in class names in *sc-core-test* module - new class names *AbstractTest* and *AbstractDbTest*
    -   several classes were moved from *sc-common* module to *sc-core*, such as the following

        ```java
        com.cleverlance.cleverbus.common.HumanReadable -> com.cleverlance.cleverbus.core.HumanReadable;
        com.cleverlance.cleverbus.common.ws.component (sc-common) -> com.cleverlance.cleverbus.core.common.ws.component (sc-core)
        com.cleverlance.cleverbus.common.spring (sc-common) -> com.cleverlance.cleverbus.core.common.spring (sc-core)

        Event:
        com.cleverlance.cleverbus.common.event.EventNotifier (sc-common) -> com.cleverlance.cleverbus.core.common.events.EventNotifier (sc-api)
        com.cleverlance.cleverbus.common.event.EventNotifierBase (sc-common) -> com.cleverlance.cleverbus.core.common.events.EventNotifierBase (sc-api)
        com.cleverlance.cleverbus.common.event.EventNotifierAutoRegistry (sc-common) -> com.cleverlance.cleverbus.core.common.events.EventNotifierAutoRegistry (sc-core)

        Direct call:
        com.cleverlance.cleverbus.common.directcall (sc-common) ->  com.cleverlance.cleverbus.core.common.directcall (sc-core)
        com.cleverlance.cleverbus.core.common.route.DirectCallHttpImpl -> com.cleverlance.cleverbus.core.common.directcall.DirectCallHttpImpl
        com.cleverlance.cleverbus.core.common.route.DirectCallWsRoute -> com.cleverlance.cleverbus.core.common.directcall.DirectCallWsRoute

        Version:
        com.cleverlance.cleverbus.common.version (sc-common) -> com.cleverlance.cleverbus.core.common.version (sc-core) 
        ```

<!-- -->

-   [[CLVBUS-81](https://jira.cleverlance.com/jira/browse/CLVBUS-81)] - changes in main implementation classes for throttling

### Highlights

-   [[CLVBUS-81](https://jira.cleverlance.com/jira/browse/CLVBUS-81)] - Vydeleni addons (nove extensions) do separatniho projektu
-   [[CLVBUS-84](https://jira.cleverlance.com/jira/browse/CLVBUS-84)] - Vytvoreni CleverBus API
-   [[CLVBUS-109](https://jira.cleverlance.com/jira/browse/CLVBUS-109)] - Vytvoreni komponenty pro throttling

<!-- -->

-   [[CLVBUS-61](https://jira.cleverlance.com/jira/browse/CLVBUS-61)] changes in pooling incoming asynchronous messages
-   [[CLVBUS-57](https://jira.cleverlance.com/jira/browse/CLVBUS-57)] [new monitoring possibilites with JavaMelody and hawtio](../Running-CleverBus/Monitoring)
-   [[CLVBUS-63](https://jira.cleverlance.com/jira/browse/CLVBUS-63)] better and nicer admin GUI 
-   [[CLVBUS-36](https://jira.cleverlance.com/jira/browse/CLVBUS-36)] authorization in routes
-   [[CLVBUS-56](https://jira.cleverlance.com/jira/browse/CLVBUS-56)] AsynchRouteBuilder - new builder class for asynchronous routes with fluent API
-   [[CLVBUS-47](https://jira.cleverlance.com/jira/browse/CLVBUS-47)] run server with selected set of routes
-   [[CLVBUS-48](https://jira.cleverlance.com/jira/browse/CLVBUS-48)] dynamic routes loading from JAR files

### Other tasks

-   [[CLVBUS-69](https://jira.cleverlance.com/jira/browse/CLVBUS-69)] - Chyba behem restartu zprav - dead lock
-   [[CLVBUS-80](https://jira.cleverlance.com/jira/browse/CLVBUS-80)] - Nefunkcni autorizace na adrese .../ws/.../v1
-   [[CLVBUS-5](https://jira.cleverlance.com/jira/browse/CLVBUS-5)] - Moznost reloadu konfigurace bez nutnosti restartovani serveru

<!-- -->

-   [[CLVBUS-15](https://jira.cleverlance.com/jira/browse/CLVBUS-15)] - Parent-child koncept
-   [[CLVBUS-64](https://jira.cleverlance.com/jira/browse/CLVBUS-64)] - Problem s poolingem u Http clienta
-   [[CLVBUS-67](https://jira.cleverlance.com/jira/browse/CLVBUS-67)] - Upgrade Apache Camel na 2.11.3
-   [[CLVBUS-76](https://jira.cleverlance.com/jira/browse/CLVBUS-76)] - Visi zprava ve stavu WAITING
-   [[CLVBUS-95](https://jira.cleverlance.com/jira/browse/CLVBUS-95)] - Moznost prohlizeni zaznamu pri pouziti H2 DB
-   [[CLVBUS-71](https://jira.cleverlance.com/jira/browse/CLVBUS-71)] - Oprava warnings z kompilace - deprecated API
-   [[CLVBUS-99](https://jira.cleverlance.com/jira/browse/CLVBUS-99)] - Pri zapnutem JavaMelody je problem s vypnutim aplikace v Tomcatu
-   [[CLVBUS-29](https://jira.cleverlance.com/jira/browse/CLVBUS-29)] - Integrace CleverBus komponent jako Addon
-   [[CLVBUS-63](https://jira.cleverlance.com/jira/browse/CLVBUS-63)] - Vylepseni admin GUI
-   [[CLVBUS-72](https://jira.cleverlance.com/jira/browse/CLVBUS-72)] - Souhrnny bod - zlepseni monitoringu a logovani zprav
-   [[CLVBUS-97](https://jira.cleverlance.com/jira/browse/CLVBUS-97)] - Direct WS call - moznost poslani SOAP header
-   [[CLVBUS-100](https://jira.cleverlance.com/jira/browse/CLVBUS-100)] - Moznost manualniho spusteni akce pro zpracovani PARTLY\_FAILED zprav a pro jejich opravu
-   [[CLVBUS-101](https://jira.cleverlance.com/jira/browse/CLVBUS-101)] - Umoznit vyhledavani zprav jen pres correlation\_id (bez nutnosti vyberu zdrojoveho systemu)
-   [[CLVBUS-68](https://jira.cleverlance.com/jira/browse/CLVBUS-68)] - Prezentace katalogu chyb
-   [[CLVBUS-65](https://jira.cleverlance.com/jira/browse/CLVBUS-65)] - dynamicke nastaveni throttlingu pomoci JMX

## 0.2 (15.6.2014)

Technický release z důvodu interních projektových záležitostí, na tu verzi se nepojit.

Všechny tasky udělané do této verze jsou uvedeny v přehledu verze 0.3.

## 0.1.1-snapshot

### Bug fixes

-   [[CLVBUS-70](https://jira.cleverlance.com/jira/browse/CLVBUS-70)] Throttling - Array index out of range: 0
-   [[CLVBUS-69](https://jira.cleverlance.com/jira/browse/CLVBUS-69)] Chyba behem restartu zprav - dead lock
-   [[CLVBUS-76](https://jira.cleverlance.com/jira/browse/CLVBUS-76)] Visi zprava ve stavu WAITING
-   [[CLVBUS-80](https://jira.cleverlance.com/jira/browse/CLVBUS-80)] Nefunkcni autorizace na adrese .../ws/.../v1
-   [[CLVBUS-61](https://jira.cleverlance.com/jira/browse/CLVBUS-61)] Overeni spravne funkcnosti prijmu asynchronnich zprav (thread-polls)
-   [[CLVBUS-97](https://jira.cleverlance.com/jira/browse/CLVBUS-97)] Direct WS call - moznost poslani SOAP header

## 0.1 (4.2.2014)

-   [[CLVBUS-1](https://jira.cleverlance.com/jira/browse/CLVBUS-1)] - Vytvoreni zakladu pro CleverBus
-   [[CLVBUS-2](https://jira.cleverlance.com/jira/browse/CLVBUS-2)] - Stav CANCEL - manualni zruseni zpracovani zpravy
-   [[CLVBUS-3](https://jira.cleverlance.com/jira/browse/CLVBUS-3)] - Podpora pro manualni zruseni zpravy (CANCEL) v admin gui
-   [[CLVBUS-6](https://jira.cleverlance.com/jira/browse/CLVBUS-6)] - Upgrade na Camel 2.12
-   [[CLVBUS-8](https://jira.cleverlance.com/jira/browse/CLVBUS-8)] - Online sledovani pres JMX
-   [[CLVBUS-14](https://jira.cleverlance.com/jira/browse/CLVBUS-14)] - Pridat processID do hlavicky asynchronnich zprav
-   [[CLVBUS-16](https://jira.cleverlance.com/jira/browse/CLVBUS-16)] - Prime volani externich systemu
-   [[CLVBUS-20](https://jira.cleverlance.com/jira/browse/CLVBUS-20)] - FileRepository presunout z projektu Centropol
-   [[CLVBUS-21](https://jira.cleverlance.com/jira/browse/CLVBUS-21)] - Pridani ukazkove routy pro volani pres WS
-   [[CLVBUS-22](https://jira.cleverlance.com/jira/browse/CLVBUS-22)] - Podpora eventu
-   [[CLVBUS-30](https://jira.cleverlance.com/jira/browse/CLVBUS-30)] - Pridat tlacitko na odhlaseni z admin casti
-   [[CLVBUS-31](https://jira.cleverlance.com/jira/browse/CLVBUS-31)] - Upgrade na quartz2
-   [[CLVBUS-33](https://jira.cleverlance.com/jira/browse/CLVBUS-33)] - Při chybě throttling exception se zpráva stejně propíše do db
-   [[CLVBUS-35](https://jira.cleverlance.com/jira/browse/CLVBUS-35)] - Pri throttlingu vyhazovat HTTP 503
-   [[CLVBUS-37](https://jira.cleverlance.com/jira/browse/CLVBUS-37)] - Provedeni vlastni akce pokud zpracovani zpravy skonci (OK, FAILED)
-   [[CLVBUS-38](https://jira.cleverlance.com/jira/browse/CLVBUS-38)] - Automaticke mapovani zakladnich vyjimek na jejich error kody
-   [[CLVBUS-41](https://jira.cleverlance.com/jira/browse/CLVBUS-41)] - Obecna architektura
-   [[CLVBUS-42](https://jira.cleverlance.com/jira/browse/CLVBUS-42)] - Upgrade Camel na 2.11.2
-   [[CLVBUS-45](https://jira.cleverlance.com/jira/browse/CLVBUS-45)] - ExceptionTranslator - vracet pouze root exception
-   [[CLVBUS-46](https://jira.cleverlance.com/jira/browse/CLVBUS-46)] - Uprava SOAP fault odpovedi - pridat explicitne element pro kod chyby
-   [[CLVBUS-49](https://jira.cleverlance.com/jira/browse/CLVBUS-49)] - proverit indexy nad tabulkami CleverBUS (message, external\_call)
-   [[CLVBUS-50](https://jira.cleverlance.com/jira/browse/CLVBUS-50)] - Integrační logy - high overview
-   [[CLVBUS-51](https://jira.cleverlance.com/jira/browse/CLVBUS-51)] - Kontrola poctu selhani pokud zprava zustava ve stavu PROCESSING
-   [[CLVBUS-53](https://jira.cleverlance.com/jira/browse/CLVBUS-53)] - Rozsireni NotificationService o formatovani zprav a vyberu vlastniho adresata
-   [[CLVBUS-54](https://jira.cleverlance.com/jira/browse/CLVBUS-54)] - Lepsi podpora pro zakladani internich asynchr. zprav
-   [[CLVBUS-55](https://jira.cleverlance.com/jira/browse/CLVBUS-55)] - Implementace "omezovatka"

