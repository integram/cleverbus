# File upload

## Description

Extension for uploading files with use *FileRepository* implementation

**Maven module:**

``` xml
<groupId>com.cleverlance.cleverbus.extensions</groupId>
<artifactId>file-upload</artifactId>
```

**Package**: *com.cleverlance.cleverbus.extension.fileupload*

 

<table>
<colgroup>
<col width="50%" />
<col width="50%" />
</colgroup>
<tbody>
<tr class="odd">
<td align="left"><strong>Route</strong></td>
<td align="left"><em>UploadFileRoute</em></td>
</tr>
<tr class="even">
<td align="left"><strong>IN/OUT</strong></td>
<td align="left">IN</td>
</tr>
<tr class="odd">
<td align="left"><strong>URL</strong></td>
<td align="left"><p>.../http/upload</p></td>
</tr>
<tr class="even">
<td align="left"><strong>Description</strong></td>
<td align="left"><p>File is uploaded via PUT HTTP method.</p>
<p>Input file is uploaded to temporary directory with unique file identifier (fileId) that is returned back.<br />When this fileId comes with other data then the file is moved and renamed to right target folder.</p>
<p><br /><span style="line-height: 1.4285715;">Uploading files can be tested with </span><span style="line-height: 1.4285715;"><a href="http://curl.haxx.se">curl</a> command tool:</span></p>
<div class="code panel pdl" style="border-width: 1px;">
<div class="codeContent panelContent pdl">
<pre class="brush: java; gutter: false; theme: Confluence" style="font-size:12px;"><code>curl -i -X PUT -T &quot;/Volumes/Obelix/context.xml&quot; http://localhost:8080/esb/http/...</code></pre>
</div>
</div></td>
</tr>
<tr class="odd">
<td align="left"><strong>Configuration </strong></td>
<td align="left"><div class="table-wrap">
Parameter
Default value
Description
<em>file.maxUploadedFileSize</em>
5000
Maximum size (in kB) for uploaded files
<em>file.upload.servletName</em>
CamelServlet
Servlet name defined in web.xml for uploading files
</div>
<p>web.xml:</p>
<div class="code panel pdl" style="border-width: 1px;">
<div class="codeContent panelContent pdl">
<pre class="brush: java; gutter: false; theme: Confluence" style="font-size:12px;"><code>    &lt;!-- Camel servlet--&gt;
    &lt;servlet&gt;
        &lt;servlet-name&gt;CamelServlet&lt;/servlet-name&gt;
        &lt;servlet-class&gt;org.apache.camel.component.servlet.CamelHttpTransportServlet&lt;/servlet-class&gt;
        &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
    &lt;/servlet&gt;
    &lt;servlet-mapping&gt;
        &lt;servlet-name&gt;CamelServlet&lt;/servlet-name&gt;
        &lt;url-pattern&gt;/http/*&lt;/url-pattern&gt;
    &lt;/servlet-mapping&gt;</code></pre>
</div>
</div></td>
</tr>
<tr class="even">
<td align="left"><strong>Error codes</strong></td>
<td align="left"><p>See <em>ErrorFileUploadEnum</em></p>
<div class="table-wrap">
Error code
Description
E100
it's possible to use HTTP PUT method only
E101
uploaded file exceeded maximum possible size
</div></td>
</tr>
<tr class="odd">
<td align="left"><strong>Prerequisites</strong></td>
<td align="left"><p>File upload uses <em>FileRepository</em> contract and therefore it's necessary to inicialize <em>FileRepository's</em> implementations.</p>
<div class="code panel pdl" style="border-width: 1px;">
<div class="codeContent panelContent pdl">
<pre class="brush: java; gutter: false; theme: Confluence" style="font-size:12px;"><code>&lt;bean id=&quot;fileRepository&quot; class=&quot;com.cleverlance.cleverbus.core.common.file.DefaultFileRepository&quot;/&gt;</code></pre>
</div>
</div>
<p><em>DefaultFileRepository</em> uses <em>dir.temp</em> and <em>dir.fileRepository</em> configuration properties, see [Configuration](Configuration) page for more details.</p></td>
</tr>
<tr class="even">
<td align="left"><strong>Notes</strong></td>
<td align="left"> </td>
</tr>
</tbody>
</table>


