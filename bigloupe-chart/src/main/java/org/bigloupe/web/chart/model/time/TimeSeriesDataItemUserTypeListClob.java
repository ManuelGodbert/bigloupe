package org.bigloupe.web.chart.model.time;

import org.bigloupe.web.chart.model.hibernate.AbstractUserTypeListClob;

/**
 * Class to store XYDataItem values in Clob
 * Use pipe to separate values
 * 
 */
public class TimeSeriesDataItemUserTypeListClob extends AbstractUserTypeListClob {

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see AbstractUserTypeList#enumToString(java.lang.Object)
	 */
	@Override
	protected String objectToString(Object e) {
		if (e instanceof TimeSeriesDataItem) {
			TimeSeriesDataItem item = (TimeSeriesDataItem) e;
			return "" + item.getValue();
		} else
			return e.toString();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see AbstractUserTypeList#stringToEnum(java.lang.String)
	 */
	@Override
	protected TimeSeriesDataItem stringToObject(String values) {
		return new TimeSeriesDataItem(0, Double.parseDouble(values));
	}

}