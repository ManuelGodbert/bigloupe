
<%@page import="java.nio.charset.Charset"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="org.codehaus.jackson.JsonEncoding"%>
<%@page import="org.codehaus.jackson.JsonFactory"%>
<%@page import="org.codehaus.jackson.JsonGenerator"%>

<%
	ByteArrayOutputStream output = (ByteArrayOutputStream) request
			.getAttribute("output");
%>

<%-- Modal window for delete record --%>
<div id="schemaModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Avro Schema</h3>
	</div>
	<div id="schemaModalBody" class="modal-body">
		<p></p>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Close</a>
	</div>
</div>

<div id="infoToRefresh">
	<div class="row">
		<div class="page-header span8">
			<h2>
				Avro File&nbsp;&nbsp;<small><span id="codec"><button id="codecBtn" class="btn btn-mini btn-success " onclick="codec()">Codec ?</button></span>&nbsp;- Record ${startRecord} - ${path}
					(in cluster ${cluster}) - <a href="javascript:schema()">Schema</a></small>
				<ul class="pager">
					<li><a href="javascript:nextRecord(${startRecord-1})">Previous</a></li>
					<li><a href="javascript:nextRecord(${startRecord+1})">Next</a></li>
				</ul>
			</h2>
		</div>
	</div>

	<div class="row show-grid">
		<div class="page-header span8">
			<h3>&nbsp;Avro in JSON</h3>
		</div>
		<div id="inputJson" class="span12">
			<pre><%=output.toString("UTF-8")%></pre>
		</div>
	</div>

	<p></p>

	<div class="row show grid">

		<div class="page-header span8">
			<h3>&nbsp;Avro Hierarchy</h3>
		</div>
		
		<div class="span8">
			<img id="loading"
				src="${request.contextPath}/resources/img/loader.gif" />

			<p id="browser-text" class="green bold">Your JSON data tree will
				appear here.</p>
			<div id="treecontrol">
				<a title="Collapse the entire tree below" href="#"><img
					src="${request.contextPath}/resources/js/jquery/jquery-treeview/images/minus.gif" />
					Collapse Base</a> <a title="Expand the entire tree below" href="#"><img
					src="${request.contextPath}/resources/js/jquery/jquery-treeview/images/plus.gif" />
					Expand Base</a>
			</div>
			<ul id="browser" class="filetree treeview-famfamfam">
			</ul>
		</div>

		<div class="span4">
			<div id="toppathwrap">
				<textarea id="accumpaths"></textarea>
				<a id="closetoppath" href="#"><i class="icon-remove-sign"></i></a>
			</div>
			<div id="pathtonode"></div>
		</div>

	</div>


</div>

