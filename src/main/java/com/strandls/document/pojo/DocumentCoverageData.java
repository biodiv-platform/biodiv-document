/**
 * 
 */
package com.strandls.document.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Abhishek Rudra
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentCoverageData {
	private Long id;
	private Long documentId;
	private Long landscapeIds;
	private String placename;
	private String topology;
	private Long geoEntityId;

	/**
	 * 
	 */
	public DocumentCoverageData() {
		super();
	}

	/**
	 * @param placename
	 * @param topology
	 * @param geoEntityId
	 */
	public DocumentCoverageData(Long id, Long documentId, Long landscapeIds, String placename, String topology,
			Long geoEntityId) {
		super();
		this.id = id;
		this.documentId = documentId;
		this.landscapeIds = landscapeIds;
		this.placename = placename;
		this.topology = topology;
		this.geoEntityId = geoEntityId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Long getLandscapeIds() {
		return landscapeIds;
	}

	public void setLandscapeIds(Long landscapeIds) {
		this.landscapeIds = landscapeIds;
	}

	public String getPlacename() {
		return placename;
	}

	public void setPlacename(String placename) {
		this.placename = placename;
	}

	public String getTopology() {
		return topology;
	}

	public void setTopology(String topology) {
		this.topology = topology;
	}

	public Long getGeoEntityId() {
		return geoEntityId;
	}

	public void setGeoEntityId(Long geoEntityId) {
		this.geoEntityId = geoEntityId;
	}

}
