package org.bigloupe.web.chart.model;

import java.math.BigInteger;
import java.util.List;

/**
 * Base interface representing series. Subclasses are left to implement the
 * actual data structures. The series has two properties ("Key" and
 * "Description")
 * 
 * @author bigloupe
 * 
 */
public interface Series {

	String getKey();

	void setKey(String key);

	BigInteger getId();

	void setId(BigInteger bigInteger);

	String getDescription();

	void setDescription(String string);

	// List of XYDataItem or TimeSeriesDataItem
	List<?> getData();

}
