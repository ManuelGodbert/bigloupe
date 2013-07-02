<%@page import="java.util.List"%>
<%@page import="org.bigloupe.web.util.GuiUtils"%>
<%@page import="org.bigloupe.web.scheduler.job.JobExecution"%>
<%@page import="org.bigloupe.web.scheduler.JobDescriptor"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://bigloupe.org/functions" prefix="bigloupe" %>
<script type="text/javascript"
	src="${request.contextPath}/resources/js/atmosphere/jquery.atmosphere.js"></script>
<script type="text/javascript">
		function showAllProps()
		{
		    document.getElementById('all_props').style.display = 'block';
		    document.getElementById('all_props_link').style.display = 'none';
		}
      
		$(function () {
			<%-- Select Tab if url with &logs : e.g. http://localhost:9090/scheduler/jobDetail.html?id=YO_DFBS&logs --%>
			<c:choose>
			<c:when test='${tab == "logs"}'> 
				$( "#tabs" ).tabs({ selected: 1 });
			</c:when>
			<c:otherwise>
				$( "#tabs" ).tabs();
			</c:otherwise>
			</c:choose>
			
			<%-- Refresh logs --%>
			var socket = $.atmosphere;
			var subSocket;

			function subscribe() {
				
				var request = {
					url : 'http://' + document.location.host
							+ '${request.contextPath}/pubsub/jobDetailLog.html',
					logLevel : 'debug',
					contentType : 'application/json',
					transport : 'long-polling'
				};

				request.onMessage = function(response) {
					var message = response.responseBody;
					//console.log('Message : ', message);
					if (response.status == 200) {
						var data = response.responseBody;
						if (data.length > 0) {
							$('#jobHistory > tbody > tr:first').after(data).focus();
						}
					}
				};

				subSocket = socket.subscribe(request);
			}

			function connect() {
				socket.unsubscribe();
				subscribe();
			}

			connect();
			
		});
</script>
<%-- ######################################### Google Charts runtime visualization #########################################--%>
<c:if test="${not empty successful_executions}">
	<script type='text/javascript' src='http://www.google.com/jsapi'></script>
	<script type='text/javascript'>
        google.load("visualization", "1", {packages:["linechart"]});
        google.setOnLoadCallback(drawChart);
        function drawChart() {
          var data = new google.visualization.DataTable();
          data.addColumn('date', 'Date');
          data.addColumn('number', 'Run Time (Minutes)');
          data.addRows(${successful_executions});
          <%List<JobExecution> executions = (List<JobExecution>) request
						.getAttribute("executions");
				List<JobExecution> executionsReversed = GuiUtils
						.reversed(executions);
				int row = 0;
				for (JobExecution executionReversed : executionsReversed) {
					if ((executionReversed != null)
							&& (executionReversed.isSucceeded())) {%>
              data.setValue(<%=row%>, 0, new Date(<%=executionReversed.getStarted().getYear()%>, <%=executionReversed.getStarted()
								.getMonthOfYear() - 1%>, <%=executionReversed.getStarted()
								.getDayOfMonth()%>));
              data.setValue(<%=row%>, 1, <%=GuiUtils.durationInMinutes(
								executionReversed.getStarted(),
								executionReversed.getEnded())%>);
          		<%row++;
					} 
				} %>  
          var chart = new google.visualization.LineChart(document.getElementById('runtime-chart'));
          chart.draw(data, {width: 500, height: 300, legend: 'bottom', title: 'Job Run Time', min:0});
         }
         
      </script>
</c:if>

<script type='text/javascript'>
	     var data = ${jsonData};
         $(function () {
         	$('#editbutton').button();
         	$('#runbutton').button();
         	$('#rundepbutton').button();
         
			var params = data;
			
			var table = document.getElementById("params");
			for (var key in params) {
				var value = params[key];
				var tr = document.createElement("tr");
				var tdKey = document.createElement("td"); 
				var tdVal = document.createElement("td");
				if (key == "pig.script") {
					$(tdKey).html(key + "<span id='pigScriptlet' value='" + value + "'/>");
					$(tdVal).html(value + '&nbsp;<a class="btn btn-info" href="javascript:editPigScriplet();">Edit</a>&nbsp;<a class="btn btn-info" href="javascript:explainPigScriplet();">Explain</a>');
					
				}
				else {
					$(tdKey).text(key);
					$(tdVal).text(value);
				}
				
				tr.appendChild(tdKey);
				tr.appendChild(tdVal);
				table.appendChild(tr);
			}
		});
         
     	function editPigScriplet() {
     		var scriptlet = $('#pigScriptlet').attr('value');
   			var editorWindow = window.open('${request.contextPath}/editPigInJob/' + scriptlet + '.html?path=${bigloupe:urlEncode(job.path)}', 'pigInJobEditor');
    		if (window.focus) { 
    				editorWindow.focus();
    		}
    	}
     	
     	function explainPigScriplet() {
     		var scriptlet = $('#pigScriptlet').attr('value');
   			var editorWindow = window.open('${request.contextPath}/explainPigInJob.html?id=${job.id}', 'pigExplain');
    		if (window.focus) { 
    				editorWindow.focus();
    		}
    	}
    
    </script>

<div class="row">
	<div class="page-header">
		<h2>
			Job Details<small>&nbsp;${job.id}</small>
		</h2>
	</div>
</div>

<div class="row">
	<div id="variableWidth">
		
		<div id="buttonbar" style="text-align: center">
			<div id="editbutton" class="btn btn-primary btn-large">
				<c:choose>
				<c:when test='${job.jobType == "sqoop"}'>
				<a href="${request.contextPath}/sqoop.html?job_id=${job.id}">Edit</a>
				</c:when>
				<c:otherwise>
				<a href="${request.contextPath}/scheduler/editJob.html?id=${job.id}">Edit</a>
				</c:otherwise>
				</c:choose>
			</div>
			<div id="runbutton" class="btn btn-info btn-large"
				onclick="runJob('${job.id}', false, '$context')"
				style="text-align: center">
				<a href="#">Run</a>
			</div>
			<div id="rundepbutton" class="btn btn-success btn-large"
				onclick="runJob('${job.id}', true, '$context')"
				style="text-align: center">
				<a href="#">Run with Dependencies</a>
			</div>
		</div>
		<br></br>
		<div id="tabs">
			<ul>
				<li><a href="#tabs-1">Details</a></li>
				<li><a href="#tabs-2">Logs</a></li>
			</ul>
			<div id="tabs-1">
				<div class="well">
					<h3>Details</h3>
					<div class="jobdesc">
						<div>
							<span class="span4">Job Name:</span> ${job.id}&nbsp;
						</div>
						<div>
							<span class="span4">Type:</span> ${job.jobType}&nbsp;
						</div>
						<div>
							<span class="span4">Path:</span> ${job.path}&nbsp;
						</div>
						<div>
							<span class="span4">Dependencies:</span>
							<%
								JobDescriptor job = (JobDescriptor) request.getAttribute("job");
							%>
							<%
								if (job.hasDependencies()) {
							%>
							<c:forEach var="dep" items="${job.dependencies}">
								<a class="job-name-white"
									href="${request.contextPath}/scheduler/jobDetail.html?id=${dep.id}">${dep.id}</a>,
							</c:forEach>
							<%
								} else {
							%>None
							<%
								}
							%>
						</div>
					</div>
				</div>

				<div class="well">
					<h3>Job Properties</h3>
					<table id="params" class="table table-striped">
						<tr>
							<th>Key</th>
							<th>Value</th>
						</tr>
					</table>
				</div>

			</div>

			<div id="tabs-2">
				<div class="box">
					<h3>
						Job History <small>(Reload every second)</small>
						<p>
							<small> <c:if test='${job.jobType == "sqoop"}'>
									<a
										href="${request.contextPath}/scheduler/viewJobConsoleLog.html?jobId=${job.id}">
										<span class="label label-warning">Sqoop logs</span>
									</a>
								</c:if></small>
						</p>
					</h3>
					<c:choose>
						<c:when test="${fn:length(executions) gt 0}">
							<table id="jobHistory" class="table table-striped table-bordered">
								<tbody>
									<tr>
										<th>Name</th>
										<th>Started</th>
										<th>Ended</th>
										<th>Elapsed</th>
										<th>Completed Successfully?</th>
										<th>Log</th>
									</tr>
								</tbody>
								<c:forEach var="exec" items="${executions}">
									<%
										JobExecution execution = (JobExecution) pageContext
															.getAttribute("exec");
									%>
									<tr>
										<td>${exec.id}</td>
										<td>
											<%
												if (execution.getStarted() == null) {
											%>&ndash;<%
												} else {
																%> <%=GuiUtils.formatDateTimeAndZone(execution
																		.getStarted())%>
											<%
															}
											%>&nbsp;
										</td>
										<td>
											<%
												if (execution.getEnded() == null) {
											%>&ndash;<%
												} else {
													%> <%=GuiUtils.formatDateTimeAndZone(execution
															.getEnded())%>
											<%
															}
											%>&nbsp;
										</td>
										<td>
											<%
												if (execution.getEnded() == null) {
											%>&ndash;<%
												} else {
													%> <%=GuiUtils.formatPeriod(GuiUtils.period(
																		execution.getStarted(),
																		execution.getEnded()))%>
											<%
															}
											%>&nbsp;
										</td>
										<td>
											<%
												if (execution.getEnded() == null) {
											%>&ndash;<%
												} else {
																if (execution.isSucceeded()) {
											%>yes<%
												} else {
											%>no<%
												}
															}
											%>&nbsp;
										</td>
										<td><a
											href="${request.contextPath}/scheduler/viewJobLog.html?file=${exec.log}">
												<span class="label label-warning">Log</span>
										</a>												
										</td>
									</tr>
								</c:forEach>
							</table>
						</c:when>
						<c:otherwise> No recorded executions </c:otherwise>
					</c:choose>
				</div>

				<c:if test="${not empty successful_executions}">
					<div class="box">
						<h3>Previous Runtimes</h3>
						<div class="jobdesc" id='runtime-chart'
							style='width: 700px; height: 300px; margin: auto'></div>
					</div>
				</c:if>
			</div>
		</div>
	</div>
</div>
<div id="modelDialog" style="display: none;" title="Info"></div>