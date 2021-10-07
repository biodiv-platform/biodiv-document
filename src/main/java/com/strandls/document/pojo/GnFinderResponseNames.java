package com.strandls.document.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GnFinderResponseNames {
	private Integer cardinality;
	private String name;
	private Long start;
	private Long end;
	private String speciesId;
	private Long taxonId;
	private String offSet;
	private Integer frequency;
	private Integer displayOrder;

	public GnFinderResponseNames() {
		super();
	}

	public GnFinderResponseNames(Integer cardinality, String name, Long start, Long end) {
		super();
		this.cardinality = cardinality;
		this.name = name;
		this.start = start;
		this.end = end;
	}

	public String getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(String speciesId) {
		this.speciesId = speciesId;
	}

	public Integer getCardinality() {
		return cardinality;
	}

	public String getName() {
		return name;
	}

	public Long getStart() {
		return start;
	}

	public Long getEnd() {
		return end;
	}

	public Long getTaxonId() {
		return taxonId;
	}

	public void setTaxonId(Long taxonId) {
		this.taxonId = taxonId;
	}

	public String getOffSet() {
		return offSet;
	}

	public void setOffSet(String offSet) {
		this.offSet = offSet;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

}
