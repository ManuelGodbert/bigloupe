<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="row">
	<div class="page-header">
		<h2>Endpoints</h2>
	</div>
</div>

<div class="row">
	<table class="table table-bordered table-striped"
		style="table-layout: fixed;">
		<thead>
			<tr>
				<th style="width: 600px">Methods</th>
				<th style="width: 150px">Patterns</th>
				<th style="width: 100px">Request Methods</th>
				<th style="width: 100px">Headers</th>
				<th style="width: 100px">Parameters</th>
				<th style="width: 100px">Consumes</th>
				<th style="width: 150px">Produces</th>
			</tr>
		</thead>
		<tbody>

			<c:forEach items="${handlerMethods}" var="entry">
				
				<tr>
					<td>
						<c:set var="methods" value="${fn:split(entry.value, ' ')}" />
						<c:forEach items="${methods}" var="part">
							<c:choose>
								<c:when test="${fn:indexOf(part, '(') != -1}">
									<c:set var="subparts1" value="${fn:substring(part, 0, fn:indexOf(part, '('))}" />
									<c:set var="subparts2" value="${fn:substring(part, fn:indexOf(part, '('), fn:length(part))}" />
									<p><span class="label label-warning">${subparts1}</span></p><p><span class="label label-info">${subparts2}</span></p>
									
								</c:when>
								<c:otherwise><p><span class="label label-important">${part}</span></p></c:otherwise>
							</c:choose>
							<br/>
						</c:forEach>
					</td>
					<td><c:if
							test="${not empty entry.key.patternsCondition.patterns}">${entry.key.patternsCondition.patterns}</c:if>&nbsp;
					</td>
					<td><c:if
							test="${not empty entry.key.methodsCondition.methods}">
            ${entry.key.methodsCondition.methods}
          </c:if>&nbsp;</td>
					<td><c:if
							test="${not empty entry.key.headersCondition.expressions}">
            ${entry.key.headersCondition.expressions}
          </c:if>&nbsp;</td>
					<td><c:if
							test="${not empty entry.key.paramsCondition.expressions}">
            ${entry.key.paramsCondition.expressions}
          </c:if>&nbsp;</td>
					<td><c:if
							test="${not empty entry.key.consumesCondition.expressions}">
            ${entry.key.consumesCondition.expressions}
          </c:if>&nbsp;</td>
					<td><c:if
							test="${not empty entry.key.producesCondition.expressions}">
            ${entry.key.producesCondition.expressions}
          </c:if>&nbsp;</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>