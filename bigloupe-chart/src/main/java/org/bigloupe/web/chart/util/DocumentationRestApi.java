package org.bigloupe.web.chart.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationAllowableRangeValues;
import com.wordnik.swagger.core.DocumentationAllowableValues;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import com.wordnik.swagger.core.DocumentationSchema;

/**
 * Create Swagger documentation for chart API
 * 
 * @author bigloupe
 * 
 */
public class DocumentationRestApi {

	private static Logger logger = LoggerFactory.getLogger(DocumentationRestApi.class);

	private Documentation documentation;

	public DocumentationRestApi(String apiVersion, String swaggerVersion,
			String documentationBasePath, String resourcePath) {
		documentation = new Documentation(apiVersion, swaggerVersion,
				documentationBasePath, resourcePath);
		addDocumentation();
	}

	private void addDocumentation() {

		// 1. Get chart
		////////////////////////////////////////////////////////////////////////
		DocumentationEndPoint documentationEndPoint = new DocumentationEndPoint(
				"/chart/chart-api/{id}.{format}", "Get chart by id");

		DocumentationOperation documentationOperation = new DocumentationOperation(
				"GET", "Get chart by id", "");
		DocumentationAllowableValues doc = new DocumentationAllowableRangeValues(
				0, 10);
		// paramType : path, query, body, or header
		DocumentationParameter idParam = new DocumentationParameter("id",
				"identifier", "Chart id", "path", "0", doc, true, false);
		idParam.setDataType("long");
		documentationOperation.addParameter(idParam);
		documentationOperation.setNickname("getChart");
		documentationOperation.setResponseClass("Chart");

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);

		// 2. Save or update chart
		///////////////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/chart-api.{format}", "Create or update chart");
		documentationOperation = new DocumentationOperation("POST",
				"Create or update chart", "");

		DocumentationParameter chartParam = new DocumentationParameter();
		chartParam.setDescription("Chart object");
		chartParam.setParamType("body");
		chartParam.setRequired(true);
		chartParam.setAllowMultiple(false);
		chartParam.setDataType("Chart");

		documentationOperation.addParameter(chartParam);
		documentationOperation.setNickname("saveChart");
		documentationOperation.setResponseClass("Chart");

		DocumentationError errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Invalid chart");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);

		// 3.a Get XY series by id
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/xy/id/{id}.{format}", "Get XY series");
		documentationOperation = new DocumentationOperation("GET",
				"Get XY series", "");

		DocumentationParameter idParamSeries = new DocumentationParameter("id",
				"identifier", "XY Series id", "path", "0", doc, true, false);
		idParamSeries.setDataType("long");
		documentationOperation.addParameter(idParamSeries);
		documentationOperation.setNickname("getXYSeriesById");
		documentationOperation.setResponseClass("XYSeries");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Invalid series");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);

		// 3.b Get time series by id
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/time/id/{id}.{format}", "Get Time series");
		documentationOperation = new DocumentationOperation("GET",
				"Get Time series", "");

		idParamSeries = new DocumentationParameter("id",
				"identifier", "Time Series id", "path", "123", doc, true, false);
		idParamSeries.setDataType("long");
		documentationOperation.addParameter(idParamSeries);
		documentationOperation.setNickname("getTimeSeriesById");
		documentationOperation.setResponseClass("TimeSeries");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Invalid series");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);
		
		// 4.a Get xy series by key
		////////////////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/xy/key/{key}.{format}", "Get XY series by key");

		documentationOperation = new DocumentationOperation(
				"GET", "Get XY series by key", "");

		DocumentationParameter keyParamSeries = new DocumentationParameter("key",
				"your identifier to retrieve your series", "XY Series key", "path", "123", null, true, false);
		keyParamSeries.setDataType("XYSeries");
		documentationOperation.addParameter(keyParamSeries);
		documentationOperation.setNickname("getXYSeriesByKey");
		documentationOperation.setResponseClass("XYSeries");
		
		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);
	
		// 4.b Get time series by key
		////////////////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/time/key/{key}.{format}", "Get time series by key");

		documentationOperation = new DocumentationOperation(
				"GET", "Get time series by key", "");

		keyParamSeries = new DocumentationParameter("key",
				"your identifier to retrieve your series", "Time Series key", "path", "123", null, true, false);
		keyParamSeries.setDataType("TimeSeries");
		documentationOperation.addParameter(keyParamSeries);
		documentationOperation.setNickname("getTimeSeriesByKey");
		documentationOperation.setResponseClass("TimeSeries");
		
		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);
		
		// 5.a Save or update XY series
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/xy.{format}", "Save or update XY series");
		documentationOperation = new DocumentationOperation("POST",
				"Save or update XY series", "");

		DocumentationParameter seriesParam = new DocumentationParameter();
		seriesParam.setDescription("XY Series object");
		seriesParam.setParamType("body");
		seriesParam.setRequired(true);
		seriesParam.setAllowMultiple(false);
		seriesParam.setDataType("XYSeries");

		documentationOperation.addParameter(seriesParam);
		documentationOperation.setNickname("saveXYSeries");
		documentationOperation.setResponseClass("XYSeries");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Invalid series");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);

		
		// 5.b Save or update Time series
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/time.{format}", "Save or update Time series");
		documentationOperation = new DocumentationOperation("POST",
				"Save or update Time series", "");

		seriesParam = new DocumentationParameter();
		seriesParam.setDescription("Time Series object");
		seriesParam.setParamType("body");
		seriesParam.setRequired(true);
		seriesParam.setAllowMultiple(false);
		seriesParam.setDataType("TimeSeries");

		documentationOperation.addParameter(seriesParam);
		documentationOperation.setNickname("saveTimeSeries");
		documentationOperation.setResponseClass("TimeSeries");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Invalid series");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);
		
		// 6.a Delete XY series from key
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/xy/{key}.{format}", "Delete XY series");
		documentationOperation = new DocumentationOperation("DELETE",
				"Delete XY series from key", "");

		keyParamSeries = new DocumentationParameter("key",
				"your identifier to delete your XY series", "XY Series key", "path", "123", null, true, false);
		keyParamSeries.setDataType("XYSeries");

		documentationOperation.addParameter(keyParamSeries);
		documentationOperation.setNickname("deleteXYSeries");
		documentationOperation.setResponseClass("void");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Unknown series");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);

		// 6.b Delete Time series from key
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/time/{key}.{format}", "Delete time series");
		documentationOperation = new DocumentationOperation("DELETE",
				"Delete Time series from key", "");

		keyParamSeries = new DocumentationParameter("key",
				"your identifier to delete your time series", "Time Series key", "path", "123", null, true, false);
		keyParamSeries.setDataType("TimeSeries");

		documentationOperation.addParameter(keyParamSeries);
		documentationOperation.setNickname("deleteTimeSeries");
		documentationOperation.setResponseClass("void");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Unknown series");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);
		
		
		// 7.a Add XY item in series
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/xy/{id}/addData.{format}", "Add XY data item in XY Series");
		documentationOperation = new DocumentationOperation("POST",
				"Add XY data item in XY series", "");

		idParamSeries.setDataType("long");
		documentationOperation.addParameter(idParamSeries);

		DocumentationParameter itemParamXYSeries = new DocumentationParameter();
		itemParamXYSeries.setDescription("XY Data values in JSON");
		itemParamXYSeries.setDefaultValue("{\"x\": 0,\"y\": 0}");
		itemParamXYSeries.setParamType("body");
		itemParamXYSeries.setRequired(true);
		itemParamXYSeries.setAllowMultiple(false);
		itemParamXYSeries.setDataType("XYDataItem");
		documentationOperation.addParameter(itemParamXYSeries);
		
		documentationOperation.setNickname("addXYDataToXYSeries");
		documentationOperation.setResponseClass("XYSeries");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Can't add item to series");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);
		
		// 7.b Add item in Time series
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/time/{id}/addData.{format}", "Add data item in Time Series");
		documentationOperation = new DocumentationOperation("POST",
				"Add data item in Time series", "");

		idParamSeries.setDataType("long");
		documentationOperation.addParameter(idParamSeries);

		DocumentationParameter itemParamTimeSeries = new DocumentationParameter();
		itemParamTimeSeries.setDescription("Data values in JSON");
		itemParamTimeSeries.setDefaultValue("12");
		itemParamTimeSeries.setParamType("body");
		itemParamTimeSeries.setRequired(true);
		itemParamTimeSeries.setAllowMultiple(false);
		itemParamTimeSeries.setDataType("TimeSeriesDataItem");
		documentationOperation.addParameter(itemParamTimeSeries);
		
		documentationOperation.setNickname("addDataToTimeSeries");
		documentationOperation.setResponseClass("TimeSeries");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Can't add item to series");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);
		
		// 8.a Create XY series
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/xy/createSeries/{key}.{format}", "Create sample XY series");
		documentationOperation = new DocumentationOperation("POST",
				"Create sample XY series", "");

		keyParamSeries = new DocumentationParameter("key",
				"your identifier to retrieve your series", "Series key", "path", "123", null, true, false);
		keyParamSeries.setDataType("XYSeries");
		documentationOperation.addParameter(keyParamSeries);
		documentationOperation.setNickname("createSeries");
		documentationOperation.setResponseClass("XYSeries");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Can't create new series");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);

		// 8.b Create time series
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/series-api/time/createSeries/{key}.{format}", "Create sample time series");
		documentationOperation = new DocumentationOperation("POST",
				"Create sample time series", "");

		keyParamSeries.setDataType("Series");
		documentationOperation.addParameter(keyParamSeries);
		documentationOperation.setNickname("createSeries");
		documentationOperation.setResponseClass("Series");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Can't create new series");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);
		
		// 9. Create chart
		////////////////////////////////////////////////////////////
		documentationEndPoint = new DocumentationEndPoint(
				"/chart/chart-api/create/{key}.{format}", "Create sample chart");
		documentationOperation = new DocumentationOperation("POST",
				"Create sample chart", "");


		DocumentationParameter keyParamChart = new DocumentationParameter("key",
				"your identifier to retrieve your charts", "Chart key", "path", "123", null, true, false);
		keyParamChart.setDataType("Chart");
		documentationOperation.addParameter(keyParamChart);

		documentationOperation.setNickname("createChart");
		documentationOperation.setResponseClass("Chart");

		errorResponse = new DocumentationError();
		errorResponse.setCode(400);
		errorResponse.setReason("Can't create new chart");
		documentationOperation.addErrorResponse(errorResponse);

		documentationEndPoint.addOperation(documentationOperation);
		documentation.addApi(documentationEndPoint);

		

		
		// 10. Create documentation about chart object
		//////////////////////////////////////////////////////////////
		DocumentationSchema chartSchema = new DocumentationSchema();
		chartSchema.setId("Chart");

		Map<String, DocumentationSchema> chartProperties = new HashMap<String, DocumentationSchema>();

		DocumentationSchema idSchema = new DocumentationSchema();
		idSchema.setName("id");
		idSchema.setType("long");
		chartProperties.put("id", idSchema);

		DocumentationSchema dataSchema = new DocumentationSchema();
		dataSchema.setName("data");
		dataSchema.setType("long");
		chartProperties.put("data", dataSchema);

		chartSchema.setProperties(chartProperties);
		documentation.addModel("Chart", chartSchema);
		
		
		// 11.a Create documentation about XY series object
		//////////////////////////////////////////////////////////////		
		DocumentationSchema seriesSchema = new DocumentationSchema();
		seriesSchema.setId("XYSeries");

		Map<String, DocumentationSchema> seriesProperties = new HashMap<String, DocumentationSchema>();
		seriesProperties.put("id", idSchema);

		DocumentationSchema keySchema = new DocumentationSchema();
		keySchema.setName("key");
		keySchema.setType("string");
		seriesProperties.put("key", keySchema);

		chartSchema.setProperties(seriesProperties);		
		documentation.addModel("XYSeries", seriesSchema);

		
		// 11.b Create documentation about Time series object
		//////////////////////////////////////////////////////////////		
		DocumentationSchema timeSchema = new DocumentationSchema();
		seriesSchema.setId("TimeSeries");

		seriesProperties = new HashMap<String, DocumentationSchema>();
		seriesProperties.put("id", idSchema);

		keySchema = new DocumentationSchema();
		keySchema.setName("key");
		keySchema.setType("string");
		seriesProperties.put("key", keySchema);

		chartSchema.setProperties(seriesProperties);		
		documentation.addModel("TimeSeries", seriesSchema);
	}

	public Documentation getResourceListing() {

		// TODO REMOVE next code after testing swagger
		documentation = new Documentation("0.1", "1.1",
				"http://localhost:2006", "/chart");
		addDocumentation();
		// ////

		return documentation;
	}
}
