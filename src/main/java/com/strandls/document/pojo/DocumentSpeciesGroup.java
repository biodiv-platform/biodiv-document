/**
 * 
 */
package com.strandls.document.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * @author Abhishek Rudra
 *
 */

@Entity
@Table(name = "document_species_group")
@JsonIgnoreProperties(ignoreUnknown = true)
@IdClass(DocumentSpeciesGroupCompositeKey.class)
public class DocumentSpeciesGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3711270807388217836L;
	private Long documentId;
	private Long speciesGroupId;

	/**
	 * 
	 */
	public DocumentSpeciesGroup() {
		super();
	}

	/**
	 * @param documentId
	 * @param speciesGroupId
	 */
	public DocumentSpeciesGroup(Long documentId, Long speciesGroupId) {
		super();
		this.documentId = documentId;
		this.speciesGroupId = speciesGroupId;
	}

	@Id
	@Column(name = "document_species_groups_id")
	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	@Id
	@Column(name = "species_group_id")
	public Long getSpeciesGroupId() {
		return speciesGroupId;
	}

	public void setSpeciesGroupId(Long speciesGroupId) {
		this.speciesGroupId = speciesGroupId;
	}

}
