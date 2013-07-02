<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String requestUri = (String) (request
	.getAttribute("javax.servlet.forward.request_uri"));
	if (requestUri == null) requestUri = "";
%>
<div class="navbar navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<a data-target=".nav-collapse" data-toggle="collapse"
				class="btn btn-navbar"> <span class="icon-bar"></span> <span
				class="icon-bar"></span> <span class="icon-bar"></span>
			</a> <a href="#" class="brand"><img src="${request.contextPath}/resources/img/bigloupe-elephant-ico.png" height="20" width="20" alt="BigLoupe"></a>
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

					<%-- Search --%>
					<li>
						<ul class="nav pull-right">
							<li class="dropdown"><a class="dropdown-toggle"
								data-toggle="dropdown" href="#"> Search <b
									class="caret"></b>

							</a>
								<ul class="dropdown-menu">
									<%
										if (requestUri.endsWith("/search/index.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									
									<li>
										<%
											}
										%> <a href="${request.contextPath}/search/index.html">Search engine</a>
									</li>
									<%
										if (requestUri.contains("hdfsBrowser")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									
									<li>
										<%
											}
										%> <c:choose>
											<c:when test="${sessionScope.clusterHadoop != null}">
												<a
													href="${request.contextPath}/${sessionScope.clusterHadoop}/hdfsBrowser.html">HDFS
													Browser</a>
											</c:when>
											<c:otherwise>
												<a href="${request.contextPath}/hdfsBrowser.html">HDFS
													Browser</a>
											</c:otherwise>
										</c:choose>
									</li>

								</ul>
						</ul>
					</li>
					<%-- end search --%>
					

					<%-- Jobs --%>
					<li>
						<ul class="nav pull-right">
							<li class="dropdown"><a class="dropdown-toggle"
								data-toggle="dropdown" href="#"> Jobs <b class="caret"></b>

							</a>
								<ul class="dropdown-menu">
									<%
										if (requestUri.endsWith("pig.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									
									<li>
										<%
											}
										%> <a href="${request.contextPath}/pig.html">Pig Launcher</a>
									</li>
									<%
										if (requestUri.endsWith("pigConsole.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									
									<li>
										<%
											}
										%> <a href="${request.contextPath}/pigConsole.html">Pig
											Console</a>
									</li>
									<%
										if (requestUri.contains("map-reduce-statistics")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									
									<li>
										<%
											}
										%> <a href="${request.contextPath}/map-reduce-statistics/index.html">Pig
											Monitoring</a>
									</li>
									<li class="divider"></li>
									<%
										if (requestUri.endsWith("sqoop.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									
									<li>
										<%
											}
										%> <a href="${request.contextPath}/sqoop.html">Sqoop Launcher</a>
									</li>
									<%
										if (requestUri.endsWith("indexFile.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									<li>
										<%
											}
										%> <a href="${request.contextPath}/indexFile.html">Index Avro files</a>
									</li>
									
									<%
										if (requestUri.endsWith("hdfsAvroSchemaDiff.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									
									<li>
										<%
											}
										%> <a href="${request.contextPath}/hdfsAvroSchemaDiff.html">Avro schema comparaison</a>
									</li>
								</ul>
						</ul>
					</li>
					<%-- end jobs --%>

					<%-- Scheduler --%>
					<li>
						<ul class="nav pull-right">
							<li class="dropdown"><a class="dropdown-toggle"
								data-toggle="dropdown" href="#"> Scheduler <b class="caret"></b>

							</a>
								<ul class="dropdown-menu">
									<%
										if (requestUri.endsWith("scheduler/index.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									
									<li>
										<%
											}
										%> <a href="${request.contextPath}/scheduler/index.html">Scheduler</a>
									</li>
									<%
										if (requestUri.endsWith("scheduler/createJob.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									
									<li>
										<%
											}
										%> <a href="${request.contextPath}/scheduler/createJob.html">Create Job</a>
									</li>
									<%
										if (requestUri.endsWith("scheduler/uploadJobs.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									
									<li>
										<%
											}
										%> <a href="${request.contextPath}/scheduler/uploadJobs.html">Upload Jobs</a>
									</li>
									<%
										if (requestUri.endsWith("scheduler/history.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									<li>
										<%
											}
										%> <a href="${request.contextPath}/scheduler/history.html">History</a>
									</li>
									<%
										if (requestUri.endsWith("jobtracker/history.html")) {
									%>
									<li class="active">
										<%
											} else {
										%>
									<li>
										<%
											}
										%> <a href="${request.contextPath}/jobtracker/history.html">Job Tracker</a>
									</li>
								</ul>
						</ul>
					</li>
					<%-- end scheduler --%>

					<%
						if (requestUri.endsWith("configuration.html")) {
					%>

					<li class="active">
						<%
							} else {
						%>
					
					<li>
						<%
							}
						%> <a href="${request.contextPath}/configuration.html">
							Configuration</a>
					</li>
					<%
						if (requestUri.endsWith("report/airportEurope.html")) {
					%>
					<li class="active">
						<%
							} else {
						%>
					
					<li>
						<%
							}
						%> <a href="${request.contextPath}/report/airportEurope.html">Report</a>
					</li>
					<%
						if (requestUri.contains("doc")) {
					%>
					<li class="active">
						<%
							} else {
						%>
					
					<li>
						<%
							}
						%> <a href="${request.contextPath}/doc/index.htm">Documentation</a>
					</li>
					<%
						if (requestUri.endsWith("contact.html")) {
					%>
					<li class="active">
						<%
							} else {
						%>
					
					<li>
						<%
							}
						%> <a href="${request.contextPath}/contact.html">Contact</a>
					</li>
					<li class="divider-vertical"></li>

				</ul>
				<ul class="nav pull-right">
					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#"> Hadoop Cluster <b
							class="caret"></b>

					</a>
						<ul class="dropdown-menu">
							<c:forEach var="cluster"
								items="${configurationHadoopKarmaCluster}">
								<li><a
									href="${request.contextPath}/changeHdfsConfiguration/${cluster.key}.html">${cluster.value}&nbsp;
										<%
											Entry cluster = (Entry)pageContext.findAttribute("cluster");	
											String hadoopConfigurationCluster = ((BigLoupeConfiguration)pageContext.findAttribute("configuration")).getCurrentHadoopCluster(request);
											if (cluster.getKey().equals(hadoopConfigurationCluster)) {
										%>
											<i class="icon-ok-circle"></i>
										<% } %>
								</a></li>
							</c:forEach>
						</ul>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>
</div>