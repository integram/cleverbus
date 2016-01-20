MVČR - identity card validation

## Extension for checking identity card state, if exists and if yes if it's not stolen

**Maven module:**

``` xml
<groupId>com.cleverlance.cleverbus.extensions</groupId>
<artifactId>mvcr</artifactId>
```

**Package**: *com.cleverlance.cleverbus.extension.mvcr*

<table>
<colgroup>
<col width="50%" />
<col width="50%" />
</colgroup>
<tbody>
<tr class="odd">
<td align="left"><strong>Route</strong></td>
<td align="left"><a href="https://hudson.clance.local/hudson/view/CleverBus/job/CleverBus%20release/javadoc/com/cleverlance/cleverbus/addons/out/mvcr/IdentityCardValidationRoute.html">IdentityCardValidationRoute</a></td>
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
<td align="left"><p>Route definition for calling MVCR identity card validation service. When some error occurs, route acts like identity card is not stolen.<br /><br />User must set the <em>IDENTITY_CARD_KEY</em> header with identityCard number.<br />Response is saved like boolean value in <em>IDENTITY_CARD_STOLEN_KEY</em> header attribute.</p></td>
</tr>
<tr class="odd">
<td align="left"><strong>Configuration </strong></td>
<td align="left"><div class="table-wrap">
Parameter
Default value
Description
<em>mvcr.uri</em>
http4://aplikace.mvcr.cz/neplatne-doklady/doklady.aspx
MVCR service URI
</div></td>
</tr>
<tr class="even">
<td align="left"><strong>Error codes</strong></td>
<td align="left"><p>no error codes</p></td>
</tr>
<tr class="odd">
<td align="left"><strong>Notes</strong></td>
<td align="left"><a href="http://www.mvcr.cz/clanek/neplatne-doklady-ve-formatu-xml.aspx">MVCR service description</a></td>
</tr>
</tbody>
</table>
