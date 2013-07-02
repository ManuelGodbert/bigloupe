<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row-fluid">
	<div class="span2">
		<label>SQL Where clause (--where)</label>
	</div>
	<div class="span10">
		<input type="text" name="jdbcWhereClause"
			value="${sqoopJob.jdbcWhereClause}" class="input-xlarge"
			style="height: 30px" />
	</div>
</div>

<div class="row-fluid">
	<div class="span2">
		<label>Map tasks to import in parallel </label>
	</div>
	<div class="span10">
		<input type="text" name="numberMappers"
			value="${sqoopJob.numberMappers}" class="input-xlarge"
			style="height: 30px" />
	</div>
</div>

<div class="row-fluid">
	<div class="span2">
		<label>HDFS Target Directory (--target-dir)</label>
	</div>
	<div class="span10">
		<input type="text" name="hdfsTargetDir"
			value="${sqoopJob.hdfsTargetDir}" class="input-xlarge"
			style="height: 30px" />
	</div>
</div>

<div class="row-fluid">
	<div class="span2">
		<label>Remove HDFS Target Directory before launching</label>
	</div>
	<div class="span10">
		<input type="checkbox" name="deleteHdfsTargetDir" value="true" class="input-xlarge"
			<c:if test="${sqoopJob.deleteHdfsTargetDir}">checked="checked" 
							</c:if>
			style="height: 30px" />
	</div>
</div>


<div class="row-fluid">
	<div class="span2">
		<label>Avro Data file (--as-avrodatafile)</label>
	</div>
	<div class="span10">
		<input type="checkbox" name="avroDataFile" value="true" class="input-xlarge"
			<c:if test="${sqoopJob.avroDataFile}">checked="checked" 
							</c:if>
			style="height: 30px" />
	</div>
</div>

<div class="row-fluid">
	<div class="span2">
		<label>Avro Schema jar files (--avsc-jar-file ${file}.jar)</label>
	</div>
	<div class="span10">
		<label>Current file : ${sqoopJob.avscJarFile}</label>
		<input type="file" name="avscJarFile"/>
</div>