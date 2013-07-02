package org.bigloupe.web.chart.model.xy;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
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
public class XYSeries implements Series, Serializable {

	private static final long serialVersionUID = 7312299621672122108L;

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

	/** Storage for the XY data items in the series. */
	@Column(name = "DATA")
	@Type(type = "org.bigloupe.web.chart.model.xy.XYDataItemUserTypeListClob")
	@JsonProperty("data")
	protected List<XYDataItem> data;

	/** The maximum number of items for the series. */
	@JsonIgnore
	private int maximumItemCount = Integer.MAX_VALUE;

	/** A flag that controls whether or not duplicate x-values are allowed. */
	@JsonIgnore
	private transient boolean allowDuplicateXValues;

	/**
	 * A flag that controls whether the items are automatically sorted (by
	 * x-value ascending).
	 */
	@JsonIgnore
	private transient boolean autoSort;

	/** The lowest x-value in the series, excluding Double.NaN values. */
	@JsonIgnore
	private double minX;

	/** The highest x-value in the series, excluding Double.NaN values. */
	@JsonIgnore
	private double maxX;

	/** The lowest y-value in the series, excluding Double.NaN values. */
	@JsonIgnore
	private double minY;

	/** The highest y-value in the series, excluding Double.NaN values. */
	@JsonIgnore
	private double maxY;

	public XYSeries() {
		super();
		data = new ArrayList<XYDataItem>();
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
	 * Adds a data item to the series and sends a {@link SeriesChangeEvent} to
	 * all registered listeners.
	 * 
	 * @param x
	 *            the x value.
	 * @param y
	 *            the y value.
	 */
	public void add(double x, double y) {
		add(new XYDataItem(x, y));
	}

	/**
	 * Adds a data item to the series
	 * 
	 * @param item
	 *            the (x, y) item (<code>null</code> not permitted).
	 */
	public void add(XYDataItem item) {
		if (item == null) {
			throw new IllegalArgumentException("Null 'item' argument.");
		}
		item = (XYDataItem) item.clone();
		// if (this.autoSort) {
		// int index = Collections.binarySearch(this.data, item);
		// if (index < 0) {
		// this.data.add(-index - 1, item);
		// }
		// else {
		// if (this.allowDuplicateXValues) {
		// // need to make sure we are adding *after* any duplicates
		// int size = this.data.size();
		// while (index < size && item.compareTo(
		// this.data.get(index)) == 0) {
		// index++;
		// }
		// if (index < this.data.size()) {
		// this.data.add(index, item);
		// }
		// else {
		// this.data.add(item);
		// }
		// }
		// else {
		// throw new SeriesException("X-value already exists.");
		// }
		// }
		// }
		// else {
		if (!this.allowDuplicateXValues) {
			// can't allow duplicate values, so we need to check whether
			// there is an item with the given x-value already
			int index = indexOf(item.getX());
			if (index >= 0) {
				throw new SeriesException("X-value '" + item.getX()
						+ "' already exists.");
			}
		}
		this.data.add(item);
		// }
		updateBoundsForAddedItem(item);
		if (getItemCount() > this.maximumItemCount) {
			XYDataItem removed = (XYDataItem) this.data.remove(0);
			updateBoundsForRemovedItem(removed);
		}

	}

	/**
	 * Returns the index of the item with the specified x-value, or a negative
	 * index if the series does not contain an item with that x-value. Be aware
	 * that for an unsorted series, the index is found by iterating through all
	 * items in the series.
	 * 
	 * @param x
	 *            the x-value (<code>null</code> not permitted).
	 * 
	 * @return The index.
	 */
	public int indexOf(Number x) {
		// if (this.autoSort) {
		// return Collections.binarySearch(this.data, new XYDataItem(x, null));
		// }
		// else {
		for (int i = 0; i < this.data.size(); i++) {
			XYDataItem item = (XYDataItem) this.data.get(i);
			if (item.getX().equals(x)) {
				return i;
			}
		}
		return -1;
		// }
	}

	/**
	 * Updates the cached values for the minimum and maximum data values on the
	 * basis that the specified item has just been removed.
	 * 
	 * @param item
	 *            the item added (<code>null</code> not permitted).
	 * 
	 * @since 1.0.13
	 */
	private void updateBoundsForRemovedItem(XYDataItem item) {
		boolean itemContributesToXBounds = false;
		boolean itemContributesToYBounds = false;
		double x = item.getXValue();
		if (!Double.isNaN(x)) {
			if (x <= this.minX || x >= this.maxX) {
				itemContributesToXBounds = true;
			}
		}
		if (item.getY() != null) {
			double y = item.getYValue();
			if (!Double.isNaN(y)) {
				if (y <= this.minY || y >= this.maxY) {
					itemContributesToYBounds = true;
				}
			}
		}
		if (itemContributesToYBounds) {
			findBoundsByIteration();
		} else if (itemContributesToXBounds) {
			if (getAutoSort()) {
				this.minX = getX(0).doubleValue();
				this.maxX = getX(getItemCount() - 1).doubleValue();
			} else {
				findBoundsByIteration();
			}
		}
	}

	/**
	 * Finds the bounds of the x and y values for the series, by iterating
	 * through all the data items.
	 * 
	 * @since 1.0.13
	 */
	private void findBoundsByIteration() {
		this.minX = Double.NaN;
		this.maxX = Double.NaN;
		this.minY = Double.NaN;
		this.maxY = Double.NaN;
		Iterator iterator = this.data.iterator();
		while (iterator.hasNext()) {
			XYDataItem item = (XYDataItem) iterator.next();
			updateBoundsForAddedItem(item);
		}
	}

	/**
	 * Updates the cached values for the minimum and maximum data values.
	 * 
	 * @param item
	 *            the item added (<code>null</code> not permitted).
	 * 
	 * @since 1.0.13
	 */
	private void updateBoundsForAddedItem(XYDataItem item) {
		double x = item.getXValue();
		this.minX = minIgnoreNaN(this.minX, x);
		this.maxX = maxIgnoreNaN(this.maxX, x);
		if (item.getY() != null) {
			double y = item.getYValue();
			this.minY = minIgnoreNaN(this.minY, y);
			this.maxY = maxIgnoreNaN(this.maxY, y);
		}
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
		}
		return Math.max(a, b);
	}

	/**
	 * Returns the flag that controls whether the items in the series are
	 * automatically sorted. There is no setter for this flag, it must be
	 * defined in the series constructor.
	 * 
	 * @return A boolean.
	 */
	public boolean getAutoSort() {
		return this.autoSort;
	}

	/**
	 * Returns the number of items in the series.
	 * 
	 * @return The item count.
	 * 
	 * @see #getItems()
	 */
	@JsonIgnore
	public int getItemCount() {
		return this.data.size();
	}

	/**
	 * Return the data item with the specified index.
	 * 
	 * @param index
	 *            the index.
	 * 
	 * @return The data item with the specified index.
	 */
	public XYDataItem getDataItem(int index) {
		XYDataItem item = (XYDataItem) this.data.get(index);
		return (XYDataItem) item.clone();
	}

	/**
	 * Return the data item with the specified index.
	 * 
	 * @param index
	 *            the index.
	 * 
	 * @return The data item with the specified index.
	 * 
	 * @since 1.0.14
	 */
	XYDataItem getRawDataItem(int index) {
		return (XYDataItem) this.data.get(index);
	}

	/**
	 * Returns the x-value at the specified index.
	 * 
	 * @param index
	 *            the index (zero-based).
	 * 
	 * @return The x-value (never <code>null</code>).
	 */
	@JsonIgnore
	public Number getX(int index) {
		return getRawDataItem(index).getX();
	}

	/**
	 * Returns the y-value at the specified index.
	 * 
	 * @param index
	 *            the index (zero-based).
	 * 
	 * @return The y-value (possibly <code>null</code>).
	 */
	@JsonIgnore
	public Number getY(int index) {
		return getRawDataItem(index).getY();
	}

	public List<XYDataItem> getData() {
		return data;
	}

}
