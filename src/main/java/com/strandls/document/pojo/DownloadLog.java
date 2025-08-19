/**
 * 
 */
package com.strandls.document.pojo;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "download_log")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7489582451169023930L;
	private Long id;
	private Long version;
	private Long authorId;
	private Date createdOn;
	private String filePath;
	private String filterUrl;
	private String notes;
	private String paramAsText;
	private String status;
	private String type;
	private String sourceType;
	private Long offsetParam;

	/**
	 * 
	 */
	public DownloadLog() {
		super();

	}

	/**
	 * @param id
	 * @param version
	 * @param authorId
	 * @param createdOn
	 * @param filePath
	 * @param filterUrl
	 * @param notes
	 * @param paramAsText
	 * @param status
	 * @param type
	 * @param sourceType
	 * @param offsetParam
	 */
	public DownloadLog(Long id, Long version, Long authorId, Date createdOn, String filePath, String filterUrl,
			String notes, String paramAsText, String status, String type, String sourceType, Long offsetParam) {
		super();
		this.id = id;
		this.version = version;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.filePath = filePath;
		this.filterUrl = filterUrl;
		this.notes = notes;
		this.paramAsText = paramAsText;
		this.status = status;
		this.type = type;
		this.sourceType = sourceType;
		this.offsetParam = offsetParam;
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

	@Column(name = "version")
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Column(name = "author_id")
	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	@Column(name = "created_on")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name = "file_path")
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Column(name = "filter_url", columnDefinition = "TEXT")
	public String getFilterUrl() {
		return filterUrl;
	}

	public void setFilterUrl(String filterUrl) {
		this.filterUrl = filterUrl;
	}

	@Column(name = "notes", columnDefinition = "TEXT")
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Column(name = "params_map_as_text", columnDefinition = "TEXT")
	public String getParamAsText() {
		return paramAsText;
	}

	public void setParamAsText(String paramAsText) {
		this.paramAsText = paramAsText;
	}

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "source_type")
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	@Column(name = "offset_param")
	public Long getOffsetParam() {
		return offsetParam;
	}

	public void setOffsetParam(Long offsetParam) {
		this.offsetParam = offsetParam;
	}

}
