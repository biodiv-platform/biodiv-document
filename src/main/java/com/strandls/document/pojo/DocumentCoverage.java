/**
 * 
 */
package com.strandls.document.pojo;

import java.io.Serializable;

import com.bedatadriven.jackson.datatype.jts.serialization.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * @author Abhishek Rudra
 *
 */

@Entity
@Table(name = "document_coverage")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentCoverage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9149317039031323675L;
	private Long id;
	private Long documentId;
	private Long geoEntityId;
	private String placeName;
	private Geometry topology;
	private Long landscapeIds;
	private String topologyWKT;

	/**
	 * 
	 */
	public DocumentCoverage() {
		super();
	}

	/**
	 * @param id
	 * @param documentId
	 * @param geoEntityId
	 * @param placeName
	 * @param topology
	 */
	public DocumentCoverage(Long id, Long documentId, Long geoEntityId, String placeName, Geometry topology) {
		super();
		this.id = id;
		this.documentId = documentId;
		this.geoEntityId = geoEntityId;
		this.placeName = placeName;
		this.topology = topology;
	}

	/**
	 * @param id
	 * @param documentId
	 * @param geoEntityId
	 * @param placeName
	 * @param topology
	 * @param landscapeIds
	 */
	public DocumentCoverage(Long id, Long documentId, Long geoEntityId, String placeName, Geometry topology,
			Long landscapeIds) {
		super();
		this.id = id;
		this.documentId = documentId;
		this.geoEntityId = geoEntityId;
		this.placeName = placeName;
		this.topology = topology;
		this.landscapeIds = landscapeIds;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "document_id")
	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	@Column(name = "geoentity_id")
	public Long getGeoEntityId() {
		return geoEntityId;
	}

	public void setGeoEntityId(Long geoEntityId) {
		this.geoEntityId = geoEntityId;
	}

	@Column(name = "placename")
	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	@Column(name = "topology", columnDefinition = "Geometry", nullable = false)
	@JsonSerialize(using = GeometrySerializer.class)
	public Geometry getTopology() {
		return topology;
	}

	public void setTopology(Geometry topology) {
		this.topology = topology;
	}

	@Transient
	public Long getLandscapeIds() {
		return landscapeIds;
	}

	public void setLandscapeIds(Long landscapeIds) {
		this.landscapeIds = landscapeIds;
	}

	@Transient
	public String getTopologyWKT() {
		return topologyWKT;
	}

	public void setTopologyWKT(String topologyWKT) {
		this.topologyWKT = topologyWKT;
	}

}
