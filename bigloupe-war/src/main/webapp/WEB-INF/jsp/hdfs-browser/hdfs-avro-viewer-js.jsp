<link rel="stylesheet"
	href="${request.contextPath}/resources/js/jquery/jquery-treeview/jquery.treeview.css"
	type="text/css" media="screen" charset="utf-8">
	
<script
	src="${request.contextPath}/resources/js/jquery/jquery.cookie.js"
	type="text/javascript" charset="utf-8"></script>
<script
	src="${request.contextPath}/resources/js/jquery/jquery-treeview/jquery.treeview.js"
	type="text/javascript" charset="utf-8"></script>
<script type="text/javascript"
	src="${request.contextPath}/resources/js/jquery/jquery-jsontreeviewer/jquery.jsontreeviewer.js"></script>	

<style type="text/css">

</style>

<script type="text/javascript">
	$(function() {
		
		
		//Initialise JQUERY4U JSON Tree Viewer
		JSONTREEVIEWER.init();
		//Events to load avro schema json
		JSONTREEVIEWER.processJSONTree('json');
		
		
	});
	
	function nextRecord(recordNumber) {
		 $.ajax({
				type : 'GET',
				url : '${request.contextPath}/hdfsBrowser${path}.html?startRecord=' + recordNumber,
				async : false,
				beforeSend : function(x) {
					if (x && x.overrideMimeType) {
						x.overrideMimeType('application/j-son;charset=UTF-8');
					}
				},
				dataType : 'html',
				success : function(data) {
					$('#loading').show();
					$('#infoToRefresh').html(data);
					/* Build JSON Tree */
					JSONTREEVIEWER.buildTree(JSONTREEVIEWER
							.processNodes($('#inputJson').text()));
					JSONTREEVIEWER.processJSONTree('json');
					$('#loading').hide();
				},
				error : function(e) {
					alert('Invalid Page : ' + e.responseText);
				}
			});
	};
	
	function schema() {
		$('#schemaModal').modal('show');
		 $.ajax({
				type : 'GET',
				url : '${request.contextPath}/hdfsAvroSchema${path}.html',
				async : false,
				beforeSend : function(x) {
					if (x && x.overrideMimeType) {
						x.overrideMimeType('application/j-son;charset=UTF-8');
					}
				},
				dataType : 'html',
				success : function(data) {
					$('#schemaModalBody').html(data);
				},
				error : function(e) {
					alert('Invalid Page : ' + e.responseText);
				}
			});	
	}
	
    <%-- Get CODEC --%>
	function codec() {
		$.getJSON('${request.contextPath}/hdfsAvroCodec${path}.html',
				function(data) {
					$('#codec').html('<span class="label label-success">' + data + "</span>");
				});
	};
</script>


