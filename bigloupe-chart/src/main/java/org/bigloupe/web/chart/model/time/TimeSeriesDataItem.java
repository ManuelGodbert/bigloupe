package org.bigloupe.web.chart.model.time;

import java.io.Serializable;
import java.util.logging.SimpleFormatter;

import org.bigloupe.web.chart.model.DataItem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents one data item in a time series.
 * 
 */
@JsonDeserialize(using = TimeSeriesDataItemDeserialize.class)
@JsonSerialize(using = TimeSeriesDataItemSerialize.class)
public class TimeSeriesDataItem implements DataItem, Cloneable,Serializable {

	/** For serialization. */
	private static final long serialVersionUID = -2235346966016401302L;

	private long date;
	
	/** The value associated with the time period. */
	private double value;

	public TimeSeriesDataItem() {
	}

	/**
	 * Constructs a new data item that associates a value with a time period.
	 * 
	 * @param date
	 *            milliseconds since January 1, 1970, 00:00:00 GMT not to exceed
	 *            the milliseconds representation for the year 8099. A negative
	 *            number indicates the number of milliseconds before January 1,
	 *            1970, 00:00:00 GMT.
	 * @param value
	 *            the value (<code>null</code> permitted).
	 */
	public TimeSeriesDataItem(long date, double value) {
		this.date = date;
		this.value = value;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public Number getX() {
		return date;
	}

	@Override
	public Number getY() {
		return value;
	}

	@Override
	public String toString() {
		
		return "TimeSeriesDataItem [date=" + date + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (date ^ (date >>> 32));
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeSeriesDataItem other = (TimeSeriesDataItem) obj;
		if (date != other.date)
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}

}
