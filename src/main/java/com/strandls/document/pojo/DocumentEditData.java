/**
 * 
 */
package com.strandls.document.pojo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.strandls.resource.pojo.UFile;

/**
 * @author Abhishek Rudra
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentEditData {

	private Long documentId;
	private Long itemTypeId;
	private String contribution;
	private String attribution;
	private Long licenseId;
	private Date fromDate;
	private Integer rating;
	private BibFieldsData bibFieldData;
	private List<DocumentCoverage> docCoverage;
	private UFile ufileData;
	private String externalUrl;

	/**
	 * 
	 */
	public DocumentEditData() {
		super();
	}

	/**
	 * @param documentId
	 * @param itemTypeId
	 * @param contribution
	 * @param attribution
	 * @param licenseId
	 * @param fromDate
	 * @param rating
	 * @param bibFieldData
	 * @param docCoverage
	 * @param ufileData
	 * @param externalUrl
	 */
	public DocumentEditData(Long documentId, Long itemTypeId, String contribution, String attribution, Long licenseId,
			Date fromDate, Integer rating, BibFieldsData bibFieldData, List<DocumentCoverage> docCoverage,
			UFile ufileData ,String externalUrl) {
		super();
		this.documentId = documentId;
		this.itemTypeId = itemTypeId;
		this.contribution = contribution;
		this.attribution = attribution;
		this.licenseId = licenseId;
		this.fromDate = fromDate;
		this.rating = rating;
		this.bibFieldData = bibFieldData;
		this.docCoverage = docCoverage;
		this.ufileData = ufileData;
		this.externalUrl = externalUrl;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Long getItemTypeId() {
		return itemTypeId;
	}

	public void setItemTypeId(Long itemTypeId) {
		this.itemTypeId = itemTypeId;
	}

	public String getContribution() {
		return contribution;
	}

	public void setContribution(String contribution) {
		this.contribution = contribution;
	}

	public String getAttribution() {
		return attribution;
	}

	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	public Long getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Long licenseId) {
		this.licenseId = licenseId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public BibFieldsData getBibFieldData() {
		return bibFieldData;
	}

	public void setBibFieldData(BibFieldsData bibFieldData) {
		this.bibFieldData = bibFieldData;
	}

	public List<DocumentCoverage> getDocCoverage() {
		return docCoverage;
	}

	public void setDocCoverage(List<DocumentCoverage> docCoverage) {
		this.docCoverage = docCoverage;
	}

	public UFile getUfileData() {
		return ufileData;
	}

	public void setUfileData(UFile ufileData) {
		this.ufileData = ufileData;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

}
