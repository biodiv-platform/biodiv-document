/**
 * 
 */
package com.strandls.document.service.Impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.Value;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.activity.controller.ActivitySerivceApi;
import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.activity.pojo.DocumentMailData;
import com.strandls.activity.pojo.MailData;
import com.strandls.activity.pojo.UserGroupMailData;
import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.document.Headers;
import com.strandls.document.dao.BibTexFieldTypeDao;
import com.strandls.document.dao.BibTexItemFieldMappingDao;
import com.strandls.document.dao.BibTexItemTypeDao;
import com.strandls.document.dao.DocSciNameDao;
import com.strandls.document.dao.DocumentCoverageDao;
import com.strandls.document.dao.DocumentDao;
import com.strandls.document.dao.DocumentHabitatDao;
import com.strandls.document.dao.DocumentSpeciesGroupDao;
import com.strandls.document.dao.DownloadLogDao;
import com.strandls.document.es.util.DocumentIndex;
import com.strandls.document.es.util.ESUpdate;
import com.strandls.document.es.util.ESUpdateThread;
import com.strandls.document.es.util.RabbitMQProducer;
import com.strandls.document.pojo.BibFieldsData;
import com.strandls.document.pojo.BibTexFieldType;
import com.strandls.document.pojo.BibTexItemFieldMapping;
import com.strandls.document.pojo.BibTexItemType;
import com.strandls.document.pojo.BulkUploadExcelData;
import com.strandls.document.pojo.DocSciName;
import com.strandls.document.pojo.Document;
import com.strandls.document.pojo.DocumentCoverage;
import com.strandls.document.pojo.DocumentCoverageData;
import com.strandls.document.pojo.DocumentCreateData;
import com.strandls.document.pojo.DocumentEditData;
import com.strandls.document.pojo.DocumentHabitat;
import com.strandls.document.pojo.DocumentMeta;
import com.strandls.document.pojo.DocumentSpeciesGroup;
import com.strandls.document.pojo.DocumentUserPermission;
import com.strandls.document.pojo.DownloadLog;
import com.strandls.document.pojo.DownloadLogData;
import com.strandls.document.pojo.ShowDocument;
import com.strandls.document.service.DocumentService;
import com.strandls.esmodule.ApiException;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.MapQueryResponse;
import com.strandls.esmodule.pojo.MapQueryResponse.ResultEnum;
import com.strandls.file.api.UploadApi;
import com.strandls.file.model.FilesDTO;
import com.strandls.geoentities.controllers.GeoentitiesServicesApi;
import com.strandls.geoentities.pojo.GeoentitiesWKTData;
import com.strandls.landscape.controller.LandscapeApi;
import com.strandls.landscape.pojo.Landscape;
import com.strandls.resource.controllers.ResourceServicesApi;
import com.strandls.resource.pojo.License;
import com.strandls.resource.pojo.UFile;
import com.strandls.resource.pojo.UFileCreateData;
import com.strandls.taxonomy.controllers.SpeciesServicesApi;
import com.strandls.taxonomy.pojo.SpeciesGroup;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.user.pojo.Follow;
import com.strandls.user.pojo.UserIbp;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.FeaturedCreate;
import com.strandls.userGroup.pojo.FeaturedCreateData;
import com.strandls.userGroup.pojo.UserGroupDocCreateData;
import com.strandls.userGroup.pojo.UserGroupIbp;
import com.strandls.userGroup.pojo.UserGroupMappingCreateData;
import com.strandls.userGroup.pojo.UserGroupMemberRole;
import com.strandls.userGroup.pojo.UserGroupPermissions;
import com.strandls.utility.controller.UtilityServiceApi;
import com.strandls.utility.pojo.FlagCreateData;
import com.strandls.utility.pojo.FlagIbp;
import com.strandls.utility.pojo.FlagShow;
import com.strandls.utility.pojo.Habitat;
import com.strandls.utility.pojo.Language;
import com.strandls.utility.pojo.Tags;
import com.strandls.utility.pojo.TagsMapping;
import com.strandls.utility.pojo.TagsMappingData;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import de.undercouch.citeproc.bibtex.BibTeXConverter;
import net.minidev.json.JSONArray;

/**
 * @author Abhishek Rudra
 *
 */
public class DocumentServiceImpl implements DocumentService {

	private final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Inject
	private DocumentDao documentDao;

	@Inject
	private DocumentHabitatDao docHabitatDao;

	@Inject
	private DocumentSpeciesGroupDao docSGroupDao;

	@Inject
	private UserServiceApi userService;

	@Inject
	private UserGroupSerivceApi ugService;

	@Inject
	private UtilityServiceApi utilityService;

	@Inject
	private ResourceServicesApi resourceService;

	@Inject
	private DocumentCoverageDao docCoverageDao;

	@Inject
	private Headers headers;

	@Inject
	private GeometryFactory geometryFactory;

	@Inject
	private BibTexFieldTypeDao bibTexFieldTypeDao;

	@Inject
	private BibTexItemFieldMappingDao bibTexItemFieldMappingDao;

	@Inject
	private BibTexItemTypeDao bibTexItemTypeDao;

	@Inject
	private MailMetaDataConverter converter;

	@Inject
	private ActivitySerivceApi activityService;

	@Inject
	private SpeciesServicesApi speciesService;

	@Inject
	private GeoentitiesServicesApi geoEntitiesServices;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private UploadApi fileUpload;

	@Inject
	private DocumentHelper docHelper;

	@Inject
	private DownloadLogDao downloadLogDao;

	@Inject
	private RabbitMQProducer producer;

	@Inject
	private LogActivities logActivity;

	@Inject
	private LandscapeApi landScapeService;

	@Inject
	private DocSciNameDao docSciNameDao;

	@Inject
	private EsServicesApi esService;

	@Inject
	private ESUpdate esUpdate;

	@Override
	public ShowDocument show(Long documentId) {
		try {
			Document document = documentDao.findById(documentId);
			if (!document.getIsDeleted()) {

				UserIbp userIbp = userService.getUserIbp(document.getAuthorId().toString());

				List<DocumentCoverage> documentCoverages = docCoverageDao.findByDocumentId(documentId);

				List<Landscape> allLandscape = landScapeService.getAllLandScapes(205L, -1, -1);
				for (DocumentCoverage docCoverage : documentCoverages) {
					if (docCoverage.getGeoEntityId() != null) {
						for (Landscape landscape : allLandscape) {
							if (landscape.getGeoEntityId().equals(docCoverage.getGeoEntityId())) {
								docCoverage.setLandscapeIds(landscape.getId());
								break;
							}
						}

					}
				}

				List<UserGroupIbp> userGroup = ugService.getUserGroupByDocId(documentId.toString());
				List<Featured> featured = ugService.getAllFeatured("content.eml.Document", documentId.toString());

				UFile resource = null;
				if (document.getuFileId() != null)
					resource = resourceService.getUFilePath(document.getuFileId().toString());

				License documentLicense = resourceService.getLicenseResource(document.getLicenseId().toString());

				List<FlagShow> flag = utilityService.getFlagByObjectType("content.eml.Document", documentId.toString());
				List<Tags> tags = utilityService.getTags("document", documentId.toString());

				List<DocumentHabitat> docHabitats = docHabitatDao.findByDocumentId(documentId);
				List<DocumentSpeciesGroup> docSGroups = docSGroupDao.findByDocumentId(documentId);
				List<Long> docHabitatIds = new ArrayList<Long>();
				List<Long> docSGroupIds = new ArrayList<Long>();

				for (DocumentHabitat docHabitat : docHabitats) {
					docHabitatIds.add(docHabitat.getHabitatId());
				}
				for (DocumentSpeciesGroup docSGroup : docSGroups) {
					docSGroupIds.add(docSGroup.getSpeciesGroupId());
				}

				ShowDocument showDoc = new ShowDocument(document, userIbp, documentCoverages, userGroup, featured,
						resource, docHabitatIds, docSGroupIds, flag, tags, documentLicense);
				return showDoc;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ShowDocument createDocument(HttpServletRequest request, DocumentCreateData documentCreateData) {

		try {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			Long authorId = Long.parseLong(profile.getId());

			UFile ufile = null;
			if (documentCreateData.getResourceURL() != null && documentCreateData.getSize() != null) {

//				file movement

				FilesDTO filesDto = new FilesDTO();
				filesDto.setFiles(Arrays.asList(documentCreateData.getResourceURL()));
				filesDto.setFolder("DOCUMENTS");
				filesDto.setModule("DOCUMENT");

				fileUpload = headers.addFileUploadHeader(fileUpload, request.getHeader(HttpHeaders.AUTHORIZATION));
				Map<String, Object> fileResponse = fileUpload.moveFiles(filesDto);

				if (fileResponse != null && !fileResponse.isEmpty()) {
					Map<String, String> files = (Map<String, String>) fileResponse
							.get(documentCreateData.getResourceURL());
					String relativePath = files.get("name").toString();
					String mimeType = files.get("mimeType").toString();
					String size = files.get("size").toString();
					UFileCreateData ufileCreateData = new UFileCreateData();
					ufileCreateData.setMimeType(mimeType);
					ufileCreateData.setPath(relativePath);
					ufileCreateData.setSize(size);
					ufileCreateData.setWeight(0);
					resourceService = headers.addResourceHeaders(resourceService,
							request.getHeader(HttpHeaders.AUTHORIZATION));
					ufile = resourceService.createUFile(ufileCreateData);
				}

			}

			BibFieldsData bibData = documentCreateData.getBibFieldData();
			Document document = new Document(null, 0L, true, documentCreateData.getAttribution(), authorId,
					documentCreateData.getContribution(), null, new Date(), bibData.getDescription(), bibData.getDoi(),
					new Date(), documentCreateData.getLicenseId(), null, null, bibData.getTitle(), bibData.getType(),
					(ufile != null ? ufile.getId() : null), documentCreateData.getFromDate(), null, null, null, null,
					null, null, null, null, documentCreateData.getFromDate(), null, 0, 0, 205L, null, null, null, null,
					null, null, null, null, null, 1, documentCreateData.getRating(), false, null, null,
					bibData.getAuthor(), bibData.getJournal(), bibData.getBooktitle(), bibData.getYear(),
					bibData.getMonth(), bibData.getVolume(), bibData.getNumber(), bibData.getPages(),
					bibData.getPublisher(), bibData.getSchool(), bibData.getEdition(), bibData.getSeries(),
					bibData.getAddress(), bibData.getChapter(), bibData.getNote(), bibData.getEditor(),
					bibData.getOrganization(), bibData.getHowpublished(), bibData.getInstitution(), bibData.getUrl(),
					bibData.getLanguage(), bibData.getFile(), bibData.getItemtype(), bibData.getIsbn(),
					bibData.getExtra());

			document = documentDao.save(document);

			logActivity.LogDocumentActivities(request.getHeader(HttpHeaders.AUTHORIZATION), null, document.getId(),
					document.getId(), "Document", null, "Document created", null);

//			speciesGroup

			for (Long speciesGroupId : documentCreateData.getSpeciesGroupIds()) {
				DocumentSpeciesGroup docSGroup = new DocumentSpeciesGroup(document.getId(), speciesGroupId);
				docSGroupDao.save(docSGroup);
			}

//			habitat 
			for (Long habitatId : documentCreateData.getHabitatIds()) {
				DocumentHabitat docHabitat = new DocumentHabitat(document.getId(), habitatId);
				docHabitatDao.save(docHabitat);
			}
//			user group
			if (documentCreateData.getUserGroupId() != null && !documentCreateData.getUserGroupId().isEmpty()) {
				UserGroupDocCreateData groupDocCreateData = new UserGroupDocCreateData();
				groupDocCreateData.setDocumentId(document.getId());
				groupDocCreateData.setUserGroupIds(documentCreateData.getUserGroupId());
				ugService = headers.addUserGroupHeader(ugService, request.getHeader(HttpHeaders.AUTHORIZATION));
				ugService.createUGDocMapping(groupDocCreateData);
			}

//			tags
			if (documentCreateData.getTags() != null && !documentCreateData.getTags().isEmpty()) {
				TagsMapping tagsMapping = new TagsMapping();
				tagsMapping.setObjectId(document.getId());
				tagsMapping.setTags(documentCreateData.getTags());
				TagsMappingData tagsMappingData = new TagsMappingData();
//				TODO fill in the mail data for document
				tagsMappingData.setMailData(null);
				tagsMappingData.setTagsMapping(tagsMapping);
				utilityService = headers.addUtilityHeaders(utilityService,
						request.getHeader(HttpHeaders.AUTHORIZATION));
				utilityService.createTags("document", tagsMappingData);
			}

//			new wkt  coverage data
			if (documentCreateData.getDocCoverageData() != null && !documentCreateData.getDocCoverageData().isEmpty()) {
				for (DocumentCoverageData docCoverageData : documentCreateData.getDocCoverageData()) {
					WKTReader reader = new WKTReader(geometryFactory);
					Geometry topology = reader.read(docCoverageData.getTopology());
					DocumentCoverage docCoverage = new DocumentCoverage(null, document.getId(),
							docCoverageData.getGeoEntityId(), docCoverageData.getPlacename(), topology);
					docCoverageDao.save(docCoverage);
				}
			}
			ShowDocument res = show(document.getId());
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			objectMapper.setDateFormat(df);
			String docString = objectMapper.writeValueAsString(res);

			ESUpdateThread updateThread = new ESUpdateThread(esUpdate, docString, document.getId().toString());
			Thread thread = new Thread(updateThread);
			thread.start();
			return res;
		} catch (

		Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	@Override
	public DocumentEditData getDocumentEditData(HttpServletRequest request, Long documentId) {
		try {

			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			Long userId = Long.parseLong(profile.getId());
			JSONArray roles = (JSONArray) profile.getAttribute("roles");

			Document document = documentDao.findById(documentId);
			BibFieldsData bibFieldData = docHelper.convertDocumentToBibField(document);

			if (roles.contains("ROLE_ADMIN") || userId.equals(document.getAuthorId())) {
				List<DocumentCoverage> docCoverages = docCoverageDao.findByDocumentId(documentId);
				for (DocumentCoverage docCoverage : docCoverages) {
					WKTWriter writer = new WKTWriter();
					String wktData = writer.write(docCoverage.getTopology());
					docCoverage.setTopologyWKT(wktData);
				}
				UFile ufile = null;
				if (document.getuFileId() != null)
					ufile = resourceService.getUFilePath(document.getuFileId().toString());

				DocumentEditData docEditData = new DocumentEditData(documentId, bibFieldData.getItemTypeId(),
						document.getContributors(), document.getAttribution(), document.getLicenseId(),
						document.getFromDate(), document.getRating(), bibFieldData, docCoverages, ufile);
				return docEditData;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ShowDocument updateDocument(HttpServletRequest request, DocumentEditData docEditData) {
		try {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			Long userId = Long.parseLong(profile.getId());
			JSONArray roles = (JSONArray) profile.getAttribute("roles");
			Document document = documentDao.findById(docEditData.getDocumentId());

			if (roles.contains("ROLE_ADMIN") || userId.equals(document.getAuthorId())) {

//				ufile update 
				UFile ufile = docEditData.getUfileData();
				if (ufile == null) {
					Long ufileId = document.getuFileId();
					if (ufileId != null) {
						resourceService = headers.addResourceHeaders(resourceService,
								request.getHeader(HttpHeaders.AUTHORIZATION));
						Boolean result = resourceService.removeUFile(ufileId.toString());
						if (result == null || (result == false))
							return null;
					}
				} else if (ufile.getId() == null) {
//					remove old uFile
					Long ufileId = document.getuFileId();
					if (ufileId != null) {
						resourceService = headers.addResourceHeaders(resourceService,
								request.getHeader(HttpHeaders.AUTHORIZATION));
						Boolean result = resourceService.removeUFile(ufileId.toString());
						if (result == null || (result == false))
							return null;
					}

//					add new uFile
					FilesDTO filesDto = new FilesDTO();
					filesDto.setFiles(Arrays.asList(ufile.getPath()));
					filesDto.setFolder("DOCUMENTS");
					filesDto.setModule("DOCUMENT");

					fileUpload = headers.addFileUploadHeader(fileUpload, request.getHeader(HttpHeaders.AUTHORIZATION));
					Map<String, Object> fileResponse = fileUpload.moveFiles(filesDto);

					if (fileResponse != null && !fileResponse.isEmpty()) {
						Map<String, String> files = (Map<String, String>) fileResponse.get(ufile.getPath());
						String relativePath = files.get("name").toString();
						String mimeType = files.get("mimeType").toString();
						String size = files.get("size").toString();
						UFileCreateData ufileCreateData = new UFileCreateData();
						ufileCreateData.setMimeType(mimeType);
						ufileCreateData.setPath(relativePath);
						ufileCreateData.setSize(size);
						ufileCreateData.setWeight(0);
						resourceService = headers.addResourceHeaders(resourceService,
								request.getHeader(HttpHeaders.AUTHORIZATION));
						ufile = resourceService.createUFile(ufileCreateData);
					}
				}

//				GeoEntitiy update
				List<DocumentCoverage> docCoverages = docEditData.getDocCoverage();
				List<DocumentCoverage> previousCoverage = docCoverageDao.findByDocumentId(docEditData.getDocumentId());
				if (docCoverages == null || docCoverages.isEmpty()) {
					for (DocumentCoverage docC : previousCoverage) {
						docCoverageDao.delete(docC);
					}
				} else {
//					update the docCoverages which has been added and removed
					List<Long> newCoveage = new ArrayList<Long>();
//					add new docCoverage
					for (DocumentCoverage coverage : docCoverages) {
						if (coverage.getId() == null) {
							WKTReader reader = new WKTReader(geometryFactory);
							Geometry topology = reader.read(coverage.getTopologyWKT());
							coverage.setDocumentId(docEditData.getDocumentId());
							coverage.setTopology(topology);
							coverage = docCoverageDao.save(coverage);
						}
						newCoveage.add(coverage.getId());
					}
//					remove the document 
					for (DocumentCoverage previous : previousCoverage) {
						if (!newCoveage.contains(previous.getId())) {
							docCoverageDao.delete(previous);
						}
					}

				}
//				document core update

				BibFieldsData bibData = docEditData.getBibFieldData();

				document.setAttribution(docEditData.getAttribution());
				document.setContributors(docEditData.getContribution());
				document.setNotes(bibData.getDescription());
				document.setDoi(bibData.getDoi());
				document.setLastRevised(new Date());
				document.setLicenseId(docEditData.getLicenseId());
				document.setTitle(bibData.getTitle());
				document.setType(bibData.getType());
				document.setuFileId(ufile != null ? ufile.getId() : null);
				document.setFromDate(docEditData.getFromDate());
				document.setToDate(docEditData.getFromDate());
				document.setRating(docEditData.getRating());
				document.setAuthor(bibData.getAuthor());
				document.setJournal(bibData.getJournal());
				document.setBookTitle(bibData.getBooktitle());
				document.setYear(bibData.getYear());
				document.setMonth(bibData.getMonth());
				document.setVolume(bibData.getVolume());
				document.setNumber(bibData.getNumber());
				document.setPages(bibData.getPages());
				document.setPublisher(bibData.getPublisher());
				document.setSchool(bibData.getSchool());
				document.setEdition(bibData.getEdition());
				document.setSeries(bibData.getSeries());
				document.setAddress(bibData.getAddress());
				document.setChapter(bibData.getChapter());
				document.setNote(bibData.getNote());
				document.setEditor(bibData.getEditor());
				document.setOrganization(bibData.getOrganization());
				document.setHowPublished(bibData.getHowpublished());
				document.setInstitution(bibData.getInstitution());
				document.setUrl(bibData.getUrl());
				document.setLanguage(bibData.getLanguage());
				document.setFile(bibData.getFile());
				document.setItemtype(bibTexItemTypeDao.findById(docEditData.getItemTypeId()).getItemType());
				document.setIsbn(bibData.getIsbn());
				document.setExtra(bibData.getExtra());

				documentDao.update(document);

				logActivity.LogDocumentActivities(request.getHeader(HttpHeaders.AUTHORIZATION), null, document.getId(),
						document.getId(), "Document", null, "Document updated", null);

				updateDocumentLastRevised(document.getId());

			}

			return show(docEditData.getDocumentId());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public MailData generateMailData(Long documentId) {
		try {

			MailData mailData = new MailData();
			DocumentMailData documentMailData = new DocumentMailData();
			Document document = documentDao.findById(documentId);
			documentMailData.setAuthorId(document.getAuthorId());
			documentMailData.setCreatedOn(document.getCreatedOn());
			documentMailData.setDocumentId(documentId);

			List<UserGroupIbp> userGroupIbp = ugService.getUserGroupByDocId(documentId.toString());
			List<UserGroupMailData> userGroupData = new ArrayList<UserGroupMailData>();
			for (UserGroupIbp ugIbp : userGroupIbp) {
				UserGroupMailData ugMailData = new UserGroupMailData();
				ugMailData.setId(ugIbp.getId());
				ugMailData.setIcon(ugIbp.getIcon());
				ugMailData.setName(ugIbp.getName());
				ugMailData.setWebAddress(ugIbp.getWebAddress());
				userGroupData.add(ugMailData);
			}

			mailData.setDocumentMailData(documentMailData);
			mailData.setUserGroupData(userGroupData);
			return mailData;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	@Override
	public Boolean removeDocument(HttpServletRequest request, Long documentId) {
		try {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			Long userId = Long.parseLong(profile.getId());
			JSONArray userRoles = (JSONArray) profile.getAttribute("roles");
			Document document = documentDao.findById(documentId);

			MapQueryResponse esResponse;

			esResponse = esService.delete(DocumentIndex.INDEX.getValue(), DocumentIndex.TYPE.getValue(),
					documentId.toString());

			ResultEnum result = esResponse.getResult();

			if (result.getValue().equals("DELETED")
					|| (document.getAuthorId().equals(userId) || userRoles.contains("ROLE_ADMIN"))) {
				document.setIsDeleted(true);
				documentDao.update(document);
				System.out.print("===deleted docuemnt===" + documentId + "returned" + result.getValue());
				return true;
			}

		} catch (ApiException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	@Override
	public BibFieldsData readBibTex(InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		try {
			Map<String, String> bibMapping = new HashMap<String, String>();

			BibTeXDatabase bibTextDB = new BibTeXConverter().loadDatabase(uploadedInputStream);
			Map<Key, BibTeXEntry> bibTexEntires = bibTextDB.getEntries();
			for (Entry<Key, BibTeXEntry> entry : bibTexEntires.entrySet()) {
				bibMapping.put("item type", entry.getValue().getType().toString());

				for (Entry<Key, Value> bibEntry : entry.getValue().getFields().entrySet()) {
					bibMapping.put(bibEntry.getKey().toString(), bibEntry.getValue().toUserString());
				}
			}
			BibFieldsData result = objectMapper.convertValue(bibMapping, BibFieldsData.class);
			Map<String, Object> bibFieldMaps = objectMapper.convertValue(result,
					new TypeReference<Map<String, Object>>() {
					});

			for (Entry<String, Object> entry : bibFieldMaps.entrySet()) {
				bibMapping.remove(entry.getKey());
			}

			result.setItemTypeId(bibTexItemTypeDao.findByName(result.getItemtype()).getId());
			String extras = objectMapper.writeValueAsString(bibMapping);
			result.setExtra(extras);

			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public String bulkUploadBibTex(HttpServletRequest request, InputStream uploadedInputStream,
			FormDataContentDisposition fileDetail) {

		try {
			BibTeXDatabase bibTexDB = new BibTeXConverter().loadDatabase(uploadedInputStream);
			Map<Key, BibTeXEntry> bibEntries = bibTexDB.getEntries();

//			iterate over each ref one after another
			for (Entry<Key, BibTeXEntry> bibEntry : bibEntries.entrySet()) {

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public String bulkUploadExcel(HttpServletRequest request, BulkUploadExcelData bulkUploadData) {
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(bulkUploadData.getFileName()));

			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			Long authorId = Long.parseLong(profile.getId());

//			read the excel sheet
			XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
			XSSFSheet dataSheet = workBook.getSheetAt(0);
			XSSFSheet notCitedData = workBook.getSheetAt(1);
			Map<String, Integer> fieldMapping = bulkUploadData.getFieldMapping();

			Iterator<Row> dataSheetIterator = dataSheet.iterator();
			int count = 1;

			dataSheetIterator.next();

//			EXTRACT SITE NUMBER TO GEOENTITY MAPPING
			List<Landscape> allLandscape = landScapeService.getAllLandScapes(205L, -1, -1);
			Map<Long, Long> siteGeoentitiyMapping = new HashMap<Long, Long>();
			for (Landscape landScape : allLandscape) {
				siteGeoentitiyMapping.put(landScape.getSiteNumber(), landScape.getGeoEntityId());
			}

//			EXTRACT CITED NAME TO GEOENTITY MAPPING
			Map<String, Long> citedNameGeoEntityMapping = new HashMap<String, Long>();
			for (Landscape landscape : allLandscape) {
				citedNameGeoEntityMapping.put(landscape.getShortName().toLowerCase(), landscape.getGeoEntityId());
			}

//			get all speciesGroup
			List<SpeciesGroup> speciesGroupList = speciesService.getAllSpeciesGroup();
			Map<String, Long> sGroupIdMap = new HashMap<String, Long>();
			for (SpeciesGroup sGroup : speciesGroupList) {
				sGroupIdMap.put(sGroup.getName(), sGroup.getId());
			}

//			get all habitat
			List<Habitat> habitatList = utilityService.getAllHabitat();
			Map<String, Long> habitatIdMap = new HashMap<String, Long>();
			for (Habitat habitat : habitatList) {
				habitatIdMap.put(habitat.getName(), habitat.getId());
			}

//			get all the files tree
			FilesDTO filesDto = new FilesDTO();
			filesDto.setFolder("DOCUMENTS");
			filesDto.setModule("DOCUMENT");

			fileUpload = headers.addFileUploadHeader(fileUpload, request.getHeader(HttpHeaders.AUTHORIZATION));
			Map<String, Object> allFiles = fileUpload.getAllFilePathsByUser(filesDto);

			while (dataSheetIterator.hasNext()) {
				Row dataRow = dataSheetIterator.next();

				System.out.println("Counter---------" + count);
				count++;

//				ufile

//				TODO changes required to handle bulk upload
				UFile ufile = null;
				Cell fileCell = dataRow.getCell(fieldMapping.get("file"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (fileCell != null) {

					String fileName = dataRow.getCell(fieldMapping.get("file")).getStringCellValue();

					if (allFiles.containsKey(fileName)) {
						System.out.println("file name : " + fileName);
						FilesDTO filesMoveDto = new FilesDTO();
						String myUploadPath = allFiles.get(fileName).toString();
						System.out.println("Myupload Path : " + myUploadPath);

						filesMoveDto.setFiles(Arrays.asList(myUploadPath));
						filesMoveDto.setFolder("DOCUMENTS");
						filesMoveDto.setModule("DOCUMENT");

						fileUpload = headers.addFileUploadHeader(fileUpload,
								request.getHeader(HttpHeaders.AUTHORIZATION));
						Map<String, Object> fileResponse = fileUpload.moveFiles(filesMoveDto);

						System.out.println("file response : " + fileResponse);

						if (fileResponse != null && !fileResponse.isEmpty()) {
							Map<String, String> files = (Map<String, String>) fileResponse.get(myUploadPath);
							String relativePath = files.get("name").toString();
							String mimeType = files.get("mimeType").toString();
							String size = files.get("size").toString();
							UFileCreateData ufileCreateData = new UFileCreateData();
							ufileCreateData.setMimeType(mimeType);
							ufileCreateData.setPath(relativePath);
							ufileCreateData.setSize(size);
							ufileCreateData.setWeight(0);
							resourceService = headers.addResourceHeaders(resourceService,
									request.getHeader(HttpHeaders.AUTHORIZATION));
							ufile = resourceService.createUFile(ufileCreateData);

						}
					}

				}

//				document creation
				Document document = docHelper.bulkUploadPayload(dataRow, fieldMapping, authorId, ufile);

				if (document == null)
					continue;

				document = documentDao.save(document);

//				Activity
				logActivity.LogDocumentActivities(request.getHeader(HttpHeaders.AUTHORIZATION), null, document.getId(),
						document.getId(), "Document", null, "Document created", null);

//				CITED NAME
				if (fieldMapping.get("citedName") != null) {
					String citedNames = null;
					Cell cell = dataRow.getCell(fieldMapping.get("citedName"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
					if (cell != null) {
						cell.setCellType(CellType.STRING);
						citedNames = cell.getStringCellValue();
					}
					if (citedNames != null) {
						String citedNameArray[] = citedNames.split(",");
						for (String citedName : citedNameArray) {
							citedName = citedName.toLowerCase();
							if (citedNameGeoEntityMapping.containsKey(citedName)) {
								GeoentitiesWKTData geoEntity = geoEntitiesServices
										.findGeoentitiesById(citedNameGeoEntityMapping.get(citedName).toString());
								if (geoEntity != null) {
									saveDocCoverage(document.getId(), citedNameGeoEntityMapping.get(citedName),
											geoEntity);
								}
							}
						}
					}
				}

//				PROTECTED CITED AREAS
				if (fieldMapping.get("siteNumber") != null) {

					String siteNumber = null;
					Cell cell = dataRow.getCell(fieldMapping.get("siteNumber"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
					if (cell != null) {
						cell.setCellType(CellType.STRING);
						siteNumber = cell.getStringCellValue();
					}
					if (siteNumber != null) {
						String siteNumberArray[] = siteNumber.split(",");

						for (String siteNumberString : siteNumberArray) {

							for (String site : siteNumberString.split(" ")) {

								site = site.toLowerCase();
								site = site.replace("site", "");
								if (!site.trim().isEmpty()) {
									try {
										Long siteLong = Long.parseLong(site.trim());

										if (siteGeoentitiyMapping.containsKey(siteLong)) {
											GeoentitiesWKTData geoEntity = geoEntitiesServices.findGeoentitiesById(
													siteGeoentitiyMapping.get(siteLong).toString());
											if (geoEntity != null) {
												saveDocCoverage(document.getId(), siteGeoentitiyMapping.get(siteLong),
														geoEntity);
											}
										}
									} catch (Exception e) {
										logger.error(e.getMessage());
									}

								}

							}

						}
					}
				}

//				GEO ENTITY
				if (fieldMapping.get("geoentities") != null) {
					String geoEntities = null;
					Cell cell = dataRow.getCell(fieldMapping.get("geoentities"),
							MissingCellPolicy.RETURN_BLANK_AS_NULL);
					if (cell != null) {
						cell.setCellType(CellType.STRING);
						geoEntities = cell.getStringCellValue();
					}
					if (geoEntities != null) {
						String geoEntitesIds[] = geoEntities.split(",");
						for (String geoEntitiesId : geoEntitesIds) {
							GeoentitiesWKTData geoEntity = geoEntitiesServices
									.findGeoentitiesById(geoEntitiesId.trim());
							if (geoEntity != null) {
								saveDocCoverage(document.getId(), Long.parseLong(geoEntitiesId.trim()), geoEntity);
							}

						}

					}

				}

//				NOT CITED AREA

				try {
					if (fieldMapping.get("notCitedArea") != null && fieldMapping.get("notCitedName") != null
							&& fieldMapping.get("wktData") != null) {

						String notCited = null;
						Cell cell = dataRow.getCell(fieldMapping.get("notCitedArea"),
								MissingCellPolicy.RETURN_BLANK_AS_NULL);
						if (cell != null) {
							cell.setCellType(CellType.STRING);
							notCited = cell.getStringCellValue();
						}
						if (notCited != null) {

							List<String> originalStrings = Arrays.asList(notCited.split(","));

//							trims out all the white space of all the String in the list
							List<String> notCitedNames = originalStrings.stream().map(String::trim)
									.collect(Collectors.toList());

							Iterator<Row> notCitedIterator = notCitedData.iterator();
							while (notCitedIterator.hasNext()) {
								Row notCitedDataRow = notCitedIterator.next();

								String citeName = null;
								Cell notCitedCell = notCitedDataRow.getCell(fieldMapping.get("notCitedName"),
										MissingCellPolicy.RETURN_BLANK_AS_NULL);
								if (notCitedCell != null) {
									notCitedCell.setCellType(CellType.STRING);
									citeName = notCitedCell.getStringCellValue();
								}
								if (notCitedNames.contains(citeName)) {
									String wktData = null;
									Cell wktCell = notCitedDataRow.getCell(fieldMapping.get("wktData"),
											MissingCellPolicy.RETURN_BLANK_AS_NULL);
									if (wktCell != null) {
										wktCell.setCellType(CellType.STRING);
										wktData = wktCell.getStringCellValue();
									}
									WKTReader reader = new WKTReader(geometryFactory);
									Geometry topology = reader.read(wktData);
									DocumentCoverage docCoverage = new DocumentCoverage(null, document.getId(), null,
											citeName, topology);
									docCoverageDao.save(docCoverage);

								}
							}

						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}

//				tags

				if (fieldMapping.get("tags") != null) {
					String docTags = null;
					Cell cell = dataRow.getCell(fieldMapping.get("tags"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
					if (cell != null) {
						cell.setCellType(CellType.STRING);
						docTags = cell.getStringCellValue();

					}
					if (docTags != null) {
						String docTag[] = docTags.split(",");

						List<Tags> tags = new ArrayList<Tags>();

						TagsMapping tagsMapping = new TagsMapping();
						tagsMapping.setObjectId(document.getId());
						for (String tag : docTag) {
							Tags t = new Tags();
							t.setName(tag.trim());
							tags.add(t);
						}
						tagsMapping.setTags(tags);
						TagsMappingData tagsMappingData = new TagsMappingData();
						tagsMappingData.setMailData(null);
						tagsMappingData.setTagsMapping(tagsMapping);
						utilityService = headers.addUtilityHeaders(utilityService,
								request.getHeader(HttpHeaders.AUTHORIZATION));
						utilityService.createTags("document", tagsMappingData);
					}

				}

//				specie group
				if (fieldMapping.get("speciesGroup") != null) {
					String sgroupList = null;
					Cell cell = dataRow.getCell(fieldMapping.get("speciesGroup"),
							MissingCellPolicy.RETURN_BLANK_AS_NULL);
					if (cell != null) {
						cell.setCellType(CellType.STRING);
						sgroupList = cell.getStringCellValue();
					}
					if (sgroupList != null) {
						for (String sgroupName : sgroupList.split(",")) {
							if (sGroupIdMap.containsKey(sgroupName.trim())) {
								DocumentSpeciesGroup docSGroup = new DocumentSpeciesGroup(document.getId(),
										sGroupIdMap.get(sgroupName.trim()));
								docSGroupDao.save(docSGroup);

							}
						}
					}
				}

//				habitat
				if (fieldMapping.get("habitat") != null) {
					String habitatNameList = null;
					Cell cell = dataRow.getCell(fieldMapping.get("habitat"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
					if (cell != null) {
						cell.setCellType(CellType.STRING);
						habitatNameList = cell.getStringCellValue();
					}
					if (habitatNameList != null) {
						for (String habitatName : habitatNameList.split(",")) {
							if (habitatIdMap.containsKey(habitatName.trim())) {
								DocumentHabitat docHabitat = new DocumentHabitat(document.getId(),
										habitatIdMap.get(habitatName.trim()));
								docHabitatDao.save(docHabitat);
							}
						}
					}

				}
			}

//			closing excel sheet
			workBook.close();
		} catch (

		Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	private void saveDocCoverage(Long documentId, Long geoEntityId, GeoentitiesWKTData geoEntity) {
		try {
			WKTReader reader = new WKTReader(geometryFactory);
			Geometry topology = reader.read(geoEntity.getWktData());
			DocumentCoverage docCoverage = new DocumentCoverage(null, documentId, geoEntityId, geoEntity.getPlaceName(),
					topology);
			docCoverageDao.save(docCoverage);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}

	}

	@Override
	public Map<String, Boolean> getAllFieldTypes(Long itemTypeId) {
		List<BibTexItemFieldMapping> itemFieldMappings = bibTexItemFieldMappingDao.findByItemTypeId(itemTypeId);
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		for (BibTexItemFieldMapping itemFieldMapping : itemFieldMappings) {
			BibTexFieldType fieldType = bibTexFieldTypeDao.findById(itemFieldMapping.getFieldId());
			result.put(fieldType.getFieldType(), itemFieldMapping.getIsRequired());
		}

		return result;
	}

	@Override
	public List<BibTexItemType> fetchAllItemType() {
		List<BibTexItemType> result = bibTexItemTypeDao.findAll();
		return result;
	}

	@Override
	public List<Tags> getTagsSuggestion(String phrase) {
		try {
			List<Tags> result = utilityService.getTagsAutoComplete(phrase);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<Tags> updateTags(HttpServletRequest request, TagsMapping tagsMapping) {
		List<Tags> result = null;
		try {
			TagsMappingData tagsMappingData = new TagsMappingData();
			tagsMappingData.setTagsMapping(tagsMapping);
			tagsMappingData.setMailData(converter.utilityMetaData(generateMailData(tagsMapping.getObjectId())));
			utilityService = headers.addUtilityHeaders(utilityService, request.getHeader(HttpHeaders.AUTHORIZATION));
			result = utilityService.updateTags("document", tagsMappingData);
			updateDocumentLastRevised(tagsMapping.getObjectId());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	@Override
	public Activity addDocumentComment(HttpServletRequest request, CommentLoggingData loggingData) {
		try {
			loggingData.setMailData(generateMailData(loggingData.getRootHolderId()));
			activityService = headers.addActivityHeaders(activityService, request.getHeader(HttpHeaders.AUTHORIZATION));
			Activity result = activityService.addComment("document", loggingData);
			updateDocumentLastRevised(loggingData.getRootHolderId());
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private void updateDocumentLastRevised(Long documentId) {
		Document document = documentDao.findById(documentId);
		document.setLastRevised(new Date());
		documentDao.update(document);

//		update the document in es instance
		produceToRabbitMQ(documentId);

	}

	private void produceToRabbitMQ(Long documentId) {
		try {
			ShowDocument res = show(documentId);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			objectMapper.setDateFormat(df);
			String documentData = objectMapper.writeValueAsString(res);

			producer.setMessage("document", documentData, documentId.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	@Override
	public List<SpeciesGroup> getAllSpeciesGroup() {
		List<SpeciesGroup> result = null;
		try {
			result = speciesService.getAllSpeciesGroup();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	@Override
	public List<Habitat> getAllHabitat() {
		try {
			List<Habitat> result = utilityService.getAllHabitat();
			return result;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public List<UserGroupIbp> updateUserGroup(HttpServletRequest request, String documentId, List<Long> userGroupList) {
		List<UserGroupIbp> result = null;
		try {

			UserGroupDocCreateData userGroupData = new UserGroupDocCreateData();
			userGroupData.setUserGroupIds(userGroupList);
			userGroupData.setMailData(converter.userGroupMetadata(generateMailData(Long.parseLong(documentId))));
			userGroupData.setDocumentId(Long.parseLong(documentId));
			ugService = headers.addUserGroupHeader(ugService, request.getHeader(HttpHeaders.AUTHORIZATION));
			result = ugService.updateUGDocMapping(userGroupData);
			updateDocumentLastRevised(Long.parseLong(documentId));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return result;
	}

	@Override
	public List<Featured> createFeatured(HttpServletRequest request, FeaturedCreate featuredCreate) {
		List<Featured> result = null;

		try {
			FeaturedCreateData featuredCreateData = new FeaturedCreateData();
			featuredCreateData.setFeaturedCreate(featuredCreate);
			featuredCreateData.setMailData(converter.userGroupMetadata(generateMailData(featuredCreate.getObjectId())));
			ugService = headers.addUserGroupHeader(ugService, request.getHeader(HttpHeaders.AUTHORIZATION));
			result = ugService.createFeatured(featuredCreateData);
			updateDocumentLastRevised(featuredCreate.getObjectId());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	@Override
	public List<Featured> unFeatured(HttpServletRequest request, String documentId, List<Long> userGroupList) {
		List<Featured> result = null;
		try {
			UserGroupMappingCreateData userGroupData = new UserGroupMappingCreateData();
			userGroupData.setUserGroups(userGroupList);
			userGroupData.setUgFilterData(null);
			userGroupData.setMailData(converter.userGroupMetadata(generateMailData(Long.parseLong(documentId))));
			ugService = headers.addUserGroupHeader(ugService, request.getHeader(HttpHeaders.AUTHORIZATION));
			result = ugService.unFeatured("observation", documentId, userGroupData);
			updateDocumentLastRevised(Long.parseLong(documentId));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	@Override
	public List<FlagShow> createFlag(HttpServletRequest request, Long documentId, FlagIbp flagIbp) {
		try {
			FlagCreateData flagData = new FlagCreateData();
			flagData.setFlagIbp(flagIbp);
			flagData.setMailData(converter.utilityMetaData(generateMailData(documentId)));
			utilityService = headers.addUtilityHeaders(utilityService, request.getHeader(HttpHeaders.AUTHORIZATION));
			List<FlagShow> flagList = utilityService.createFlag("document", documentId.toString(), flagData);
			int flagCount = 0;
			if (flagList != null)
				flagCount = flagList.size();

			Document document = documentDao.findById(documentId);
			document.setFlagCount(flagCount);
			documentDao.update(document);

			updateDocumentLastRevised(documentId);

			return flagList;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	@Override
	public List<FlagShow> unFlag(HttpServletRequest request, Long documentId, String flagId) {

		try {

			com.strandls.utility.pojo.MailData mailData = converter.utilityMetaData(generateMailData(documentId));
			utilityService = headers.addUtilityHeaders(utilityService, request.getHeader(HttpHeaders.AUTHORIZATION));
			List<FlagShow> result = utilityService.unFlag("document", documentId.toString(), flagId, mailData);
			int flagCount = 0;
			if (result != null)
				flagCount = result.size();

			Document document = documentDao.findById(documentId);
			document.setFlagCount(flagCount);
			documentDao.update(document);

			updateDocumentLastRevised(documentId);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public Follow followRequest(HttpServletRequest request, Long documentId) {
		try {
			userService = headers.addUserHeaders(userService, request.getHeader(HttpHeaders.AUTHORIZATION));
			Follow result = userService.updateFollow("document", documentId.toString());
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Follow unFollowRequest(HttpServletRequest request, Long documentId) {
		try {
			userService = headers.addUserHeaders(userService, request.getHeader(HttpHeaders.AUTHORIZATION));
			Follow result = userService.unfollow("document", documentId.toString());
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public DocumentUserPermission getUserPermission(HttpServletRequest request, String documentId) {

		DocumentUserPermission permission = new DocumentUserPermission();
		try {

			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			List<UserGroupIbp> allowedUserGroup = new ArrayList<UserGroupIbp>();
			List<Long> userGroupFeatureRole = new ArrayList<Long>();

			userService = headers.addUserHeaders(userService, request.getHeader(HttpHeaders.AUTHORIZATION));
			Follow follow = userService.getFollowByObject("document", documentId);

			List<UserGroupIbp> associatedUserGroup = ugService.getUserGroupByDocId(documentId);
			ugService = headers.addUserGroupHeader(ugService, request.getHeader(HttpHeaders.AUTHORIZATION));
			UserGroupPermissions userGroupPermission = ugService.getUserGroupPermission();

			JSONArray userRole = (JSONArray) profile.getAttribute("roles");
			if (userRole.contains("ROLE_ADMIN")) {

				allowedUserGroup = ugService.getAllUserGroup();
				for (UserGroupIbp ug : allowedUserGroup) {
					userGroupFeatureRole.add(ug.getId());
				}

			} else {

				List<Long> userGroupMember = new ArrayList<Long>();
				for (UserGroupMemberRole userMemberRole : userGroupPermission.getUserMemberRole()) {
					userGroupMember.add(userMemberRole.getUserGroupId());
				}
				String s = userGroupMember.toString();
				if (s.substring(1, s.length() - 1).trim().length() != 0)
					allowedUserGroup = ugService.getUserGroupList(s.substring(1, s.length() - 1));

				for (UserGroupMemberRole userFeatureRole : userGroupPermission.getUserFeatureRole()) {
					userGroupFeatureRole.add(userFeatureRole.getUserGroupId());
				}
			}

			List<Long> userGroupIdList = new ArrayList<Long>();
			List<UserGroupIbp> featureableGroup = new ArrayList<UserGroupIbp>();
			for (UserGroupIbp userGroup : associatedUserGroup) {
				userGroupIdList.add(userGroup.getId());
				if (userGroupFeatureRole.contains(userGroup.getId()))
					featureableGroup.add(userGroup);

			}

			permission = new DocumentUserPermission(allowedUserGroup, featureableGroup,
					(follow != null) ? true : false);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return permission;
	}

	@Override
	public List<UserGroupIbp> getAllowedUserGroupList(HttpServletRequest request) {
		try {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			List<UserGroupIbp> allowedUserGroup = null;
			JSONArray userRole = (JSONArray) profile.getAttribute("roles");
			if (userRole.contains("ROLE_ADMIN")) {
				allowedUserGroup = ugService.getAllUserGroup();
			} else {

				ugService = headers.addUserGroupHeader(ugService, request.getHeader(HttpHeaders.AUTHORIZATION));
				UserGroupPermissions userGroupPermission = ugService.getUserGroupPermission();

				List<Long> userGroupMember = new ArrayList<Long>();
				for (UserGroupMemberRole userMemberRole : userGroupPermission.getUserMemberRole()) {
					userGroupMember.add(userMemberRole.getUserGroupId());
				}
				String s = userGroupMember.toString();
				allowedUserGroup = ugService.getUserGroupList(s.substring(1, s.length() - 1));
			}

			return allowedUserGroup;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<Language> getLanguages(Boolean isDirty) {

		List<Language> result = null;
		try {
			result = utilityService.getAllLanguages(isDirty);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	@Override
	public List<Long> updateSpeciesGroup(HttpServletRequest request, Long documentId, List<Long> speciesGroupList) {

		List<DocumentSpeciesGroup> previousDocSGroup = docSGroupDao.findByDocumentId(documentId);
		for (DocumentSpeciesGroup docSgroup : previousDocSGroup) {
			if (!speciesGroupList.contains(docSgroup.getSpeciesGroupId())) {
				docSGroupDao.delete(docSgroup);
			} else {
				speciesGroupList.remove(docSgroup.getSpeciesGroupId());
			}
		}

		for (Long speciesGroupId : speciesGroupList) {
			DocumentSpeciesGroup docSGroup = new DocumentSpeciesGroup(documentId, speciesGroupId);
			docSGroupDao.save(docSGroup);
		}

		List<DocumentSpeciesGroup> newDocSgroup = docSGroupDao.findByDocumentId(documentId);
		List<Long> result = new ArrayList<Long>();
		for (DocumentSpeciesGroup sGroup : newDocSgroup)
			result.add(sGroup.getSpeciesGroupId());

		updateDocumentLastRevised(documentId);

		return result;
	}

	@Override
	public List<Long> updateHabitat(HttpServletRequest request, Long documentId, List<Long> habitatList) {
		List<DocumentHabitat> previousHabitats = docHabitatDao.findByDocumentId(documentId);
		for (DocumentHabitat docHabitat : previousHabitats) {
			if (!habitatList.contains(docHabitat.getHabitatId())) {
				docHabitatDao.delete(docHabitat);
			} else {
				habitatList.remove(docHabitat.getHabitatId());
			}
		}

		for (Long habitatId : habitatList) {
			DocumentHabitat docHabitat = new DocumentHabitat(documentId, habitatId);
			docHabitatDao.save(docHabitat);
		}
		List<DocumentHabitat> newDocHabitat = docHabitatDao.findByDocumentId(documentId);
		List<Long> habitatId = new ArrayList<Long>();
		for (DocumentHabitat docHabitat : newDocHabitat)
			habitatId.add(docHabitat.getHabitatId());

		updateDocumentLastRevised(documentId);
		return habitatId;
	}

	@Override
	public Boolean documentDownloadLog(HttpServletRequest request, DownloadLogData downloadLogData) {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		Long authorId = Long.parseLong(profile.getId());

		DownloadLog downloadLog = new DownloadLog(null, 0L, authorId, new Date(), downloadLogData.getFilePath(),
				downloadLogData.getFilterUrl(), null, null, downloadLogData.getStatus().toLowerCase(),
				downloadLogData.getFileType().toUpperCase(), "Document", 0L);

		downloadLog = downloadLogDao.save(downloadLog);
		if (downloadLog.getId() != null)
			return true;
		return false;
	}

	@Override
	public List<DocumentMeta> getDocumentByTaxonId(Long taxonConceptId) {
		try {
			List<DocumentMeta> result = new ArrayList<DocumentMeta>();
			List<DocSciName> docSciNameList = docSciNameDao.findByTaxonConceptId(taxonConceptId);
			for (DocSciName docSciName : docSciNameList) {
				Document document = documentDao.findById(docSciName.getDocumentId());
				UserIbp author = userService.getUserIbp(document.getAuthorId().toString());
				result.add(new DocumentMeta(document.getId(), document.getTitle(), document.getNotes(), author,
						document.getCreatedOn(), docSciName.getDisplayOrder()));
			}
			Collections.sort(result, Collections.reverseOrder());

			return result;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;

	}

}
