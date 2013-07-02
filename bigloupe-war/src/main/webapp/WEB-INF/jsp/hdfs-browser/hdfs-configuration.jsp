<%@page import="org.apache.hadoop.util.PlatformName"%>
<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@page import="org.apache.hadoop.util.StringUtils"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@page import="org.apache.hadoop.conf.Configuration"%>
<%@page import="org.apache.hadoop.fs.FileSystem"%>

<%
	FileSystem fs = (FileSystem) request.getAttribute("fileSystem");
%>
<% if (fs!= null) { %>
<div class="row">
	<div class="span8">
		Status :
		<%
		long total = fs.getStatus().getCapacity();
		long remaining = fs.getStatus().getRemaining();
		long used = fs.getStatus().getUsed();
		long nonDFS = total - remaining - used;
		nonDFS = nonDFS < 0 ? 0 : nonDFS;
		float percentUsed = total <= 0 ? 0f : ((float) used * 100.0f)
				/ (float) total;
		float percentRemaining = total <= 0 ? 100f
				: ((float) remaining * 100.0f) / (float) total;
	%>

		<small>Capacity</small> <strong><%=StringUtils.byteDesc(total)%></strong>
		<small>Used&nbsp;</small><strong><%=StringUtils.limitDecimalTo2(percentUsed)%>%</strong>
		<small>Remaining</small> <strong><%=StringUtils.limitDecimalTo2(percentRemaining)%>%</strong>
	</div>
	<div class="span8">
		Default Block size : <strong><%=StringUtils.byteDesc(fs.getDefaultBlockSize())%></strong>
	</div>
	<div class="span12">
		<%
			Configuration conf = fs.getConf();
		%>
		FileSystem :
		<%=conf.get("fs.file.impl")%>
	</div>


	<div class="span12">&nbsp;</div>
</div>

<div class="row-fluid">
	<table class="table table-bordered table-striped">
		<thead>
			<tr>
				<th>Key</th>
				<th>Value</th>
			</tr>
		</thead>
		<tbody>
			<%
				for (Entry<String, String> item : conf) {
			%>

			<tr>
				<td><%=(String) item.getKey()%></td>
				<td><%=(String) item.getValue()%></td>
			</tr>
			<%
				}
			%>
		</tbody>
	</table>
</div>
<% } else { %>
<div class="row">
No HDFS fileSystem available in
<% BigLoupeConfiguration configuration = (BigLoupeConfiguration)request.getAttribute("configuration");  %>
<%=configuration.getCurrentHadoopCluster(request) %>
</div>
<% } %>
