<style type="text/css">
.highlight {
	stroke: blue;
}

#states path {
	fill: #ccc;
	stroke: #fff;
}

path.arc {
	pointer-events: none;
	fill: none;
	stroke: #000;
	display: none;
}

path.cell {
	fill: none;
	pointer-events: all;
}

circle {
	fill: steelblue;
	fill-opacity: .8;
	stroke: #fff;
}
</style>

<div id="airportInformation" class="popover fade"
	style="top: 120px; left: 40px; display: block;">
	<div class="popover-inner">
		<h3 class="popover-title">
			<div id="airportInformationTitle"></div>
		</h3>
		<div class="popover-content">
			<div id="airportInformationContent"></div>
		</div>
	</div>
</div>

<div class="row">
	<div class="page-header">
		<h3>
			Airports<small>&nbsp;Europe</small>
		</h3>

	</div>
</div>
<div class="row">
	<div class="span12" id="map"></div>
</div>

<script type="text/javascript">
	var w = 1280, h = 800;

	var projection = d3.geo.azimuthal().mode("equidistant").origin([ 5, 38 ])
			.scale(1400).translate([ 640, 560 ]);

	var circle = d3.geo.greatCircle().origin(projection.origin());

	var path = d3.geo.path().projection(projection);

	var svg = d3.select("#map").insert("svg:svg", "h2").attr("width", w).attr(
			"height", h); //.on("mousedown", mousedown);

	var states = svg.append("svg:g").attr("id", "states");

	var circles = svg.append("svg:g").attr("id", "circles");

	d3.json("${request.contextPath}/data/json/EURASIAFRICA.geo.json", function(
			collection) {
		states.selectAll("path").data(collection.features).enter().append(
				"svg:path").attr("d", path);
	});

	d3.csv("${request.contextPath}/data/csv/airports-afklm.csv", function(
			airports) {
		var positions = [];
		airports.forEach(function(airport) {
			var location = [ airport.longitude, airport.latitude ];
			positions.push(projection(location));

		});

		circles.selectAll("circle").data(airports).enter().append("svg:circle")
				.attr("class", "circle").attr("cx", function(d, i) {
					return positions[i][0];
				}).attr("cy", function(d, i) {
					return positions[i][1];
				}).attr("r", 12).on("mouseover.highlight", function(d, i) {
					displayAirportInformation(this, positions[i][1], positions[i][0]);
					return d3.select(this).classed("highlight", true);
				}).on("mouseout.highlight", function(d) {
					removeAirportInformation(this);
					return d3.select(this).classed("highlight", false);
				});

	});

	function displayAirportInformation(airport, top, left) {
		if (top < 120) {
			top = 120;
		}
		$('#airportInformation').removeClass('fade').css('top', top).css('left', 40 + left/2);
		
		$('#airportInformationTitle').html(airport.__data__.name);
		$('#airportInformationContent').html(
				'<p>IATA ' + airport.__data__.iata + '</p><p>City '
						+ airport.__data__.city + '</p><p>Country '
						+ airport.__data__.country + '</p><p>Latitude '
						+ airport.__data__.latitude + '</p><p>Longitude'
						+ airport.__data__.longitude + '</p>');
	};

	function removeAirportInformation(airport) {
		$('#airportInformation').addClass('fade');
	};


</script>