package org.bigloupe.web.chart.model.mongo;

import java.util.Date;

import org.bigloupe.web.chart.model.Chart;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ChartWriteConverter implements Converter<Chart, DBObject> {

	@Override
	public DBObject convert(Chart chart) {
		DBObject dbo = new BasicDBObject();
		if ((chart!= null) && (chart.getId() != null))
			dbo.put("_id", new ObjectId(chart.getId().toString(16)));
		else
			dbo.put("_id", new ObjectId(new Date()));
		dbo.put("key", chart.getKey());
		return dbo;
	}

}
