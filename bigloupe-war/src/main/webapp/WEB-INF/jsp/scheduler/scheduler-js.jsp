
<link rel="stylesheet"
	href="${request.contextPath}/resources/js/jquery/jquery-treeview/jquery.treeview.css"
	type="text/css" media="screen" charset="utf-8">
	
<style type="text/css">
.jobtable {
    border-collapse: collapse;
    border-radius: 5px 5px 5px 5px;
    border-spacing: 0;
    font-size: 100%;
    margin: auto;
}
.jobtable th {
    padding: 5px;
}
.jobtable td {
    border-color: rgba(255, 255, 255, 0.3);
    border-style: solid;
    border-width: 1px;
    padding: 5px;
}
.jobfolder .folderIcon {
    cursor: pointer;
    float: left;
    height: 16px;
    margin-left: 2px;
    margin-right: 5px;
    margin-top: 7px;
    width: 16px;
}
.jobfolder.expand .folderIcon {
    background-image: url("${request.contextPath}/resources/css/default/images/ui-icons_222222_256x240.png");
    background-position: -32px -16px;
}
.jobfolder.jobfoldercollapse .folderIcon {
    background-image: url("${request.contextPath}/resources/css/default/images/ui-icons_222222_256x240.png");
    background-position: 0 -16px;
}
.jobfolder.wait .folderIcon {
    background-image: url("${request.contextPath}/resources/css/default/images/ui-icons_222222_256x240.png");
    background-position: -64px -80px;
}
/* Index page job list*/
.joblist {

	text-align: left;
	margin-left: 20px;
	margin-right: 20px;
	margin-top: 15px;
	padding: 2px 3px;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
}

.joblist a {
	color: #A7CEFF;
	padding-left:3px;
	text-decoration:none;
	vertical-align:top;
}

.joblist a:hover {
	color: #FFFFFF;
	padding-left:3px;
	text-decoration:none;
	vertical-align:top;
}

.joblist li {
	background-color: transparent;
}

.joblist li {
	xlist-style-type: none;
	xpadding: 1px;
	xmargin: 1px;
}

/* Index page schedule*/

.jobfolder p{
	padding: 7px 10px 7px 10px;
	margin: 2px;
	cursor: pointer;
	text-shadow: rgba(0,0,0, 0.4) 0px 0px 0.2em;
}

.jobfolder {
	color: #FFFFFF;
	font-size: 12pt;
	
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;

	background: #A0A5AF; /* old browsers */
	background: -moz-linear-gradient(top, #A0A5AF 0%, #828c95 36%, #28343b 100%); /* firefox */
	background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#A0A5AF), color-stop(36%,#828c95), color-stop(100%,#28343b)); /* webkit */
	filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#A0A5AF', endColorstr='#28343b',GradientType=0 ); /* ie */
}

.jobtable a {
	text-decoration: none;
	color: #A7CEFF;
}

.jobtable .job-name-FAILED {
	color: darkred;
}

.jobtable .job-name-READY {
	color: #CCC; 
}

.jobtable .job-name-RUNNING {
	color: #008;
}

.jobtable .job-name-SUCCEEDED {
	color: #0C5;
}

.jobtable .job-name-COMPLETED {
	color: #CCC;
}

.jobtable .job-name-IGNORED {
	color: #888;
}

.jobdesc {
	margin: 10px;
	color: #AAA;
}

.jobdesc input {
	margin-bottom: 10px;
}

.jobdesc a{
	text-decoration: none;
}

.jobtable a:hover {
	color: #FFF;
}

.treeview {
	text-align: left;
}

.treeview ul {
	background-color: transparent;
	padding: 1 0 3 16px;
}

.treeview .hitarea {
	background-color: transparent;
	xpadding-right: 3px;
}

.translucent {
	background-color: rgba(255, 255, 255, .25);
}

.flowViewButton  {

}

.flowViewButton.hidden{
	visibility: hidden;
}

.flowViewButton.show{
}

.flowViewButton.show:hover{
}

#sched-tree-control {
	background-color: pink;
	text-align: left;
	padding-top: 1em;
}

#sched-tree-control a {
	color: darkred;
}

#sched-tree {
	background-color: transparent;
	padding: .5em;
}

.zoomSlider line {
	stroke-width: 4;
	stroke: #777;
	stroke-dasharray: 3, 25;
}

.zoomButton {
	cursor: pointer;
}

.zoomButton rect {
	fill: #CCC;
}

.zoomButton circle {
	fill: #666;
}

.zoomButton:hover rect{
	fill: #FF0;
}

span.running {
	color: #037;
}

span.failed {
	color: #A00;
}

span.succeeded {
	color: #070;
}

span.ready {
	color: #444;
}

rect.button.running  {
	fill: #037;
}

rect.button.failed  {
	fill: #A00;
}

rect.button.succeeded  {
	fill: #070;
}

rect.button {
	fill: #444;
	stroke: #000;
	stroke-width: 3px;
}

g.node {
	cursor: pointer;
}

g.node text{
	fill: #FFF;
}

g.node.selected text{
	fill: #EE0;
}

g.node:hover {
	stroke-width: 0.5px;
	stroke: #0091D8;
}

g.node.disabled {
	opacity: 0.2;
}

rect.zoomPicker {
	fill: #666;
	cursor: pointer;
}

rect.zoomLine {
	fill: #AAA;
}

#graph{ 
	width: 100%;
	position: relative;
	top: 0px;
	left: 0px;
}

#graphTab{
	background: #F1F1F1;
	height: 510px;
}

#navigation {
	float:left;
	width:270px;
	margin-left:-100%;
	/*background: #FFFFFF;*/
	height:561px;
}

</style>

<script
	src="${request.contextPath}/resources/js/jquery/jquery.cookie.js"
	type="text/javascript" charset="utf-8"></script>
<script
	src="${request.contextPath}/resources/js/jquery/jquery-treeview/jquery.treeview.js"
	type="text/javascript" charset="utf-8"></script>

<script type="text/javascript" charset="utf-8">
	$(function() {
		var treeElems = $(".execing-jobs");
		if (treeElems.length > 0) {
			var execingJobsTree;
			for (i = 0; i < treeElems.length; i++) {
				$("#" + treeElems[i].id).treeview({
					collapsed : true,
					animated : "medium",
					persist : "cookie"
				});
			}
		}

 		$(".jobfolder").each(
				function(index) {
					var d = new Date();
					var numMinuteThreshold = 15;
					if ($.cookie(this.id)) {
						if ($.cookie(this.id) > d.getTime()
								- numMinuteThreshold * 60000) {
							expandFlow(this);
						} else {
							$.cookie(this.id, null);
						}
					}
				}); 
	});

	function runJob(id, withDep, contextURL, callback, modal) {
		if (!callback) {
			callback = function() {
			};
		}
		jQuery.ajax({
			'type' : 'POST',
			'url' : '${request.contextPath}/scheduler/runJob.html',
			'async' : false,
			'data' : {
				"action" : "run_job",
				"id" : id,
				"include_deps" : withDep
			},
			'success' : function(data) {
				var title = "Info";

				if (data.success) {
					$("#modelDialog").text(data.success);
				} else {
					title = "ERROR";
					$("#modelDialog").text(data.error);
				}

				// Just take in account the null/undefined case.
				var isModal = false;
				if (modal) {
					isModal = true;
				}

				$("#modelDialog").dialog({
					width : '350px',
					modal : isModal,
					close : function(event, ui) {
						callback();
					},
					title : title,
					buttons : [ {
						text : "Ok",
						click : function() {
							$(this).dialog("close");
						}
					} ]
				});
			}
		});
	}

	function getList(data) {
		var jobName = data.name;

		var li = document.createElement("li");
		li['jobname'] = jobName;
		// Setup checkbox
		var input = document.createElement("input");
		input.setAttribute("id", jobName + "-checkbox");
		input.setAttribute("type", "checkbox");
		input.setAttribute("name", "jobs");
		input.setAttribute("value", jobName);
		input.setAttribute("class", "sched-tree-checkbox");
		li.appendChild(input);

		
		// Setup anchor
		var a = document.createElement("a");
		a
				.setAttribute("href",
						"${request.contextPath}/scheduler/jobDetail.html?id="
								+ jobName);
		a.setAttribute("class", "job-name");
		a.setAttribute("name", "sched-tree-link");
		$(a).text(jobName);
		li.appendChild(a);

		var divBtn = document.createElement("div");
		divBtn.setAttribute("class", "btn-group");
	    
	    
		// Setup flow button
		var flowButton = document.createElement("a");
		$(flowButton).text("View Flow");
		flowButton.setAttribute("id", jobName + "-flowbutton");
		flowButton.setAttribute("class", "flowViewButton btn btn-danger btn-mini span2");
		flowButton.setAttribute("href",
				"${request.contextPath}/scheduler/flow.html?job_id=" + jobName);
		$(flowButton).addClass("hidden");
		divBtn.appendChild(flowButton);

		var runButton = document.createElement("a");
		$(runButton).text("Run");
		runButton.setAttribute("id", jobName + "-runbutton");
		runButton.setAttribute("class", "flowViewButton btn btn-warning btn-mini span2");
		runButton.setAttribute("href", "#");
		$(runButton).addClass("hidden");
		$(runButton).click(
				function() {
					runJob(jobName, false,
							'${request.contextPath}/scheduler/runJob.html',
							function() {
								window.location.reload()
							}, true);
				});
		divBtn.appendChild(runButton);

		var runDepButton = document.createElement("a");
		$(runDepButton).text("Run with Dependencies");
		runDepButton.setAttribute("id", jobName + "-rundepbutton");
		runDepButton.setAttribute("class", "flowViewButton btn btn-success btn-mini span2");
		runDepButton.setAttribute("href", "#");
		$(runDepButton).addClass("hidden");
		$(runDepButton).click(function() {
			runJob(jobName, true, '${request.contextPath}/', function() {
				window.location.reload()
			}, true);
		});
		divBtn.appendChild(runDepButton);

		//li.appendChild(divBtn);
		
		li.setAttribute("onMouseOver", "flowButtonShow(true, this.jobname)");
		li.setAttribute("onMouseOut", "flowButtonShow(false, this.jobname)");

		if (data["dep"]) {
			var ul = document.createElement("ul");
			var children = data["dep"];
			for ( var i = 0; i < children.length; i++) {
				var childLI = getList(children[i]);
				ul.appendChild(childLI);
			}

			li.appendChild(ul);
		}

		return li;
	}

	function flowButtonShow(show, jobname) {
		var flowButton = jobname + "-flowbutton";
		var runButton = jobname + "-runbutton"
		var runDepButton = jobname + "-rundepbutton"
		if (show) {
			$("#" + flowButton).removeClass("hidden");
			$("#" + flowButton).addClass("show");
			$("#" + runButton).removeClass("hidden");
			$("#" + runButton).addClass("show");
			$("#" + runDepButton).removeClass("hidden");
			$("#" + runDepButton).addClass("show");
		} else {
			$("#" + flowButton).removeClass("show");
			$("#" + flowButton).addClass("hidden");
			$("#" + runButton).removeClass("show");
			$("#" + runButton).addClass("hidden");
			$("#" + runDepButton).removeClass("show");
			$("#" + runDepButton).addClass("hidden");
		}
	}
	
	<%-- Expand flow available --%>
	function expandFlow(folderDiv) {
		var folderId = folderDiv.id;
		var d = new Date();
		if (!folderDiv['fold']) {
			$.cookie(folderId, d.getTime());
			$(folderDiv).removeClass('expand');
			$(folderDiv).addClass('wait');

			var foldableDiv = document.getElementById(folderId + "-panel");
			$(foldableDiv).hide();
			folderDiv['fold'] = foldableDiv;
			foldableDiv['hidden'] = true;
			$(foldableDiv).hide();
			$.ajax({
				"type" : "POST",
				"url" : '${request.contextPath}/scheduler/loadJobs.html',
				"data" : {
					"action" : "loadjobs",
					"folder" : folderId
				},
				success : function(data) {
					var ul = document.createElement("ul");
					ul.setAttribute("class", "sched-tree");

					for ( var i = 0; i < data.length; i++) {
						<%-- Create list for all flow --%>
						var root = getList(data[i]);
						ul.appendChild(root);
					}

					foldableDiv.appendChild(ul);
					$(ul).treeview({
						collapsed : true,
						animated : "medium"
					});

					foldableDiv['hidden'] = false;
					$(foldableDiv).show('medium');
					$(folderDiv).removeClass('wait');
					$(folderDiv).addClass('jobfoldercollapse');
				}
			});
		} else {
			var foldable = folderDiv['fold'];
			if (foldable['hidden']) {
				$.cookie(folderId, d.getTime());
				$(foldable).show('medium');
				$(folderDiv).removeClass('expand');
				$(folderDiv).addClass('jobfoldercollapse');
				foldable['hidden'] = false;
			} else {
				$.cookie(folderId, null);
				$(foldable).hide('medium');
				$(folderDiv).removeClass('jobfoldercollapse');
				$(folderDiv).addClass('expand');
				foldable['hidden'] = true;
			}
		}
	}
</script>