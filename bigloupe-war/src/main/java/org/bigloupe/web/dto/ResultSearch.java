package org.bigloupe.web.dto;

/**
 * DTO for search 
 *  
 * @author bigloupe
 *
 */
public class ResultSearch implements Comparable<ResultSearch>  {
	
	String id;
	String label;
	String value;
	
	public ResultSearch() {
	}

	public ResultSearch(String value) {
		this.id = value;
		this.label = value;
		this.value = value;
	}
	
	public ResultSearch(String id, String label, String value) {
		this.id = id;
		this.label = label;
		this.value = value;
	}
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}


	@Override
	public int compareTo(ResultSearch resultSearch) {
		return getLabel().compareTo(resultSearch.getLabel());
	}
	

}
