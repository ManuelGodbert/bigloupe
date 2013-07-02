package org.bigloupe.web.chart.graphite.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bigloupe.web.chart.model.time.TimeSeries;
import org.bigloupe.web.chart.model.time.TimeSeriesDataItem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Chart wrapper compatible with Graphite
 */
@JsonSerialize(using = GraphiteSeriesSerialize.class)
public class GraphiteSeries {

	@JsonProperty
	String target;

	@JsonProperty
	List<TimeSeriesDataItem> datapoints;

	public GraphiteSeries(String target, TimeSeries series) {
		this.target = target;

		if (series != null) {
			datapoints = series.getData();

		} else {
			datapoints = new ArrayList<TimeSeriesDataItem>();
			// only for demo. Add some points if series == null
			for (int i = 0; i < 20; i++) {
				datapoints.add(new TimeSeriesDataItem(10 + i, (new Date()).getTime()
						/ 1000 + (60 * i)));
			}
		}
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<TimeSeriesDataItem> getDatapoints() {
		return datapoints;
	}

	public void setDatapoints(List<TimeSeriesDataItem> datapoints) {
		this.datapoints = datapoints;
	}

}
