<%@page import="java.io.IOException"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="org.apache.commons.io.LineIterator"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>



<div class="row">
	<div class="page-header">
		<h2>
			File viewer&nbsp;<small>${fileToView.name}</small>
		</h2>
	</div>
</div>

<p>
<pre class="prettyprint linenums">
	<ol class="linenums">
	<%
		File fileToView = (File) request.getAttribute("fileToView");
		LineIterator lineIterator = FileUtils.lineIterator(fileToView);
		int i = 0;
		try {
			while (lineIterator.hasNext()) {
				%>
				
					<li class="L<%=i%>"><span class="pln"><%= lineIterator.next() %></span></li>
				<%
		}
		} catch (IOException ex) {
		%> <%=ex.getMessage()%> <%
 		} finally {
 			LineIterator.closeQuietly(lineIterator);
 		}
 %>
	</ol>
</pre>
</p>




