/**
 * 
 */
package com.strandls.document.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.activity.pojo.MailData;
import com.strandls.document.pojo.BibFieldsData;
import com.strandls.document.pojo.BibTexItemType;
import com.strandls.document.pojo.BulkUploadExcelData;
import com.strandls.document.pojo.DocSciName;
import com.strandls.document.pojo.DocumentCreateData;
import com.strandls.document.pojo.DocumentEditData;
import com.strandls.document.pojo.DocumentMeta;
import com.strandls.document.pojo.DocumentUserPermission;
import com.strandls.document.pojo.DownloadLogData;
import com.strandls.document.pojo.GNFinderResponseMap;
import com.strandls.document.pojo.ShowDocument;
import com.strandls.taxonomy.pojo.SpeciesGroup;
import com.strandls.user.pojo.Follow;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.FeaturedCreate;
import com.strandls.userGroup.pojo.UserGroupIbp;
import com.strandls.utility.pojo.FlagIbp;
import com.strandls.utility.pojo.FlagShow;
import com.strandls.utility.pojo.Habitat;
import com.strandls.utility.pojo.Language;
import com.strandls.utility.pojo.Tags;
import com.strandls.utility.pojo.TagsMapping;

/**
 * @author Abhishek Rudra
 *
 */
public interface DocumentService {

	public ShowDocument show(Long documentId);

	public ShowDocument createDocument(HttpServletRequest request, DocumentCreateData documentCreateData);

	public DocumentEditData getDocumentEditData(HttpServletRequest request, Long documentId);

	public ShowDocument updateDocument(HttpServletRequest request, DocumentEditData docEditData);

	public Boolean removeDocument(HttpServletRequest request, Long documentId);

	public BibFieldsData readBibTex(InputStream uploadedInputStream, FormDataContentDisposition fileDetail);

	public String bulkUploadBibTex(HttpServletRequest request, InputStream uploadedInputStream,
			FormDataContentDisposition fileDetail);

	public String bulkUploadExcel(HttpServletRequest request, BulkUploadExcelData bulkUploadData);

	public List<BibTexItemType> fetchAllItemType();

	public Map<String, Boolean> getAllFieldTypes(Long itemTypeId);

	public MailData generateMailData(Long documentId);

	public List<Tags> getTagsSuggestion(String phrase);

	public List<Tags> updateTags(HttpServletRequest request, TagsMapping tagsMapping);

	public Activity addDocumentComment(HttpServletRequest request, CommentLoggingData loggingData);

	public List<SpeciesGroup> getAllSpeciesGroup();

	public List<Long> updateSpeciesGroup(HttpServletRequest request, Long documentId, List<Long> speciesGroupList);

	public List<Habitat> getAllHabitat();

	public List<Long> updateHabitat(HttpServletRequest request, Long documentId, List<Long> habitatList);

	public List<UserGroupIbp> updateUserGroup(HttpServletRequest request, String documentId, List<Long> userGroupList);

	public List<Featured> createFeatured(HttpServletRequest request, FeaturedCreate featuredCreate);

	public List<Featured> unFeatured(HttpServletRequest request, String documentId, List<Long> userGroupList);

	public List<FlagShow> createFlag(HttpServletRequest request, Long documentId, FlagIbp flagIbp);

	public List<FlagShow> unFlag(HttpServletRequest request, Long documentId, String flagId);

	public Follow followRequest(HttpServletRequest request, Long documentId);

	public Follow unFollowRequest(HttpServletRequest request, Long documentId);

	public DocumentUserPermission getUserPermission(HttpServletRequest request, String documentId);

	public List<UserGroupIbp> getAllowedUserGroupList(HttpServletRequest request);

	public List<Language> getLanguages(Boolean isDirty);

	public Boolean documentDownloadLog(HttpServletRequest request, DownloadLogData downloadLogData);

	public List<DocumentMeta> getDocumentByTaxonId(Long taxonConceptId);

	public GNFinderResponseMap parsePdfWithGNFinder(String filePath, String fileUrl, Long documentId);

	public List<DocSciName> getNamesByDocumentId(Long documentId,int offset);

}
