<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String requestUri = (String) (request
			.getAttribute("javax.servlet.forward.request_uri"));
%>
<div class="navbar navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<a data-target=".nav-collapse" data-toggle="collapse"
				class="btn btn-navbar"> <span class="icon-bar"></span> <span
				class="icon-bar"></span> <span class="icon-bar"></span>
			</a> <a href="#" class="brand">KARMA Report</a>
			<div class="nav-collapse">
				<ul class="nav">
					<%
						if (requestUri.endsWith("index.jsp")) {
					%>
					<li class="active">
						<%
							} else {
						%>
					
					<li>
						<%
							}
						%> <a href="${request.contextPath}/index.jsp">Home</a>
					</li>

					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#"> Geographical Report <b
							class="caret"></b>

					</a>
						<ul class="dropdown-menu">
							<li><a href="${request.contextPath}/report/geoZones.html">Geographical
									Zones </a></li>
							<li><a
								href="${request.contextPath}/report/franceGeoZones.html">Geographical
									Zones (France) </a></li>
							<li><a
								href="${request.contextPath}/report/airport.html">Airport </a></li>
							<li><a
								href="${request.contextPath}/report/airportEurope.html">Airport Europe</a></li>
						</ul></li>

				</ul>

			</div>
			<!--/.nav-collapse -->
		</div>
	</div>
</div>