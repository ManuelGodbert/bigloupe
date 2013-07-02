package org.bigloupe.web.chart.model.mongo;

import java.util.Date;

import org.bigloupe.web.chart.model.time.TimeSeries;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ClassUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class TimeSeriesWriteConverter implements Converter<TimeSeries, DBObject> {

	@Override
	public DBObject convert(TimeSeries series) {
		DBObject dbo = new BasicDBObject();
		if ((series!= null) && (series.getId() != null))
			dbo.put("_id", new ObjectId(series.getId().toString(16)));
		else
			dbo.put("_id", new ObjectId(new Date()));
		dbo.put("type", ClassUtils.getShortName(series.getClass()));
		dbo.put("key", series.getKey());
		dbo.put("description", series.getDescription());
		return dbo;
	}

}
