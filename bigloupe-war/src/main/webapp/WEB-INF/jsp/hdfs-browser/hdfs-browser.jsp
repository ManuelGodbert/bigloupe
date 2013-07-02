<%@page import="org.apache.hadoop.fs.FileSystem"%>
<%@page import="org.bigloupe.web.hdfs.HDFSFileSystemManager"%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="org.bigloupe.web.util.GuiUtils"%>
<%@page import="java.util.Date"%>
<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@page import="org.apache.hadoop.util.StringUtils"%>
<%@page import="org.apache.hadoop.fs.FileStatus"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%-- Modal window - Action delete record --%>
<div id="deleteModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Delete this file ?</h3>
	</div>
	<div class="modal-body">
		<p></p>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Cancel</a> <a
			href="javascript:deleteFile();" class="btn btn-primary">Delete</a>
	</div>
</div>

<%-- Modal window - Action merge directory --%>
<div id="mergeModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Merge this directory in a file ?</h3>
	</div>
	<div class="modal-body">
		<p>Take this source directory and a destination file as input and concatenate files in src into the destination local file.</p>
		<label>Destination file</label><input id="mergeDirectoryDestination" type="text" name="mergeDirectoryDestination" class="span4">
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Cancel</a> <a
			href="javascript:mergeDirectory();" class="btn btn-primary">Merge</a>
	</div>
</div>

<%-- Modal window - Action compact directory --%>
<div id="compactModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Compact with a Map/Reduce this directory in a new directory ?</h3>
	</div>
	<div class="modal-body">
		<p>Take this source directory and a destination file as input and compact with a map/reduce all files in source directory 
		into the destination file.	
		</p>
		<label>Destination file</label><input id="compactDirectoryDestination" type="text" name="compactDirectoryDestination" class="span4">
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Cancel</a> <a
			href="javascript:compactDirectory();" class="btn btn-primary">Compact</a>
	</div>
</div>

<%-- Modal window - Action index directory or file --%>
<div id="indexModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Index with a Map/Reduce in ElasticSearch ?</h3>
	</div>
	<div class="modal-body">
		<p>ElasticSearch helps us to search easily your data in your HDFS files</p>
		<label>Index name </label><input id="elasticSearchIndexName" type="text" name="elasticSearchIndexName" class="span4"><span id="checkIndex"></span>
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Cancel</a><a
			id="indexButton" href="javascript:indexAvro();" class="btn btn-primary disabled">Index</a>
	</div>
</div>

<%-- Modal window - Action filter directory or file --%>
<div id="filterModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Filter with a Map/Reduce Avro files or directory ?</h3>
	</div>
	<div class="modal-body">
		<p>Fill your filter :</p>
		<label>Parameter </label><select id="pigFilterParam" name="pigFilterParam" class="span4"></select>
		<label>Value <span id="pigFilterType"></span></label><input id="pigFilterValue" type="text" name="pigFilterValue" class="span4">
		<p><label>Destination directory</label></p>
		<input id="pigFilterDirectoryDestination" type="text" name="pigFilterDirectoryDestination" class="span4">
	</div>
	<div class="modal-footer">
		<a href="#" class="btn" data-dismiss="modal">Cancel</a><a
			id="indexButton" href="javascript:filterAvro();" class="btn btn-primary disabled">Filter</a>
	</div>
</div>

<%-- Modal - Action upload --%>
<div id="uploadModal" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Upload a file in HDFS</h3>
	</div>
	<form action="${request.contextPath}/hdfsAction/upload.html" method="POST" class="well form-inline" enctype="multipart/form-data">
	<div class="modal-body">
		<p>
		
		
			<label>Upload file</label> 
			<input type="file" name="file" class="span6"/>
			<input name="path" value="${paths}" type="hidden" />
		
		
		</p>
	</div>
	<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal">Close</a>
			<input type="submit" class="btn btn-primary"/>
	</div>
	</form>
</div>

<%-- Modal for help relative to a quick link to HDFS --%>
<div id="helpGotoHDFS" class="modal hide">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">×</button>
		<h3>Help for "Goto" feature</h3>
	</div>
	<div class="modal-body">
		<p>
			This feature help you to go quickly to a specific path and server. Different urls can be accepted :
			<ul>
				<li><b>hdfs://server:port/path</b>&nbsp;: go to a specific HDFS server and port and in a specific path</li> 
				<li><b>/path</b>&nbsp; : go directly to path in current HDFS browser </li>
			</ul>	
		</p>
	</div>
	<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal">Close</a>
	</div>
</div>



<div class="row">
	<div class="page-header span8">
		<div class="span">
		<h2>
			HDFS Browser&nbsp;&nbsp;
		</h2>
		</div>
		<div class="span8">
				<div class="dropdown">
					<a class="dropdown-toggle" data-toggle="dropdown" href="#"><%=((BigLoupeConfiguration) pageContext
					.findAttribute("configuration"))
					.getCurrentHadoopCluster(request)%></a>
					<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
						<li><a href="${request.contextPath}/diskUsageHdfs/<%= ((BigLoupeConfiguration) pageContext
								.findAttribute("configuration"))
								.getCurrentHadoopCluster(request) %>.html">Disk Usage</a></li>
						<li><a href="${request.contextPath}/editHdfsConfiguration/<%= ((BigLoupeConfiguration) pageContext
								.findAttribute("configuration"))
								.getCurrentHadoopCluster(request) %>.html">Configuration</a></li>
					</ul>
				</div>
		</div>



	</div>
	<div class="span4">
		<p>&nbsp;</p>
		<h6>
			Download file size <small><div id=downloadSize>${downloadSize}%</div>
				&nbsp;<a id="increaseSize" href="#"><i class="icon-plus"></i></a><a
				id="decreaseSize" href="#"><i class="icon-minus"></i></a></small>
		</h6>
		<div class="progress progress-striped">
			<div class="bar" style="width: ${downloadSize}%;"></div>
		</div>
	</div>
</div>
<div class="row">
	<div id="result"></div>
</div>

<header id="overview" class="jumbotron subhead">

	<div id="subnavbar" class="subnav subnav-fixed">
		<div class="row">
			<ul id="breadcrumb" class="breadcrumb span9">
				<a href="${request.contextPath}/hdfsBrowser.html">/</a>
				<c:forEach var="path" items="${paths}" varStatus="rowCounter">
					<c:choose>
						<c:when test="${sessionScope.clusterHadoop != null}">
							<a href="${request.contextPath}/${sessionScope.clusterHadoop}/hdfsBrowser${path}.html">
						</c:when>
						<c:otherwise>
							<a href="${request.contextPath}/hdfsBrowser${path}.html">
						</c:otherwise>
					</c:choose>${segments[rowCounter.index]}</a>
					<span class="divider">/</span>
				</c:forEach>
				<span class="input-prepend"><button id="copy-button" data-clipboard-text="${paths[fn:length(paths)-1]}" data-toggle="tooltip" title="Copy path '${paths[fn:length(paths)-1]}' in clipboard" class="btn btn-small" ><i class="icon-file"></i>&nbsp;</button></span><span class="input-append"><a href="javascript:goToHDFS()" class="btn btn-small" data-toggle="tooltip" title="Go to a specific path" data-dismiss="modal"><i class="icon-folder-open"></i>&nbsp;Goto</a></span>
			</ul>
			<span class="input-prepend">
			&nbsp;<button id="upload" class="btn btn-mini btn-success " onclick="upload()">Upload</button>
			<button id="showDirectorySize" class="btn btn-mini btn-success " onclick="showDirectorySize()">Show size</button></span>
			<span class="input-append"><button id="activeClipboard" class="btn btn-mini btn-success " onclick="clipBoard()">Active Clipboard</button></span>
		</div>
	</div>
</header>


<div class="row"  style="margin: 40px">
	<table class="table table-striped">
		<thead>
			<th>File</th>
			<th>Modification Time</th>
			<th>Size <span class="divider">/</span> Block Size</th>
			
			<th></th>
		</thead>
		<c:forEach var="status" items="${subdirs}" varStatus="rowCounter">

			<tr>
				<%
					FileStatus fileStatus = (FileStatus) pageContext
										.getAttribute("status");
				%>
				<td><c:choose>
						<c:when test="${(sessionScope.clusterHadoop != null) && (not empty sessionScope.clusterHadoop)}">
							<a
								href="${request.contextPath}/${sessionScope.clusterHadoop}/hdfsBrowser<%=fileStatus.getPath().toUri().getPath()%>.html">
						</c:when>
						<c:otherwise>
							<% BigLoupeConfiguration configuration = (BigLoupeConfiguration)request.getAttribute("configuration"); %>
							<a
								href="${request.contextPath}/<%= configuration.getCurrentHadoopCluster(request) %>/hdfsBrowser<%=fileStatus.getPath().toUri().getPath()%>.html">
						</c:otherwise>
					</c:choose> ${status.path.name}
						 <%
						 	if (fileStatus.isDir()) {
						 %>/<%
						 	}
						 %>
				</a></td>
				<td style="text-align: center"><%=GuiUtils.formatDate(
						new DateTime(fileStatus.getModificationTime()),
						"yyyy-MM-dd HH:mm")%></td>
				<td style="text-align: center"><c:choose>

						<c:when test="${status.dir}">dir
						
						<c:if test="${param.showDirectorySize}">
						<%
							String fsCanonicalServiceName = (String) request.getSession()
																.getAttribute(BigLoupeConfiguration.FILESYSTEM);
														if (fsCanonicalServiceName != null) {
															FileSystem fs = HDFSFileSystemManager.getFileSystem(fsCanonicalServiceName);
															try {
						%>
						&nbsp;-&nbsp;<span class="label label-success"><%=StringUtils.byteDesc(fs
										.getContentSummary(fileStatus.getPath())
										.getLength())%></span>
						<%
							} catch (Exception e) {
											}
										}
						%>
						</c:if>
								</c:when>
						<c:otherwise><%=StringUtils.byteDesc(fileStatus.getLen())%>/<%=StringUtils.byteDesc(fileStatus
								.getBlockSize())%></c:otherwise>
					</c:choose></td>
				<td>

					<form id="hdfsAction${rowCounter.index}"
						action="${request.contextPath}/hdfs.html" method="post">
						<input type="hidden" name="file"
							value="<%=fileStatus.getPath().toUri().getPath()%>" /> <input
							type="button" class="btn btn-primary"
							id="delete${rowCounter.index}" value="Delete" /> <input
							type="button" class="btn btn-primary"
							id="blockFinder${rowCounter.index}" value="Block finder" />
							<%-- Action for directory --%>
							<c:if test="${status.dir}">
								<input type="button" class="btn btn-warning" id="merge${rowCounter.index}" value="Merge" />
								<input type="button" class="btn btn-warning" id="compact${rowCounter.index}" value="Compact" />
							</c:if>
							<%-- Action for file --%>
							<c:if test="${!status.dir}">
							<a href="${request.contextPath}/hdfsAction/download<%=fileStatus.getPath().toUri().getPath()%>.html"><input
							type="button" class="btn btn-warning"
							id="download${rowCounter.index}" value="Download" /></a> <input
							type="button" class="btn btn-warning"
							id="view${rowCounter.index}" value="View extraction" /> 
							</c:if>
							<input type="button" class="btn btn-warning" id="index${rowCounter.index}" value="Index" />
							<input type="button" class="btn btn-warning" id="filter${rowCounter.index}" value="Filter" />
					</form>
				</td>
			</tr>
		</c:forEach>
	</table>
</div>
