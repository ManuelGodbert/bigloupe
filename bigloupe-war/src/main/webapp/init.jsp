<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<script src="${request.contextPath}/resources/js/bootstrap.min.js"
	type="text/javascript"></script>
<link media="screen" type="text/css"
	href="${request.contextPath}/resources/css/common/bootstrap.min.css"
	rel="stylesheet" />
<link media="screen" type="text/css"
	href="${request.contextPath}/resources/css/common/bootstrap-responsive.min.css"
	rel="stylesheet" />
</head>

<body>
	<header id="overview" class="hero-unit">
		<div class="container">

			<%
				String serverUrl = BigLoupeConfiguration
						.getConfigurationWebBigLoupeServer();
				// Test if configuration already exist
				if (serverUrl == null) {
			%>
			<h2>BigLoupe has not been correctly installed !</h2>
			<p>Check your bigloupe.properties file in WEB-INF/classes</p>
			<%
				} else {
			%>
			<h2>BigLoupe has been correctly installed (<%= serverUrl %>) !</h2>
			<%
				}
			%>
			<p class="lead">
				<a href="http://www.bigloupe.org/tooling/big-loupe-documentation/"
					target="_blank" class="btn btn-warning">Documentation</a>
									<a href="${request.contextPath}/"
					target="_blank" class="btn btn-primary">Start</a>
			</p>
		</div>
	</header>
</body>

</html>
