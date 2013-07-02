package org.bigloupe.web.chart.function;

import java.util.ArrayList;
import java.util.List;

import org.bigloupe.web.chart.model.DataItem;
import org.bigloupe.web.chart.model.Series;

/**
 * Function Sum
 * 
 */
public class SumFunction extends AbstractFunction implements Function {
	
	public SumFunction() {
	}

	@Override
	public List<Series> execute(List<Series> listSeriesWithFunction) {
		double sumX = 0;
		double sumY = 0;
		for (Series series : listSeriesWithFunction) {
			List<DataItem> datas = (List<DataItem>) series.getData();
			for (DataItem data : datas) {
				sumX = sumX + data.getX().doubleValue();
				sumY = sumY + data.getY().doubleValue();
			}
		}
		List<Series> series = new ArrayList<Series>(1);
//		StatisticsSeries statisticsSeries = new StatisticsSeries();
//		statisticsSeries.add(new StatisticsDataItem(sumX, sumY));
//		series.add(statisticsSeries);
		return series;
	}

}
