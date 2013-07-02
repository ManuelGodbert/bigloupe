<style type="text/css">
.cell {
  border: solid 1px white;
  font: 10px sans-serif;
  line-height: 12px;
  overflow: hidden;
  position: absolute;
  text-indent: 2px;
}
</style>
<div class="row">
	<div class="page-header">
		<h3>
			Database information<small>&nbsp;Number of records per table</small>
		</h3>

	</div>
</div>
<div class="row">
	<div class="span12" id="chart"></div>
</div>

<script type="text/javascript">
	var width = 960, height = 500, color = d3.scale.category20c();

	var treemap = d3.layout.treemap().size([ width, height ]).sticky(true)
			.value(function(d) {
				return d.size;
			});

	var div = d3.select("#chart").append("div").style("position", "relative")
			.style("width", width + "px").style("height", height + "px");

	d3.json("${request.contextPath}/data/json/database-information.json", function(json) {
		div.data([ json ]).selectAll("div").data(treemap.nodes).enter().append(
				"div").attr("class", "cell").style("background", function(d) {
			return d.children ? color(d.name) : color(Math.random());
		}).call(cell).text(function(d) {
			return d.children ? null : d.name + '(' + byteDesc(d.value) + ')';
		});

		d3.select("#size").on("click", function() {
			div.selectAll("div").data(treemap.value(function(d) {
				return d.size;
			})).transition().duration(1500).call(cell);

			d3.select("#size").classed("active", true);
			d3.select("#count").classed("active", false);
		});

		d3.select("#count").on("click", function() {
			div.selectAll("div").data(treemap.value(function(d) {
				return 1;
			})).transition().duration(1500).call(cell);

			d3.select("#size").classed("active", false);
			d3.select("#count").classed("active", true);
		});
	});

	function cell() {
		this.style("left", function(d) {
			return d.x + "px";
		}).style("top", function(d) {
			return d.y + "px";
		}).style("width", function(d) {
			return Math.max(0, d.dx - 1) + "px";
		}).style("height", function(d) {
			return Math.max(0, d.dy - 1) + "px";
		});
	}
	
	function byteDesc(len) {
		    var val = 0.0;
		    var ending = "";
		    if (len < (1024 * 1024)) {
		      val = (1.0 * len) / 1024;
		      ending = " KB";
		    } else if (len < 1024 * 1024 * 1024) {
		      val = (1.0 * len) / (1024 * 1024);
		      ending = " MB";
		    } else if (len < 1024 * 1024 * 1024 * 1024) {
		      val = (1.0 * len) / (1024 * 1024 * 1024);
		      ending = " GB";
		    } else if (len < 1024 * 1024 * 1024 * 1024 * 1024) {
		      val = (1.0 * len) / (1024 * 1024 * 1024 * 1024);
		      ending = " TB";
		    } else {
		      val = (1.0 * len) / (1024 * 1024 * 1024 * 1024 * 1024);
		      ending = " PB";
		    }
		    return val.toFixed(2) + ending;
	}
</script>