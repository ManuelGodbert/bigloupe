<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<style type="text/css">
/* Sidenav for Docs
-------------------------------------------------- */

.bs-docs-sidenav {
  width: 228px;
  margin: 30px 0 0;
  padding: 0;
  background-color: #fff;
  -webkit-border-radius: 6px;
     -moz-border-radius: 6px;
          border-radius: 6px;
  -webkit-box-shadow: 0 1px 4px rgba(0,0,0,.065);
     -moz-box-shadow: 0 1px 4px rgba(0,0,0,.065);
          box-shadow: 0 1px 4px rgba(0,0,0,.065);
}
.bs-docs-sidenav > li > a {
  display: block;
  *width: 190px;
  margin: 0 0 -1px;
  padding: 8px 14px;
  border: 1px solid #e5e5e5;
}
.bs-docs-sidenav > li:first-child > a {
  -webkit-border-radius: 6px 6px 0 0;
     -moz-border-radius: 6px 6px 0 0;
          border-radius: 6px 6px 0 0;
}
.bs-docs-sidenav > li:last-child > a {
  -webkit-border-radius: 0 0 6px 6px;
     -moz-border-radius: 0 0 6px 6px;
          border-radius: 0 0 6px 6px;
}
.bs-docs-sidenav > .active > a {
  position: relative;
  z-index: 2;
  padding: 9px 15px;
  border: 0;
  text-shadow: 0 1px 0 rgba(0,0,0,.15);
  -webkit-box-shadow: inset 1px 0 0 rgba(0,0,0,.1), inset -1px 0 0 rgba(0,0,0,.1);
     -moz-box-shadow: inset 1px 0 0 rgba(0,0,0,.1), inset -1px 0 0 rgba(0,0,0,.1);
          box-shadow: inset 1px 0 0 rgba(0,0,0,.1), inset -1px 0 0 rgba(0,0,0,.1);
}
/* Chevrons */
.bs-docs-sidenav .icon-chevron-right {
  float: right;
  margin-top: -25px;
  margin-right: 6px;
  opacity: .25;
}
.bs-docs-sidenav > li > a:hover {
  background-color: #f5f5f5;
}
.bs-docs-sidenav a:hover .icon-chevron-right {
  opacity: .5;
}
.bs-docs-sidenav .active .icon-chevron-right,
.bs-docs-sidenav .active a:hover .icon-chevron-right {
  background-image: url(${request.contextPath}/resources/img/glyphicons-halflings-white.png);
  opacity: 1;
}
.bs-docs-sidenav.affix {
  top: 40px;
}
.bs-docs-sidenav.affix-bottom {
  position: absolute;
  top: auto;
  bottom: 270px;
}



</style>
<script type="text/javascript">
	$(document).ready(function() {
		$('#hadoopConfigurationCluster').change(function() {
			$.ajax({
				type : 'POST',
				url : '${request.contextPath}/hdfsConfiguration.html',
				dataType : 'html',
				data : $(this).serialize(),
				success : function(html, textStatus) {
					$('#hdfs').html(html);
				},
				error : function(xhr, textStatus, errorThrown) {

				}
			});
		});
		// Manage checkbox useAmbroseInBigLoupe
		if ($("#useAmbroseInBigLoupe").change(function() {
				// checkbox is checked 
				$.ajax({
					type : 'POST',
					url : '${request.contextPath}/changeUseAmbroseInBigLoupe.html',
					dataType : 'json',
					data : $(this).serialize(),
					success : function(json, textStatus) {
						$('#responseStatusChangeUseAmbroseInBigLoupe').html(json.message[0]);
					},
					error : function(xhr, textStatus, errorThrown) {
					}
				});
		}));
			

		$.ajax({
			type : 'POST',
			url : '${request.contextPath}/hdfsConfiguration.html',
			dataType : 'html',
			data : $(this).serialize(),
			success : function(html, textStatus) {
				$('#hdfs').html(html);
			},
			error : function(xhr, textStatus, errorThrown) {

			}
		});
		
		$('#hadoopConfiguration  a').click(function (e) {
			e.preventDefault();
			$(this).tab('show');
		})
	});
</script>
<div class="row">
	<div class="page-header">
		<h2>Configuration</h2>
	</div>
</div>
<div class="container">
	<div class="row">
		<div class="span3 bs-docs-sidebar">
			<ul class="nav nav-list bs-docs-sidenav">
				<li><a href="#elasticSearch">Elastic Search</a><i
					class="icon-chevron-right"></i></li>
				<li><a href="#database">Database configuration</a><i
					class="icon-chevron-right"></i></li>
				<li><a href="#restapi">REST API</a><i
					class="icon-chevron-right"></i></li>
				<li><a href="#hadoop">Hadoop configuration</a><i
					class="icon-chevron-right"></i></li>
				<li><a href="#bigloupe">BigLoupe configuration</a><i
					class="icon-chevron-right"></i></li>
			</ul>
		</div>

		<div class="span9">
			<section id="elasticSearch">
				<h4>Elasticsearch</h4>
				<table class="table table-bordered table-striped">
				<thead><tr><th>Elasticsearch server</th><th>Elasticsearch administration</th></tr></thead>
				<tr>
				<td><a href="<%=BigLoupeConfiguration.getConfigurationWebElasticSearchServer()%>"><%=BigLoupeConfiguration.getConfigurationWebElasticSearchServer()%></a></td>
				<td><a href="<%=BigLoupeConfiguration.getConfigurationWebElasticSearchServer()%>/_plugin/head/">Web interface</a></td>
				</tr>
				</table>
			</section>


			<%
				BigLoupeConfiguration configuration = (BigLoupeConfiguration) request
								.getAttribute("configuration");
			%>
			<section id="serverFileManager">
				<h4>Web Server file Manager</h4>
				<a href="${request.contextPath}/filemanager.html"><i class="icon-folder-open"></i></a>
			</section>			


			<section id="database">
				<h4>Database Information</h4>
				<table class="table table-bordered table-striped">
				<thead><tr><th>Database URL</th><th>Space</th></tr></thead>
				<tr>
				<td>${dataSource.url}</td>
				<td><a
					href="${request.contextPath}/databaseInformation.html">Table
					space</a></td>
				</tr>
				</table>
			</section>

			<section id="restapi">
				<h4>REST Api</h4>
				<p>
				BigLoupe supports HTTP Request in JSON with GET,PUT, DELETE methods.
				</p>
				<table class="table table-bordered table-striped">
				<thead><tr><th>Endpoints</th><th>Chart API</th></tr></thead>
				<tr>
				<td><a href="${request.contextPath}/endpoint.html">Endpoints</a></td>
				<td><a href="${request.contextPath}/api-docs/index.jsp">Rest API</a>  - <a href="${request.contextPath}/api-docs/curl.jsp">HTTP request examples</a></td>
				</tr>
				</table>
			</section>
			
			<section id="hadoop">
				<h4>Hadoop Configuration</h4>

				<ul class="nav nav-tabs" id="hadoopConfiguration">
					<li class="active"><a href="#hdfsConfiguration">Name Node Configuration</a></li>
					<li><a href="#changeparameters">Change parameters</a></li>
					<li><a href="#diskusage">Disk Usage</a></li>
					<li><a href="#ambrose">Ambrose</a></li>
				</ul>

				<div class="tab-content">
				<div class="tab-pane active" id="hdfsConfiguration">
					<div id="hdfs"></div>
				</div>
				<div class="tab-pane" id="changeparameters">
					<p>By default BigLoupe uses XML configuration files in hdfs/cluster/<%=configuration.getCurrentHadoopCluster(request)%> directory where current hadoop
					You can change value of several parameters with this interface.
					</p>
					
					<a href="${request.contextPath}/editHdfsConfiguration/<%= configuration.getCurrentHadoopCluster(request) %>.html"><%=configuration.getCurrentHadoopCluster(request)%></a>
					<p>&nbsp;</p>			
					<p>&nbsp;</p>
				</div>
				<div class="tab-pane" id="diskusage">
					<p>HDFS-DU is an interactive visualization of the Hadoop distributed file system. 
					The project aims to monitor different snapshots for the entire HDFS system in an interactive way, showing the size of the folders, 
					the rate at which the size increases / decreases, and to highlight inefficient file storage.</p>
					<a href="${request.contextPath}/diskUsageHdfs/<%= configuration.getCurrentHadoopCluster(request) %>.html">Disk usage</a>
					<p>&nbsp;</p>			
					<p>&nbsp;</p>
				</div>
				<div class="tab-pane" id="ambrose">
					<p>Acivate or Desactivate Twitter Ambrose, a tool which helps authors of large-scale data workflows keep track of the overall status of a workflow and visualize its progress.</p>
					<h4>Use Ambrose to have chord diagram during Pig execution</h4>
					<input id="useAmbroseInBigLoupe" type="checkbox" name="useAmbroseInBigLoupe" <% if (BigLoupeConfiguration.isUseAmbroseInBigLoupe()) { %> checked <% } %>"/>
					<span id="responseStatusChangeUseAmbroseInBigLoupe"></span>	
					<p>&nbsp;</p>			
					<p>&nbsp;</p>
				</div>
				</div>
			</section>
			
			<section id="bigloupe">
				<h4>BigLoupe Configuration</h4>
				<p>
				Run on system : <%= System.getProperty("os.name") + "-" + System.getProperty("os.arch") + "-" + System.getProperty("sun.arch.data.model") %>
	    		</p>
			</section>
			
		</div>
	</div>
</div>
		
		