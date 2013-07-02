<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>



<div class="row-fluid">
	<div class="page-header">
		<h6>Pig Console</h6>
	</div>
</div>
<c:if test="${pigException !=null}">
	<div class="alert">
		${pigException.message}<small>${pigException.cause}</small>
	</div>
</c:if>
<div class="row-fluid">
	<pre>
	${output}
	</pre>
</div>