package org.bigloupe.web.chart.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.bigloupe.web.chart.dao.TimeSeriesRepositoryCustom;
import org.bigloupe.web.chart.model.time.TimeSeries;
import org.bigloupe.web.chart.model.time.TimeSeriesDataItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Warning class name must respect Spring Data Convention
 * 
 * @author bigloupe
 *
 */
public class TimeSeriesRepositoryMongoImpl implements TimeSeriesRepositoryCustom {

	@Autowired
	MongoTemplate mongoTemplate;

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void addTimeSeriesDataItem(String key, long date, double value) {
		Query query = new Query(where("key").is(key));
		Update update = new Update().push("data", new TimeSeriesDataItem(date, value));
		TimeSeries timeSeries = mongoTemplate.findAndModify(query, update, TimeSeries.class);
		if (timeSeries == null) {
			timeSeries = new TimeSeries();
			timeSeries.setKey(key);
			mongoTemplate.save(timeSeries);
		}
	}


}
