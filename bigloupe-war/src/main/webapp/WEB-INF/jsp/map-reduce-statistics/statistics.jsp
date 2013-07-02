<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="row">
	<div class="page-header">
		<div class="row-fluid">
			<div class="span6">
				<h2>Map Reduce Monitoring</h2>
			</div>
			<div class="span">
				<a href="${request.contextPath}/map-reduce-statistics/index.html"
					class="btn btn-primary small">Back to the list</a>
			</div>
		</div>

	</div>
</div>

<c:if test="${empty param.workflowId}">
	<table class="table">
		<thead>
			<tr>
				<th>Workflow</th>
				<th>Action</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="workflow" items="${workflows}">
				<tr>
					<td><a
						href="${request.contextPath}/map-reduce-statistics/index.html?workflowId=${workflow}">${workflow}</a></td>
					<td><a
						href="${request.contextPath}/map-reduce-statistics/delete.html?workflowId=${workflow}"
						class="btn btn-primary">Delete</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>

<c:if test="${not empty param.workflowId}">
	<div class="container-fluid">
		<!-- status line -->
		<div class="row-fluid">
			<div class="span12">
				<p>
					Status: <span id="scriptStatusDialog"></span>
				</p>
			</div>
		</div>

		<!-- workflow progress bar -->
		<div class="row-fluid">
			<div class="span12">
				<div id="progressbar" class="progress">
					<div class="bar"></div>
				</div>
			</div>
		</div>

		<div class="row-fluid">
			<!-- chart tabs -->
			<div class="span12 tabbable">
				<ul class="nav nav-tabs" id="vizTabs"></ul>
				<div class="tab-content" id="vizGroup"></div>
			</div>
		</div>

		<!-- summary job table -->
		<div class="row-fluid">
			<div class="span12">
				<table id="job-summary" class="table table-striped">
					<thead></thead>
					<tbody></tbody>
				</table>
			</div>
		</div>

	</div>

	<script data-main="${request.contextPath}/resources/js/ambrose/main.js"
		src="${request.contextPath}/resources/js/require-js/require-jquery.js"></script>

	<!-- /container-fluid -->
</c:if>



