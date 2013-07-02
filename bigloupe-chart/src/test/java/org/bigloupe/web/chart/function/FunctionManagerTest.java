package org.bigloupe.web.chart.function;

import java.util.ArrayList;
import java.util.List;

import org.bigloupe.web.chart.model.Series;
import org.bigloupe.web.chart.model.xy.XYDataItem;
import org.bigloupe.web.chart.model.xy.XYSeries;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class FunctionManagerTest {

	FunctionManager functionManager = new FunctionManager();
	List<Series> seriesXY = new ArrayList<Series>();

	@Before
	public void setUp() {
		XYSeries xySeries = new XYSeries();
		for (int i = 0; i < 10; i++) {
			xySeries.add(i, i);
		}
		
		seriesXY.add(xySeries);
		
	}
	
	@Test
	public void testSumFunction() {
		List<Series> listSeries = functionManager.execute("sum", seriesXY);
		Assert.assertEquals(1, listSeries.size());
		Assert.assertEquals(Double.valueOf(1+2+3+4+5+6+7+8+9), ((XYDataItem) listSeries.get(0).getData().get(0)).getX());
		Assert.assertEquals(Double.valueOf(1+2+3+4+5+6+7+8+9), ((XYDataItem) listSeries.get(0).getData().get(0)).getY());
	}
	
	@Test
	public void testMaxFunction() {
		List<Series> listSeries = functionManager.execute("max", seriesXY);
		Assert.assertEquals(1, listSeries.size());
		Assert.assertEquals(Double.valueOf(9), ((XYDataItem) listSeries.get(0).getData().get(0)).getX());
		Assert.assertEquals(Double.valueOf(9), ((XYDataItem) listSeries.get(0).getData().get(0)).getY());
	}

}
