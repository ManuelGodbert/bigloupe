<%@page import="org.bigloupe.web.BigLoupeConfiguration"%>
<style type="text/css">

#breadcrumb {
	font-style: italic;
	padding: 5px;
	border-bottom: 1px solid #ccc;
	margin-bottom: 5px;
}

#legend {
	
}

#controls {
	text-align: center;
	margin: 8px 0;
}

.description {
	text-align: center;
	font-size: 0.8em;
	margin: 10px;
}

.description .range {
	background-image: url(${request.contextPath}/resources/img/range.png);
	background-image: -webkit-linear-gradient(left, #FFF7FB, #ECE7F2, #D0D1E6, #A6BDDB, #74A9CF, #3690C0, #0570B0, #045A8D, #023858);
	width: 490px;
	height: 15px;
	font-size: 12px;
	font-weight: bold;
	border: 1px solid #aaa;
	margin: 4px auto 0 auto;
}

.description .to {
	float: left;
	margin-left: 7px;
}

.description .from {
	float: right;
	margin-right: 7px;
	color: white;
}

#controls ul {
	margin: 0;
	padding: 0;
}
#controls ul li {
	list-style-type: none;
    display: inline-block;
}

#controls button {
	padding: 5px;
	font-size: 0.7em;
    color: black;
    background: #ddd;
    border-radius: 0.5em;
    
    border: 1px solid #ccc;

    cursor: pointer;

    -webkit-box-shadow: 0 0 0 #333;
    -o-box-shadow: 0 0 0 #333;
    -moz-box-shadow: 0 0 0 #333;
    box-shadow: 0 0 0 #333;

    -webkit-transition: -webkit-box-shadow 300ms;	
    -moz-transition: -moz-box-shadow 300ms;   
    -o-transition: -o-box-shadow 300ms;   
    transition: box-shadow 300ms;   
}

#controls button:hover {
	-webkit-box-shadow:   0 0 5px #333;
    -o-box-shadow:   0 0 5px #333;
    -moz-box-shadow:   0 0 5px #333;
    box-shadow:   0 0 5px #333;	
}

#controls button.selected {
    -webkit-box-shadow: inset  0 0 5px #333;
    -o-box-shadow:  inset 0 0 5px #333;
    -moz-box-shadow:  inset 0 0 5px #333;
    box-shadow:  inset 0 0 5px #333; 
}

button#back {
	background: #555;
	color: white;
	border: 1px solid #111;
}

#treemap {
  position:relative;
  margin:auto;
  height: 700px;
  
  /*box-shadow: 0 0 10px #555;*/
}

#treemap .node {
  /*color:#fff;*/
  color: white;
  text-shadow: 0 0 5px #000;
  font-size:11px;
  font-weight: bold;
  overflow:hidden;
  cursor:pointer;
  text-align: center;
  font-family: "Lucida Grande", Verdana;
  
  -webkit-transition: border 200ms;
  border: 2px solid transparent; 
/*  
  text-shadow:2px 2px 5px #000;
  -o-text-shadow:2px 2px 5px #000;
  -webkit-text-shadow:2px 2px 5px #000;
  -moz-text-shadow:2px 2px 5px #000;
*/
}

#treemap .node:hover {
  border: 2px solid #FF5555; 
}

.tip {
    color: black;
    max-width: 500px;
    background-color: white;
    opacity:0.9;
    font-size:10px;
    font-family:Verdana, Geneva, Arial, Helvetica, sans-serif;
    padding:7px;
    
    -webkit-box-shadow: 0 0 6px #222;
    -o-box-shadow: 0 0 6px #222;
    -moz-box-shadow: 0 0 6px #222;
    box-shadow: 0 0 6px #222;
}

.tip .tip-title {
	font-weight: bold;
	font-size: 12px;
	text-align: center;
}

.tip ul {
	margin: 5px;
	padding: 0;
}

.tip ul li {
	list-style-type: none;
    padding: 3px;
}

</style>
	
<script
	src="${request.contextPath}/resources/js/jit/jit.js"
	type="text/javascript" charset="utf-8"></script>
	
<script
	src="${request.contextPath}/resources/js/chroma/chroma.js"
	type="text/javascript" charset="utf-8"></script>	

<script type="text/javascript"
	src="${request.contextPath}/resources/js/d3/d3.v2.min.js"></script>

<script type="text/javascript"
	src="${request.contextPath}/resources/js/filetree/htmltools.js"></script>
<link rel="stylesheet"
	href="${request.contextPath}/resources/js/filetree/filetree.css"
	type="text/css" media="screen" charset="utf-8">


<script type="text/javascript">
(function() {

	var QueryConfig = {
		limit: 50000,
		depth: 2
	};
	
	var Observer = {
		callbacks: {},
		addEvent: function(type, fn) {
			var callbacks = this.callbacks;
			if (!callbacks[type]) {
				callbacks[type] = [];
			}
			callbacks[type].push(fn);
		},
		
		fireEvent: function(type, e) {
			var callbacks = this.callbacks[type];
			if (!callbacks) return;
			callbacks.forEach(function(cb) {
				cb(e);
			});
		}
	};
	
	var XHR = function(opt) {
		var xhr = new XMLHttpRequest(),
			qs = opt.params || {},
			url = opt.url;
			
		var k = Object.keys(qs),
			queryString = [];
		k.forEach(function(key) {
			queryString.push(key + '=' + encodeURIComponent(qs[key]));
		});
		
		if (queryString.length) {
			queryString = queryString.join('&');
			queryString = '?' + queryString;
			url += queryString;
		}
		
		xhr.open('GET', url, true);
		xhr.onreadystatechange = function(e) {
			if (xhr.readyState == 4) {
				opt.onSuccess && opt.onSuccess(xhr.responseText);
			}
		};
		
		this.xhr = xhr;
	};

	XHR.prototype = {
		send: function() {
			this.xhr.send(null);
		}
	};
	window.Observer = Observer;
	window.XHR = XHR;
	
	window.addEventListener('DOMContentLoaded', function(e) {
		Observer.fireEvent('load', e);
		
		new XHR({
			url: '${request.contextPath}/diskUsageHdfs/treeSizeByPath/<%=((BigLoupeConfiguration)pageContext.findAttribute("configuration")).getCurrentHadoopCluster(request)%>.html',
			params: {
				path: '/',
				limit: QueryConfig.limit,
				depth: QueryConfig.depth
			},
			onSuccess: function(text) {
				Observer.fireEvent('initdataloaded', text);
			}
		}).send();	
	});
	
var queryLimit = 50000,
	maxFolders = 70e6,
	maxSize = 70e8,
	sizeThreshold = 150 * (1 << 20), //150 M Bytes
	depth = 2;

var $ = function(d) { return document.getElementById(d); },
	$$ = function(d) { return document.querySelectorAll(d); };

function FileTreeMap(chart_id) {
	var that = this, 
		size = $('size'),
		count = $('count'),
//		tm = new $jit.TM.Voronoi({
		tm = new $jit.TM.Squarified({
	    injectInto: chart_id,
	    titleHeight: 15,
//	    titleHeight: 0,
	    levelsToShow: depth,
//	    labelsToShow: [0, 1],
	    animate: true,
	    offset: 1,
	    duration: 1000,
	    hideLabels: true,
	    Label: {
	    	type: 'HTML',
//	    	type: 'Native',//'HTML',
//	    	size: 1,
//	    	color: 'white'
	    },
	    Events: {
	      enable: true,
	      onClick: function(node) {
	        if(node) {
	        	Observer.fireEvent('click', node);
	        }
	      },
	      onRightClick: function() {
	    	if (tm.clickedNode && tm.clickedNode.getParents().length) {
	    		Observer.fireEvent('back', tm.clickedNode);
	    	}
	      }
	    },
	    onCreateLabel: function(domElement, node){
	        domElement.innerHTML = node.name;

	      domElement.onmouseover = function(event) {
	    		Observer.fireEvent('mouseover', node);
        };
	      domElement.onmouseout = function(event) {
	    		Observer.fireEvent('mouseout', node);
        };
	    },
	    Tips: {
	      enable: true,
	      offsetX: 20,
	      offsetY: 20,
	      onShow: function(tip, node, isLeaf, domElement) {
	          var html = "<div class=\"tip-title\">" + node.name
			+ "</div><div class=\"tip-text\"><ul><li>";
				var data = node.data;
				html += "<b>folder size:</b> " + Math.round(data.fileSize / (1 << 20)) + " MB</li><li>";
				html += "<b>n. of descendants:</b> " + data.nChildren + "</li><li>";
				html += "<b>avg. file size:</b> " + Math.round((data.fileSize / data.nChildren) / (1 << 20)) + " MB</li></ul></div>";
				tip.innerHTML = html;
	      }  
	    },
	    
	    request: function(nodeId, level, callback){
	    	if (level <= depth -1) {
	    		callback.onComplete(nodeId, { children: [] });
	    		return;
	    	}
	    	new XHR({
	    		url: '${request.contextPath}/diskUsageHdfs/treeSizeByPath/<%=((BigLoupeConfiguration)pageContext.findAttribute("configuration")).getCurrentHadoopCluster(request)%>.html',
	    		params: {
	    			path: nodeId,
	    			limit: queryLimit / level,
	    			depth: level
	    		},
	    		onSuccess: function(text) {
	    			var json = JSON.parse(text);
	    			json = treemap.processJSON(json);
	    			json.id = nodeId;
	    			callback.onComplete(nodeId, json);
	    		}
	    	}).send();
	    }	    
	  });
	
	  this.tm = tm;
	  this.bc = $('breadcrumb');

	  $('back').addEventListener('click', function() {
		  if (tm.clickedNode) Observer.fireEvent('back', tm.clickedNode)
	  });
	  size.addEventListener('click', function(e) {
		  size.classList.add('selected');
		  count.classList.remove('selected');
		  that.setSize();
	  });
	  count.addEventListener('click', function(e) {
		  count.classList.add('selected');
		  size.classList.remove('selected');
		  that.setCount();
	  });
}

FileTreeMap.prototype = {
	size: true,
	
	scale: new chroma.ColorScale({
//	    colors: ['#6A000B', '#F7E1C5']
//		colors: ['#A50026', '#D73027', '#F46D43', '#FDAE61', '#FEE090', '#FFFFBF', '#E0F3F8', '#ABD9E9', '#74ADD1', '#4575B4', '#313695']
//		colors: ['#67001F', '#B2182B', '#D6604D', '#F4A582', '#FDDBC7', '#F7F7F7', '#D1E5F0', '#92C5DE', '#4393C3', '#2166AC', '#053061']
//		colors: ['#CA0020', '#F4A582', '#F7F7F7', '#92C5DE', '#0571B0'],
		colors: ['#FFF7FB', '#ECE7F2', '#D0D1E6', '#A6BDDB', '#74A9CF', '#3690C0', '#0570B0', '#045A8D', '#023858']
//		limits: chroma.limits([0, 0.2, 0.4, 0.6, 0.8, 1], 'equal', 5)
	}),
	
	color: function(data) {
		var ratio = (data.fileSize / data.nChildren) / sizeThreshold;
		if (ratio > 1) {
			return this.scale.getColor(1).hex();
		} else {
			return this.scale.getColor(ratio).hex();
		}
	},
	
	load: function(json) {
		this.tm.loadJSON(json);
		this.tm.refresh();
	},
	
	processJSON: function(json) {
		if (!json.id) {
			return json;
		}
		
		var fileSize = json.data.fileSize,
			min = Math.min,
			len = fileSize.length,
			smallNums = len > 9,
			decimals = 6,
			that = this,
			count = 0, div;
			
		div = 350 / (this.size ? maxFolders : maxSize);

		$jit.json.each(json, function(n) {
			var fileSizeText = n.data.fileSize,
				nChildren = n.data.nChildren,
				len = fileSizeText.length,
				size;
			//cut the file size
			if (smallNums) {
				fileSizeText = fileSizeText.slice(0, len - decimals) + '.' + fileSizeText.slice(len-decimals);
			}
			size = parseFloat(fileSizeText);
			n.data.$area = that.size ? (size || 1) : +nChildren;
			n.data.$color = that.color(n.data);
		});
		return json;
	},
	
	setVoronoi: function() {
		var tm = this.tm,
			util = $jit.util;
		util.extend(tm, new $jit.Layouts.TM.Voronoi());
		tm.config.Node.type = 'polygon';
		tm.config.Label.textBaseline = 'middle';
		tm.config.labelsToShow = [1, 1],
		tm.config.animate = false;
		tm.refresh();
		tm.config.animate = true;
	},
	
	setSquarified: function() {
		var tm = this.tm,
			util = $jit.util,
			$C = $jit.Complex,
			dist2 = $jit.geometry.dist2;
		
		util.extend(tm, new $jit.Layouts.TM.Squarified());
		tm.config.Node.type = 'rectangle';
		tm.config.Label.textBaseline = 'top';
		tm.config.labelsToShow = false,
		tm.config.animate = false;
		tm.refresh();
		tm.config.animate = true;
	},
	
	setSize: function() {
		if (this.size || this.busy) return;
		this.size = this.busy = true;
		
		var that = this,
			util = $jit.util,
			min = Math.min,
			tm = this.tm,
			g = tm.graph;
		
		g.eachNode(function(n) {
			n.setData('area', +that.parseFileSize(n.data.fileSize, 6), 'end');
		})
		
		tm.compute('end');
		tm.fx.animate({
			modes: {
				'position': 'linear',
				'node-property': ['width', 'height']
			},
			duration: 1000,
			fps: 60,
			onComplete: function() {
				g.eachNode(function(n) {
					n.setData('area', n.getData('area', 'end'));
				});
				that.busy = false;
			}
		});
	},
	
	setCount: function() {
		if (!this.size || this.busy) return;
		this.size = false;
		this.busy = true;
		
		var that = this,
			util = $jit.util,
			min = Math.min,
			tm = this.tm,
			g = tm.graph;
		
		g.eachNode(function(n) {
			n.setData('area', +n.data.nChildren, 'end');
		})
		
		tm.compute('end');
		tm.fx.animate({
			modes: {
				'position': 'linear',
				'node-property': ['width', 'height']
			},
			fps: 60,
			duration: 1000,
			onComplete: function() {
				g.eachNode(function(n) {
					n.setData('area', n.getData('area', 'end'));
				});
				that.busy = false;
			}
		});
	},
	
	updateBreadCrumb: function(node) {
		if (!node) return;
		var names = [node.name],
			par = node.getParents();
		
		while (par.length) {
			names.unshift(par[0].name);
			par = par[0].getParents();
		}
		this.bc.innerHTML = names.join(' &rsaquo; ');
	},
	
	parseFileSize: function(size, decimals) {
		var len = size.length;
		return size.slice(0, len - decimals) + '.' + size.slice(len-decimals);
	},
	
	clickHandler: function(nodeElem) {
		var tm = this.tm,
			node = tm.graph.getNode(nodeElem.id),
			currentRoot = (tm.clickedNode || tm.graph.getNode(tm.root)).id;
		
		if (!node.isDescendantOf(currentRoot)) {
			this.backHandler();
			return;
		}
		
    	tm.enter(node);
    	this.updateBreadCrumb(node);
	},
	
	backHandler: function() {
		var tm = this.tm;
	
		if (!tm.clickedNode) return;
		
		var par = tm.clickedNode.getParents()[0];
		if (par) {
	        tm.out();
	    	this.updateBreadCrumb(par);
		}
	}
};

var treemap;
Observer.addEvent('load', function() {
	treemap = new FileTreeMap('treemap');
});

Observer.addEvent('initdataloaded', function (text) {
	var json = JSON.parse(text);
	json = treemap.processJSON(json);
	treemap.load(json);
});

Observer.addEvent('click', function (nodeId) {
	treemap.clickHandler(nodeId);
});

Observer.addEvent('back', function (nodeId) {
	treemap.backHandler(nodeId);
});


})();

</script>

<script>


(function() {
	

function FileTree(chart_id) {

	  var context = this;

	  // misc varibles

	  this.margin = {top: 0, right: 0, bottom: 0, left: 60};
	  this.width = $(chart_id).width();
	  this.height = $(chart_id).height();
	  this.root;
	  this.sibling_limit = 5;
	  this.query_limit = 100000;
	  this.duration = 500;
	  this.letter_size = 8;
	  this.column_width = 150;
	  this.depth = 2;
	  this.small_file_height = "0.9em";
	  this.large_file_height = "3em";
	  this.data_source = "${request.contextPath}/diskUsageHdfs/treeSizeByPath/<%=((BigLoupeConfiguration)pageContext.findAttribute("configuration")).getCurrentHadoopCluster(request)%>.html";

	  // constants

	  // construct tree

	  this.tree = d3.layout.tree()
	    .size([this.height, this.width]);

	  this.tree.separation(function (a, b) {
	    return a.parent == b.parent ? 1 : 1.5;
	  });

	  // create link connector factory

	  this.diagonal = function(d) {
	    var s = d.source;
	    var t = d.target;

	    if (s.name) {
	      s = {x:s.x, y: s.y + context.letter_size * s.name.length};
	    }

	    var path = "M X0, Y0 C X1, Y1, X2, Y2, X3, Y3";
	    var points = [];
	    points.push({x: s.y, y: s.x});
	    points.push({x: (s.y + t.y) / 2, y: s.x});
	    points.push({x: (s.y + t.y) / 2, y: t.x});
	    points.push({x: t.y, y: t.x});
	    path = context.populate_path(path, points);
	    return path;
	  }


	  // create the svg work space

	  this.vis = d3.select(chart_id).append("svg")
	    .attr("width", this.width)
	    .attr("height", this.height)
	    .append("g")
	    .attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");

	}

	FileTree.prototype.handle_data = function(json_string, open_root)
	{
		var context = this,
			json = JSON.parse(json_string);

	    context.root = json;
	    context.preprocess_tree(context.root);
	    context.root.x0 = context.height / 2;
	    context.root.y0 = 0;

	    // match (as closely as possible) the collapsed stat of the old tree

	    function match_collapse(node) {

	      if (node.children) {

	        // recurse down through the nodes

	        node.children.forEach(match_collapse);

	        // close nodes by default

	        context.close_node(node);

	        // if the old version of the node is exists and is open, open this new version

	        if (context.nodes) {
	          context.nodes.forEach(function(old_node) {
	            if (old_node.id == node.id && context.is_open(old_node))
	              context.open_node(node);
	          });
	        }
	      }
	    }

	    // match the collapsed state of the old tree

	    match_collapse(context.root);

	    // if there was no old tree, then open up the root node

	    if (context.nodes === undefined || open_root)
	      context.open_node(context.root);

	    context.update(context.root);
	};


	FileTree.prototype.load_data = function(path, open_root)
	{
		var that = this;
		new XHR({
			url: this.data_source,
			params: {
				path: path,
				depth: this.depth,
				limit: this.query_limit
			},
			onSuccess: function(text) {
				that.handle_data(text, open_root);
			}
		}).send();
	}

	FileTree.prototype.preprocess_tree = function(tree) {

	  var context = this;

	  // sort children

	  tree.children.sort(function(a, b) {
	    return b.data.fileSize - a.data.fileSize;
	  });

	  // collapse large lists

	  if (tree.children.length > context.sibling_limit) {
	    tree.other = tree.children.slice(context.sibling_limit);
	    tree.children = tree.children.slice(0, context.sibling_limit);
	    var other_sum = "0";
	    var other_count = 0;
	    tree.other.forEach(function (child) {
	      var x = "-" + child.data.fileSize;
	      other_sum = other_sum - x;
	      ++other_count;
	    });

	    tree.children.push({
	      name: other_count + " more...",
	       id: tree.id + "/more...",
	      data: {fileSize: other_sum},
	      children: tree.other});
	  }

	  // process each child

	  for (var i in tree.children) {
	    context.preprocess_tree(tree.children[i]);
	  }
	}

	FileTree.prototype.update = function(source) {

	  var context = this;

	  // compute the new tree layout

	  context.nodes = this.tree.nodes(this.root).reverse();

	  // normalize for fixed-depth and do any pre formatting

	  context.nodes.forEach(function(d) {
	    d.y = d.depth * context.column_width;
	    context.preformat_node(d);
	  });

	  // bind nodes to the dom

	  var node = this.vis.selectAll("g.node")
	      .data(context.nodes, function(d) { return d.id || (d.id = d.id);});

	  // enter  new nodes at the parent's previous position

	  var enter_nodes = node
	    .enter()
	    .append("g")
	    .attr("class", "node")
	    .attr("transform", function(d) {
	      var x = source.y0;
	      var y = source.x0;

	      // if (d.parent !== undefined) {
	      //   x = d.parent.y;
	      //   y = d.parent.x;
	      // };

	      return "translate(" + x + "," + y + ")";
	    })
	    .on("mouseover", function(d) { Observer.fireEvent('mouseover', d)})
	    .on("mouseout", function(d) { Observer.fireEvent('mouseout', d);})
	    .on("click", function(d) { Observer.fireEvent('click', d);});

	  // add node circle

	  enter_nodes
	    .filter(function (d) {return !d.leaf;})
	    .append("circle")
	    .attr("class",  function(d) {return context.is_open(d) ? "open_branch" : "closed_branch"; })
	    .attr("r", 1e-6);

	  // add node text

	  enter_nodes
	    .append("svg:foreignObject")
	    .classed("text_fo", true)
	    .attr("width", "8em")
	    .attr("height", context.small_file_height)
	    .attr("x", ".5em")
	    .attr("y", "-.3em")
	    .append("xhtml:body")
	    .attr("class", function(d) {return d.leaf ? "leaf_text" : "branch_text";})
	    .html(context.node_html);

	  d3.selectAll("text.arrow_text").
	    remove();

	  // add up arrow

	  d3.selectAll("g.node")
	    .filter(function(d) {
	      return d.name != "/" && d.parent == null;})
	    .append("svg:text")
	    .attr("class", "arrow_text")
	    .attr("x", -context.margin.left)
	    .attr("dy", ".35em")
	    .text(String.fromCharCode(0x2b05))
	    .on("click", function(d) { return Observer.fireEvent('back', d);});



	  // transition nodes to their new position.

	  var update_nodes = node.transition()
	    .duration(context.duration)
	    .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

	  // add node circle

	  update_nodes.select("circle")
	    .attr("class",  function(d) {return context.is_open(d) ? "open_branch" : "closed_branch"; })
	    .attr("r", 6);
	    // .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

	  update_nodes.select("text")
	      .style("fill-opacity", 1);

	  // transition exiting nodes to the parent's new position.

	  var exit_nodes = node.exit().transition()
	      .duration(context.duration)
	      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
	      .remove();

	  exit_nodes.select("circle")
	    .attr("r", 1e-6);

	  exit_nodes.select("text")
	      .style("fill-opacity", 1e-6);

	  // bind links to dom

	  var link = context.vis.selectAll("path.link")
	      .data(context.tree.links(context.nodes), function(d) { return d.target.id; });

	  // nnter any new links at the parent's previous position

	  link.enter().insert("path", "g")
	      .attr("class", "link")
	      .attr("d", function(d) {
	        var o = {x: source.x0, y: source.y0};
	        return context.diagonal({source: o, target: o});
	      });

	  // transition links to their new position

	  link.transition()
	      .duration(context.duration)
	      .attr("d", context.diagonal);

	  // transition exiting nodes to the parent's new position

	  link.exit().transition()
	      .duration(context.duration)
	      .attr("d", function(d) {
	        var o = {x: source.x, y: source.y};
	        return context.diagonal({source: o, target: o});
	      })
	      .remove();

	  // stash the old positions for transition

	  context.nodes.forEach(function(d) {
	    d.x0 = d.x;
	    d.y0 = d.y;
	  });
	}

	FileTree.prototype.preformat_node = function(node) {
	  node.size = this.format_number_string(node.data.fileSize);
	}

	FileTree.prototype.node_html = function(node) {
	  var size_html = node.size !== undefined
	    ? HT.tRow({}, HT.tCell({class: "size_text"}, "size: " + node.size)) : "";

	  var html = HT.table({class: "node_text"}, HT.tRow({}, HT.tCell({}, node.name)) + size_html);

	  return html;
	};


	FileTree.prototype.populate_path = function(path, points){
	  for(index in points) {
	    path = path
	      .replace("X" + index, points[index].x)
	      .replace("Y" + index, points[index].y);
	  };
	  return path;
	}

	// get node by id

	FileTree.prototype.get_node_by_id = function(node_id) {
	  var found = undefined;
	  this.nodes.forEach(function (node) {
	    if (node_id == node.id)
	      found = node;
	  });
	  return found;
	}


	// handle up click

	FileTree.prototype.up_click = function(node) {
	  console.log("up click", node.name);
	  var target = node.id.substring(0, node.id.lastIndexOf("/"));
	  target = target.length == 0 ? "/" : target;
	  this.load_data(target, true);
	}

	// handle node click

	FileTree.prototype.click = function(node) {

		console.log('click', node.id);

	  var id = node.id;
	  for (var i = 0, l = this.nodes.length; i < l; ++i) {
		  if (id == this.nodes[i].id) {
			  node = this.nodes[i];
			  break;
		  }
	  }

	  if (i == l) {
		  console.log('did not find the node');
		  return;
	  }

	  console.log("expand click", node.name);

	  // toggle children on this node

	  this.toggle_children(node);

	  // if this is a bottom most visible node, time to zoom into it

	  if (node.depth == this.depth) {

	    // pretend mouse left this node (cause it's gonna)

	    this.mouseout(node);

	    // establish node ancestor

	    var ancestor = node.parent;
	    while (ancestor.depth > 1)
	      ancestor = ancestor.parent;

	    // load ancestor

	    this.load_data(ancestor.id, false);
	  }

	  // otherwise simply update this node

	  else {
	    this.update(node);
	  }
	}

	// handle node hover

	FileTree.prototype.mouseover = function(node) {
	  var context = this;

	  d3.selectAll("circle")
	    .filter(function(d) {return d.id == node.id;})
	    .classed("node_hover", true);

	  d3.selectAll(".text_fo")
	    .filter(function(d) {return d.id == node.id;})
	    .attr("height", context.large_file_height);
	}

	FileTree.prototype.mouseout = function(node) {
	  var context = this;

	  d3.selectAll("circle")
	    .filter(function(d) {return d.id == node.id})
	    .classed("node_hover", false);

	  d3.selectAll(".text_fo")
	    .filter(function(d) {return d.id == node.id;})
	    .attr("height", context.small_file_height);
	}

	// make node root

	FileTree.prototype.make_root = function(node) {
	  this.root = node;
	}

	// toggle children

	FileTree.prototype.toggle_children = function(node) {
	  this.is_open(node)
	    ? this.close_node(node)
	    : this.open_node(node);
	}

	FileTree.prototype.open_node = function(node) {
	  node.children = node._children;
	  node._children = null;
	}

	FileTree.prototype.close_node = function(node) {
	  node._children = node.children;
	  node.children = null;
	}


	FileTree.prototype.is_open = function(node) {
	  return node.children != null;
	}

	FileTree.prototype.get_children = function(node) {
	  return node.children ? node.children : node._children;
	}

	FileTree.prototype.is_branch = function(node) {
	  var children = this.get_children(node);
	  return children !== undefined && children != null && children.length > 0;
	}

	FileTree.prototype.show_node = function(node, indent) {
	  indent = indent | 0;
	  console.log(Array(indent).join(" ") + node.name + "(" + node.x + ", " + node.y + ")");
	};


	FileTree.prototype.format_number_string = function(number_string)
	{
	  var billions = number_string.length > 15;
	  number_string = billions ? number_string.substring(0, number_string.length - 6) : number_string;
	  return this.format_numbers(parseInt(number_string), billions);
	}

	FileTree.prototype.format_numbers = function(number, billions)
	{
	  var context = this;

	  var digits = Math.log(number) / Math.log(10);

	  // if the number is less then and not billions large, return return the nacked number

	  if (number < 1000 && !billions)
	    return number;

	  for (var power = 3; power <= 18; power += 3) {
	    var scale = Math.pow(10, power);
	    if (number < scale)
	      return context.format_number(number, Math.pow(10, power - 3), billions);
	  }

	  return "BIG!";
	}

	FileTree.prototype.format_number = function(number, scale, billions)
	{
	  var tags = {
	    1000: "K",
	    1000000: "M",
	    1000000000: "G",
	    1000000000000: "T",
	    1000000000000000: "P"
	  };

	  var billions_tags = {
	    1000: "G",
	    1000000: "T",
	    1000000000: "P",
	    1000000000000: "E",
	    1000000000000000: "Z"
	  };

	  var value = Math.round(10 * (number / scale)) / 10;
	  var tag = billions ? billions_tags[scale] : tags[scale];
	  return value+tag;
	}

	FileTree.prototype.test_format_numbers = function()
	{
	  var context = this;

	  var test_numbers = [
	    {input: "9", output: "9"},
	    {input: "10", output: "10"},
	    {input: "999", output: "999"},
	    {input: "1100", output: "1.1K"},
	    {input: "999900", output: "999.9K"},
	    {input: "1100000", output: "1.1M"},
	    {input: "999900000", output: "999.9M"},
	    {input: "1100000000", output: "1.1G"},
	    {input: "999900000000", output: "999.9G"},
	    {input: "1100000000000", output: "1.1T"},
	    {input: "999900000000000", output: "999.9T"},
	    {input: "1100000000000000", output: "1.1P"},
	    {input: "999900000000000000", output: "999.9P"},
	    {input: "1100000000000000000", output: "1.1E"},
	    {input: "999900000000000000000", output: "999.9E"},
	    {input: "1100000000000000000000", output: "1.1Z"},
	    {input: "999900000000000000000000", output: "999.9Z"},
	  ];

	  test_numbers.forEach(function(test_number) {
	    var candiate = context.format_number_string(test_number.input);
	    if (candiate != test_number.output)
	      console.log(test_number, "!=", candiate);
	    // else
	    //   console.log(test_number, "==", candiate);
	  });
	}

	var file_tree;

	Observer.addEvent('load', function() {
		file_tree = new FileTree("#filetree");
	});

	Observer.addEvent('initdataloaded', function(data) {
		file_tree.handle_data(data);
	});

	Observer.addEvent('click', function(node) {
		file_tree.click(node);
	});

	Observer.addEvent('mouseover', function(node) {
		file_tree.mouseover(node);
	});

	Observer.addEvent('mouseout', function(node) {
		file_tree.mouseout(node);
	});

	Observer.addEvent('back', function(node) {
		file_tree.up_click(node);
	});

})();
</script>

