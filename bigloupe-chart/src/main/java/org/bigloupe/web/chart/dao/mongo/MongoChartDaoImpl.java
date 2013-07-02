package org.bigloupe.web.chart.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bigloupe.web.chart.dao.ChartDao;
import org.bigloupe.web.chart.model.Chart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Just for test : use instead Spring-MongoDB Repositories
 * 
 * @author bigloupe
 *
 */
public class MongoChartDaoImpl implements ChartDao {

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	public Chart save(Chart chart) {
		mongoTemplate.save(chart);
		return chart;
	}

	@Override
	public Chart findOne(Long id) {
		Assert.notNull(id, "The given id must not be null!");
		return mongoTemplate.findById(id, Chart.class);
	}

	@Override
	public long count() {
		Query query = new Query();
		return mongoTemplate.count(query , Chart.class);
	}

	@Override
	public List<Chart> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Chart findByKey(String key) {
		return mongoTemplate.findOne(new Query(where("key").is(key)),Chart.class);
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

}
