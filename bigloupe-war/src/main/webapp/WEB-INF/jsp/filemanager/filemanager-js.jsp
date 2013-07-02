<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

	<link rel="stylesheet" href="${request.contextPath}/resources/css/elfinder/common.css"      type="text/css" media="screen" charset="utf-8">
	<link rel="stylesheet" href="${request.contextPath}/resources/css/elfinder/dialog.css"      type="text/css" media="screen" charset="utf-8">
	<link rel="stylesheet" href="${request.contextPath}/resources/css/elfinder/toolbar.css"     type="text/css" media="screen" charset="utf-8">
	<link rel="stylesheet" href="${request.contextPath}/resources/css/elfinder/navbar.css"      type="text/css" media="screen" charset="utf-8">
	<link rel="stylesheet" href="${request.contextPath}/resources/css/elfinder/statusbar.css"   type="text/css" media="screen" charset="utf-8">
	<link rel="stylesheet" href="${request.contextPath}/resources/css/elfinder/contextmenu.css" type="text/css" media="screen" charset="utf-8">
	<link rel="stylesheet" href="${request.contextPath}/resources/css/elfinder/cwd.css"         type="text/css" media="screen" charset="utf-8">
	<link rel="stylesheet" href="${request.contextPath}/resources/css/elfinder/quicklook.css"   type="text/css" media="screen" charset="utf-8">
	<link rel="stylesheet" href="${request.contextPath}/resources/css/elfinder/commands.css"    type="text/css" media="screen" charset="utf-8">
	
	<link rel="stylesheet" href="${request.contextPath}/resources/css/elfinder/theme.css"       type="text/css" media="screen" charset="utf-8">
	
	<!-- elfinder core -->
	<script src="${request.contextPath}/resources/js/elfinder/elFinder.js"           type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/elFinder.version.js"   type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/jquery.elfinder.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/elFinder.resources.js" type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/elFinder.options.js"   type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/elFinder.history.js"   type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/elFinder.command.js"   type="text/javascript" charset="utf-8"></script>
	
	<!-- elfinder ui -->
	<script src="${request.contextPath}/resources/js/elfinder/ui/overlay.js"       type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/workzone.js"      type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/navbar.js"        type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/dialog.js"        type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/tree.js"          type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/cwd.js"           type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/toolbar.js"       type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/button.js"        type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/uploadButton.js"  type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/viewbutton.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/searchbutton.js"  type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/sortbutton.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/panel.js"         type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/contextmenu.js"   type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/path.js"          type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/stat.js"          type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/ui/places.js"        type="text/javascript" charset="utf-8"></script>
	
	<!-- elfinder commands -->
	<script src="${request.contextPath}/resources/js/elfinder/commands/back.js"      type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/forward.js"   type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/reload.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/up.js"        type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/home.js"      type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/copy.js"      type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/cut.js"       type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/paste.js"     type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/open.js"      type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/rm.js"        type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/info.js"      type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/duplicate.js" type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/rename.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/help.js"      type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/getfile.js"   type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/mkdir.js"     type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/mkfile.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/upload.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/download.js"  type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/edit.js"      type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/quicklook.js" type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/quicklook.plugins.js" type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/extract.js"   type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/archive.js"   type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/search.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/view.js"      type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/resize.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/commands/sort.js"      type="text/javascript" charset="utf-8"></script>	

	<!-- elfinder languages -->
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.ar.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.bg.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.ca.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.cs.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.de.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.en.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.es.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.fr.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.hu.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.jp.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.nl.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.pl.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.pt_BR.js" type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.ru.js"    type="text/javascript" charset="utf-8"></script>
	<script src="${request.contextPath}/resources/js/elfinder/i18n/elfinder.zh_CN.js" type="text/javascript" charset="utf-8"></script>

	<!-- elfinder dialog -->
	<script src="${request.contextPath}/resources/js/elfinder/jquery.dialogelfinder.js"     type="text/javascript" charset="utf-8"></script>

	<!-- elfinder 1.x connector API support -->
	<script src="${request.contextPath}/resources/js/elfinder/proxy/elFinderSupportVer1.js" type="text/javascript" charset="utf-8"></script>
	
	
	<style type="text/css">
		body { font-family:arial, verdana, sans-serif;}
		.button {
			width: 100px;
			position:relative;
			display: -moz-inline-stack;
			display: inline-block;
			vertical-align: top;
			zoom: 1;
			*display: inline;
			margin:0 3px 3px 0;
			padding:1px 0;
			text-align:center;
			border:1px solid #ccc;
			background-color:#eee;
			margin:1em .5em;
			padding:.3em .7em;
			border-radius:5px; 
			-moz-border-radius:5px; 
			-webkit-border-radius:5px;
			cursor:pointer;
		}
		
/*		#dialog {
			position:absolute;
			left:50%;
			top:1000px;
		}
*/		
	</style>

	<script type="text/javascript" charset="utf-8">
		$().ready(function() {

			// tinyMCE.init({});

			$('#finder').elfinder({
				// url : 'php/connector.php',
				url : '${request.contextPath}/filemanager',
				transport : new elFinderSupportVer1(),
				// getFileCallback : function(files, fm) {
				// 	console.log(files);
				// },
				handlers : {
					load : function(e, fm) {
						// fm.error('All your base belongs to us >_<')
					}
				},
				// onlyMimes : ['image', 'text/plain']
				lang : 'en',
				customData : {answer : 42}
				// requestType : 'POST',
				// rememberLastDir : false,
				// ui : ['tree', 'toolbar'],
				// ui : ['toolbar', 'places', 'tree', 'path', 'stat'],
				// commands : [],
				// commandsOptions : {
				// 	edit : {
				// 		mimes : ['text/plain', 'text/html', 'text/javascript'],
				// 		editors : [
				// 			{
				// 				mimes : ['text/html'],
				// 				load : function(textarea) {
				// 					tinyMCE.execCommand("mceAddControl", true, textarea.id);
				// 				},
				// 				close : function(textarea, instance) {
				// 					tinyMCE.execCommand('mceRemoveControl', false, textarea.id);
				// 				},
				// 				save : function(textarea, editor) {
				// 					textarea.value = tinyMCE.get(textarea.id).selection.getContent({format : 'html'});
				// 					tinyMCE.execCommand('mceRemoveControl', false, textarea.id);
				// 				}
				// 
				// 			}
				// 		]
				// 	}
				// }
				// uiOptions : {
				// 	toolbar : [['help']]
				// }
			})

			$('#back').click(function(e) {
				f1.exec('back')
			})
			$('#fwd').click(function(e) {
				f1.exec('forward')
			})
			
			$('#dialog').click(function() {
				
				var fm = $('<div/>').dialogelfinder({
					url : '${request.contextPath}/filemanager',
					lang : 'en',
					width : 840,
					destroyOnClose : true,
					getFileCallback : function(files, fm) {
						console.log(files);
					},
					commandsOptions : {
						getfile : {
							oncomplete : 'close',
							folders : true
						}
					}
				}).dialogelfinder('instance')
				 console.log(fm)
			})
			
		})
		
		

		
	</script>

