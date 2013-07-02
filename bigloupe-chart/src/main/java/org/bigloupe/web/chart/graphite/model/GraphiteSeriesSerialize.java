package org.bigloupe.web.chart.graphite.model;

import java.io.IOException;

import org.bigloupe.web.chart.model.time.TimeSeriesDataItem;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class GraphiteSeriesSerialize extends
		JsonSerializer<GraphiteSeries> {

	@Override
	public void serialize(GraphiteSeries graphiteChart, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();

		jgen.writeStringField("target", graphiteChart.getTarget() + ",");
		jgen.writeFieldName("datapoints");
		jgen.writeStartArray();
		
		for (TimeSeriesDataItem xyDataItem : graphiteChart.getDatapoints()) {
			jgen.writeStartArray();
			// Value + time in seconds
			jgen.writeNumber(xyDataItem.getValue());
			jgen.writeNumber(xyDataItem.getDate()/ 1000);
			jgen.writeEndArray();
		}
		jgen.writeEndArray();
		jgen.writeEndObject();
	}
}
