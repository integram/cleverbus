#<link rel="stylesheet" type="text/css" href="http://www.cleverlance.com/_catalogs/masterpage/cleverlance/css/cleverlance.css">

CleverBus DEV prostředí
=======================


Přístupy
----------

>**SSH** 

>pristup na jednotlive nody pouzivej primo hostname jednotlivych nodu **esbdev1.cleverbus.cleverlance.com a esbdev2.cleverbus.cleverlance.com**,
peo samotny pristup do aplikace pouzivej ale clusterovou adresu/hostname **esbdev.cleverbus.cleverlance.com**, která je pak smerovana na patricne nody,
defaultne zatím necham balancer smerovan pouze na node1. 

> **DB**

> tak jsem tam rozbehal rovnou dve, kdy jedna je master a druha je RO replika.
Oba dva aplikacni nody (tomcaty) jsou primarne smerovany pouze na master databazi na nodu1, a jsou opet smerovany přes clusterovou adresu 
**dbdev.cleverbus.cleverlance.com**, která urcuje která z databazi je zrovna v danou chvili master.

Nódy
--------------

|ip	|hostname	|note	|note
|---|-----------|-------|-------------
|192.168.0.23|	esbdev1.cleverbus.cleverlance.com|	application node1 / no direct access|	use for SSH to login on node1|
|192.168.0.24|	esbdev2.cleverbus.cleverlance.com|	application node2 / no direct access|	use for SSH to login on node1|
|192.168.0.25|	dbdev1.cleverbus.cleverlance.com|	master - RW postgres||
|192.168.0.26|	dbdev2.cleverbus.cleverlance.com|	slave - only RO postgres||
|192.168.0.27|	esbdev.cleverbus.cleverlance.com|	application balancer / active on node1	use for https acces to ESB application||
|192.168.0.28|	dbdev.cleverbus.cleverlance.com	database balancer / active on node1||	use for sql access to master database|


**SW**
----------
>
> **JDK8**

> + rad bych to posunul i z tomcata 7 na tomcata 8, tedy ten tam ted je, 
pokud ten tomcat 8 bude delat nejake problemy, tak tam vratim zpatky 7.

> **Postgres** 

> + je tam rovnou 9.4, je tam vytvorena prazdna databaze a schema „cleverbus“, 
„uzivatel/heslo“ je „cleverbus/dbcleverbuspwd“ / databaze na tom druhem nodu je rovnou rozbehana jako RO replika
TCP Forwarding je tam povolen, takze si ten post muzes kdyztak protunelovat k sobe na localhost.

> **Tomcat** 

> + a cleverbus apliakce je jako na ostatynich esb serverech v /srv/cleverbus



**Správa**
-------------
> restarty a další věci mas povolene přes sudo (opet stejne jako na jinych serverech), napr.:
/etc/init.d/tomcat-cleverbus [stop|start|restart]

Zkus se tam prihlasit, jestli se tam dostanes,
zbytek doladime spolecne dle potreby

aplikacni war deployuj, jako esb.war, nyní jsem si tam udelal male test warko, kterym jsem si zkontroloval ze mi jede jndi na db, tak jej muzes smazat,
aplikace je pak tedy dostupna na: https://esbdev.cleverbus.cleverlance.com/esb/


