
<%@page import="java.nio.charset.Charset"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="org.codehaus.jackson.JsonEncoding"%>
<%@page import="org.codehaus.jackson.JsonFactory"%>
<%@page import="org.codehaus.jackson.JsonGenerator"%>
<%
	ByteArrayOutputStream output = (ByteArrayOutputStream) request
			.getAttribute("output");
%>
<div class="row">
	<div class="page-header span8">
		<h2>
			File &nbsp;&nbsp;<small>${path} (in cluster ${cluster})</small>
		</h2>
	</div>
</div>

<div class="row">
	<code>
	<%= output.toString() %>
	</code>
	<div class="span8"></div>
	<div class="span12"></div>
	<div class="span12">&nbsp;</div>
</div>

