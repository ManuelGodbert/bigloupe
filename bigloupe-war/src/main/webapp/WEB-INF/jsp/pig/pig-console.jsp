<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript"
	src="${request.contextPath}/resources/js/atmosphere/jquery.atmosphere.js"></script>

<script type="text/javascript">
	$(document).ready(
			function() {
				var socket = $.atmosphere;
				var subSocket;

				function subscribe() {
					var request = {
						url : 'http://' + document.location.host
								+ '${request.contextPath}/pubsub/pig.html',
						logLevel : 'debug',
						contentType : 'application/json',
						transport : 'long-polling'
					};

					request.onMessage = function(response) {
						var message = response.responseBody;
						//console.log('Message : ', message);
						if (response.status == 200) {
							var data = response.responseBody;
							if (data.length > 0) {
								$('#result tbody').html(data);
							}
						}
					};

					subSocket = socket.subscribe(request);
				}

				function connect() {
					socket.unsubscribe();
					subscribe();
				}

				connect();
				

			});
	
	/// Change directory
	function changeLogDirectoryToScan(logDir) {
		$.getJSON('${request.contextPath}/changeLogDirectoryToScan.html', 
				{ logDir : logDir }, function() {
					location.reload();			
				});
		
	};
	
	function deleteLog() {
		$.getJSON('${request.contextPath}/deleteLog.html', function(data) {
					$('#message').html(data.message[0]);
		});	
	}
	
</script>

<div class="row">
	<div class="page-header">
		<h2>
			Pig Console&nbsp;&nbsp;<small>All log files list is refreshed
				every 5 seconds</small>
		</h2>
	</div>
</div>

<%-- 
<button id="submit" type="submit" class="btn">Submit</button>
--%>
<div id="message"></div>

<div id="result" class="row">
	<table class="table">
		<thead>
			<tr>
				<th>Log files (<c:choose>
						<c:when test="${configuration.pigLogDirOrSchedulerLogDir}">
							<a 
								id="pig" href="javascript:changeLogDirectoryToScan('true')"
								rel="tooltip" title="${pigPath}" class="label label-important">Pig
								directory ON</a>/<a id="scheduler"
								href="javascript:changeLogDirectoryToScan('false')"
								rel="tooltip" title="${schedulerPath}" class="label">Scheduler
								directory OFF</a>
						</c:when>
						<c:otherwise>
							<a
								id="pig" href="javascript:changeLogDirectoryToScan('true')"
								rel="tooltip" title="${pigPath}" class="label">Pig
								directory OFF</a>/<a id="scheduler"
								href="javascript:changeLogDirectoryToScan('false')"
								rel="tooltip" title="${schedulerPath}" class="label label-important">Scheduler
								directory ON</a>						
						</c:otherwise>
					</c:choose>) - <a href="javascript:deleteLog()"><i class="icon-trash"></i> Delete all logs</a>
				</th>
				<th>&nbsp;</th>
				<th>&nbsp;</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>


</div>