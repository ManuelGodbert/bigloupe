package org.bigloupe.web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO object for Treemap view
 * 
 * @author bigloupe
 * 
 */
public class Treemap {

	String name;
	
	String size;

	List<Treemap> children;

	public Treemap() {
	}
	
	public Treemap(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public List<Treemap> getChildren() {
		return children;
	}

	public void setChildren(List<Treemap> children) {
		this.children = children;
	}

	public void addChildren(Treemap treemap) {
		if (this.children == null)
			this.children = new ArrayList<Treemap>();
		this.children.add(treemap);
		
	}	
}
