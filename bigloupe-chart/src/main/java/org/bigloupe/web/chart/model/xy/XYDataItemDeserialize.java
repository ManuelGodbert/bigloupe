package org.bigloupe.web.chart.model.xy;

import java.io.IOException;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class XYDataItemDeserialize extends JsonDeserializer<XYDataItem>{

	@Override
	public XYDataItem deserialize(JsonParser jsonParser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		 ObjectCodec oc = jsonParser.getCodec();
		 JsonNode node = oc.readTree(jsonParser);
		return new XYDataItem(node.get("x").asDouble(), node.get("y").asDouble());
	}



}
