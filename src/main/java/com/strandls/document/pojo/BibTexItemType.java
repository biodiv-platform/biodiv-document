/**
 * 
 */
package com.strandls.document.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "bibtex_item_type")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BibTexItemType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3223265886707177692L;
	@JsonProperty(value = "value")
	private Long id;
	@JsonProperty(value = "label")
	private String itemType;

	/**
	 * 
	 */
	public BibTexItemType() {
		super();
	}

	/**
	 * @param id
	 * @param itemType
	 */
	public BibTexItemType(Long id, String itemType) {
		super();
		this.id = id;
		this.itemType = itemType;
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

	@Column(name = "item_type")
	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

}
