<%@page import="java.util.TreeSet"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Set"%>
<%@page import="org.elasticsearch.search.SearchHitField"%>
<%@page import="java.util.Map"%>
<%@page import="org.elasticsearch.search.SearchHit"%>
<%@page import="org.elasticsearch.search.SearchHits"%>
<%@page import="org.elasticsearch.search.facet.Facets"%>
<%@page import="org.elasticsearch.action.search.SearchResponse"%>
<%@page import="java.util.Collection"%>
<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>
<%
	SearchResponse searchResponse = (SearchResponse) request
			.getAttribute("elasticSearchDocuments");
	Facets facets = searchResponse.getFacets();
%>
<div class="row offset1">
<div class="page-header">
	<h1>
		Results <small><%=searchResponse.hits().getTotalHits()%> elements (in <%=searchResponse.getTookInMillis()%> ms)</small>
	</h1>
	<%  int nbPageTotal =  (int)(searchResponse.hits().getTotalHits()/60) + 1;
	    int nbPageToDisplay = 18;
		if (nbPageTotal < nbPageToDisplay ) 
			nbPageToDisplay = nbPageTotal;
	%>
	<c:set var="nbPageToDisplay" value="<%= nbPageToDisplay %>"/>
	<c:set var="nbPageTotal" value="<%= nbPageTotal %>"/>
	<%
		if (searchResponse.hits().getTotalHits() > 0) {
	%>
	<div class="pagination">
		<ul>
			<c:choose>
				<c:when test="${serie == 0 }">
					<li class="disabled">
				</c:when>
				<c:otherwise>
					<li>
				</c:otherwise>
			</c:choose>
			<a href="javascript:;" onclick="searchDetails(${(serie-1)*18})">«</a>
			</li>
			<c:forEach var="i" begin="1" end="${nbPageToDisplay}">
				<c:choose>
					<c:when test="${(page+1) == i}">
						<li class="active">
					</c:when>
					<c:otherwise>
						<li>
					</c:otherwise>
				</c:choose>
				<a href="javascript:;" onclick="searchDetails(${i + (serie*18) -1})">${i + (serie*18)}</a>
				</li>
			</c:forEach>
			<c:if test="${nbPageTotal > 18}">
			<li><a href="javascript:;" onclick="searchDetails(${(serie+1)*18})">»</a></li>
			</c:if>
		</ul>
	</div>
	<%
		}
	%>
</div>
<%
	if (searchResponse.hits().getTotalHits() > 0) {
%>

	<section id="results">
		<div class="row">
			<table class="table table-hover table-bordered">

				<%
						SearchHits hits = searchResponse.getHits();
						SearchHit[] hitsArray = hits.getHits();

						Map<String, Object> map0 = hitsArray[0].sourceAsMap();
						Set<String> keySetMap0 = map0.keySet();
						Set<String> orderedKeySet = new TreeSet<String>(keySetMap0);						
						
						for (int i = 0; i < hitsArray.length; i++) {
							Map<String, Object> map = hitsArray[i].sourceAsMap();
							Set<String> keySet = map.keySet();
							
							if (i == 0) {
				%>
				<thead><tr><th>#</th>
										<%
							
									for (String key : orderedKeySet) {
						%>
							<th><%=key%></th>
						<%
							}
						%>
				</tr></<thead>
				<%
					} if (i % 2 == 0) { %>

						<tr>
						<%
					} else {
				%><tr style="background-color: #F5F5F5;">
					<%
						}
					%>
						<c:set var="nb" value="<%= i %>"/>
						<%-- serie = 18 pages ; page = 60 records --%>
						<td><span class="badge badge-info">${(nb+1) + (page*60) + (serie*18*60)}</span></td>

						<%

									for (String key : orderedKeySet) {
						%>
						<td>
							<% if ((map.get(key) != null) && (!map.get(key).equals(""))) { %>		
								<%=  map.get(key) %>
							<% } else { %>
								&nbsp;
							<% }%>
						</td>

						<%
							}
						%>
					</tr>

					<%
						}
					%>
				
			</table>
		</div>
</div>
</section>

<%
	}
%>
</div>