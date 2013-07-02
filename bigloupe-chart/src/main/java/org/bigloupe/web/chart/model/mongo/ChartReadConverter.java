package org.bigloupe.web.chart.model.mongo;

import java.math.BigInteger;

import org.bigloupe.web.chart.model.Chart;
import org.springframework.core.convert.converter.Converter;

import com.mongodb.DBObject;

public class ChartReadConverter implements Converter<DBObject, Chart> {
	
	@Override
	public Chart convert(DBObject source) {
		Chart chart = new Chart();
		chart.setId(new BigInteger(((source.get("_id"))).toString(), 16));

		chart.setKey((String)source.get("key"));
		return chart;
	}
}
