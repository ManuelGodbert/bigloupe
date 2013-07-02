<%@page import="java.io.ByteArrayOutputStream"%>
<link rel="stylesheet"
	href="${request.contextPath}/resources/js/jquery/jquery-treeview/jquery.treeview.css"
	type="text/css" media="screen" charset="utf-8">

<script
	src="${request.contextPath}/resources/js/jquery/jquery.cookie.js"
	type="text/javascript" charset="utf-8"></script>
<script
	src="${request.contextPath}/resources/js/jquery/jquery-treeview/jquery.treeview.js"
	type="text/javascript" charset="utf-8"></script>
	
<script type="text/javascript">
	$(document).ready(function(){
		$("#browser").treeview({
			toggle: function() {
				console.log("%s was toggled.", $(this).find(">span").text());
			}
		});
		
	});
</script>	

<%
	ByteArrayOutputStream output = (ByteArrayOutputStream) request
			.getAttribute("output");
	if (output == null) {
		output = new ByteArrayOutputStream();
	}
%>

<div class="row show-grid">
	<div class="page-header span8">
		<h3>&nbsp;Avro schema comparaison</h3>
	</div>
</div>

<div class="row show-grid">
	<form id="avroSchemaDiffForm" method="GET" action="${request.contextPath}/hdfsAvroSchemaDiff.html">
		<label>HDFS Cluster ${cluster}&nbsp;</label><label>Avro file 1</label><input
			type="text" style="height: 25px" class="span8" name="fsPath1" value="${param.fsPath1}">&nbsp;
			<label>Avro file 2</label><input type="text" style="height: 25px" class="span8" name="fsPath2" value="${param.fsPath2}">
			<input type="submit" class="btn btn-primary" value="Compare">
	</form>
</div>

<div class="row show-grid">	
	<ul id="browser" class="filetree treeview-famfamfam">
		<%=output.toString("UTF-8")%>
	</ul>
</div>
