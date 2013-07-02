<%@page import="java.util.List"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<html>
<head>
<title>KPI Server</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Data visualization and monitoring">

<link rel="Shortcut Icon" href="${request.contextPath}/resources/img/favicon.ico" />
<script type="text/javascript"
	src="${request.contextPath}/resources/js/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript"
	src="${request.contextPath}/resources/js/jqueryui/1.8.17/jquery-ui.min.js"></script>

<script type="text/javascript"
	src="${request.contextPath}/resources/js/bootstrap.min.js"></script>
<link rel="stylesheet"
	href="${request.contextPath}/resources/css/common/bootstrap.min.css"
	type="text/css" media="screen" />
<link rel="stylesheet"
	href="${request.contextPath}/resources/css/common/bootstrap-responsive.min.css"
	type="text/css" media="screen" />
<link rel="stylesheet"
	href="${request.contextPath}/resources/css/default/jquery-ui-1.8.17.custom.css"
	type="text/css" />

<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->


<tiles:insertAttribute name="additionalJavascript" ignore="true" />
<tiles:useAttribute id="javascriptList" name="additionalJavascript"
	classname="java.util.List" />
<c:forEach var="javascript" items="${javascriptList}">
	<tiles:insertAttribute value="${javascript}" />
</c:forEach>
<style>
.ui-autocomplete-loading {
	background: white url('${request.contextPath}/resources/img/loader.gif')
		right center no-repeat;
}

.ui-autocomplete {
	max-height: 100px;
	overflow-y: auto;
	/* prevent horizontal scrollbar */
	overflow-x: hidden;
	/* add padding to account for vertical scrollbar */
	padding-right: 20px;
}
/* IE 6 doesn't support max-height
	 * we use height instead, but this forces the menu to always be this tall
	 */
* html .ui-autocomplete {
	height: 100px;
}
</style>
</head>

<body data-target=".bs-docs-sidebar" data-spy="scroll" data-twttr-rendered="true" style="margin-left: 20px">

	<tiles:insertAttribute name="menu" />
	<p>
		<br />
	</p>
	
	<div class="container">
		<% 
		List<String> messages = (List<String>)request.getAttribute("messages"); 
		if (messages != null) { %>
		<p></p>
		<div id="messages" class="row alert">
			<% for (String message : messages) { %>
			<%= message %>
			<% } %>
		</div>
		<% } %>
		<tiles:insertAttribute name="body" />
	</div>
</body>

</html>