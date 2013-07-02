package org.bigloupe.web.chart.dao;

import java.util.List;

import org.bigloupe.web.chart.model.time.TimeSeries;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Series Data Access Object (DAO) interface.
 * 
 */
@Transactional(readOnly = true)
public interface TimeSeriesRepository extends PagingAndSortingRepository<TimeSeries, Long>, TimeSeriesDao, TimeSeriesRepositoryCustom {

	List<TimeSeries> findById(Long id);

	List<TimeSeries> findByKey(String key);

	List<TimeSeries> findByKeyContaining(String key);
	
}
