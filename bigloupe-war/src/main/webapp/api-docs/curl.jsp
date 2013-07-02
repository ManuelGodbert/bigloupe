<!DOCTYPE html>
<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<head>
<title>Bigloupe - Rest API - Chart UI - Examples</title>
<link rel="Shortcut Icon"
	href="/resources/img/bigloupe-elephant-ico.png" />
<link href='http://fonts.googleapis.com/css?family=Droid+Sans:400,700'
	rel='stylesheet' type='text/css' />
<link
	href='${request.contextPath}/resources/css/swagger/hightlight.default.css'
	media='screen' rel='stylesheet' type='text/css' />
<link href='${request.contextPath}/resources/css/swagger/screen.css'
	media='screen' rel='stylesheet' type='text/css' />
<script type="text/javascript"
	src="${request.contextPath}/resources/js/jquery/1.7.1/jquery.min.js"></script>
<script
	src='${request.contextPath}/resources/js/swagger/jquery.slideto.min.js'
	type='text/javascript'></script>
<script
	src='${request.contextPath}/resources/js/swagger/jquery.wiggle.min.js'
	type='text/javascript'></script>
<script
	src='${request.contextPath}/resources/js/swagger/jquery.ba-bbq.min.js'
	type='text/javascript'></script>
<script
	src='${request.contextPath}/resources/js/swagger/handlebars-1.0.rc.1.js'
	type='text/javascript'></script>
<script
	src='${request.contextPath}/resources/js/swagger/underscore-min.js'
	type='text/javascript'></script>
<script
	src='${request.contextPath}/resources/js/swagger/backbone-min.js'
	type='text/javascript'></script>
<script src='${request.contextPath}/resources/js/swagger/swagger.js'
	type='text/javascript'></script>
<script src='${request.contextPath}/resources/js/swagger/swagger-ui.js'
	type='text/javascript'></script>
<script
	src='${request.contextPath}/resources/js/swagger/highlight.7.3.pack.js'
	type='text/javascript'></script>

<style type="text/css">
.swagger-ui-wrap {
	max-width: 960px;
	margin-left: auto;
	margin-right: auto;
}

.icon-btn {
	cursor: pointer;
}

#message-bar {
	min-height: 30px;
	text-align: center;
	padding-top: 10px;
}

.message-success {
	color: #89BF04;
}

.message-fail {
	color: #cc0000;
}

.http_response {
    background-color: #D44836;
    border-radius: 2px 2px 2px 2px;
    color: white;
    display: inline-block;
    font-size: 0.7em;
    padding: 7px 0 4px;
    text-align: center;
    text-decoration: none;
    text-transform: uppercase;
    width: 150px;
}
</style>

</head>
<body>

	<div id='header'>
		<div class="swagger-ui-wrap">
			<a id="logo" href="${request.contextPath}">BigLoupe REST API - Curl examples</a>

		</div>
	</div>

	<div id="message-bar" class="swagger-ui-wrap">&nbsp;</div>

	<div id="swagger-ui-container" class="swagger-ui-wrap">

		<ul id="resources">
		
			<%-- /chart API --%>
			<li id="resource_chart" class="resource active">
				<div class="heading">
					<h2>
						<a href="#!/chart"
							onclick="Docs.toggleEndpointListForResource('chart');">/chart</a>
					</h2>
					<ul class="options">
						<li><a href="#!/chart" id="endpointListTogger_chart"
							onclick="Docs.toggleEndpointListForResource('chart');">Show/Hide</a>
						</li>
						<li><a href="#"
							onclick="Docs.collapseOperationsForResource('chart'); return false;">
								List Operations </a></li>
						<li><a href="#"
							onclick="Docs.expandOperationsForResource('chart'); return false;">
								Expand Operations </a></li>
					</ul>
				</div>
				<ul class="endpoints" id="chart_endpoint_list">

					<li class="endpoint">
						<ul class="operations">
							<li class="get operation">
								<div class="heading">
									<h3>
										<span class="http_method"><a hraf="#">GET</a></span><span class="path">/chart/chart-api/{id}.json</span>
									</h3>
								</div>
								<div class="content">
									curl -X POST { "type" : "xy", "x": 78, "y": 0}</div>
							</li>
						</ul>
						
						
						<ul class="operations">
							<li class="post operation">
								<div class="heading">
									<h3>
										<span class="http_method"><a hraf="#">POST</a></span><span class="path">/chart/chart-api/{id}.json</span>
									</h3>
								</div>
								<div class="content">
									curl -X POST -H "Content-Type: application/json" <%= BigLoupeConfiguration.getConfigurationWebBigLoupeServer() %>/chart/series-api/1/addXY.json -d '{ "x": 78, "y": 0}'</div>
							</li>
						</ul>
						
					</li>

				</ul>
			</li>
			
			<%-- /render Graphite --%>
			<li id="resource_render" class="resource active">
				<div class="heading">
					<h2>
						<a href="#!/render"
							onclick="Docs.toggleEndpointListForResource('render');">/render (compatible with Graphite)</a>
					</h2>
					<ul class="options">
						<li><a href="#!/render" id="endpointListTogger_render"
							onclick="Docs.toggleEndpointListForResource('render');">Show/Hide</a>
						</li>
						<li><a href="#"
							onclick="Docs.collapseOperationsForResource('render'); return false;">
								List Operations </a></li>
						<li><a href="#"
							onclick="Docs.expandOperationsForResource('render'); return false;">
								Expand Operations </a></li>
					</ul>
				</div>
				<ul class="endpoints" id="chart_endpoint_list">

					<li class="endpoint">
						<ul class="operations">
							<li class="get operation">
								<div class="heading">
									<h3>
										<span class="http_method"><a hraf="#">GET</a></span><span class="path">/render (in JSONP)</span>
									</h3>
								</div>
								<div class="content">
									curl "<%= BigLoupeConfiguration.getConfigurationWebBigLoupeServer() %>/render?from=-70560minutes&&target=servers.namenode_8005.heap.HeapMemoryUsage_committed&format=json&jsonp=jQuery171043881340669696267_1362515493369&_=1362515553810"</div>
							</li>
						</ul>
						
						
						<ul class="operations">
							<li class="get operation">
								<div class="heading">
									<h3>
										<span class="http_method"><a hraf="#">GET</a></span><span class="path">/render (in JSON)</span>
									</h3>
								</div>
								<div class="content">
									curl "<%= BigLoupeConfiguration.getConfigurationWebBigLoupeServer() %>/render?from=-70560minutes&&target=servers.namenode_8005.heap.HeapMemoryUsage_committed&format=json"</div>
								<div class="heading">
									<h3>
										<span class="http_response">RESPONSE - JSON</span>
									</h3>
								</div>
								<div class="content">
									<pre class="json">
{
	"target" : "servers.namenode_8005.heap.HeapMemoryUsage_committed,",
	"datapoints" : [[10.0, 1.358283216E9], [11.0, 1.3582
			.358283336E9], [13.0, 1.358283396E9], [14.0, 1.358283456E9], [15.0, 1.358283516E9], [16.0, 1.358283576E9], [17.0, 1.358283636E9], [18.0, 1.358283696E9], [19.0
			[20.0, 1.358283816E9], [21.0, 1.358283876E9], [22.0, 1.358283936E9], [23.0, 1.358283996E9], [24.0, 1.358284056E9], [25.0, 1.358284116E9], [26.0, 1.358284176E9
				236E9], [28.0, 1.358284296E9], [29.0, 1.358284356E9]]
}									
									</pre>
									
								</div>	
							</li>
						</ul>
						
					</li>

				</ul>
			</li>
		</ul>

		<div class="footer">
			<br> <br>
			<h4 style="color: #999">
				[ <span style="font-variant: small-caps">base url</span>:
				http://localhost:9090 , <span style="font-variant: small-caps">api
					version</span>: 0.1 ]
			</h4>
		</div>
	</div>
</body>

</html>