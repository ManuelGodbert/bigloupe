package org.bigloupe.web.chart.function;

import java.util.ArrayList;
import java.util.List;

import org.bigloupe.web.chart.model.DataItem;
import org.bigloupe.web.chart.model.Series;

/**
 * Function Max
 */
public class MaxFunction extends AbstractFunction implements Function {

	public MaxFunction() {
	}
	
	@Override
	public List<Series> execute(List<Series> listSeriesWithFunction) {
		double yMax = 0;
		double x = 0;
		for (Series series : listSeriesWithFunction) {
			List<DataItem> datas = (List<DataItem>) series.getData();
			for (DataItem data : datas) {
				yMax = Math.max(data.getY().doubleValue(), yMax);
				x = data.getX().doubleValue();
			}
		}
		List<Series> series = new ArrayList<Series>(1);

	//	StatisticsSeries statisticsSeries = new StatisticsSeries();
	//	statisticsSeries.add(new StatisticsDataItem(x, yMax));
	//	series.add(statisticsSeries);
		return series;

	}
}

