/**
 * 
 */
package com.strandls.document.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Abhishek Rudra
 *
 */

@Entity
@Table(name = "bibtex_item_field_mapping")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BibTexItemFieldMapping implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 385208002422913110L;
	private Long id;
	private Long itemTypeId;
	private Long fieldId;
	private Boolean isRequired;

	/**
	 * 
	 */
	public BibTexItemFieldMapping() {
		super();
	}

	/**
	 * @param id
	 * @param itemTypeId
	 * @param fieldId
	 * @param isRequired
	 */
	public BibTexItemFieldMapping(Long id, Long itemTypeId, Long fieldId, Boolean isRequired) {
		super();
		this.id = id;
		this.itemTypeId = itemTypeId;
		this.fieldId = fieldId;
		this.isRequired = isRequired;
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

	@Column(name = "item_type_id")
	public Long getItemTypeId() {
		return itemTypeId;
	}

	public void setItemTypeId(Long itemTypeId) {
		this.itemTypeId = itemTypeId;
	}

	@Column(name = "field_id")
	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	@Column(name = "is_required")
	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

}
