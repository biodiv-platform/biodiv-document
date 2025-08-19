/**
 * 
 */
package com.strandls.document.pojo;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "document")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1429553368493728184L;
	private Long id;
	private Boolean agreeTerms;
	private String attribution;
	private Long authorId;
	private String contributors;
	private Date createdOn;
	private String notes;
	private String doi;
	private Date lastRevised;
	private Long licenseId;
	private String title;
	private String type;
	@JsonProperty(value = "uFileId")
	private Long uFileId;
	private Date fromDate;
	private Date toDate;
	private Integer featureCount;
	private Integer flagCount;
	private Long languageId;
	private String externalUrl;
	private Integer visitCount;
	private Integer rating;
	private Boolean isDeleted;
	private Long dataTableId;
	private String author;
	private String journal;
	private String bookTitle;
	private String year;
	private String month;
	private String volume;
	private String number;
	private String pages;
	private String publisher;
	private String school;
	private String edition;
	private String series;
	private String address;
	private String chapter;
	private String note;
	private String editor;
	private String organization;
	private String howPublished;
	private String institution;
	private String url;
	private String language;
	private String file;
	private String itemtype;
	private String isbn;
	private String extra;
	private String documentSocialPreview;

	/**
	 * 
	 */
	public Document() {
		super();
	}

	/**
	 * @param id
	 * @param agreeTerms
	 * @param attribution
	 * @param authorId
	 * @param contributors
	 * @param createdOn
	 * @param notes
	 * @param doi
	 * @param lastRevised
	 * @param licenseId
	 * @param title
	 * @param type
	 * @param uFileId
	 * @param fromDate
	 * @param toDate
	 * @param featureCount
	 * @param flagCount
	 * @param languageId
	 * @param externalUrl
	 * @param visitCount
	 * @param rating
	 * @param isDeleted
	 * @param dataTableId
	 * @param author
	 * @param journal
	 * @param bookTitle
	 * @param year
	 * @param month
	 * @param volume
	 * @param number
	 * @param pages
	 * @param publisher
	 * @param school
	 * @param edition
	 * @param series
	 * @param address
	 * @param chapter
	 * @param note
	 * @param editor
	 * @param organization
	 * @param howPublished
	 * @param institution
	 * @param url
	 * @param language
	 * @param file
	 * @param itemtype
	 * @param isbn
	 * @param extra
	 * @param documentSocialPreview
	 */
	public Document(Long id, Boolean agreeTerms, String attribution, Long authorId, String contributors, Date createdOn,
			String notes, String doi, Date lastRevised, Long licenseId, String title, String type, Long uFileId,
			Date fromDate, Date toDate, Integer featureCount, Integer flagCount, Long languageId, String externalUrl,
			Integer visitCount, Integer rating, Boolean isDeleted, Long dataTableId, String author, String journal,
			String bookTitle, String year, String month, String volume, String number, String pages, String publisher,
			String school, String edition, String series, String address, String chapter, String note, String editor,
			String organization, String howPublished, String institution, String url, String language, String file,
			String itemtype, String isbn, String extra, String documentSocialPreview) {
		super();
		this.id = id;
		this.agreeTerms = agreeTerms;
		this.attribution = attribution;
		this.authorId = authorId;
		this.contributors = contributors;
		this.createdOn = createdOn;
		this.notes = notes;
		this.doi = doi;
		this.lastRevised = lastRevised;
		this.licenseId = licenseId;
		this.title = title;
		this.type = type;
		this.uFileId = uFileId;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.featureCount = featureCount;
		this.flagCount = flagCount;
		this.languageId = languageId;
		this.externalUrl = externalUrl;
		this.visitCount = visitCount;
		this.rating = rating;
		this.isDeleted = isDeleted;
		this.dataTableId = dataTableId;
		this.author = author;
		this.journal = journal;
		this.bookTitle = bookTitle;
		this.year = year;
		this.month = month;
		this.volume = volume;
		this.number = number;
		this.pages = pages;
		this.publisher = publisher;
		this.school = school;
		this.edition = edition;
		this.series = series;
		this.address = address;
		this.chapter = chapter;
		this.note = note;
		this.editor = editor;
		this.organization = organization;
		this.howPublished = howPublished;
		this.institution = institution;
		this.url = url;
		this.language = language;
		this.file = file;
		this.itemtype = itemtype;
		this.isbn = isbn;
		this.extra = extra;
		this.documentSocialPreview = documentSocialPreview;
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

	@Column(name = "agree_terms")
	public Boolean getAgreeTerms() {
		return agreeTerms;
	}

	public void setAgreeTerms(Boolean agreeTerms) {
		this.agreeTerms = agreeTerms;
	}

	@Column(name = "attribution", columnDefinition = "TEXT")
	public String getAttribution() {
		return attribution;
	}

	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	@Column(name = "author_id")
	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	@Column(name = "contributors", columnDefinition = "TEXT")
	public String getContributors() {
		return contributors;
	}

	public void setContributors(String contributors) {
		this.contributors = contributors;
	}

	@Column(name = "created_on")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name = "notes", columnDefinition = "TEXT")
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Column(name = "doi")
	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	@Column(name = "last_revised")
	public Date getLastRevised() {
		return lastRevised;
	}

	public void setLastRevised(Date lastRevised) {
		this.lastRevised = lastRevised;
	}

	@Column(name = "license_id")
	public Long getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Long licenseId) {
		this.licenseId = licenseId;
	}

	@Column(name = "title", columnDefinition = "TEXT")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "type", columnDefinition = "TEXT")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "u_file_id")
	public Long getuFileId() {
		return uFileId;
	}

	public void setuFileId(Long uFileId) {
		this.uFileId = uFileId;
	}

	@Column(name = "from_date")
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	@Column(name = "to_date")
	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Column(name = "feature_count")
	public Integer getFeatureCount() {
		return featureCount;
	}

	public void setFeatureCount(Integer featureCount) {
		this.featureCount = featureCount;
	}

	@Column(name = "flag_count")
	public Integer getFlagCount() {
		return flagCount;
	}

	public void setFlagCount(Integer flagCount) {
		this.flagCount = flagCount;
	}

	@Column(name = "language_id")
	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	@Column(name = "external_url")
	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	@Column(name = "visit_count")
	public Integer getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(Integer visitCount) {
		this.visitCount = visitCount;
	}

	@Column(name = "rating")
	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	@Column(name = "is_deleted")
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Column(name = "data_table_id")
	public Long getDataTableId() {
		return dataTableId;
	}

	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}

	@Column(name = "author", columnDefinition = "TEXT")
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Column(name = "journal", columnDefinition = "TEXT")
	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	@Column(name = "book_title", columnDefinition = "TEXT")
	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	@Column(name = "year")
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Column(name = "month")
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	@Column(name = "volume")
	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	@Column(name = "number")
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Column(name = "pages")
	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	@Column(name = "publisher", columnDefinition = "TEXT")
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	@Column(name = "school")
	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	@Column(name = "edition", columnDefinition = "TEXT")
	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	@Column(name = "series", columnDefinition = "TEXT")
	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	@Column(name = "address", columnDefinition = "TEXT")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "chapter", columnDefinition = "TEXT")
	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	@Column(name = "note", columnDefinition = "TEXT")
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = "editor", columnDefinition = "TEXT")
	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	@Column(name = "organization", columnDefinition = "TEXT")
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Column(name = "how_published")
	public String getHowPublished() {
		return howPublished;
	}

	public void setHowPublished(String howPublished) {
		this.howPublished = howPublished;
	}

	@Column(name = "institution")
	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	@Column(name = "extra", columnDefinition = "TEXT")
	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	@Column(name = "url", columnDefinition = "TEXT")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "language")
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Column(name = "file", columnDefinition = "TEXT")
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Column(name = "item_type")
	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	@Column(name = "isbn")
	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	@Column(name = "document_social_preview", columnDefinition = "text")
	public String getDocumentSocialPreview() {
		return documentSocialPreview;
	}

	public void setDocumentSocialPreview(String documentSocialPreview) {
		this.documentSocialPreview = documentSocialPreview;
	}

}
