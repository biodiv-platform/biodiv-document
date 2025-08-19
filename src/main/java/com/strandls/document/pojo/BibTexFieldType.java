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
@Table(name = "bibtex_field_type")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BibTexFieldType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3625270387164324863L;
	private Long id;
	private String fieldType;

	/**
	 * 
	 */
	public BibTexFieldType() {
		super();
	}

	/**
	 * @param id
	 * @param fieldType
	 */
	public BibTexFieldType(Long id, String fieldType) {
		super();
		this.id = id;
		this.fieldType = fieldType;
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

	@Column(name = "field_type")
	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

}
