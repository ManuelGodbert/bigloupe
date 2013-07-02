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

<script
	src="${request.contextPath}/resources/js/jquery/jquery-typing/jquery.typing-0.2.0.min.js"
	type="text/javascript" charset="utf-8"></script>
<script
	src="${request.contextPath}/resources/js/zeroclipboard/ZeroClipboard.min.js"
	type="text/javascript" charset="utf-8"></script>


<script type="text/javascript">
	var recordId;
	var clip;
	var downloadSize=${downloadSize};
	<%-- Executed after document ready --%>
	$(document).ready(
			function() {
				<c:if test="${param.clipboard}">
 				clip = new ZeroClipboard( document.getElementById("copy-button"), {
					  moviePath: "${request.contextPath}/resources/js/zeroclipboard/ZeroClipboard.swf"
				}); 
 				</c:if>
				
				<%-- Delete action --%>
				$("input[id^='delete']").click(function() {
					$('#deleteModal').modal('show');
					recordId = $(this).attr('id');
				});

				<%-- Blockfinder information by file --%>
				$("input[id^='blockFinder']").click(function() {
						recordId = $(this).attr('id').substring(11);
						$.ajax({type : 'POST',
								url : '${request.contextPath}/hdfsAction/blockFinder.html',
								dataType : 'json',
								data : $('#hdfsAction' + recordId).serialize(),
								success : function(result,textStatus) {
										$('#result').addClass('alert').html('<p>' + result.message + '<p>');
								},
								error : function(xhr, textStatus, errorThrown) {
								}
						});
				});

				<%-- Merge action (only for directory) --%>
				$("input[id^='merge']").click(function() {
					$('#mergeModal').modal('show');
				});

				// Compact action (only for directory)
				$("input[id^='compact']").click(function() {
					recordId = $(this).attr('id').substring(7);
					$('#compactDirectoryDestination').val($("#hdfsAction" + recordId
							+ " > input[name='file']").val() + "_COMPACTED");
					$('#compactModal').modal('show');
					
				});
				
				<%-- View file --%>
				$("input[id^='view']").click(
						function() {
							recordId = $(this).attr('id').substring(4);
							file = $("#hdfsAction" + recordId + " > input[name='file']").val();
							fileContentWindow = window.open(
									'${request.contextPath}/hdfsBrowser'
											+ file + '.html',
									'BigLoupe : File content',
									'width=800;height=200');
							fileContentWindow.focus();
						});

				<%-- Index file or directory --%>
				$("input[id^='index']").click(
						function() {
							recordId = $(this).attr('id').substring(5);
							$('#indexModal').modal('show');
				});

				
				<%-- Filter file or directory --%>
				$("input[id^='filter']").click(
						function() {
							<%-- Read fields in Avro --%>
							recordId = $(this).attr('id').substring(6);
							file = $("#hdfsAction" + recordId + " > input[name='file']").val();
							$('#pigFilterDirectoryDestination').val(file + "_FILTERED");
							$.getJSON('${request.contextPath}/hdfsAvroFields' + file + ".html",
									function(data) {
										var options = "";
										for (var i=0; i< data.length; i++) {
											options = options + ('<option value="' + data[i].value + '" data-type="' +
													data[i].name + '">' + data[i].value + '</option>');
										}
										$("#pigFilterParam").html(options);
									}
							);							
							$('#filterModal').modal('show');
				});
				
				<%-- Increase / Decrease size for file downloading --%> 
				$('#increaseSize').click(function() {
					downloadSize = downloadSize + 10;
					changeSize();
				});

				$('#decreaseSize').click(function() {
					downloadSize = downloadSize - 10;
					changeSize();
				});
				
				<%-- Check availability of index name in ElasticSearch--%>
				$("#elasticSearchIndexName").typing({
					start: function (event, $elem) {
				        
				    },					    
					stop: function(event, $elem) {
						if ($('#elasticSearchIndexName').val().length != 0) {
							$.getJSON('${request.contextPath}/search/testIndex/' + $('#elasticSearchIndexName').val() + ".html",
							function(data) {
								<%-- data.success == false if index not exist --%>
								if (!data.success) {
									$('#checkIndex').html('<i class="icon-ok"></i>');
									$('#indexButton').removeClass("disabled");
								}
								else {
									$('#checkIndex').html('<i class="icon-remove"></i>');
									$('#indexButton').addClass("disabled");
									$elem.css('background', '#f00');
								}
							});
						} else {
							$('#checkIndex').html('<i class="icon-remove"></i>');
							$('#indexButton').addClass("disabled");
						}
					},
					delay : 400
				});
				
				<%-- Display type of field in filter Modal --%> 
				$('#pigFilterParam').change(function(){
				       var selected = $(this).find('option:selected');
				       var type = selected.data('type'); 
				       $('#pigFilterType').html(type).addClass('label label-important');
				});
	});

	<%-- delete file --%>
	function deleteFile() {
		recordId = recordId.substring(6);
		$.ajax({
			type : 'POST',
			url : '${request.contextPath}/hdfsAction/delete.html',
			dataType : 'json',
			data : $('#hdfsAction' + recordId).serialize(),
			success : function(result, textStatus) {
				$('#result').addClass('alert').html(
						'<p>' + result.message + '<p>');
				$('#delete' + recordId).closest('tr').remove();
			},
			error : function(xhr, textStatus, errorThrown) {

			}
		});
		$('#deleteModal').modal('hide');
	};

	<%-- Merge directory --%>
	function mergeDirectory() {
		<%-- delete merge word --%>
		recordId = recordId.substring(5);
		$.ajax({
			type : 'POST',
			url : '${request.contextPath}/hdfsAction/merge' + $('#mergeDirectoryDestination').val() +  '.html',
			dataType : 'json',
			data : $('#hdfsAction' + recordId).serialize(),
			success : function(result, textStatus) {
				$('#result').addClass('alert').html(
						'<p>' + result.message + '<p>');
			},
			error : function(xhr, textStatus, errorThrown) {

			}
		});
		$('#mergeModal').modal('hide');
	};
	
	<%-- Compact directory --%>
	function compactDirectory() {
		<%-- recordId set by previous javascript method --%>
		$.ajax({
			type : 'GET',
			url : '${request.contextPath}/job/compactorAvro' + $("#hdfsAction" + recordId + " > input[name='file']").val() 
					+  '.html?targetDir=' + $('#compactDirectoryDestination').val(),
			dataType : 'json',
			success : function(result, textStatus) {
				$('#result').addClass('alert').html(
						'<p>' + result.message + '</p>');
			},
			error : function(xhr, textStatus, errorThrown) {

			}
		});
		$('#compactModal').modal('hide');
	};
		
    <%-- Index file or directory --%>
	function indexAvro() {
		if ($('#indexButton').hasClass('disabled') != true) {
			file = $("#hdfsAction" + recordId + " > input[name='file']").val();
			$.getJSON('${request.contextPath}/job/indexAvro' + file + '.html?elasticSearchIndexName=' + $('#elasticSearchIndexName').val(),
					function(data) {
						$('#result').addClass('alert').html('<p>' + data.message[0] + '</p>');
			});
			$('#indexModal').modal('hide');		
		}
	};

    <%-- Filter file or directory in Avro --%>
	function filterAvro() {
			file = $("#hdfsAction" + recordId + " > input[name='file']").val();
			$.getJSON('${request.contextPath}/job/filterAvro' + file + '.html?targetDir=' 
					+ $('#pigFilterDirectoryDestination').val() + '&filterParam=' + $('#pigFilterParam').val() 
					+ '&filterValue=' + $('#pigFilterValue').val(),
					function(data) {
						$('#result').addClass('alert').html('<p>' + data.message[0] + '</p>');
			});
			$('#filterModal').modal('hide');		
	};
	
    <%-- Change download size --%>
	function changeSize() {
		$.getJSON('${request.contextPath}/hdfsAction/downloadSize/' + downloadSize + '.html',
				function(data) {
					$('#downloadSize').html('' + data.message + '%');
					$('.bar').width(data.message + '%');
				});
	};
	
	<%-- Upload file --%>
	function upload() {
		$('#uploadModal').modal('show');
	};
	
	function uploadFile() {
		$('#uploadModal').modal('hide');
	};
	

	function helpGotoHDFS() {
		$('#helpGotoHDFS').modal('show');
	};
	
	<%-- Quick goto HDFS --%>
	function goToHDFS() {
		$('#breadcrumb').html('<form id="gotoHDFSForm" action="${request.contextPath}/hdfsGotoBrowser.html" method="GET"><label>HDFS (e.g. hdfs://server:port/directory)&nbsp;<a href="javascript:helpGotoHDFS()"><i class="icon-info-sign"></i></a></label><input id="suggestHDFSPath" type="text" name="gotoHDFSPath" class="span8" style="height : 25px"/>'
		+ '&nbsp;<input type="submit" value="Goto" class="btn btn-primary"/></form>');
		$("#suggestHDFSPath").autocomplete(
				{
					source : function(request, response) {
						$.getJSON("${request.contextPath}/suggestHdfsPath.html",
										{ obj : $('#suggestHDFSPath').val()	}, response);
						
					},
					minLength : 1
				}).data( "autocomplete" )._renderItem = function( ul, item ) {
				    return $( "<li></li>" )
			        .data( "item.autocomplete", item )
			        .append( "<a>" + item.label + "</a>" )
			        .appendTo( ul );
			};
	};
	
	var sURL = unescape(window.location);

	function showDirectorySize() {
		if (sURL.indexOf('?') > -1) {
				sURL += '&showDirectorySize=true';
			} else {
				sURL += '?showDirectorySize=true';
		}
		window.location.href = sURL;
	}
	
	function clipBoard() {
		if (sURL.indexOf('?') > -1) {
			sURL += '&clipboard=true';
		} else {
			sURL += '?clipboard=true';
		}
		window.location.href = sURL;
	}
	
</script>

