package org.bigloupe.web.chart.graphite.model;

import java.util.ArrayList;
import java.util.List;

import org.bigloupe.web.util.json.IJsonpObject;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ListGraphiteChartForJsonPSerialize.class)
public class ListGraphiteChartForJsonP implements IJsonpObject {

	@JsonIgnore
	String jsonCallback;

	@JsonProperty
	List<GraphiteSeries> listGraphiteSeries = new ArrayList<GraphiteSeries>();

	public List<GraphiteSeries> getListGraphiteSeries() {
		return listGraphiteSeries;
	}

	@Override
	public String getJsonCallback() {
		return jsonCallback;
	}

	public void setJsonCallback(String jsonCallback) {
		this.jsonCallback = jsonCallback;
	}
	
	public void add(GraphiteSeries graphiteChart) {
		listGraphiteSeries.add(graphiteChart);
	}

}
