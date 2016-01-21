<h1>Release notes</h1>

Release notes are published for the following products
- <a href="#enterprise-edition">CleverBus Enterprise Edition</a>
- <a href="#community-edition">CleverBus Community Edition</a>

<h1>Enterprise Edition</h1>

<h2> v2.2.0 (20.1.2016)</h2>

<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-234'>CLVBUS-234</a>] -         Pokud je zastaveno ESB, pak pořád lze zavolat synchronní zprávy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-253'>CLVBUS-253</a>] -         Chyba při volání funnelu komponenty, pokud má route více funnel value a nemá nastaveno garantované pořadí
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-260'>CLVBUS-260</a>] -         Chyba při zobrazení detailu async zprávy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-261'>CLVBUS-261</a>] -         Chyba ConcurrentModification při vyhodnocování throttlingu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-265'>CLVBUS-265</a>] -         Při výpočtu throttlingu dochází k chybě java.lang.StackOverflowError
</li>
</ul>
                                                                                                                                
<h3>        Úkol
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-192'>CLVBUS-192</a>] -         Změna logování CLVB zpráv a vyhledávání v logu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-225'>CLVBUS-225</a>] -         Výstupní throttling - limit na úrovni systému
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-232'>CLVBUS-232</a>] -         Umožnit nastavit namespace pro traceHeader při kontrola vstupní zprávy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-233'>CLVBUS-233</a>] -         Při volání REST služeb musí být ověřování typu BASIC
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-238'>CLVBUS-238</a>] -         Jednotny format sluzeb pro sync a async zpravy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-240'>CLVBUS-240</a>] -         Release CE v2.0.4
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-241'>CLVBUS-241</a>] -         Vytvorit prostor pro publikovani dokumentace a release CleverBus
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-242'>CLVBUS-242</a>] -         V přehledu asynchronních zpráv podle stavů umožnit rozkliknout seznam zpráv
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-243'>CLVBUS-243</a>] -         Upravit script pro archivaci záznamů, aby se první smazala tabulka response a pak request
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-245'>CLVBUS-245</a>] -         Modularizace webového GUI
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-247'>CLVBUS-247</a>] -         Z CleverBusu odstranit výpis statistik z hibernate do logu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-249'>CLVBUS-249</a>] -         Při odesální emailů nastavit defaultně kódování na UTF8
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-257'>CLVBUS-257</a>] -         V komponentě pro vyhodnocování funnelu se zbytečně ukládají funnely i když se nezměnily
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-258'>CLVBUS-258</a>] -         Ve vyhledání async zpráv není v seznamu zobrazena funnel value u zprávy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-259'>CLVBUS-259</a>] -         Urychlit vyhodnocování funnel hodnot
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-262'>CLVBUS-262</a>] -         Přepracovat vyhodnocování alertů pro více vláken
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-266'>CLVBUS-266</a>] -         Vytvořit REST rozhraní pro management zpráv
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-270'>CLVBUS-270</a>] -         Upravit archetype pro jednodušší správu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-200'>CLVBUS-200</a>] -         Migrace dokumentace CE
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-236'>CLVBUS-236</a>] -         Priprava na cluster - Presun konfigurace do DB
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-237'>CLVBUS-237</a>] -         Priprava na cluster - Integracni scenar jako modul
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-272'>CLVBUS-272</a>] -         UserDoc: Změna logování CLVB zpráv a vyhledávání v logu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-274'>CLVBUS-274</a>] -         UserDoc: Výstupní throttling - limit na úrovni systému
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-276'>CLVBUS-276</a>] -         UserDoc: Jednotny format sluzeb pro sync a async zpravy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-273'>CLVBUS-273</a>] -         Test: Změna logování CLVB zpráv a vyhledávání v logu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-275'>CLVBUS-275</a>] -         Test: Výstupní throttling - limit na úrovni systému
</li>
</ul>

<h2>v2.1.3 (21.12.2015)</h2>

<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-265'>CLVBUS-265</a>] -         Při výpočtu throttlingu dochází k chybě java.lang.StackOverflowError
</li>
</ul>
                                                                                                                                
<h3>        Úkol
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-262'>CLVBUS-262</a>] -         Přepracovat vyhodnocování alertů pro více vláken
</li>
</ul>

<h2>v2.1.2 (11.12.2015)</h2>

<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-261'>CLVBUS-261</a>] -         Chyba ConcurrentModification při vyhodnocování throttlingu
</li>
</ul>

<h2>v2.1.1 (9.12.2015)</h2>

<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-253'>CLVBUS-253</a>] -         Chyba při volání funnelu komponenty, pokud má route více funnel value a nemá nastaveno garantované pořadí
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-260'>CLVBUS-260</a>] -         Chyba při zobrazení detailu async zprávy
</li>
</ul>
                                                                                                                                
<h3>        Úkol
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-232'>CLVBUS-232</a>] -         Umožnit nastavit namespace pro traceHeader při kontrola vstupní zprávy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-233'>CLVBUS-233</a>] -         Při volání REST služeb musí být ověřování typu BASIC
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-242'>CLVBUS-242</a>] -         V přehledu asynchronních zpráv podle stavů umožnit rozkliknout seznam zpráv
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-243'>CLVBUS-243</a>] -         Upravit script pro archivaci záznamů, aby se první smazala tabulka response a pak request
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-247'>CLVBUS-247</a>] -         Z CleverBusu odstranit výpis statistik z hibernate do logu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-249'>CLVBUS-249</a>] -         Při odesální emailů nastavit defaultně kódování na UTF8
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-257'>CLVBUS-257</a>] -         V komponentě pro vyhodnocování funnelu se zbytečně ukládají funnely i když se nezměnily
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-258'>CLVBUS-258</a>] -         Ve vyhledání async zpráv není v seznamu zobrazena funnel value u zprávy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-259'>CLVBUS-259</a>] -         Urychlit vyhodnocování funnel hodnot
</li>
</ul>

<h2>v2.1.0 (31.7.2015)</h2>

<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-179'>CLVBUS-179</a>] -         Při zátěži dojde k zablokování ukládání asynchronních zpráv do databáze
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-186'>CLVBUS-186</a>] -         Zajištění kvality dodávek verzí produktu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-191'>CLVBUS-191</a>] -         Po startu serveru náhodně dochází při volání externích webových služeb k chybě NullPointerException
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-226'>CLVBUS-226</a>] -         Při startu CleverBusu musí být zpožděn start všech časovačů (minimálně o jednu minutu)
</li>
</ul>
        
<h3>        Change Request
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-216'>CLVBUS-216</a>] -         Uprava funell komponenty - multifunell
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-223'>CLVBUS-223</a>] -         Monitoring - uprava stavajici funkcnosti alertu
</li>
</ul>
                                                                                                                        
<h3>        Úkol
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-169'>CLVBUS-169</a>] -         Throttling na výstupu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-171'>CLVBUS-171</a>] -         Regulace zátěže externích systémů, interaction time, plánované odstávky
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-207'>CLVBUS-207</a>] -         Přechod na camel 2.15.2
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-208'>CLVBUS-208</a>] -         Optimalizace archivace
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-212'>CLVBUS-212</a>] -         CI – automatické performance testy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-213'>CLVBUS-213</a>] -         Při vytváření asynch. routy umožnit říci, že odeslání zprávy má být naplánováno na určitý čas
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-214'>CLVBUS-214</a>] -         Umožnit na komponentě Funnel nastavit hodnotu funnel value v URI
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-224'>CLVBUS-224</a>] -         Monitoring - uprava stavajici funkcnosti alertu - optimalizace vyhodnocovani
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-228'>CLVBUS-228</a>] -         Umožnit v nastavení zabránit spuštění job pro kontrolu alertů
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-229'>CLVBUS-229</a>] -         Při throttlingu nevkládat automaticky defaultní throttling na vše
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-230'>CLVBUS-230</a>] -         Funkcni testy v 2.1.0
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-175'>CLVBUS-175</a>] -         Priprava DEV prostredi
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-180'>CLVBUS-180</a>] -         Regulace zátěže externích systémů, interaction time, plánované odstávky - analýza
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-193'>CLVBUS-193</a>] -         Vytvořit správu odstávky
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-194'>CLVBUS-194</a>] -         Vytvořit funkci pro znovuodeslání asnychronní zprávy v definovaný čas
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-195'>CLVBUS-195</a>] -         Rozmyslet jak zapracovat identifikaci volaného systému na koncových endpointech
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-196'>CLVBUS-196</a>] -         Zapracovat funkci pro přeplánování zprávy, pokud má systém odstávku
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-211'>CLVBUS-211</a>] -         Regulace zátěže externích systémů
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-217'>CLVBUS-217</a>] -         cleverbus - konfigurace jenkins pro pristup na esbdev1/esbdev2
</li>

</ul>


<h2>v2.0.4 (30.10.2015)</h2>

<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-226'>CLVBUS-226</a>] -         Při startu CleverBusu musí být zpožděn start všech časovačů (minimálně o jednu minutu)
</li>
</ul>
        
<h3>        Change Request
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-216'>CLVBUS-216</a>] -         Uprava funell komponenty - multifunell
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-222'>CLVBUS-222</a>] -         Vytvoreni WS pro prohledavani asynchronnich zprav
</li>
</ul>
                                                                                                                        
<h3>        Úkol
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-228'>CLVBUS-228</a>] -         Umožnit v nastavení zabránit spuštění job pro kontrolu alertů
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-232'>CLVBUS-232</a>] -         Umožnit nastavit namespace pro traceHeader při kontrola vstupní zprávy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-243'>CLVBUS-243</a>] -         Upravit script pro archivaci záznamů, aby se první smazala tabulka response a pak request
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-247'>CLVBUS-247</a>] -         Z CleverBusu odstranit výpis statistik z hibernate do logu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-249'>CLVBUS-249</a>] -         Při odesální emailů nastavit defaultně kódování na UTF8
</li>
</ul>

<h2>v2.0.3 (16.6.2015)</h2>

<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-226'>CLVBUS-226</a>] -         Při startu CleverBusu musí být zpožděn start všech časovačů (minimálně o jednu minutu)
</li>
</ul>

<h3>        Úkol
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-208'>CLVBUS-208</a>] -         Optimalizace archivace
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-214'>CLVBUS-214</a>] -         Umožnit na komponentě Funnel nastavit hodnotu funnel value v URI
</li>
</ul>

<h2>v2.0.2 (4.5.2015)</h2>

<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-191'>CLVBUS-191</a>] -         Po startu serveru náhodně dochází při volání externích webových služeb k chybě NullPointerException
</li>
</ul>

<h2>v2.0.1 (17.4.2015)<h2>

<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-179'>CLVBUS-179</a>] -         Při zátěži dojde k zablokování ukládání asynchronních zpráv do databáze
</li>
</ul>

<h2>v2.0.0 (13.4.2015)</h2>

<h3>        Úkol
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-170'>CLVBUS-170</a>] -         Přechod na verzi java 1.8
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-176'>CLVBUS-176</a>] -         Podpora archivace provoznich dat
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-178'>CLVBUS-178</a>] -         Umožnit odesílání emailů s přílohami na rozhraní EmailService
</li>
</ul>

<h2>v1.3 (29.7.2015)</h2>
<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-226'>CLVBUS-226</a>] -         Při startu CleverBusu musí být zpožděn start všech časovačů (minimálně o jednu minutu)
</li>
</ul>
        
<h3>        Change Request
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-222'>CLVBUS-222</a>] -         Vytvoreni WS pro prohledavani asynchronnich zprav
</li>
</ul>
                                                                                                                        
<h3>        Úkol
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-176'>CLVBUS-176</a>] -         Podpora archivace provoznich dat
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-178'>CLVBUS-178</a>] -         Umožnit odesílání emailů s přílohami na rozhraní EmailService
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-208'>CLVBUS-208</a>] -         Optimalizace archivace
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-228'>CLVBUS-228</a>] -         Umožnit v nastavení zabránit spuštění job pro kontrolu alertů
</li>
</ul>

<h2>v1.2 (1.4.2015)</h2>

GitHub verze (org.cleverbus) Java 1.6 suppport

<h2>1.1 (24.11.2014)</h2>
    
<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-166'>CLVBUS-166</a>] -         Garantovane poradi nechodi jak by melo - kontrola postponedIntervalWhenFailed
</li>
</ul>
                                                                                                                                
<h3>        Úkol
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-117'>CLVBUS-117</a>] -         Automaticke generovani jmena beany - ROUTE BEAN
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-132'>CLVBUS-132</a>] -         Moznost vlastni implementace kontroly pri inicializaci aplikace
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-155'>CLVBUS-155</a>] -         Odmazani @Deprecated veci
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-161'>CLVBUS-161</a>] -         Zavislost v jinych Maven projektech na stejne verzi Camelu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-162'>CLVBUS-162</a>] -         Vice entity manageru na projektu s CleverBusem
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-164'>CLVBUS-164</a>] -         Prejmenovat packages na org.cleverbus a zmenit licenci
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-167'>CLVBUS-167</a>] -         Upgrade Extensions na posledni verzi CleverBusu
</li>
</ul>


<h1>Community Edition</h1>

<<Info("CleverBus can be downloaded from <a href='https://github.com/integram/cleverbus/releases'>GitHub releases</a>")>>

<h2>v2.0 (30.10.2015)</h2>

<h3>        Chyba
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-179'>CLVBUS-179</a>] -         Při zátěži dojde k zablokování ukládání asynchronních zpráv do databáze
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-191'>CLVBUS-191</a>] -         Po startu serveru náhodně dochází při volání externích webových služeb k chybě NullPointerException
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-226'>CLVBUS-226</a>] -         Při startu CleverBusu musí být zpožděn start všech časovačů (minimálně o jednu minutu)
</li>
</ul>
        
<h3>        Change Request
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-216'>CLVBUS-216</a>] -         Uprava funell komponenty - multifunell
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-222'>CLVBUS-222</a>] -         Vytvoreni WS pro prohledavani asynchronnich zprav
</li>
</ul>
                                                                                                                        
<h3>        Úkol
</h3>
<ul>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-170'>CLVBUS-170</a>] -         Přechod na verzi java 1.8
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-176'>CLVBUS-176</a>] -         Podpora archivace provoznich dat
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-178'>CLVBUS-178</a>] -         Umožnit odesílání emailů s přílohami na rozhraní EmailService
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-208'>CLVBUS-208</a>] -         Optimalizace archivace
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-214'>CLVBUS-214</a>] -         Umožnit na komponentě Funnel nastavit hodnotu funnel value v URI
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-228'>CLVBUS-228</a>] -         Umožnit v nastavení zabránit spuštění job pro kontrolu alertů
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-232'>CLVBUS-232</a>] -         Umožnit nastavit namespace pro traceHeader při kontrola vstupní zprávy
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-243'>CLVBUS-243</a>] -         Upravit script pro archivaci záznamů, aby se první smazala tabulka response a pak request
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-247'>CLVBUS-247</a>] -         Z CleverBusu odstranit výpis statistik z hibernate do logu
</li>
<li>[<a href='https://jira.cleverlance.com/jira/browse/CLVBUS-249'>CLVBUS-249</a>] -         Při odesální emailů nastavit defaultně kódování na UTF8
</li>
</ul>                                                                                                   

<h2>1.1 (24.11.2014)</h2>

<a href="https://github.com/integram/cleverbus/releases/tag/cleverbus-integration-1.1">Repository</a>

<a href="http://integram.github.io/cleverbus/javadocs/cleverbus-1.1/index.html">Javadoc for Cleverbus-1.1</a>

Incompatible changes:

-   [Replace Cleverlance namespaces](https://github.com/integram/cleverbus/issues/13)

Highlights:

Other tasks:

-   <a href="https://github.com/integram/cleverbus/issues/17">Repackage web admin console in web-admin</a>
-   <a href="https://github.com/integram/cleverbus/issues/11">Rename Maven modules with prefix cleverbus</a>

Bugs:

-   <a href="https://github.com/integram/cleverbus/issues/12">Mismatch of JAXB classes and real package structure</a>

