;(function($)
{
    JSONTREEVIEWER =
    {
        name: 'JSONTREEVIEWER',
		
        settings: {},
		
        init: function() { 
			var _this = this;	
		},
		
		/*Load the JSON file either by upload or example file and process tree*/
		processJSONTree: function(filename) {
			$('#loading').show();
			var data = '', branches = '';
			var jsonToValidate = $('#inputJson').text().trim();
			
			/* validate JSON */
			if (JSONTREEVIEWER.isValidJSON(jsonToValidate)) {
				data = jsonToValidate;
			} else {
				return false;
			}
			if (data === false) {
				return false;
			}
			/* Build JSON Tree */
			JSONTREEVIEWER.buildTree(JSONTREEVIEWER.processNodes(jQuery
					.parseJSON(data)), filename);

		},
		
		/*Build JSON Tree*/
		buildTree: function(branches) {
			//console.log('branches' + branches);
			if (typeof branches !== 'undefined' || branches !== '') {
				$('#browser').empty().html(branches);
				$('#browser').treeview({
					control: '#treecontrol',
					add: branches
				});
				$('#loading').hide();
				$('#browser-text').hide();
			} else {
				$('#loading').hide();
			}
		},
		
		/*Process each node by its type (branch or leaf)*/
		processNodes: function(node) {
			var return_str = '';
			switch(jQuery.type(node))
			{
			case 'string':
				  return_str += '<ul><li><span class="file">'+node+'</span></li></ul>';
			  break;
			case 'array':
				$.each(node, function(item, value){
					return_str += JSONTREEVIEWER.processNodes(this);
				});
			  break;
			default:
				/*object*/
				$.each(node, function(item, value){
						return_str += '<ul><li><span class="folder">'+ item + ':' + value +'</span>';
						return_str += JSONTREEVIEWER.processNodes(this);
						return_str += '</li></ul>';
				});
			}
			/*Clean up any undefined elements*/
			return_str = return_str.replace('undefined', '');
			return return_str;
		},
		
		/*Populate the path of the node ready for copy*/
		getNodePath: function(node) {
			var pathresult = $(node).getPath();
			return  pathresult.replace('> null >', '');
		},
		
		/*Helper function to manage node paths display*/
		addtoppath: function(path) {
			$('#accumpaths').val(path);
			$('#toppathwrap').show();
			$('#accumpaths').focus();
			$('#accumpaths').select();
		},
		
		/*Helper function to check if JSON is valid*/
		isValidJSON: function(jsonData) {
			try {
				jsonData = jQuery.parseJSON(jsonData);
				//console.log('valid json');
				return true;
			}
			catch(e) {
				//console.log('invalid json');
				alert(e);
				JSONTREEVIEWER.showErrorMsg();
				return false;
			}
		},
		
		/*Helper function to show error message*/
		showErrorMsg: function() {
			$('#loading').hide();
			$('#browser').empty();
		}
	}
	
	/*jQuery function to create path function used to get the path of the node in the tree*/
	jQuery.fn.extend({
		getPath: function( path ) {
			/*The first time this function is called, path won't be defined*/
			if ( typeof path == 'undefined' ) path = '';
			/*Add the element name*/
			var cur = this.get(0).nodeName.toLowerCase();
			var id  = this.attr('id');
			/*Add the #id if there is one*/
			if ( typeof id != 'undefined' ) {
				/*escape goat*/
				if (id == 'browser') { return path; }
			}
			var html = this.html();
			if (html.search('<li')) {
				/*add the variable name*/
				var val = this.find('span').first().html();
				/*Recurse up the DOM*/
				return this.parent().getPath( val + ' > ' + path );
			} else {
				return this.parent().getPath( path );
			}
		}
	});
	
	/*EVENTS ON LIVE ELEMENTS (DYNAMICALLY INSERTED DOM) ------------*/
	
	/*store nodepath value to clipboard	(copy to top of page)*/
	$('#browser li').live('click', function(){
		var path = $('#pathtonode').html();
		JSONTREEVIEWER.addtoppath(path);
	});
		
	$('#browser li span').live('mouseenter', function(){
		$('#pathtonode').html('<b>Path</b> : ' + JSONTREEVIEWER.getNodePath(this));
		$('#pathtonode').tooltip('show');
	});
	
	$('#browser li span').live('mouseleave', function(){
		$('#pathtonode').empty();
		$('#pathtonode').tooltip('hide');
	});

	/*click event when the user closes the node path window*/
	$('#closetoppath').live('click', function() {
		$('#toppathwrap').hide();
	});
	
})(jQuery);