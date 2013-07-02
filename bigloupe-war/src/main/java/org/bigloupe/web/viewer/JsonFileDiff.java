package org.bigloupe.web.viewer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ContainerNode;
import org.codehaus.jackson.node.ValueNode;

/**
 * Compare 2 Avro Schema in Json
 * 
 * @author bigloupe
 * 
 */
public class JsonFileDiff {

	public static void main(String[] args) throws IOException {

		JsonFileDiff jsonFileDiff = new JsonFileDiff();

		BufferedReader fileJson1 = new BufferedReader(
				new FileReader(
						"D:/Workspace/Workspace-BigData/bigloupe/java/bigloupe-war/src/main/webapp/json1.json"));
		BufferedReader fileJson2 = new BufferedReader(
				new FileReader(
						"D:/Workspace/Workspace-BigData/bigloupe/java/bigloupe-war/src/main/webapp/json2.json"));

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		jsonFileDiff.diff(fileJson1, fileJson2, outputStream);
		System.out.print(outputStream.toString());
	}

	public void diff(String json1, String json2, OutputStream outputStream)
			throws FileNotFoundException, IOException, JsonProcessingException,
			UnsupportedEncodingException {
		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsonNode1 = mapper.readTree(json1);
		JsonNode jsonNode2 = mapper.readTree(json2);

		StringBuffer strBuffer = new StringBuffer();
		diff(strBuffer, jsonNode1, jsonNode2);
		outputStream.write(strBuffer.toString().getBytes("UTF-8"));
	}

	public void diff(Reader json1, Reader json2, OutputStream outputStream)
			throws FileNotFoundException, IOException, JsonProcessingException,
			UnsupportedEncodingException {
		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsonNode1 = mapper.readTree(json1);
		JsonNode jsonNode2 = mapper.readTree(json2);
		StringBuffer strBuffer = new StringBuffer();
		diff(strBuffer, jsonNode1, jsonNode2);
		outputStream.write(strBuffer.toString().getBytes("UTF-8"));
	}

	private void diff(StringBuffer strBuffer, JsonNode jsonNode1,
			JsonNode jsonNode2) throws IOException,
			UnsupportedEncodingException {
		if (jsonNode1.equals(jsonNode2)) {
			strBuffer.append("No differences");
			return;
		}

		// If JSON Object
		Iterator<Entry<String, JsonNode>> fields1 = jsonNode1.getFields();
		Iterator<Entry<String, JsonNode>> fields2 = jsonNode2.getFields();
		while (fields1.hasNext()) {
			Entry<String, JsonNode> fieldName1 = fields1.next();
			JsonNode fieldName2 = jsonNode2.get(fieldName1.getKey());
			// key not found
			if (fieldName2 == null) {
				strBuffer.append("<li><span class=\"folder\">&nbsp;");
				strBuffer.append(fieldName1.getKey()).append("&nbsp;:&nbsp;");
				strBuffer.append(fieldName1.getValue());
				strBuffer
						.append("<span class=\"label label-important\">Attribute not found</span>");
				strBuffer.append("</span></li>");
			} else
			// key found
			{
				strBuffer.append("<li><span class=\"folder\">&nbsp;");
				strBuffer.append(fieldName1.getKey()).append(
						"&nbsp;:&nbsp;");
				// Array : go in subfolder
				if (fieldName2 instanceof ArrayNode) {
					strBuffer.append("<ul>");
					JsonNode arrayNode1 = jsonNode1.get(fieldName1.getKey());
					JsonNode arrayNode2 = jsonNode2.get(fieldName1.getKey());
					diff(strBuffer, arrayNode1, arrayNode2);
					strBuffer.append("</ul>");
				} else {
					// Same value
					if (fieldName1.getValue().equals(fieldName2.getTextValue())) {
						strBuffer.append(fieldName1.getValue());
						// Different value
					} else {
						strBuffer
								.append("<span class=\"label label-warning\">")
								.append(fieldName1.getValue())
								.append("</span>");
						strBuffer
								.append("&nbsp - &nbsp<span class=\"label label-important\">\"")
								.append(fieldName2.getTextValue())
								.append("\"</span>");
					}

				}
				strBuffer.append("</span></li>");
			}
		} // end while

		// If JSON Array
		if (jsonNode1.isArray() && jsonNode2.isArray()) {
			Iterator<JsonNode> elements1 = jsonNode1.getElements();
			Iterator<JsonNode> elements2 = jsonNode2.getElements();
			while (elements1.hasNext()) {
				JsonNode jsonNode11 = (JsonNode) elements1.next();
				JsonNode jsonNode21 = (JsonNode) elements2.next();
				if (jsonNode11 instanceof ArrayNode) {
					strBuffer.append("<ul>");
					strBuffer.append(jsonNode11.getTextValue());
					diff(strBuffer, jsonNode11, jsonNode21);
					strBuffer.append("</ul>");
				} else {
					Iterator<String> fieldNames11 = jsonNode11.getFieldNames();
					while (fieldNames11.hasNext()) {
						String fieldNames111 = fieldNames11.next();
						JsonNode fieldName211 = jsonNode21.get(fieldNames111);
						if (fieldName211 == null) {
							strBuffer
									.append("<li><span class=\"file\">&nbsp;");
							strBuffer.append(fieldNames111).append(
									"&nbsp;:&nbsp;");
							strBuffer.append(jsonNode11
									.getFieldValue(fieldNames111));
							strBuffer
									.append("<span class=\"label label-important\">Attribute not found</span>");
							strBuffer.append("</span></li>");
						} else {
							strBuffer
									.append("<li><span class=\"file\">&nbsp;");
							strBuffer.append(fieldNames111).append(
									"&nbsp;:&nbsp;");
							// if same value
							if (jsonNode11.getFieldValue(fieldNames111).equals(
									jsonNode21.getFieldValue(fieldNames111))) {
								strBuffer.append(jsonNode11
										.getFieldValue(fieldNames111));
							} else {
								// if value different
								strBuffer
										.append("<span class=\"label label-warning\">")
										.append(jsonNode11
												.getFieldValue(fieldNames111))
										.append("</span>");
								strBuffer
										.append("&nbsp - &nbsp<span class=\"label label-important\">\"")
										.append(jsonNode21
												.getFieldValue(fieldNames111))
										.append("\"</span>");

								strBuffer.append("</span></li>");
							}
						}
					} // end while
					strBuffer
					.append("<li><span>&nbsp;</span></li>");
				}

			}
		}
	}
}
