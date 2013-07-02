package org.bigloupe.web.chart.exception;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ChartExceptionSerialize extends JsonSerializer<ChartException> {

	@Override
	public void serialize(ChartException value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeObjectField("code", value.code);
		jgen.writeObjectField("reason", value.reason);
		jgen.writeEndObject();

	}

}
