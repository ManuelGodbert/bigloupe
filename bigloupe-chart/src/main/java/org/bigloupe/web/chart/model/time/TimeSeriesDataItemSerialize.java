package org.bigloupe.web.chart.model.time;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TimeSeriesDataItemSerialize extends
		JsonSerializer<TimeSeriesDataItem> {

	@Override
	public void serialize(TimeSeriesDataItem item, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeNumber(item.getValue());
	}
}
