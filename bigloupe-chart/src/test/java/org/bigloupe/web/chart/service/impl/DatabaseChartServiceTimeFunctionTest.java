package org.bigloupe.web.chart.service.impl;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseChartServiceTimeFunctionTest {

	DatabaseChartServiceImpl databaseChartServiceImpl = new DatabaseChartServiceImpl();
	ReadablePeriod period;
	
	// Time of execution must be < 1000 ms
	int executionTime = 1000;

	@Before
	public void setUp() {
	}

	@Test
	public void testCalculatePeriodNow() {

		period = databaseChartServiceImpl.calculatePeriod("now", null);
		// 1000 ms to execute fucntion to have a period between now and now +
		// execution time
		Assert.assertTrue(period.toPeriod().getMillis() < executionTime);
	}

	@Test
	public void testCalculatePeriodOneDayLastWeek() {
		// shows same day last week
		period = databaseChartServiceImpl.calculatePeriod("-8d", "-7d");
		Period executionPeriod = period.toPeriod().minus(Days.days(1));
		Assert.assertEquals(0, executionPeriod.toPeriod().getDays());

	}

	@Test
	public void testCalculatePeriodLast8Minutes() {
		// shows same day last week
		period = databaseChartServiceImpl.calculatePeriod("-8min", "now");
		Period executionPeriod = period.toPeriod().minus(Minutes.minutes(8));
		Assert.assertEquals(0, executionPeriod.toPeriod().getMinutes());
	}
	
	@Test
	public void testCalculatePeriodLast10Hours() {
		// shows same day last week
		period = databaseChartServiceImpl.calculatePeriod("-10h", "now");
		Period executionPeriod = period.toPeriod().minus(Hours.hours(10));
		Assert.assertEquals(0, executionPeriod.toPeriod().getHours());
	}
	
	@Test
	public void testCalculatePeriodLast12Hours1() {
		// shows same day last week
		period = databaseChartServiceImpl.calculatePeriod("-1d+12h", "now");
		Assert.assertEquals(-12, period.toPeriod().getHours());
	}
	
	@Test
	public void testCalculatePeriodLast12Hours2() {
		// shows same day last week
		period = databaseChartServiceImpl.calculatePeriod("-1d+12h+13min", "now");
		Assert.assertEquals(-12, period.toPeriod().getHours());
		Assert.assertEquals(-13, period.toPeriod().getMinutes());
	}
}
