# ARES

**Description: **extension for checking if specified company ID is in ARES system and verifies if the company isn't in the registry of debtors.

**Maven module:**

``` xml
<groupId>com.cleverlance.cleverbus.extensions</groupId>
<artifactId>ares</artifactId>
```

**Package**: *com.cleverlance.cleverbus.extension.ares*

<table>
<colgroup>
<col width="50%" />
<col width="50%" />
</colgroup>
<tbody>
<tr class="odd">
<td align="left"><strong>Route</strong></td>
<td align="left"><em>CompanyIDValidationRoute</em></td>
</tr>
<tr class="even">
<td align="left"><strong>IN/OUT</strong></td>
<td align="left">OUT</td>
</tr>
<tr class="odd">
<td align="left"><strong>URL</strong></td>
<td align="left"><p> </p></td>
</tr>
<tr class="even">
<td align="left"><strong>Description</strong></td>
<td align="left"><p>Route definition for calling the external registry ARES, based on the response a Company (Company ID) is listed as debtor.<br /><br />The only required parameter for the core functionality is the <em>COMPANY_ID_KEY</em>, it's necessary for the ext. query.</p></td>
</tr>
<tr class="odd">
<td align="left"><strong>Configuration </strong></td>
<td align="left"><div class="table-wrap">
Parameter
Default value
Description
<em>ares.uri</em>
http4://wwwinfo.mfcr.cz/cgi-bin/ares/darv_bas.cgi
ARES service URI
</div></td>
</tr>
<tr class="even">
<td align="left"><strong>Error codes</strong></td>
<td align="left"><p>no error codes</p></td>
</tr>
<tr class="odd">
<td align="left"><strong>Notes</strong></td>
<td align="left"><a href="http://wwwinfo.mfcr.cz/ares/ares_xml_basic.html.cz">ARES service description</a></td>
</tr>
</tbody>
</table>
