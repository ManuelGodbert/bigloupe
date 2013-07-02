<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="container" style="margin: 40px">
	<div class="row">
		<h6>Pig Variables</h6>
	</div>


	<div class="row">
		<c:choose>
		<c:when test="${empty pigOption.scriptPigVariables}">
			<div class="well form-inline">
				No variables
			</div>
		</c:when>
		<c:otherwise>
		<c:forEach var="scriptPigVariable"
			items="${pigOption.scriptPigVariables}" varStatus="rowCounter">
			<div class="well form-inline">
				<fieldset>
					<label class="span4">
						<h5>Pig variable ${rowCounter.index + 1} :
							${scriptPigVariable.name}</h5>
					</label> <input type="text"
						name="scriptPigVariables[${rowCounter.index}].value"
						cssClass="input-xlarge" style="height: 30px" /> <input
						type="hidden" name="scriptPigVariables[${rowCounter.index}].name"
						value="${scriptPigVariable.name}" />


				</fieldset>
			</div>

		</c:forEach>
		</c:otherwise>
		</c:choose>
		<div class="form-actions">
			<a href="javascript:openSaveJob()"><input type="button"
				class="btn btn-warning" id="saveConfiguration"
				value="Save Pig configuration as a scheduled job"
				alt="Save Pig configuration as a scheduled job" /></a>
		</div>
	</div>
</div>