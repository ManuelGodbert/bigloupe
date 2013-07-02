<%@page import="org.bigloupe.web.scheduler.job.builtin.IndexFileProcessJob"%>
<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<script type="text/javascript">
	$(document).ready(function() {

	});

</script>

<div class="row">
	<div class="page-header">
		<h2>Index a file in ElasticSearch</h2>
	</div>
</div>

<div class="row-fluid">

	<div class="well">
		<form id="formIndexFile" method="POST" action="${request.contextPath}/indexFileSaveJob.html">
			<legend>Job Configuration to index avro files</legend>
			<div class="row-fluid">
				<div class="span2">
					<label>Job name</label>
				</div>
				<div class="span10">
					<input type="text" readonly="readonly" name="jobName" class="input-xlarge"
						value="index_generic_avro_file_with_elasticsearch" style="height: 30px" />
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<label>Job path</label>
				</div>
				<div class="span10">
					<input type="text" readonly="readonly" name="jobPath" value="index_avro_file"
						class="input-xlarge" style="height: 30px" />
				</div>
			</div>
			<div class="row-fluid">
				<div class="span2">
					<label>Job type</label>
				</div>
				<div class="span10">
					<input type="text" readonly="readonly" name="jobType" value="indexfile"
						class="input-xlarge" style="height: 30px" />
				</div>
			</div>

			<div class="row-fluid">
				<div class="span2">
					<label>Elastic search installation directory</label>
				</div>
				<div class="span10">
					<input type="text" readonly="readonly" name="<%= IndexFileProcessJob.ELASTICSEARCH_DIRECTORY %>" value="<%= BigLoupeConfiguration.getConfigurationElasticSearchDirectory() %>"
						class="input-xlarge" style="height: 30px" />
				</div>
			</div>


			<div class="row-fluid">
				<div class="span2">
					<label>Avro file</label>
				</div>
				<div class="span10">
					<input type="text" name="<%= IndexFileProcessJob.FILE_TO_INDEX %>" value="${indexFileJob.fileToIndex}"
						class="input-xlarge" style="height: 30px" />
				</div>
			</div>

			<div class="row-fluid">
				<button id="save" class="btn btn-primary btn-large span4">Save configuration</button>
				<c:if test="${not empty jobDescriptor.id}">
				<a
					href="${request.contextPath}/scheduler/jobDetail.html?id=${jobDescriptor.id}" class="btn btn-info span4">See Job Definition</a>
				</c:if>
			</div>

		</form>
	</div>
</div>


