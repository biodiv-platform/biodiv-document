/**
 * 
 */
package com.strandls.document.pojo;

public class DocumentScientificName {

	private Long taxonId;
	private String name;

	/**
	 * 
	 */
	public DocumentScientificName() {
		super();
	}

	/**
	 * @param taxonId
	 * @param name
	 */
	public DocumentScientificName(Long taxonId, String name) {
		super();
		this.taxonId = taxonId;
		this.name = name;
	}

	public Long getTaxonId() {
		return taxonId;
	}

	public void setTaxonId(Long taxonId) {
		this.taxonId = taxonId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}