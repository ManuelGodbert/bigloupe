<%@page import="java.util.Collection"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>

<div class="page-header">
	<h1>
		Results <small>0 elements</small>
	</h1>
</div>
<div class="row">${searchException.message}</div>