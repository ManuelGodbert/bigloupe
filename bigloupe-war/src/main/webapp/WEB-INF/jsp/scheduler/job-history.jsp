<%@page import="org.joda.time.DateTime"%>
<%@page import="org.bigloupe.web.scheduler.flow.ExecutableFlow"%>
<%@page import="org.bigloupe.web.util.GuiUtils"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.joda.org/joda/time/tags" prefix="joda"%>
<style>
#executionHistory {
	padding: 10px;
	width: 90%;
}

.historytree {
	text-align: left; 
}

.tooltip {
	background-color: #000;
	border: 1px solid #fff;
	padding: 10px 15px;
	width: 200px;
	display: none;
	color: #fff;
	text-align: left;
	font-size: 12px;
	/* outline radius for mozilla/firefox only */
	-moz-box-shadow: 0 0 10px #000;
	-webkit-box-shadow: 0 0 10px #000;
}
</style>

<script type="text/javascript">

	$(function() {
		$(".job").tooltip({
			position : "center right",
			offset : [ 0, 10 ],
			effect : "fade",
			opacity : 0.9,
			predelay : 500
		});
	});
</script>
<div class="row">
	<div class="page-header">
		<h2>Job History</h2>
	</div>
</div>

<div class="row">
  	    <div id="variableWidth">
  	       <div class="box">
		      <c:choose>
		      <c:when test="${!empty executions}">
		        <table id="executionHistory" class="jobtable translucent">
		          <tr>
		            <th>Id</th>
		            <th>Name</th>
		            <th>Started</th>
		            <th>Ended</th>
		            <th>Elapsed</th>
		            <th>Status</th>
		            <th>Action</th>
		          </tr>
		          <c:forEach var="flow"	items="${executions}" varStatus="rowCounter">
		            <tr>
		              <td>${flow.id}</td>
		              <td class="historytree"><ul class="sched-tree"><c:set var="flowNode" value="${flow}" scope="request"/><jsp:include page="job-history-flow.jsp"/></ul></td>
		              <td>
		                <joda:format value="${flow.startTime}" style="SM"/>
		              </td>
		              <td>
						<joda:format value="${flow.endTime}" style="SM"/>
		              </td>
		              <td>
		              	  <% DateTime startTime = ((ExecutableFlow)pageContext.getAttribute("flow")).getStartTime(); %>
		              	  <% DateTime endTime = ((ExecutableFlow)pageContext.getAttribute("flow")).getEndTime(); %>	
		                  <%=GuiUtils.formatPeriod(GuiUtils.period(startTime, endTime))%>
		              </td>
		              <td>
		                ${flow.status}
		              </td>
		              <td class="blue">
		                <span class="label"><a href="${request.contextPath}/scheduler/flowId.html?action=restart&flow_id=${flow.id}">view&#47;restart</a></span>
		              </td>
		            </tr>
		          </c:forEach>
		        </table>
		        <div class="pagination">
		          <ul>
		          <c:if test="${begin > 0}">
		            <li><a href="${request.contextPath}/scheduler/history.html?begin=${begin - size}&amp;size=${size}">
		              Previous &middot;
		            </a></li>
		          </c:if>
		          <li><a href="${request.contextPath}/scheduler/history.html?begin=${begin + size}&size=${size}">
		            Next
		          </a></li> 
		          </ul>
		        </div>
		      </c:when>
		      <c:otherwise>
		        No recorded executions
		      </c:otherwise>
		      </c:choose>
  	       </div>
  	    </div>
</div>



