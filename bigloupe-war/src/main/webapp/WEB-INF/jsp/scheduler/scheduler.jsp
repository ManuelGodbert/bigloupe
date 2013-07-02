<%@page import="org.joda.time.ReadablePeriod"%>
<%@page import="org.bigloupe.web.scheduler.ScheduledJob"%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="org.bigloupe.web.scheduler.flow.ExecutableFlow"%>
<%@page import="org.bigloupe.web.scheduler.job.JobExecutorManager.ExecutingJobAndInstance"%>
<%@page import="org.bigloupe.web.scheduler.InitializeScheduler"%>
<%@page import="org.bigloupe.web.util.GuiUtils"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.joda.org/joda/time/tags" prefix="joda"%>
<div class="row">
	<div class="page-header">
		<h2>Scheduler</h2>
	</div>
</div>
<script>
	$(function() {
		$(".scheduled_date").datepicker({
			dateFormat : 'mm-dd-yy'
		});
		$("#ui-datepicker-div").css("display", "none");
		
		<c:choose>
		<c:when test='${tab == "scheduled"}'> 
			$( "#tabs" ).tabs({ selected: 1 });
		</c:when>
		<c:when test='${tab == "jobs"}'> 
		$( "#tabs" ).tabs({ selected: 2 });
		</c:when>
		<c:otherwise>
			$( "#tabs" ).tabs();
		</c:otherwise>
		</c:choose>
		
	});
</script>

<div class="row">
	<div id="tabs" class="span12">
		<ul class="nav nav-tabs">
			<li><a href="#executing-tab">Executing</a></li>
			<li>
			<li><a href="#schedule-tab">Schedule</a></li>
			<li><a href="#job-tab">Jobs</a></li>
		</ul>

		<%-- Jobs in execution --%>
		<div id="executing-tab">
			<div class="box">
				<h3>Executing Jobs</h3>
				<%
					InitializeScheduler initScheduler = (InitializeScheduler) pageContext
							.getAttribute("initScheduler");
				%>
				<c:choose>
					<c:when
						test="${fn:length(initScheduler.jobExecutorManager.executingJobs) gt 0}">
						<table class="jobtable execing-jobs-table translucent">
							<tr>
								<th>Name</th>
								<th>Start Time</th>
								<th>Period</th>
								<th></th>
							</tr>
							<c:forEach var="executingJob"
								items="${initScheduler.jobExecutorManager.executingJobs}"
								varStatus="rowCounter">
								<tr>
									<td>
										<ul id="execing-jobs-tree-${rowCount}" class="execing-jobs">
											<li><a class="job-name"
												href="${request.contextPath}/scheduler/jobDetail.html?id=${executingJob.executableFlow.name}&logs"
												title="${executingJob.executableFlow.name}"> <span class="label">${executingJob.executableFlow.name}</span> </a>
												<%
													ExecutingJobAndInstance executingJobAndInstance = (ExecutingJobAndInstance) pageContext
																		.getAttribute("executingJob");
																ExecutableFlow executableFlow = (ExecutableFlow) executingJobAndInstance
																		.getExecutableFlow();
																if (executableFlow.hasChildren()) {
												%>
												<ul>
													<%
														for (ExecutableFlow subExecutableFlow : executableFlow
																				.getChildren()) {
													%>
													<li><a class="job-name"
														href="${request.contextPath}/scheduler/jobDetail.html?id=${executingJob.executableFlow.name}&logs"
														title="${executingJob.executableFlow.name}">
															${executingJob.executableFlow.name} </a> <%
 														if (executableFlow.hasChildren()) {
 														%>
														<ul>

														</ul> <% } %></li>

													<%
														}
													%>
												</ul>
												<%
													}
												%>
											</li>
										</ul>
									</td>
									<td>
										<joda:format value="${executingJob.executableFlow.startTime}" style="SM"/><br />
										<% DateTime startTime = ((ExecutingJobAndInstance)pageContext.getAttribute("executingJob")).getExecutableFlow().getStartTime(); %>
		                  				<%=GuiUtils.formatPeriod(GuiUtils.period(startTime, GuiUtils.now())) %> ago
									</td>
									<td>
										<% DateTime started = ((ExecutingJobAndInstance)pageContext.getAttribute("executingJob")).getScheduledJob().getStarted(); %>
										<% DateTime ended = ((ExecutingJobAndInstance)pageContext.getAttribute("executingJob")).getScheduledJob().getEnded(); %>
										<%=GuiUtils.formatPeriod(GuiUtils.period(started,ended))%></td>
									<td>
										<form action="${request.contextPath}/scheduler/cancelJob.html" method="post"
											style="display: inline">
											<input
												type="hidden" name="id" value="${executingJob.executableFlow.name}" />
											<input type="submit" value="Cancel" />
										</form>
									</td>
								</tr>
							</c:forEach>

						</table>
					</c:when>
					<c:otherwise>No jobs currently executing. </c:otherwise>
				</c:choose>
			</div>
		</div>

		<%-- Scheduled Jobs --%>
		<div id="schedule-tab">
			<div class="box">
				<h3>Scheduled Jobs</h3>
				<c:choose>
					<c:when
						test="${fn:length(initScheduler.scheduleManager.schedule) gt 0}">
						<table class="jobtable translucent">
							<tr>
								<th>Name</th>
								<th>Next Execution</th>
								<th>Period</th>
								<th></th>
							</tr>
							<c:forEach var="schedule"
								items="${initScheduler.scheduleManager.schedule}"
								varStatus="rowCounter">
								<tr>
									<td><a class="job-name"
										href="${request.contextPath}/scheduler/jobDetail.html?id=${schedule.id}&logs"><span class="label">${schedule.id}</span></a></td>
									<td><joda:format value="${schedule.scheduledExecution}" style="SM"/></td>
									<td>
									<% ReadablePeriod period = ((ScheduledJob)pageContext.getAttribute("schedule")).getPeriod(); %>
										<%=GuiUtils.formatPeriod(period)%></td>
									<td>
										<form action="${request.contextPath}/scheduler/unscheduleJob.html" method="post"
											style="display: inline">
											<input type="hidden" name="action" value="unschedule" /> <input
												type="hidden" name="id" value="${schedule.id}" /> <input
												type="submit" value="Remove" />
										</form>
									</td>
								</tr>
							</c:forEach>
						</table>
					</c:when>
					<c:otherwise> No scheduled jobs at this time. </c:otherwise>
				</c:choose>
			</div>
		</div>


		<%-- All Jobs --%>
		<div id="job-tab">
			<div class="box">
				<h3>All Jobs</h3>
				<div class="all-jobs">
					<form id="runSubmit" method="post" action="${request.contextPath}/scheduler/scheduleJob.html">
						<div class="well">
							<div>
								Run immediately (<input name="include_deps" id="withdep"
									type="checkbox" checked /> with dependencies) <input
									type="submit" name="run_now" value="Run" /> <input
									id="submitType" type="hidden" />
							</div>
							<hr>
							<div>
								<input type="hidden" name="action" value="schedule" /> Schedule
								to run at <input name="hour" type="text" size="2" value="12" style="height : 30px" class="span1"/>
								: <input name="minutes" type="text" size="2" value="00" style="height : 30px" class="span1"/> <select
									name="am_pm" style="height : 30px" class="span1">
									<option>pm</option>
									<option>am</option>
								</select>
								<%=GuiUtils.getShortTimeZone()%>
								on <input class="scheduled_date" type="text" name="date"
									size="10" value="<%=GuiUtils.formatDate(GuiUtils.now())%>" style="height : 30px" class="span1"/>
								and <input name="is_recurring" type="checkbox" checked />
								repeat every <input name="period" type="text" size="2" value="1" style="height : 30px" class="span1"/>
								<select name="period_units" style="height : 30px" class="span2">
									<option value="d">Days</option>
									<option value="h">Hours</option>
									<option value="m">Minutes</option>
									<option value="s">Seconds</option>
								</select> <input type="submit" name="schedule" value="Schedule" />
							</div>
						</div>
						<div class="well">
							<c:forEach var="folder" items="${initScheduler.allFlows.folders}"
									varStatus="rowCounter">
								<div class="jobfolder expand" onclick="expandFlow(this)"
									id="${folder}">
									<span><div class="folderIcon"></div></span>
									<p>${folder}</p>
								</div>
								<div id="${folder}-panel"></div>
							</c:forEach>
						</div>
					</form>
				</div>
			</div>
		</div>


	</div>
</div>
<div id="modelDialog" style="display: none;" title="Info"/>
  