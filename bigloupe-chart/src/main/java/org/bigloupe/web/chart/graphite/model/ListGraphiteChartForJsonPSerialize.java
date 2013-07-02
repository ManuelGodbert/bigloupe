package org.bigloupe.web.chart.graphite.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ListGraphiteChartForJsonPSerialize extends JsonSerializer<ListGraphiteChartForJsonP> {

	@Override
	public void serialize(ListGraphiteChartForJsonP listGraphiteChartForJsonP, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {

		JsonSerializer<Object> serializer = provider.findTypedValueSerializer(GraphiteSeries.class, true, null);
		//jgen.writeStartArray();
		for (int i = 0; i < listGraphiteChartForJsonP.getListGraphiteSeries().size(); i++) {
			GraphiteSeries graphiteSeries = listGraphiteChartForJsonP.getListGraphiteSeries().get(i);
			
			serializer.serialize(graphiteSeries, jgen, provider);
			if (i+1 < listGraphiteChartForJsonP.getListGraphiteSeries().size())
				jgen.writeRaw(",");
				
		}
		//jgen.writeEndArray();

		
	}

}
