package org.bigloupe.web.chart.model.time;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.bigloupe.web.chart.exception.SeriesException;
import org.bigloupe.web.chart.model.Series;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class TimeSeries implements Series, Serializable {

	private static final long serialVersionUID = -2972548962741626191L;

	/** Default value for the domain description. */
	protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";

	/** Default value for the range description. */
	protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";

	@Id
	@GeneratedValue
	@JsonProperty("id")
	@Column(name = "SERIES_ID")
	private BigInteger id;
	
	/** The key for the series. */
	@JsonProperty("key")
	@Column(name = "KEY")
	private String key;
	
	
	/** A description of the series. */
	@JsonProperty("description")
	@Column(name = "DESCRIPTION")
	private String description;

	/** A description of the series. */
	@JsonProperty("color")
	@Column(name = "COLOR")
	private String color;
	
	/** A description of the range. */
	@Column(name = "RANGE")
	private String range;

	/** Storage for the XY data items in the series. */
	@Column(name = "DATA")
	@Type(type = "org.bigloupe.web.chart.model.time.TimeSeriesDataItemUserTypeListClob")
	@JsonProperty("data")
	protected List<TimeSeriesDataItem> data;

	/** The maximum number of items for the series. */
	@Column(name = "MAXIMUM_ITEM_COUNT")
	private int maximumItemCount;

	/**
	 * The maximum age of items for the series, specified as a number of time
	 * periods.
	 */
	@Column(name = "MAXIMUM_ITEM_AGE")
	private long maximumItemAge;

	/**
	 * The minimum y-value in the series.
	 * 
	 */
	@Column(name = "MIN_Y")
	private double minY;

	/**
	 * The maximum y-value in the series.
	 * 
	 */
	@Column(name = "MIN_X")
	private double maxY;

	/**
	 * Creates a new time series that contains no data.
	 * <P>
	 * Descriptions can be specified for the domain and range. One situation
	 * where this is helpful is when generating a chart for the time series -
	 * axis labels can be taken from the domain and range description.
	 * 
	 * @param name
	 *            the name of the series (<code>null</code> not permitted).
	 * @param range
	 *            the range description (<code>null</code> permitted).
	 * 
	 */
	public TimeSeries() {
		super();
		this.range = DEFAULT_RANGE_DESCRIPTION;
		this.data = new java.util.ArrayList<TimeSeriesDataItem>();
		this.maximumItemCount = Integer.MAX_VALUE;
		this.maximumItemAge = Long.MAX_VALUE;
		this.minY = Double.NaN;
		this.maxY = Double.NaN;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	

	/**
	 * Returns the range description.
	 * 
	 * @return The range description (possibly <code>null</code>).
	 * 
	 * @see #setRangeDescription(String)
	 */
	public String getRangeDescription() {
		return this.range;
	}

	/**
	 * Sets the range description and sends a <code>PropertyChangeEvent</code>
	 * (with the property name <code>Range</code>) to all registered listeners.
	 * 
	 * @param description
	 *            the description (<code>null</code> permitted).
	 * 
	 * @see #getRangeDescription()
	 */
	public void setRangeDescription(String description) {
		this.range = description;
	}

	/**
	 * Returns the number of items in the series.
	 * 
	 * @return The item count.
	 */
	public int getItemCount() {
		return this.data.size();
	}

	/**
	 * Returns the list of data items for the series (the list contains
	 * {@link TimeSeriesDataItem} objects and is unmodifiable).
	 * 
	 * @return The list of data items.
	 */
	@JsonIgnore
	public List<TimeSeriesDataItem> getItems() {
		// FIXME: perhaps we should clone the data list
		return Collections.unmodifiableList(this.data);
	}

	/**
	 * Returns the maximum number of items that will be retained in the series.
	 * The default value is <code>Integer.MAX_VALUE</code>.
	 * 
	 * @return The maximum item count.
	 * 
	 * @see #setMaximumItemCount(int)
	 */
	public int getMaximumItemCount() {
		return this.maximumItemCount;
	}

	/**
	 * Sets the maximum number of items that will be retained in the series. If
	 * you add a new item to the series such that the number of items will
	 * exceed the maximum item count, then the FIRST element in the series is
	 * automatically removed, ensuring that the maximum item count is not
	 * exceeded.
	 * 
	 * @param maximum
	 *            the maximum (requires >= 0).
	 * 
	 * @see #getMaximumItemCount()
	 */
	public void setMaximumItemCount(int maximum) {
		if (maximum < 0) {
			throw new IllegalArgumentException("Negative 'maximum' argument.");
		}
		this.maximumItemCount = maximum;
		int count = this.data.size();
		if (count > maximum) {
			delete(0, count - maximum - 1);
		}
	}

	/**
	 * Returns the maximum item age (in time periods) for the series.
	 * 
	 * @return The maximum item age.
	 * 
	 * @see #setMaximumItemAge(long)
	 */
	public long getMaximumItemAge() {
		return this.maximumItemAge;
	}

	/**
	 * Sets the number of time units in the 'history' for the series. This
	 * provides one mechanism for automatically dropping old data from the time
	 * series. For example, if a series contains daily data, you might set the
	 * history count to 30. Then, when you add a new data item, all data items
	 * more than 30 days older than the latest value are automatically dropped
	 * from the series.
	 * 
	 * @param periods
	 *            the number of time periods.
	 * 
	 * @see #getMaximumItemAge()
	 */
	public void setMaximumItemAge(long periods) {
		if (periods < 0) {
			throw new IllegalArgumentException("Negative 'periods' argument.");
		}
		this.maximumItemAge = periods;
	}

	/**
	 * Returns the smallest y-value in the series, ignoring any null and
	 * Double.NaN values. This method returns Double.NaN if there is no smallest
	 * y-value (for example, when the series is empty).
	 * 
	 * @return The smallest y-value.
	 * 
	 * @see #getMaxY()
	 * 
	 * @since 1.0.14
	 */
	public double getMinY() {
		return this.minY;
	}

	/**
	 * Returns the largest y-value in the series, ignoring any Double.NaN
	 * values. This method returns Double.NaN if there is no largest y-value
	 * (for example, when the series is empty).
	 * 
	 * @return The largest y-value.
	 * 
	 * @see #getMinY()
	 * 
	 * @since 1.0.14
	 */
	public double getMaxY() {
		return this.maxY;
	}




	/**
	 * Returns a data item from the dataset. Note that the returned object is a
	 * clone of the item in the series, so modifying it will have no effect on
	 * the data series.
	 * 
	 * @param index
	 *            the item index.
	 * 
	 * @return The data item.
	 */
	public TimeSeriesDataItem getDataItem(int index) {
		return this.data.get(index);

	}




	/**
	 * Adds a data item to the series and sends a {@link SeriesChangeEvent} to
	 * all registered listeners.
	 * 
	 * @param item
	 *            the (timeperiod, value) pair (<code>null</code> not
	 *            permitted).
	 */
	public void add(TimeSeriesDataItem item) {
		this.data.add(item);
	}
	
	public void add(long millis, double value) {
		this.data.add(new TimeSeriesDataItem(millis, value));
	}

	/**
	 * Deletes data from start until end index (end inclusive).
	 * 
	 * @param start
	 *            the index of the first period to delete.
	 * @param end
	 *            the index of the last period to delete.
	 * @param notify
	 *            notify listeners?
	 * 
	 * @since 1.0.14
	 */
	public void delete(int start, int end) {
		if (end < start) {
			throw new IllegalArgumentException("Requires start <= end.");
		}
		for (int i = 0; i <= (end - start); i++) {
			this.data.remove(start);
		}

	}

	/**
	 * Returns a clone of the time series.
	 * <P>
	 * Notes:
	 * <ul>
	 * <li>no need to clone the domain and range descriptions, since String
	 * object is immutable;</li>
	 * <li>we pass over to the more general method clone(start, end).</li>
	 * </ul>
	 * 
	 * @return A clone of the time series.
	 * 
	 * @throws CloneNotSupportedException
	 *             not thrown by this class, but subclasses may differ.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		TimeSeries clone = (TimeSeries) super.clone();
		clone.data = (List<TimeSeriesDataItem>) ((ArrayList<TimeSeriesDataItem>) (this.data))
				.clone();
		return clone;
	}

	/**
	 * Creates a new timeseries by copying a subset of the data in this time
	 * series.
	 * 
	 * @param start
	 *            the index of the first time period to copy.
	 * @param end
	 *            the index of the last time period to copy.
	 * 
	 * @return A series containing a copy of this times series from start until
	 *         end.
	 * 
	 * @throws CloneNotSupportedException
	 *             if there is a cloning problem.
	 */
	public TimeSeries createCopy(int start, int end)
			throws CloneNotSupportedException {
		if (start < 0) {
			throw new IllegalArgumentException("Requires start >= 0.");
		}
		if (end < start) {
			throw new IllegalArgumentException("Requires start <= end.");
		}
		TimeSeries copy = (TimeSeries) super.clone();
		copy.minY = Double.NaN;
		copy.maxY = Double.NaN;
		copy.data = new java.util.ArrayList<TimeSeriesDataItem>();
		if (this.data.size() > 0) {
			for (int index = start; index <= end; index++) {
				TimeSeriesDataItem item = this.data.get(index);
				try {
					copy.add(item);
				} catch (SeriesException e) {
					throw new RuntimeException(
							"Could not add cloned item to series", e);
				}
			}
		}
		return copy;
	}


	/**
	 * A function to find the minimum of two values, but ignoring any Double.NaN
	 * values.
	 * 
	 * @param a
	 *            the first value.
	 * @param b
	 *            the second value.
	 * 
	 * @return The minimum of the two values.
	 */
	private double minIgnoreNaN(double a, double b) {
		if (Double.isNaN(a)) {
			return b;
		}
		if (Double.isNaN(b)) {
			return a;
		}
		return Math.min(a, b);
	}

	/**
	 * A function to find the maximum of two values, but ignoring any Double.NaN
	 * values.
	 * 
	 * @param a
	 *            the first value.
	 * @param b
	 *            the second value.
	 * 
	 * @return The maximum of the two values.
	 */
	private double maxIgnoreNaN(double a, double b) {
		if (Double.isNaN(a)) {
			return b;
		}
		if (Double.isNaN(b)) {
			return a;
		} else {
			return Math.max(a, b);
		}
	}

	
	@Override
	public List<TimeSeriesDataItem> getData() {
		return data;
	}



}
