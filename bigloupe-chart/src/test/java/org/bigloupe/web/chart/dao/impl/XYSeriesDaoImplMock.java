package org.bigloupe.web.chart.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bigloupe.web.chart.dao.XYSeriesDao;
import org.bigloupe.web.chart.model.Series;
import org.bigloupe.web.chart.model.xy.XYSeries;

public class XYSeriesDaoImplMock implements XYSeriesDao {

	List<XYSeries> listSeries = new ArrayList<XYSeries>();

	public XYSeriesDaoImplMock() {
		// Fill test series
		listSeries = new ArrayList<XYSeries>();

		XYSeries xySeries = new XYSeries();
		xySeries.setKey("company.server1.applicationInstance1.requestsHandled");
		listSeries.add(xySeries);

		xySeries = new XYSeries();
		xySeries.setKey("company.server1.applicationInstance2.requestsHandled");
		listSeries.add(xySeries);

		xySeries = new XYSeries();
		xySeries.setKey("company.server1.applicationInstance3.requestsHandled");
		listSeries.add(xySeries);

		xySeries = new XYSeries();
		xySeries.setKey("company.server2.applicationInstance1.requestsHandled");
		listSeries.add(xySeries);

		xySeries = new XYSeries();
		xySeries.setKey("company.server2.applicationInstance2.requestsHandled");
		listSeries.add(xySeries);

		xySeries = new XYSeries();
		xySeries.setKey("company.server2.applicationInstance3.requestsHandled");
		listSeries.add(xySeries);
	}

	@Override
	public XYSeries save(XYSeries series) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(XYSeries id) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<XYSeries> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XYSeries findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<XYSeries> findByKey(String key) {
		List<XYSeries> listFoundSeries = new ArrayList<XYSeries>();

		for (XYSeries series : listSeries) {
			if (series.getKey().equals(key))
				listFoundSeries.add(series);
		}

		return listFoundSeries;
	}

	@Override
	public List<XYSeries> findByKeyContaining(String key) {
		List<XYSeries> listFoundSeries = new ArrayList<XYSeries>();
		key = key.replace(".", "\\.");
		key = key.replace("%", "(\\d*)");

		for (XYSeries series : listSeries) {
			if (series.getKey().matches(key))
				listFoundSeries.add(series);
		}

		return listFoundSeries;
	}

}
