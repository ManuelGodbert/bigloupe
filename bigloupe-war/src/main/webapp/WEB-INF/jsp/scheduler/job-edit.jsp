<%@page import="org.bigloupe.web.dto.PairValue"%>
<%@page import="org.bigloupe.web.util.Props"%>
<%@page import="org.bigloupe.web.scheduler.JobDescriptor"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<style type="text/css">
.jobtable td {
	border: none
}
</style>
<script type="text/javascript">
	var data = ${jsonData};
	var rownum = 0;

	$(function() {
		var params = data;

		for ( var key in params) {
			var value = params[key];
			addNewRow(key, value);
		}

		addNewRow("", "");
	});

	function addNewRow(key, value) {
		var tbody = document.getElementById("data_rows");
		var tr = document.createElement("tr");
		tr.setAttribute("id", "row" + rownum);

		var tdKey = document.createElement("td");
		var keyInput = document.createElement("input");
		keyInput.setAttribute("type", "text");
		keyInput.setAttribute("name", "key" + rownum);
		keyInput.setAttribute("class", "key-input span6");
		keyInput.setAttribute("style", "height : 24px");
		$(keyInput).val(key);
		tdKey.appendChild(keyInput);

		var tdValue = document.createElement("td");
		var valInput = document.createElement("input");
		valInput.setAttribute("type", "text");
		valInput.setAttribute("name", "val" + rownum);
		valInput.setAttribute("class", "val-input span6");
		valInput.setAttribute("style", "height : 30px");
		valInput.setAttribute("size", 50);
		$(valInput).val(value);
		tdValue.appendChild(valInput);

		tr.appendChild(tdKey);
		tr.appendChild(tdValue);

		tbody.appendChild(tr);
		rownum++;
	}

	function addRow() {
		addNewRow("", "");
	}

	$(document).ready(function() {
		$("#add_row_button").click(addRow);
	});
</script>
<div class="row">
	<div class="page-header">
		<h2>Edit Job</h2>
	</div>
</div>

<div class="row">
	<div id="variableWidth">
		<form name="props_form" method="post" action="${request.contextPath}/scheduler/saveJob.html" class="well form-horizontal">
			<div class="box">
				<h3>Job Description</h3>
				<div class="jobdesc">
					<div>
						<span class="span2">Job Name</span> <input id="jobname"
							name="jobName" type="text" value="${job.id}" style="height : 24px" class="span8"/><br />
					</div>
					<div>
						<span class="span2">Type</span>
							<select id="jobType" name="jobType">
							<option value="noop">--- Select ---</option>
							<%
										JobDescriptor jobDescriptor = (JobDescriptor) pageContext.findAttribute("job");
							%>											
							<c:forEach var="jobTypeItem" items="${jobTypeList}">
								<option name="${jobTypeItem}" value="${jobTypeItem}" 							
										<%	
										String jobTypeItem = (String)pageContext.findAttribute("jobTypeItem");
										if (jobDescriptor != null) {
											Props props = jobDescriptor.getProps();
											String jobType = props.get("type");
										    if ((jobType != null) && (jobType.equals(jobTypeItem))) {
										%>
										selected="selected"
										<% }} %>
								><%= jobTypeItem.toUpperCase() %></option>
							</c:forEach>
							</select>						
					</div>
					<br />
					<div>
						<span class="span2">Path</span> <input id="editPath"
							name="jobPath" type="text" value="${job.path}" style="height : 24px" class="span8"/>
					</div>
				</div>
			</div>
			<div class="box">
				<h3>Job Parameters</h3>
				<table id="jobtable" class="table table-striped">
					<tr>
						<th>Key</th>
						<th>Value</th>
					</tr>
					<tbody id="data_rows">
					</tbody>

				</table>

				<table class="jobtable">
					<tr>
						<td style="text-align: right"><input id="add_row_button"
							type="button" value="Add Row"> <input id="save_button"
								type="submit" value="Save"></td>
					</tr>
				</table>
			</div>
		</form>
	</div>
</div>
