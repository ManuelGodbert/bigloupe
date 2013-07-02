
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

	<div class="row show-grid">
		<div id="inputJson" class="span12">
			<pre><%=output.toString("UTF-8")%></pre>
		</div>
	</div>

