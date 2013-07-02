<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<link rel="stylesheet" type="text/css" href="${request.contextPath}/resources/css/tango/tango_icons_32x32.css" media="all" />
<script type="text/javascript">
	$(document).ready(
			$.ajax({
				url : '${request.contextPath}/jobtracker/jobs.json',
				dataType : 'json',
				success : function(data) {
					$.each(data, function(i, row) {
						var part1 = $('<tr>').attr('id', i);
						part1.append($('<td>').html('<a href="http://' + '${jobTracker.jobTrackerWebPort}' 
							+ '/jobdetails.jsp?jobid=' + row.jobId + '">' + row.jobId + '</a>'));
						
						part1.append($('<td>').text(row.username));
						if (row.runState == 0) 
							part1.append($('<td>').text('UNKNOWN'));
						 else if (row.runState == 1)
							 part1.append($('<td>').text('RUNNING'));
						 else if (row.runState == 2)
							 part1.append($('<td>').text('SUCCEEDED'));
						 else if (row.runState == 3)
							 part1.append($('<td>').text('FAILED'));
						 else if (row.runState == 4)
							 part1.append($('<td>').text('PREP'));
						 else if (row.runState == 5)
							 part1.append($('<td>').text('KILLED'));						
						 else
							 part1.append($('<td>').html('&nbsp;'));
						if (row.jobComplete) {
							part1.append($('<td>').html('<span class="tango face-smile">&nbsp;</span>&nbsp;'));
						}
						else {
							part1.append($('<td>').html('<span class="tango face-crying">&nbsp;</span>&nbsp;'));
						} 
						part1.appendTo($('#tbody'));
					});
				}
			})

	);
</script>
<div class="row">
	<div class="page-header">
		<h2>JobTracker Information ${jobTracker.jobTrackerServiceName}</h2>
	</div>
</div>
<div class="container">
	<div class="row">
		<table id="jobtable" class="table table-bordered table-striped">
			<thead>
				<tr>
					<th>Job ID</th>
					<th>Username</th>
					<th>State</th>
					<th>Job Complete</th>
				</tr>
			</thead>
			<tbody id="tbody">

			</tbody>
		</table>
	</div>
</div>

