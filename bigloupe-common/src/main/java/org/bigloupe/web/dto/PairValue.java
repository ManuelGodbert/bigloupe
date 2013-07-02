package org.bigloupe.web.dto;

/**
 * DTO used in JSP to fill select/option tag
 * 
 * @author bigloupe
 *
 */
public class PairValue {
	String name;
	String value;

	public PairValue() {
	}

	public PairValue(String name) {
		this.name = name;
	}

	public PairValue(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "PairValue (name-value) : "  + name + "-" + value; 
	}
}
