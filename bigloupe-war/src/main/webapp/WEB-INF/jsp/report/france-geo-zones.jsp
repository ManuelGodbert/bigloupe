<style>

.background {
  fill: none;
  pointer-events: all;
}

#states {
  fill: #aaa;
  stroke: #fff;
  stroke-width: 1.5px;
}

#states path:hover {
  stroke: white;
}

</style>
<div class="row">
	<div class="page-header">
		<h3>
			France<small>&nbsp;Click to zoom</small>
		</h3>
	</div>
</div>
<div id="body"></div>
<script type="text/javascript">

var xy = d3.geo.albers();

var path = d3.geo.path();

var svg = d3.select("body")
  .append("svg:svg")
    .call(d3.behavior.zoom()
    .on("zoom", redraw))
    .append("svg:g") ;

var states = svg.append("svg:g")
    .attr("id", "states");

//data for my choropleth
d3.json("${request.contextPath}/data/json/FRA.geo.json", function(json) {
   data = json;
   states.selectAll("path")
   .attr("class", quantize);
          });

//borders
d3.json("C18_lt.json", function(json) {
  svg.select("#states")
    .selectAll("path")
     .data(json.features)
    .enter().append("svg:path")
     .attr("d", d3.geo.path().projection(xy))
.attr("fill", function(d) { return fill(data[d.id]) ; })

});

var fill = function(v) {
  if (v < 0.1) return "#969696";
  if (v < 10) return "#FFEDA0";
  if (v < 200) return "#FFEDA0";
  if (v < 500) return "#FED976";
  if (v < 1000) return "#FEB24C";
  if (v < 5000) return "#FD8D3C";
  if (v < 10000) return "#FC4E2A";
  if (v < 50000) return "#E31A1C";
  if (v < 60000) return "#B10026";
  return "#B7161E";

};

function quantize(d) {
  return "q" + Math.min(8, ~~(data[d.id]/500 * 9 / 12)) + "-9";

}

function redraw() {
  svg.attr("transform", "translate(" + d3.event.translate + ")scale("
+ d3.event.scale + ")"); 
}
  
	/* var width = 960, height = 500, centered;

	var projection = d3.geo.albersUsa().scale(width).translate([ 0, 0 ]);

	var path = d3.geo.path().projection(projection);

	var svg = d3.select("#body").append("svg").attr("width", width).attr(
			"height", height);

	svg.append("rect").attr("class", "background").attr("width", width).attr(
			"height", height).on("click", click);

	var states = svg.append("g").attr("transform",
			"translate(" + width / 2 + "," + height / 2 + ")").append("g")
			.attr("id", "states");

	d3.json("${request.contextPath}/data/json/FRA.geo.json", function(json) {
		states.selectAll("path").data(json.features).enter().append("path")
				.attr("d", path).on("click", click);
	});

	function click(d) {
		var x = 0, y = 0, k = 1;

		if (d && centered !== d) {
			var centroid = path.centroid(d);
			x = -centroid[0];
			y = -centroid[1];
			k = 4;
			centered = d;
		} else {
			centered = null;
		}

		states.transition().duration(1000).attr("transform",
				"scale(" + k + ")translate(" + x + "," + y + ")").style(
				"stroke-width", 1.5 / k + "px");
	} */
	
</script>