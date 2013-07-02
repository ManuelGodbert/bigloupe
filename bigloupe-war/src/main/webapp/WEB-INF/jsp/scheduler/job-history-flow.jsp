<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
  <li>
    <a class="job job-name-${flowNode.status}" href="${request.contextPath}/scheduler/jobDetail.html?id=${flowNode.name}&logs" title="${flowNode.name}<br>Status: ${flowNode.status}" name="sched-tree-link">
      <span class="label label-warning">${flowNode.name}</span>
    </a>
	
    <c:if test="${!empty flowNode.children}">
      <ul>
        <c:forEach var="flowSubNode" items="${flowNode.children}">
				<c:set var="flowNode" value="${flowSubNode}" scope="request"/>
				<jsp:include page="job-history-flow.jsp"/>
        </c:forEach>
      </ul>
    </c:if>
  </li>
