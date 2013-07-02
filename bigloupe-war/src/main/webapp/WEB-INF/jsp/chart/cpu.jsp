

<div class="row">
	
	<div class="row-fluid">
	<br/><br/><br/>
		<div id="chart" class="span4"></div>
		<div id="legend" class="span2"></div>
	</div>
</div>

<script>
	var palette = new Rickshaw.Color.Palette({
		scheme : 'spectrum14'
	});

	
	var wrapper = new Rickshaw.Graph.Ajax({
		element : document.getElementById("chart"),
		dataURL : '${request.contextPath}/chart/series-api/3.json',
		//dataURL : '${request.contextPath}/chart/chart-api/create/0.json',
		width : 320,
		height : 160,
		renderer: 'bar',
		onData : function(d) {
			return transformData(d.data)
		},
		onComplete : function(w) {
			var legend = new Rickshaw.Graph.Legend({
				element : document.querySelector('#legend'),
				graph : w.graph
			});
			
			var xAxis = new Rickshaw.Graph.Axis.Time({
			    graph: w.graph
			});

			xAxis.render();
			
			var yAxis = new Rickshaw.Graph.Axis.Y({
			    graph: w.graph
			});

			yAxis.render();
		}
	});

	function transformData(d) {
		var data = [];
		var statusCounts = {};

		Rickshaw.keys(d).sort().forEach(function(t) {
			Rickshaw.keys(d[t]).forEach(function(status) {
				statusCounts[status] = statusCounts[status] || [];
				statusCounts[status].push({
					x : parseFloat(t),
					y : d[t][status]
				});
			});
		});

		Rickshaw.keys(statusCounts).sort().forEach(function(status) {
			data.push({
				name : status,
				data : statusCounts[status],
				color : palette.color(status)
			});
		});

		Rickshaw.Series.zeroFill(data);
		return data;
	}
</script>