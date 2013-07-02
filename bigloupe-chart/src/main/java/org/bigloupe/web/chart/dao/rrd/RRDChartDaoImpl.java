package org.bigloupe.web.chart.dao.rrd;

import java.io.IOException;
import java.util.List;

import org.bigloupe.ErrorCode;
import org.bigloupe.web.chart.dao.ChartDao;
import org.bigloupe.web.chart.exception.ChartException;
import org.bigloupe.web.chart.model.Chart;
import org.bigloupe.web.chart.model.DataItem;
import org.bigloupe.web.chart.model.xy.XYDataItem;
import org.bigloupe.web.chart.model.xy.XYSeries;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.ArcDef;
import org.rrd4j.core.DsDef;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;

/**
 * ChartDao implementation with RRD4J
 * 
 * @author bigloupe
 * 
 */
public class RRDChartDaoImpl implements ChartDao {

	@SuppressWarnings("unchecked")
	@Override
	public Chart save(Chart chart) throws ChartException {
		try {
			if (chart == null) {
				throw new ChartException(ErrorCode.CHART_MUST_BE_NOT_NULL);
			}
			if ((chart.getId() == null) || (chart.getKey() == null)) {
				throw new ChartException(ErrorCode.CHART_MUST_HAVE_ID_AND_KEY);
			}
			RrdDef rrdDef = new RrdDef(chart.getId() + "-" + chart.getKey(),
					300);
			int steps = 1;
			if (chart.getListXYSeries() != null) {
				steps = chart.getListXYSeries().size();
			}
			List<XYSeries> listSeries = chart.getListXYSeries();
			for (int i = 0; i < chart.getListXYSeries().size(); i++) {
				XYSeries series = listSeries.get(i);
				List<XYDataItem> listDataItem = series.getData();
				int rows = 0;
				if (listDataItem != null) {
					rows = listDataItem.size();
				}
				ArcDef arcDef = new ArcDef(ConsolFun.TOTAL, 0.5, steps, rows);
				rrdDef.addArchive(arcDef);
				DsDef dsDef = new DsDef("y", DsType.COUNTER, 1, Double.NaN,
						Double.NaN);
				rrdDef.addDatasource(dsDef);
				RrdDb rrdDb = new RrdDb(rrdDef);

				Sample sample = rrdDb.createSample();

				for (Object objDataItem : listDataItem) {
					DataItem dataItem = (DataItem) objDataItem;
					sample.setTime(dataItem.getX().longValue());
					sample.setValue("y", dataItem.getY().longValue());
					sample.update();
				}

				rrdDb.close();
			}
			return chart;

		} catch (IOException e) {
			e.printStackTrace();
			throw new ChartException(ErrorCode.CHART_CANNOT_BE_SAVED);
		}
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Chart> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Chart findByKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Chart findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
