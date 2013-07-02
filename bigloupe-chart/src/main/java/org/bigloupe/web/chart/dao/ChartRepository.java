package org.bigloupe.web.chart.dao;

import java.util.List;

import org.bigloupe.web.chart.model.Chart;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Chart Data Access Object (Dao) interface.
 * 
 */
@Transactional(readOnly = true)
public interface ChartRepository extends PagingAndSortingRepository<Chart, Long>, ChartDao {


}
