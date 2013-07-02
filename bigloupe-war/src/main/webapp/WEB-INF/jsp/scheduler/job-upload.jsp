<%@page import="org.joda.time.DateTime"%>
<%@page import="org.bigloupe.web.scheduler.flow.ExecutableFlow"%>
<%@page import="org.bigloupe.web.util.GuiUtils"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.joda.org/joda/time/tags" prefix="joda"%>
<div class="row">
	<div class="page-header">
		<h2>Job Upload&nbsp;<small>Must be a ZIP file containing job or scripts files</small></h2>
	</div>
</div>

<div class="row">
	<form id="uploadJob" class="well" action="${request.contextPath}/scheduler/uploadJobs.html" enctype="multipart/form-data" method="POST">
		<label>File :</label>
		<input type="file" name="files" style="height: 25px;width: 400px" class="span8"/>
		<label>Path : </label>
		<input type="text" name="path" style="height: 25px" class="span8" value="${path}"/>
		<input type="submit" class="btn btn-primary" value="Upload"/>
	</form>
</div>