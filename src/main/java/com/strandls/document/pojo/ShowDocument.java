/**
 * 
 */
package com.strandls.document.pojo;

import java.util.List;

import com.strandls.resource.pojo.License;
import com.strandls.resource.pojo.UFile;
import com.strandls.user.pojo.UserIbp;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.UserGroupIbp;
import com.strandls.utility.pojo.FlagShow;
import com.strandls.utility.pojo.Tags;

/**
 * @author Abhishek Rudra
 *
 */
public class ShowDocument {

	private Document document;
	private UserIbp userIbp;
	private List<DocumentCoverage> documentCoverages;
	private List<UserGroupIbp> userGroupIbp;
	private List<Featured> featured;
	private UFile uFile;
	private List<Long> habitatIds;
	private List<Long> speciesGroupIds;
	private List<Long> taxonIds;
	private List<FlagShow> flag;
	private List<Tags> tags;
	private License documentLicense;

	/**
	 * 
	 */
	public ShowDocument() {
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
	 * @param taxonIds
	 * @param flag
	 * @param tags
	 * @param documentLicense
	 */
	public ShowDocument(Document document, UserIbp userIbp, List<DocumentCoverage> documentCoverages,
			List<UserGroupIbp> userGroupIbp, List<Featured> featured, UFile uFile, List<Long> habitatIds,
			List<Long> speciesGroupIds, List<Long> taxonIds, List<FlagShow> flag, List<Tags> tags,
			License documentLicense) {
		super();
		this.document = document;
		this.userIbp = userIbp;
		this.documentCoverages = documentCoverages;
		this.userGroupIbp = userGroupIbp;
		this.featured = featured;
		this.uFile = uFile;
		this.habitatIds = habitatIds;
		this.speciesGroupIds = speciesGroupIds;
		this.taxonIds = taxonIds;
		this.flag = flag;
		this.tags = tags;
		this.documentLicense = documentLicense;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public UserIbp getUserIbp() {
		return userIbp;
	}

	public void setUserIbp(UserIbp userIbp) {
		this.userIbp = userIbp;
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

	public UFile getuFile() {
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

	public List<Long> getTaxonIds(List<Long> taxonIds) {
		return taxonIds;
	}

	public void setTaxonIds(List<Long> taxonIds) {
		this.taxonIds = taxonIds;
	}

	public List<FlagShow> getFlag() {
		return flag;
	}

	public void setFlag(List<FlagShow> flag) {
		this.flag = flag;
	}

	public List<Tags> getTags() {
		return tags;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

	public License getDocumentLicense() {
		return documentLicense;
	}

	public void setDocumentLicense(License documentLicense) {
		this.documentLicense = documentLicense;
	}

}
