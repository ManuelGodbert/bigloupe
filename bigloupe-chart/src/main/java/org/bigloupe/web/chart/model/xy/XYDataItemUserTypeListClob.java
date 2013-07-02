package org.bigloupe.web.chart.model.xy;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.bigloupe.web.chart.model.hibernate.AbstractUserTypeListClob;

/**
 * Class to store XYDataItem values in Clob
 * Use pipe to separate values
 * 
 */
public class XYDataItemUserTypeListClob extends AbstractUserTypeListClob {

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see AbstractUserTypeList#enumToString(java.lang.Object)
	 */
	@Override
	protected String objectToString(Object e) {
		if (e instanceof XYDataItem) {
			XYDataItem item = (XYDataItem) e;
			return item.getX() + "," + item.getY();
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
	protected XYDataItem stringToObject(String values) {
		StringTokenizer tokenizer = new StringTokenizer(values, ",");
		while (tokenizer.hasMoreTokens() && tokenizer.countTokens() == 2) {
			return new XYDataItem(Float.parseFloat(tokenizer.nextToken()),
					Float.parseFloat(tokenizer.nextToken()));
		}
		return null;
	}

}