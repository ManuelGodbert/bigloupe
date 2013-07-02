<%@page import="org.apache.hadoop.fs.FileSystem"%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="org.bigloupe.web.util.GuiUtils"%>
<%@page import="java.util.Date"%>
<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@page import="org.apache.hadoop.util.StringUtils"%>
<%@page import="org.apache.hadoop.fs.FileStatus"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="row">
	<div class="page-header span8">
		<h2>
			HDFS Disk Usage&nbsp;&nbsp;<small>Cluster
				<%=((BigLoupeConfiguration)pageContext.findAttribute("configuration")).getCurrentHadoopCluster(request)%></small>
		</h2>
	</div>
</div>
<div class="row">
	<div id="breadcrumb">/</div>
	<div class="description">
		Color encodes average file size in folders. Numerous small files are
		not desirable in HDFS.

		<div class="range">
			<div class="from">contains efficient files</div>
			<div class="to">contains inefficient files</div>
		</div>

	</div>
</div>

<div class="row">
 	<div class="span6">
	    <div id="treemap"></div>
	    <div id="controls">
	        <ul>
	            <li>
	                <button id="back">Up one level</button>
	            </li>
	            <li>
	                <button id="size" class='selected'>File size as area</button>
	            </li>
	            <li>
	                <button id="count">File count as area</button>
	            </li>
	        </ul>
	    </div>
    </div>
    
    <div class="span6">  
        <div class="widget right">
        	<div id="filetree"></div>
    	</div>
	</div>
</div>

