<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
	var tagNumber = 0;

	<%-- Search all elements --%>
	function searchDetails(page) {
		$("#searchResults").load('${request.contextPath}/search/result.html', {
			obj : $('input:radio[name=search_type]:checked').val(),
			searchInput : $("#searchInput").val(),
			structureSelect : $('#structureSelect').val(),
			tags : $('#formTag').serialize(),
			page : page
		});
	};
	
	<%-- Add tag in query --%>
	function addTag() {
		if ($("#searchInput").val() && !$("#structure").hasClass("hide")) {
			var tag = "<span id=\"tag" + tagNumber + "\" class=\"label label-success\">" + $('#structureSelect').val() + " - " + $("#searchInput").val();
			tag = tag + "&nbsp;<a style=\"color : red\" href=\"javascript:removeTag(" + tagNumber + ")\">&nbsp;×</a>";
			tag = tag + "<input type=\"hidden\" name=\"" + $('#structureSelect').val() + "\" value=\"" +  $("#searchInput").val() + "\"></span>";
			$("#tags").removeClass('hide').append(tag).append("&nbsp;");
			tagNumber++;
		}
	}

	<%-- Remove tag in query --%>
	function removeTag(tagToDelete) {
		$("#tag" + tagToDelete).remove();
	}

	var indexToDeleteVar;
	
	<%-- Remove index --%>
	function modalRemoveIndex(indexToDelete) {
		indexToDeleteVar = indexToDelete;
		$('#indexToDeleteModal').modal('show');
	}
	
	function removeIndex() {
		$.getJSON("${request.contextPath}/search/removeIndex/" + indexToDeleteVar + ".html", function(response, status, xhr) {
			if (status == "error") {
				var msg = "Sorry but there was an error: ";
				$("#error")
						.addClass("alert")
						.html(
								'<button class="close" data-dismiss="alert">×</button><strong> Warning! </strong>'
										+ msg
										+ xhr.status
										+ ' '
										+ xhr.statusText);
			} else {
				$("#result").html("<p>" + response.message + "</p>");
				$("#result").addClass('alert');
				$("#index" + indexToDeleteVar).remove();
			}
		});

		$('#indexToDeleteModal').modal('hide');
	}
	
	$().ready(function() {
						$("#searchInput").autocomplete(
								{
									source : function(request, response) {
										$.getJSON("${request.contextPath}/search/suggest.html",
												{
													obj : $('input:radio[name=search_type]:checked').val(),
													searchInput : $('#searchInput').val(),
													structureSelect : $('#structureSelect').val()
												}, response);
									},
									minLength : 1,
									select : function(event, ui) {
										(ui.item ? "Selected: "
												+ ui.item.value
												+ " aka " + ui.item.id
												: "Nothing selected, input was "
														+ this.value);
									}
								});

						// Search number of indexed elements 
						$.getJSON('${request.contextPath}/search/count.html',
										function(response, status, xhr) {
											if (status == "error") {
												var msg = "Sorry but there was an error: ";
												$("#error")
														.addClass("alert")
														.html(
																'<button class="close" data-dismiss="alert">×</button><strong> Warning! </strong>'
																		+ msg
																		+ xhr.status
																		+ ' '
																		+ xhr.statusText);
											} else {
												$("#searchInfo").html(
														response[0].value);
											}
									});

						$("#searchDetails").click(function() {
							searchDetails(${page});
						});

						$(document).bind('keyup', function(event) {
							if (event.keyCode == 13) {
								searchDetails(${page});
							}
						});
						
						$(":radio").click(function() {
							// Search description of obj available in elasticsearch 
							$.getJSON('${request.contextPath}/search/describeFields.html', 
											{
												obj : $('input:radio[name=search_type]:checked').val()
											},
											function(response, status, xhr) {
												if (status == "error") {
													var msg = "Sorry but there was an error: ";
													$("#error")
															.addClass("alert")
															.html(
																	'<button class="close" data-dismiss="alert">×</button><strong> Warning! </strong>'
																			+ msg
																			+ xhr.status
																			+ ' '
																			+ xhr.statusText);
												} else {
													if (($.isEmptyObject(response)) || (response[0].value.match('Error') == "Error")) {
														$("#structure").addClass('hide');
													} else {
														$("#structure").removeClass('hide');
														var options = "";
														for (var i=0; i< response.length; i=i+1) {
															options = options + ('<option>' +
																response[i].value + '</option>');
														}
														$("#structureSelect").html(options);
													}
												}
										});
						});

					});
</script>


<%-- Modal window for delete index --%>
<div id="indexToDeleteModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Delete this search index ?</h3>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Close</a> <a
			href="javascript:removeIndex();" class="btn btn-primary">Delete</a>
	</div>
</div>
<br/>
<div class="row">
	<div id="result"></div>
</div>

<section class="well">
	<div class="row">
		<div class="page-header" style="margin: 20px">
			<h2>
				<div id="searchInfo">Searching # objects</div>
			</h2>
		</div>
	</div>

	<div id="error"></div>
<%
	BigLoupeConfiguration configuration = (BigLoupeConfiguration) request
	.getAttribute("configuration");
%>

		<div class="row-fluid">
			<div class="carousel slide" data-interval="" id="myCarousel">
				<div class="carousel-inner">
					<c:set var="col" value="0" />
					<div class="item active">
						<div class="span12">
						<div class="span6 alert alert-info" style="padding: 8px 35px 8px 70px;">
							<c:forEach var="indice" items="${indices}"
								varStatus="indiceCounter">
								
		
											<div id="index${indice.key}">
												<input name="search_type" value="${indice.key}" type="radio"
													id="search_type" /> &nbsp;&nbsp;${indice.key} -
												(${indice.value.docs.numDocs}) <a
													href="javascript:modalRemoveIndex('${indice.key}')">&nbsp;<span
													class="label label-important">x</span></a>&nbsp;&nbsp;
											</div>
											<c:if test="${((indiceCounter.index+1) % 6) == 0}"></div><%-- /span6 --%><c:set var="col" value="${col +1}" /><c:if test="${col != 2}"><div class="span6 alert alert-info"></c:if><%-- /colonne --%></c:if>

								<c:if test="${col == 2}">
									</div><%-- /span12 --%></div><%-- /item active --%><c:set var="col" value="0" /><div class="item"><div class="span12"><div class="span6 alert alert-info" style="padding: 8px 35px 8px 70px;">
								</c:if>
								</c:forEach>
						</div> <%-- /span4 --%>
						</div> <%-- /span12 --%>
					</div> <%-- /item (active or not active) --%>
				</div>
				<a data-slide="prev" href="#myCarousel"
					class="left carousel-control">&lsaquo;</a> <a data-slide="next"
					href="#myCarousel" class="right carousel-control">&rsaquo;</a>
			</div> <%-- /carousel slide--%>
		</div>



	<div id="structure" class="row hide">
		<br></br>
		<div class="span">
			<select id="structureSelect">
				<option>---</option>
			</select>
		</div>
	</div>

	<form id="formTag">
	<div id="tags" class="row hide">
	</div>
	</form>
	
	<div class="row">
		<div>
			<form id="searchform" autocomplete="off" class="form-search"
				action="#">
				<div class="span12">
					<input id="searchInput" type="text"
						class="input-xlarge search-query" style="height: 50px" />
						<a href="javascript:addTag()" class="btn btn-large btn-info">Add tags</a>
					<button id="searchDetails" class="btn btn-large btn-primary">Search</button>
				</div>
			</form>
		</div>
	</div>
</section>
<div class="row">
	<div id="searchResults"></div>
</div>