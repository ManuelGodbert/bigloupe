<%@page import="org.bigloupe.web.util.GuiUtils"%>
<%@page import="org.joda.time.Period"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.joda.org/joda/time/tags" prefix="joda"%>

<script type="text/javascript">
	var flowData = '${jsonflow}';
	var contextURL = '${request.contextPath}';
	var flowID = '${id}';
	var action = '${action}';
	var name = '${name}';
	var jobList = ${joblist};
</script>
<style>
.tooltip {
	background-color: #000;
	border: 1px solid #fff;
	padding: 10px 15px;
	width: 125px;
	display: none;
	color: #fff;
	text-align: left;
	font-size: 12px;
	/* outline radius for mozilla/firefox only */
	-moz-box-shadow: 0 0 10px #000;
	-webkit-box-shadow: 0 0 10px #000;
}

.flowDuration {
	float: right;
}

.searchBox {
	margin-bottom: 10px;
}

#jobsearch {
	width: 250px;
}
</style>
<div class="row">
	<div class="page-header">
		<h2 class="title">
			Flow Instance&nbsp;<small>${name}</small>
		</h2>
	</div>
</div>

<div class="row">
			<div class="span12">
			<c:choose>
				<c:when test="${id == 0}">
					<h3>Name  <small>${name}</small></h3>
				</c:when>
				<c:otherwise>
					<h3>Name <small>${name} Flow ID: ${id}</small></h3>
				</c:otherwise>
			</c:choose>
			</div>
			<div class="span12">
			<c:if test="${showTimes}">
				<table class="jobtable flowDuration">
					<tr>
						<th>Start Time</th>
						<th>End Time</th>
						<th>Period</th>
					</tr>
					<tr>
						<td><joda:format value="${startTime}" style="SM"/></td>
						<td><joda:format value="${endTime}" style="SM"/></td>
						<td><% Period period = (Period)pageContext.getAttribute("period"); %><% if (period != null) { %><%= GuiUtils.formatPeriod(period)%><% } %></td>
					</tr>
				</table>
			</c:if>
			</div>
			<div class="span4">
				<tr>
					<th>Search</th>
				</tr>
				<tr>
					<td><input id="jobsearch" type="text"
						class="input-xlarge search-query ui-autocomplete-input"
						style="height: 30px" autocomplete="off"></input></td>
				</tr>
			</div>

			<div id="extra2" class="span4">
				<div id="buttonpanel">
					<button class="btn btn-primary" id="executeButton"
						onclick="executeFlow()">Execute</button>
					<button class="btn btn-warning" id="executeButton"
						onclick="showHelpScreen()">Help</button>
				</div>
			</div>
			
			<div class="span12">&nbsp;</div>


</div>

<div class="row">
			<div id="content2" >
				<div id="graphTab" onmousedown="moveGraph(this, event, 'down')"
					onmouseup="moveGraph(this, event, 'up')"
					onmouseover="mouseFocus('true')" onmouseout="mouseFocus('false')">
					<svg id="graph" xmlns="http://www.w3.org/2000/svg" version="1.1"
						viewBox="0 0 100% 100%"> 
						</svg>
				</div>
			</div>
</div>

<ul id="nodeMenu" class="contextmenu">
	<li class="jobdetails"><a href="#jobdetails">Job Details...</a></li>
	<li class="enable"><a href="#enable">Enable</a></li>
	<li class="enableAll"><a href="#enableAll">Enable All</a></li>
	<li class="disable"><a href="#disable">Disable</a></li>
	<li class="disableAncestor"><a href="#disableAncestor">Disable
			All Dependencies</a></li>
</ul>
<div id="modelDialog" style="display: none;" title="Info" />
<div id="nonModelDialog" style="display: none;" title="Help">
	This page shows the dependency graph for your job. <br /> <br /> <b>Flow</b><br />
	Dependencies are displayed as nodes with arrows connecting them. The
	arrows direction points to the job that is the dependent. The
	dependencies must complete successfully before the dependent job
	starts. <br /> <br /> <b>Navigation</b><br /> Click and Hold the
	left mouse button to drag nodes and pan the graph view. <br /> Use the
	mouse wheel or the zoom bar to zoom in and out of the graph. <br /> <br />
	<b>Node Status</b><br /> When restarting a flow, some nodes will have
	special colors to denote its status. <br /> <span class="running">Running</span>,
	<span class="failed">Failed</span>, <span class="button succeeded">Succeeded</span>,
	<span class="ready">Ready, Completed</span>. <br /> The Ready status
	is the state of the job before it is run.<br /> Completed status is a
	legacy status which means that the job is done without being a success
	or failure. It is treated like a ready job. <br /> <br /> <b>Enabled/Disabled</b><br />
	Jobs that are faded-out are disabled. Disabled nodes act as no-op jobs
	and will always complete successfully. A disabled job's dependencies
	will always run. <br /> Right click on a Node to disable and enable
	it. <br /> <br /> <b>Execute Flow</b><br /> Clicking the Execute
	button will submit this flow for immediate execution. Disabled nodes
	will be observed.
</div>
<div id="graphToolTip" class="tooltip" style="display: none;"></div>
