<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	$(document).ready(
			function() {
				<%-- Launch pig script --%>
				$('#launch').click(
						function() {
							$('#loadingmessage').show();
							$.ajax({
								type : 'POST',
								url : '${request.contextPath}/pig.html',
								dataType : 'html',
								data : $("#pigOption").serialize(),
								success : function(html, textStatus) {
									$('#result').html(html);
									$('#loadingmessage').hide(); 
								},
								error : function(xhr, textStatus, errorThrown) {
									 
								}
							});
						});
				$('#scriptPig').change(
						function() {
							$.ajax({
								type : 'POST',
								url : '${request.contextPath}/pigVariables.html',
								dataType : 'html',
								data : $(this).serialize(),
								success : function(html, textStatus) {
									$('#pigVariables').html(html);
								},
								error : function(xhr, textStatus, errorThrown) {
									 
								}
							});
						});
				
			});
	
	<%-- Save job --%>
	function openSaveJob() {
		$('#saveJobModal').modal('show');
	};

	<%-- Save job --%>
	function saveJob() {
		$.ajax({
			type : 'POST',
			url : '${request.contextPath}/scheduler/saveJob.html',
			dataType : 'json',
			data : $('#pigOption').serialize(),
			success : function(
					result,
					textStatus) {
				$('#result')
						.addClass(
								'alert')
						.html(
								'<p>'
										+ result.message
										+ '<p>');
			},
			error : function(xhr,
					textStatus,
					errorThrown) {

			}
		});
		$('#saveJobModal').modal('hide');
	};

	<%-- Add new scriptlet pig --%>
	function openUploadPigScriplet() {
		$('#uploadPigScripletModal').modal('show');
	};
	
	function editPigScriplet() {
		if ($('#scriptPig option:selected').val() != "NONE") {
			var editorWindow = window.open('${request.contextPath}/editPig/' + $('#scriptPig option:selected').val() + '.html', 'pigeditor');
			if (window.focus) { 
				editorWindow.focus();
			}
		} else {
			$('#helpEditPigScripletModal').modal('show');
		}
	}
</script>

<div class="row">
	<div class="page-header">
		<h2>Pig Launcher</h2>
	</div>
</div>

<c:if test="${pigException !=null}">
	<div class="alert">
		${pigException.message}<small>${pigException.cause}</small>
	</div>

</c:if>
<form:form commandName="pigOption">
	<div class="row">
		<div id="result"></div>
	</div>
	<div class="row">
		<div class="span4">
			<h4>Pig script</h4>
		</div>
		<div class="span4">
			<form:select path="scriptPig">
				<form:option value="NONE" label="--- Select ---" />
				<form:options items="${scriptPigList}" />
			</form:select>

			<form:errors path="scriptPig" cssClass="error" />
			<a href="javascript:openUploadPigScriplet();" class="btn btn-info">+</a>
			<a href="javascript:editPigScriplet();" class="btn btn-info">Edit</a>
		</div>
	</div>

	<div class="row">
		<div class="span4">&nbsp;</div>
		<div class="span4">
			<input type="button" id="launch" class="btn btn-primary btn-large" value="Launch Pig script"/>
			<div id="loadingmessage" style="display:none"><img src="${request.contextPath}/resources/img/loader.gif"/></div>
		</div>
	</div>


<%-- Pig variables  --%>
<div id="pigVariables" class="row-fluid">

</div>

<%-- Result Pig launch --%>
<div id="result" class="row-fluid">

</div>
	
<%-- Modal window for save pig configuration to schedule a new job --%>
<div id="saveJobModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Save this configuration as a scheduled job ?</h3>
	</div>
	<div class="modal-body">
		<p>
		<div class="well">
			<label>Job name</label> 
			<input type="text" name="jobName" class="input-xlarge" style="height: 35px"/>
			<label>Job path</label>
			<input type="text" name="jobPath" class="input-xlarge" style="height: 35px"/>
			<input type="hidden" name="jobType" value="pig"/>
		</div>
		</p>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Close</a> <a
			href="javascript:saveJob();" class="btn btn-primary">Save</a>
	</div>
</div>

</form:form>

<%-- Modal window for save pig configuration to schedule a new job --%>
<div id="uploadPigScripletModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Upload new Pig scriptlet ?</h3>
	</div>
	<div class="modal-body">
		<p>
		<form id="uploadScriptlet" action="${request.contextPath}/uploadPig.html" method="POST" enctype="multipart/form-data">
		<div class="well">
			<label>Script Pig to upload</label>
			<input type="file" name="file" class="input-xlarge" style="height: 35px"/>
		</div>

		</p>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Close</a> <input type="submit"
			 class="btn btn-primary" value="Upload"/>
	</div>
			</form>
	
</div>

<%-- Modal to help user when editing a pig file --%>
<div id="helpEditPigScripletModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Edit a Pig scriptlet ?</h3>
	</div>
	<div class="modal-body">
		<p>
			You must select your pig script to edit it. Upload your local pig file if it's a new one.
		</p>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Close</a> 
	</div>
</div>

