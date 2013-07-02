<%@page import="org.apache.hadoop.conf.Configuration"%>
<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@page import="org.apache.hadoop.util.StringUtils"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%
	Configuration configurationHadoop = (Configuration) request
								.getAttribute("configurationHadoop");
	if (configurationHadoop == null) {
		configurationHadoop = new Configuration();
	}
	BigLoupeConfiguration configuration = (BigLoupeConfiguration) request
								.getAttribute("configuration");

%>
<div class="row-fluid">
	<div class="page-header span8">
		<h2>
			Change HDFS Configuration&nbsp;&nbsp;<small>stored in directory <a href="#" rel="tooltip" title="<%= configuration.getFullHadoopConfigurationCluster(request) %>"><%= configuration.getHadoopConfigurationCluster(request) %></a></small>
		</h2>
	</div>
</div>

<div class="row-fluid">

	<div class="span8">
		<form class="form-horizontal well" action="${request.contextPath}/saveHdfsConfiguration.html" method="POST">
			<fieldset>
				<legend>HDFS Configuration</legend>
				<div class="control-group">
					<label>fs.default.name</label> <label class="control-label"
						for="fs.default.name">(in core-site.xml)&nbsp;&nbsp;</label><input
						type="text" class="input-xlarge" id="fs.default.name" name="fs.default.name"
						style="height: 30px"
						value="<%=configurationHadoop.get("fs.default.name")%>" />&nbsp;<span class="label" rel="tooltip" title="Hadoop name node">&nbsp;?</a></span>
				</div>
				<div class="control-group"> 
					<label>mapred.job.tracker</label> <label class="control-label"
						for="mapred.job.tracker">(in mapred-site.xml)&nbsp;&nbsp;</label><input
						type="text" class="input-xlarge" id="mapred.job.tracker" name="mapred.job.tracker"
						style="height: 30px"
						value="<%=configurationHadoop.get("mapred.job.tracker")%>" />&nbsp;<span class="label" rel="tooltip" title="JobTracker name node">&nbsp;?</a></span>
				</div>
				<div class="control-group">
					<label>user</label> <label class="control-label"
						for="user">(in user.txt)&nbsp;&nbsp;</label><input
						type="text" class="input-xlarge" id="user" name="user"
						style="height: 30px"
						value="<%=BigLoupeConfiguration.getUser(configuration.getFullHadoopConfigurationCluster(request))%>" />
				</div>
			</fieldset>
			<fieldset>
				<legend>Apache Pig temporary file</legend>
				<div class="control-group">
					<label>Apache Pig needs a temporary directory to store its mapreduce library</label><input
						type="text" class="input-xlarge" id="pigTemp" name="pigTemp"
						style="height: 30px"
						value="<%=BigLoupeConfiguration.getPigTemp(configuration.getFullHadoopConfigurationCluster(request))%>" />
				</div>
				<div class="form-actions">
					<input type="submit" class="btn btn-warning"
						id="saveConfiguration" value="Save configuration" />
				</div>
			</fieldset>
		</form>
	</div>

	<div class="span12">&nbsp;</div>
</div>

