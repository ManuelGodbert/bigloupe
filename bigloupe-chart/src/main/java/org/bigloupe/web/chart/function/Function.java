package org.bigloupe.web.chart.function;

import java.util.List;

import org.bigloupe.web.chart.model.Series;
import org.bigloupe.web.chart.model.xy.XYSeries;

public interface Function {

	public List<Series> execute(List<Series> listSeriesWithFunction);
}
