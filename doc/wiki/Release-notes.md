# Release notes

Release notes are published for the following products
- [CleverBus Community Edition](#community-edition)
- [CleverBus Enterprise Edition](#enterprise-edition)


# Enterprise Edition

## v2.2.0 (20.1.2016)

<h3>        Fixed
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-234'>CLVBUS-234</a>] -         
Even if the ESB is stopped the sync services works
</li>
</ul>
                                                                                                                                
<h3>        New Features
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-192'>CLVBUS-192</a>] - Redesign of sync/async message logging and log searching
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-225'>CLVBUS-225</a>] - Throttling - per system limit added
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-238'>CLVBUS-238</a>] - Sync/async messages - unified message format
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-245'>CLVBUS-245</a>] - Web Admin GUI - modularization
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-266'>CLVBUS-266</a>] - Web Admin GUI  - REST Management API 
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-259'>CLVBUS-259</a>] - Funnel - performance improvement
</li>
</ul>

## v2.1.3 (21.12.2015)

<h3>        Fixed
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-265'>CLVBUS-265</a>] -         
Calling throttling component produces StackOverflowError
</li>
</ul>
                                                                                                                                
<h3>        New Features
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-262'>CLVBUS-262</a>] - Alerting - multitasking for threshold calculation added
</li>
</ul>

## v2.1.2 (11.12.2015)

<h3>        Fixed
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-261'>CLVBUS-261</a>] - Throttling - produce ConcurrentModificationException
</li>
</ul>

## v2.1.1 (9.12.2015)

<h3>        Fixed
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-253'>CLVBUS-253</a>] - Funnel - failes when multifunnel option is used and quaranteed order option is not used
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-260'>CLVBUS-260</a>] - Async message - error occures while message detail is displayed
</li>
</ul>
                                                                                                                                
<h3>        New Features
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-233'>CLVBUS-233</a>] - REST services - Added basic authentication support 
</li>
</ul>

## v2.1.0 (31.7.2015)

<h3>        Fixed
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-191'>CLVBUS-191</a>] - ESB startup - random occurence of NullPointerException
</li>
</ul>
        
                                                                                                                      
<h3>        New Features
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-223'>CLVBUS-223</a>] - Monitoring - zabbix integration added
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-224'>CLVBUS-224</a>] - Monitoring - introduced threshold calculation optimization
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-229'>CLVBUS-229</a>] - Throttling - explicit throtling - no defaults now
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-169'>CLVBUS-169</a>] - Throttling - output throtling added
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-171'>CLVBUS-171</a>] - Planned outages - management by REST API
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-193'>CLVBUS-193</a>] - Planned outages - WEB GUI
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-207'>CLVBUS-207</a>] - Migrated to camel 2.15.2
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-208'>CLVBUS-208</a>] - Async message - archivation optimalization
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-213'>CLVBUS-213</a>] - Async message - schedulling support added 
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-212'>CLVBUS-212</a>] - CI - automatic performance tests support added
</li>
</ul>


## v2.0.4 (30.10.2015)
                                                                                                                       
<h3>        New Features
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-222'>CLVBUS-222</a>] - Async message - message search web service added
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-216'>CLVBUS-216</a>] - Funnel - multifunnel option support added
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-228'>CLVBUS-228</a>] - Alerting - global switch off configuration option added
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-232'>CLVBUS-232</a>] - Messages - Custom namespace can be used for traceHeader
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-249'>CLVBUS-249</a>] - EmailService - Default UTF-8 encoding for outgoing emails
</li>
</ul>

## v2.0.3 (16.6.2015)

<h3>        Fixed
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-226'>CLVBUS-226</a>] - ESB startup - Timers delay (atleast 1 minute) is must 
</li>
</ul>

<h3>        New Features
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-208'>CLVBUS-208</a>] - Async message - archivation optimalization
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-214'>CLVBUS-214</a>] - Funnel - funnel value in URI support added
</li>
</ul>

## v2.0.2 (4.5.2015)

<h3>        Fixed
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-191'>CLVBUS-191</a>] - ESB startup - random occurence of NullPointerException while external system is called after ESB startup
</li>
</ul>

## v2.0.1 (17.4.2015)

<h3>        Fixed
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-179'>CLVBUS-179</a>] - Async message - serialization error under stress
</li>
</ul>

## v2.0.0 (13.4.2015)

<h3>        New Features
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-170'>CLVBUS-170</a>] - Runtime - Java 1.8 support
</li>
</ul>

## v1.3 (29.7.2015)
                                                                                                                        
<h3>        New Features
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-176'>CLVBUS-176</a>] - DB Archivation - with custom job implementation
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-178'>CLVBUS-178</a>] - EmailService - attachement support introduced
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-208'>CLVBUS-208</a>] - DB Archivation - big data optimalization
</li>
</ul>

## v1.2 (1.4.2015)

GitHub verze (org.cleverbus) Java 1.6 suppport

## v1.1 (24.11.2014)
    
<h3>        Fixed
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-166'>CLVBUS-166</a>] - Funnel - check of postponedIntervalWhenFailed not working
</li>
</ul>
                                                                                                                                
<h3>        New Features
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-117'>CLVBUS-117</a>] -  Automatic bean name generation - ROUTE BEAN
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-132'>CLVBUS-132</a>] -  Custom check support added at the startup of ESB
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-162'>CLVBUS-162</a>] -  Multiple entity managers support
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-167'>CLVBUS-167</a>] -  CleverBus Extensions upgrade to CleverBus latest version
</li>
</ul>


# Community Edition

CleverBus can be downloaded from <a href='https://github.com/integram/cleverbus/releases'>GitHub releases</a>

## v2.0 (22.1.2016)


<h3>        Fixed
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-191'>CLVBUS-191</a>] - ESB startup - random occurence of NullPointerException while external system is called after ESB startup
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-226'>CLVBUS-226</a>] - ESB startup - Timers delay (atleast 1 minute) is must 
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-179'>CLVBUS-179</a>] - Async message - serialization error under stress
</li>

</ul>

<h3>        New Features
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-170'>CLVBUS-170</a>] - Runtime - Java 1.8 support
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-208'>CLVBUS-208</a>] - Async message - archivation optimalization
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-214'>CLVBUS-214</a>] - Funnel - funnel value in URI support added
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-176'>CLVBUS-176</a>] - DB Archivation - with custom job implementation
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-178'>CLVBUS-178</a>] - EmailService - attachement support introduced
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-208'>CLVBUS-208</a>] - DB Archivation - big data optimalization
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-222'>CLVBUS-222</a>] - Async message - message search web service added
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-216'>CLVBUS-216</a>] - Funnel - multifunnel option support added
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-228'>CLVBUS-228</a>] - Alerting - global switch off configuration option added
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-232'>CLVBUS-232</a>] - Messages - Custom namespace can be used for traceHeader
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-249'>CLVBUS-249</a>] - EmailService - Default UTF-8 encoding for outgoing emails
</li>
</ul>

                                                                                                

## 1.1 (24.11.2014)

<a href="https://github.com/integram/cleverbus/releases/tag/cleverbus-integration-1.1">Repository</a>

<a href="http://integram.github.io/cleverbus/javadocs/cleverbus-1.1/index.html">Javadoc for Cleverbus-1.1</a>

Incompatible changes:

-   [Replace Cleverlance namespaces](https://github.com/integram/cleverbus/Fixed/13)

Highlights:

Other New Features:

-   <a href="https://github.com/integram/cleverbus/Fixed/17">Repackage web admin console in web-admin</a>
-   <a href="https://github.com/integram/cleverbus/Fixed/11">Rename Maven modules with prefix cleverbus</a>

Bugs:

-   <a href="https://github.com/integram/cleverbus/Fixed/12">Mismatch of JAXB classes and real package structure</a>

