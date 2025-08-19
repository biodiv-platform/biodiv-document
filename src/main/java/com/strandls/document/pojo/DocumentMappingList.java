package com.strandls.document.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.strandls.resource.pojo.UFile;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.UserGroupIbp;
import com.strandls.userGroup.pojo.UserIbp;
import com.strandls.utility.pojo.Flag;
import com.strandls.utility.pojo.Tags;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentMappingList {

	private Document document;
	private List<DocumentCoverage> documentCoverages;
	private List<UserGroupIbp> userGroupIbp;
	private List<Featured> featured;
	private UFile uFile;
	private List<Long> habitatIds;
	private List<Long> speciesGroupIds;
	private List<Flag> flag;
	private UserIbp userIbp;
	private List<Tags> tags;

	/**
	 * 
	 */
	public DocumentMappingList() {
		super();
	}

	/**
	 * @param document
	 * @param userIbp
	 * @param documentCoverages
	 * @param userGroupIbp
	 * @param featured
	 * @param uFile
	 * @param habitatIds
	 * @param speciesGroupIds
	 * @param flag
	 * @param tags
	 */
	public DocumentMappingList(Document document, List<DocumentCoverage> documentCoverages,
			List<UserGroupIbp> userGroupIbp, List<Featured> featured, UserIbp userIbp, UFile uFile,
			List<Long> habitatIds, List<Long> speciesGroupIds, List<Flag> flag, List<Tags> tags) {
		super();
		this.document = document;

		this.documentCoverages = documentCoverages;
		this.userGroupIbp = userGroupIbp;
		this.featured = featured;
		this.uFile = uFile;
		this.habitatIds = habitatIds;
		this.speciesGroupIds = speciesGroupIds;
		this.userIbp = userIbp;
		this.flag = flag;
		this.tags = tags;
	}

	public Object getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public List<DocumentCoverage> getDocumentCoverages() {
		return documentCoverages;
	}

	public void setDocumentCoverages(List<DocumentCoverage> documentCoverages) {
		this.documentCoverages = documentCoverages;
	}

	public List<UserGroupIbp> getUserGroupIbp() {
		return userGroupIbp;
	}

	public void setUserGroupIbp(List<UserGroupIbp> userGroupIbp) {
		this.userGroupIbp = userGroupIbp;
	}

	public List<Featured> getFeatured() {
		return featured;
	}

	public void setFeatured(List<Featured> featured) {
		this.featured = featured;
	}

	public Object getuFile() {
		return uFile;
	}

	public void setuFile(UFile uFile) {
		this.uFile = uFile;
	}

	public List<Long> getHabitatIds() {
		return habitatIds;
	}

	public void setHabitatIds(List<Long> habitatIds) {
		this.habitatIds = habitatIds;
	}

	public List<Long> getSpeciesGroupIds() {
		return speciesGroupIds;
	}

	public void setSpeciesGroupIds(List<Long> speciesGroupIds) {
		this.speciesGroupIds = speciesGroupIds;
	}

	public List<Flag> getFlag() {
		return flag;
	}

	public void setFlag(List<Flag> flag) {
		this.flag = flag;
	}

	public List<Tags> getTags() {
		return tags;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

	public Object getUserIbp() {
		return userIbp;
	}

	public void setUserIbp(UserIbp userIbp) {
		this.userIbp = userIbp;
	}

}
