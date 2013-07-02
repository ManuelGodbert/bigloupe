<%@page import="java.io.PrintWriter"%>
<%@ page isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<link rel="stylesheet"
	href="${request.contextPath}/resources/css/common/bootstrap.min.css"
	type="text/css" media="screen" />
<link rel="stylesheet"
	href="${request.contextPath}/resources/css/default/jquery-ui-1.8.17.custom.css"
	type="text/css" />	
<script type="text/javascript"
	src="${request.contextPath}/resources/js/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript"
	src="${request.contextPath}/resources/js/jqueryui/1.8.17/jquery-ui.min.js"></script>
<script>
	$(function() {
		$("#accordion").accordion({
			autoHeight : false
		});
	});
</script>
</head>
<body>
<p>
<div id="accordion">

	<%
		if (exception instanceof Throwable) {
			Throwable throwableException = (Throwable) exception;
	%>
	<h3>
		<a href="#">Error ${pageContext.errorData.statusCode}, URI : ${pageContext.errorData.requestURI}</a>
	</h3>
	<div>
		<% exception.printStackTrace(new PrintWriter(out)); %>
	</div>
	<% while (throwableException.getCause() != null) {%>
	<h3>
		<a href="#">Root cause </a>
	</h3>
	<div>
		<%= throwableException.getCause() %>
		<% throwableException.printStackTrace(new PrintWriter(out));
		   // For server side
		   throwableException.printStackTrace();
		   throwableException = throwableException.getCause();
		%>
	</div>
	<% } %>

	<%
		} else {
	%>
	<h3>
		<a href="#">Error ${pageContext.errorData.statusCode}, URI : ${pageContext.errorData.requestURI}</a>
	</h3>
	<div>
		<%= exception %>
		<%= exception.getStackTrace() %>
	</div>
	<%
		}
	%>
</div>
</p>
</body>
</html>
