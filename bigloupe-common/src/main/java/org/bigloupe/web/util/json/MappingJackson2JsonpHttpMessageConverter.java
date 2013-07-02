package org.bigloupe.web.util.json;

import java.io.IOException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Compatible for JSONP
 * 
 */
public class MappingJackson2JsonpHttpMessageConverter extends
		MappingJackson2HttpMessageConverter {

	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders()
				.getContentType());
		JsonGenerator jsonGenerator = this.getObjectMapper().getFactory()
				.createJsonGenerator(outputMessage.getBody(), encoding);
		try {
			if (object instanceof IJsonpObject) {
				// If the callback doesn't provide, use the default callback

				String jsonCallback = ((IJsonpObject) object).getJsonCallback();
				if (jsonCallback != null) {

					jsonGenerator.writeRaw(jsonCallback);
					jsonGenerator.writeRaw('(');
					this.getObjectMapper().writeValue(jsonGenerator, object);
					jsonGenerator.writeRaw(");");
				} else
					this.getObjectMapper().writeValue(jsonGenerator, object);

				jsonGenerator.flush();
			} else {
				this.getObjectMapper().writeValue(jsonGenerator, object);
				jsonGenerator.flush();
			}
		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotWritableException("Could not write JSON: "
					+ ex.getMessage(), ex);
		}
	}
}
