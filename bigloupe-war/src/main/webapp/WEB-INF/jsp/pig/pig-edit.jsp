<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="row" style="margin: 40px">
	<div class="page-header">
		<h2>Pig Editor</h2>
	</div>
	
<a class="btn btn-info" href="javascript:savePigScriplet();">Save</a>	
<div id="embed_ace_wrapper" class="ace_editor_wrapper">
	<div id="embedded_ace_code">${pigContent}</div>
</div>

</div>
	
<script type="text/javascript">
	var editor = ace.edit("embedded_ace_code");
    editor.setTheme("ace/theme/monokai");
    <%-- Setting the programming language mode --%>
    editor.getSession().setMode("ace/mode/pig");
    
	function savePigScriplet() {
		var content = editor.getSession().getValue();
		$.post("${request.contextPath}/savePig/${pigFileName}.html?pigPath=${pigPath}", 
	            {content: content },
	            function() {
	                    // add error checking
	                    alert('Successful save');
	            }
	    );
	}
	
</script>