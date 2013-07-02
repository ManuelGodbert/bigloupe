package org.bigloupe.web.chart.dao;

import java.util.List;

import org.bigloupe.web.chart.model.xy.XYSeries;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Series Data Access Object (DAO) interface.
 * 
 */
@Transactional(readOnly = true)
public interface XYSeriesRepository extends PagingAndSortingRepository<XYSeries, Long>, XYSeriesDao, XYSeriesRepositoryCustom {

	List<XYSeries> findById(Long id);

	List<XYSeries> findByKey(String key);

	List<XYSeries> findByKeyContaining(String key);
	
}
