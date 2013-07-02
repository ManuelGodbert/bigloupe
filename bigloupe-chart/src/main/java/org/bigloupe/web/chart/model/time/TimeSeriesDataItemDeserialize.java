package org.bigloupe.web.chart.model.time;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class TimeSeriesDataItemDeserialize extends
		JsonDeserializer<TimeSeriesDataItem> {

	@Override
	public TimeSeriesDataItem deserialize(JsonParser jsonParser,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);
		return new TimeSeriesDataItem((new Date()).getTime(), node.asDouble());
	}
}
