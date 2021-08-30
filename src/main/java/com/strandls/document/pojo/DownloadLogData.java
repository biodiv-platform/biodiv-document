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
public class DownloadLogData {

	private String filePath;
	private String filterUrl;
	private String status;
	private String fileType;
	private String notes;

	/**
	 * 
	 */
	public DownloadLogData() {
		super();
	}

	/**
	 * @param filePath
	 * @param filterUrl
	 * @param status
	 * @param fileType
	 */
	public DownloadLogData(String filePath, String filterUrl, String status, String fileType, String notes) {
		super();
		this.filePath = filePath;
		this.filterUrl = filterUrl;
		this.status = status;
		this.fileType = fileType;
		this.notes = notes;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilterUrl() {
		return filterUrl;
	}

	public void setFilterUrl(String filterUrl) {
		this.filterUrl = filterUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
