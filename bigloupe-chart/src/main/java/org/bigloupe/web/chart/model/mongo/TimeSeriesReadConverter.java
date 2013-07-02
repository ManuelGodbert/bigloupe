package org.bigloupe.web.chart.model.mongo;

import java.math.BigInteger;

import org.bigloupe.web.chart.model.Series;
import org.bigloupe.web.chart.model.time.TimeSeries;
import org.bigloupe.web.chart.model.xy.XYSeries;
import org.springframework.core.convert.converter.Converter;

import com.mongodb.DBObject;

public class TimeSeriesReadConverter implements Converter<DBObject, TimeSeries> {

	@Override
	public TimeSeries convert(DBObject source) {
		TimeSeries series = new TimeSeries();
		series.setId(new BigInteger(((source.get("_id"))).toString(), 16));

		series.setKey((String) source.get("key"));
		series.setDescription((String) source.get("description"));
		
		return series;
	}
}
