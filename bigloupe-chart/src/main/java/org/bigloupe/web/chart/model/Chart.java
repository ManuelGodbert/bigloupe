package org.bigloupe.web.chart.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.bigloupe.web.chart.model.time.TimeSeries;
import org.bigloupe.web.chart.model.xy.XYSeries;
import org.bigloupe.web.model.BaseObject;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "CHART")
public class Chart extends BaseObject implements Serializable {

	private static final long serialVersionUID = 8349038274504551914L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonProperty("id")
	@Column(name = "CHART_ID")
	private BigInteger id;
	
	/** No lazy loading **/
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonProperty("xyseries")
	private List<XYSeries> listXYSeries;
	
	/** No lazy loading **/
	@ManyToMany(fetch = FetchType.LAZY)
	@JsonProperty("timeseries")	
	private List<TimeSeries> listTimeSeries;
	
	/** A description of the series. */
	@JsonProperty("description")
	@Column(name = "DESCRIPTION")
	private String description;	

	/** The key for the series. */
	@JsonProperty("key")
	@Column(name = "KEY", unique=true, nullable=false)
	private String key;
	
	/** The key for the series. */
	@JsonProperty("renderer")
	@Column(name = "RENDERER")
	private String renderer;
	
	public Chart() {
		listXYSeries = new ArrayList<XYSeries>();
		listTimeSeries = new ArrayList<TimeSeries>();
	}
	
	
    /**
     * Adds a series to the collection
     *
     * @param series  the series (<code>null</code> not permitted).
     */
    public void addSeries(XYSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.listXYSeries.add(series);
    }
    public void addSeries(TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.listTimeSeries.add(series);
    }    
	
	@Override
	public String toString() {
		return "Chart [id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Chart other = (Chart) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer.toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	public List<XYSeries> getListXYSeries() {
		return listXYSeries;
	}

	public List<TimeSeries> getListTimeSeries() {
		return listTimeSeries;
	}	
	
}
