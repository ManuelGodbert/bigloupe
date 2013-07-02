<style type="text/css">
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

#cells.voronoi path.cell {
	stroke: brown;
}

#cells g:hover path.arc {
	display: inherit;
}
</style>
<div class="row">
	<div class="page-header">
		<h3>
			Airports<small>&nbsp;Europe</small>
		</h3>

	</div>
</div>

<script type="text/javascript">
	var w = 1280, h = 800;

	var projection = d3.geo.azimuthal().mode("equidistant").origin([ 5, 38 ])
			.scale(1400).translate([ 640, 560 ]);

	var path = d3.geo.path().projection(projection);

	var svg = d3.select("body").insert("svg:svg", "h2").attr("width", w).attr(
			"height", h);

	var states = svg.append("svg:g").attr("id", "states");

	var circles = svg.append("svg:g").attr("id", "circles");

	var cells = svg.append("svg:g").attr("id", "cells");

	d3.select("input[type=checkbox]").on("change", function() {
		cells.classed("voronoi", this.checked);
	});

 	d3.json("${request.contextPath}/data/json/EURASIAFRICA.geo.json", function(collection) {
		states.selectAll("path").data(collection.features).enter().append(
				"svg:path").attr("d", path);
	}); 

	
	d3
	.csv(
			"${request.contextPath}/data/csv/airports-afklm.csv",
			function(airports) {
					var positions = [];
					airports
						.forEach(function(airport) {
							var location = [
											+airport.longitude,
											+airport.latitude ];
							positions
								.push(projection(location));
							
						});
						
					circles
					.selectAll("circle")
					.data(airports)
					.enter()
					.append("svg:circle")
					.attr("class", "circle")
					.attr("cx", function(d, i) {
						return positions[i][0];
					})
					.attr("cy", function(d, i) {
						return positions[i][1];
					})
					.attr(
							"r",
							12);
			 
	});
				
				
/*				
  	d3
			.csv(
					"${request.contextPath}/data/csv/flights-airport-usa.csv",
					function(flights) {
						var linksByOrigin = {}, countByAirport = {}, locationByAirport = {}, positions = [];

						var arc = d3.geo.greatArc().source(function(d) {
							return locationByAirport[d.source];
						}).target(function(d) {
							return locationByAirport[d.target];
						});

						flights
								.forEach(function(flight) {
									var origin = flight.origin, destination = flight.destination, links = linksByOrigin[origin]
											|| (linksByOrigin[origin] = []);
									links.push({
										source : origin,
										target : destination
									});
									countByAirport[origin] = (countByAirport[origin] || 0) + 1;
									countByAirport[destination] = (countByAirport[destination] || 0) + 1;
								}); 

						d3
								.csv(
										"${request.contextPath}/data/csv/airports-afklm.csv",
										function(airports) {

											// Only consider airports with at least one flight.
											airports = airports
													.filter(function(airport) {
														if (countByAirport[airport.iata]) {
															var location = [
																	+airport.longitude,
																	+airport.latitude ];
															locationByAirport[airport.iata] = location;
															positions
																	.push(projection(location));
															return true;
														}
													});

											// Compute the Voronoi diagram of airports' projected positions.
											var polygons = d3.geom
													.voronoi(positions);

											var g = cells.selectAll("g").data(
													airports).enter().append(
													"svg:g");

											g
													.append("svg:path")
													.attr("class", "cell")
													.attr(
															"d",
															function(d, i) {
																return "M"
																		+ polygons[i]
																				.join("L")
																		+ "Z";
															})
													.on(
															"mouseover",
															function(d, i) {
																d3
																		.select(
																				"h2 span")
																		.text(
																				d.name);
															});

											g
													.selectAll("path.arc")
													.data(
															function(d) {
																return linksByOrigin[d.iata]
																		|| [];
															}).enter().append(
															"svg:path").attr(
															"class", "arc")
													.attr("d", function(d) {
														return path(arc(d));
													});

											circles
													.selectAll("circle")
													.data(airports)
													.enter()
													.append("svg:circle")
													.attr("cx", function(d, i) {
														return positions[i][0];
													})
													.attr("cy", function(d, i) {
														return positions[i][1];
													})
													.attr(
															"r",
															function(d, i) {
																return Math
																		.sqrt(countByAirport[d.iata]);
															})
													.sort(
															function(a, b) {
																return countByAirport[b.iata]
																		- countByAirport[a.iata];
															});
										});
					});  */
</script>