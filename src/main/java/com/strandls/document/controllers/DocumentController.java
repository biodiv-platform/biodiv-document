/**
 * 
 */
package com.strandls.document.controllers;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.authentication_utility.filter.ValidateUser;
import com.strandls.document.ApiConstants;
import com.strandls.document.es.util.ESUtility;
import com.strandls.document.pojo.BibFieldsData;
import com.strandls.document.pojo.BibTexItemType;
import com.strandls.document.pojo.BulkUploadExcelData;
import com.strandls.document.pojo.DocSciName;
import com.strandls.document.pojo.DocumentCreateData;
import com.strandls.document.pojo.DocumentEditData;
import com.strandls.document.pojo.DocumentListData;
import com.strandls.document.pojo.DocumentListParams;
import com.strandls.document.pojo.DocumentMeta;
import com.strandls.document.pojo.DocumentUserPermission;
import com.strandls.document.pojo.DownloadLogData;
import com.strandls.document.pojo.GNFinderResponseMap;
import com.strandls.document.pojo.MapAggregationResponse;
import com.strandls.document.pojo.ShowDocument;
import com.strandls.document.service.DocumentListService;
import com.strandls.document.service.DocumentService;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.MapBoundParams;
import com.strandls.esmodule.pojo.MapBounds;
import com.strandls.esmodule.pojo.MapGeoPoint;
import com.strandls.esmodule.pojo.MapSearchParams;
import com.strandls.esmodule.pojo.MapSearchParams.SortTypeEnum;
import com.strandls.document.es.util.ESUpdate;
import com.strandls.document.Headers;
import com.strandls.document.util.DocumentBulkMappingThread;
import com.strandls.esmodule.pojo.MapSearchQuery;
import com.strandls.user.pojo.Follow;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.FeaturedCreate;
import com.strandls.userGroup.pojo.UserGroupIbp;
import com.strandls.utility.pojo.FlagIbp;
import com.strandls.utility.pojo.FlagShow;
import com.strandls.utility.pojo.Habitat;
import com.strandls.utility.pojo.Language;
import com.strandls.utility.pojo.Tags;
import com.strandls.utility.pojo.TagsMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Abhishek Rudra
 *
 */

@Api("Document Service")
@Path(ApiConstants.V1 + ApiConstants.SERVICES)
public class DocumentController {

	@Inject
	private DocumentService docService;

	@Inject
	private DocumentListService docListService;

	@Inject
	private ESUtility esUtility;
	
	@Inject
	private UserGroupSerivceApi ugService;
	
	@Inject
	private EsServicesApi esService;
	
	@Inject
	private Headers headers;
	
	@Inject
	private ObjectMapper objectMapper;
	
	@Inject
	private ESUpdate esUpdate;

	@GET
	@Path(ApiConstants.PING)
	@Produces(MediaType.TEXT_PLAIN)

	public Response getPong() {
		return Response.status(Status.OK).entity("PONG").build();
	}

	@PUT
	@Path(ApiConstants.DELETENAME + "/{nameId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "delete scientific name detected for a document by gnfinder", notes = "returns the object with updated column", response = DocSciName.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	@ValidateUser
	public Response deleteScientificName(@Context HttpServletRequest request, @PathParam("nameId") Long nameId) {
		try {
			DocSciName response = docService.updateScienticNametoIsDeleted(request, nameId);
			return Response.status(Status.OK).entity(response).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@GET
	@Path(ApiConstants.GNRD)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "detects scientific names in pdf files through gnfinder", notes = "returns the scientific names details detected by gnfinder", response = GNFinderResponseMap.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response parsePdfWithGNFinder(@QueryParam("filePath") String filePath,
			@QueryParam("documentId") Long documentId) {

		try {
			GNFinderResponseMap response = docService.parsePdfWithGNFinder(filePath, documentId);
			return Response.status(Status.OK).entity(response).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.NAMES + "/{documentId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "fetch the scientific names in pdf documents", notes = "returns the scientific names detected by gnfinder", response = DocSciName.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response findNamesInDocument(@PathParam("documentId") Long documentId,
			@QueryParam("offset") Integer offset) {

		try {
			List<DocSciName> response = docService.getNamesByDocumentId(documentId, offset);
			return Response.status(Status.OK).entity(response).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@GET
	@Path(ApiConstants.SHOW + "/{documentId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "fetch the document show page data", notes = "returns the document show page data", response = ShowDocument.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response showDocument(@PathParam("documentId") String documentId) {
		try {
			Long docId = Long.parseLong(documentId);
			ShowDocument result = docService.show(docId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.CREATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "create the document ", notes = "returns the document show page data", response = ShowDocument.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response createDocument(@Context HttpServletRequest request,
			@ApiParam(name = "documentCreate") DocumentCreateData documentCreate) {
		try {
			ShowDocument result = docService.createDocument(request, documentCreate);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.EDIT + "/{documentId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "fetch the document for edit page", notes = "returns the document edit data", response = DocumentEditData.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response getEditDocument(@Context HttpServletRequest request, @PathParam("documentId") String documentId) {
		try {
			Long docId = Long.parseLong(documentId);
			DocumentEditData result = docService.getDocumentEditData(request, docId);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update the document", notes = "returns the document show page data", response = ShowDocument.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response updateDocument(@Context HttpServletRequest request,
			@ApiParam(name = "docEditData") DocumentEditData docEditData) {
		try {
			ShowDocument result = docService.updateDocument(request, docEditData);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_MODIFIED).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@POST
	@Path(ApiConstants.UPLOAD + ApiConstants.BIB)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "upload the bib file", notes = "returns the bib file data in a object", response = BibFieldsData.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response uploadBib(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		try {
			BibFieldsData result = docService.readBibTex(uploadedInputStream, fileDetail);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.BULK + ApiConstants.UPLOAD + ApiConstants.BIB)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	public Response bulkUploadBib(@Context HttpServletRequest request,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		try {

			return null;
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.BULK + ApiConstants.UPLOAD + ApiConstants.EXCEL)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "bulk upload the excel file", notes = "starts the process of bulk upload", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response bulkUploadExcel(@Context HttpServletRequest request,
			@ApiParam(name = "bulkUploadData") BulkUploadExcelData bulkUploadData) {
		try {
			docService.bulkUploadExcel(request, bulkUploadData);

			return Response.status(Status.OK).entity("process completed").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.BIB + ApiConstants.ITEM + ApiConstants.ALL)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "fetch all the bibtex item type", notes = "return all the item type with id", response = BibTexItemType.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to fetch the bib item type", response = String.class) })

	public Response getAllBibItemType() {
		try {
			List<BibTexItemType> result = docService.fetchAllItemType();
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.BIB + ApiConstants.FIELDS + "/{itemId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "fetch all the fields based on item type", notes = "returns all the relevant field based on item type", response = Map.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response getItemsFieldType(@PathParam("itemId") String itemId) {
		try {
			Long itemTypeId = Long.parseLong(itemId);
			Map<String, Boolean> result = docService.getAllFieldTypes(itemTypeId);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@DELETE
	@Path(ApiConstants.DELETE + "/{documentId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Deletet the document", notes = "Confirms if the document got deleted", response = Boolean.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to delete the document", response = String.class) })

	public Response deleteDocument(@Context HttpServletRequest request, @PathParam("documentId") String documentId) {
		try {
			Long docId = Long.parseLong(documentId);
			Boolean result = docService.removeDocument(request, docId);
			if (result)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.PERMISSION + "/{documentId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "fetch user permission on document show page", notes = "returns the document show page permission", response = DocumentUserPermission.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to fetch user permission", response = String.class) })

	public Response getUserPermission(@Context HttpServletRequest request, @PathParam("documentId") String documentId) {
		try {
			DocumentUserPermission result = docService.getUserPermission(request, documentId);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.USERGROUP + ApiConstants.PERMISSION)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "fetch all the usergroup associated with a user", notes = "returns usergroup list associated with user", response = UserGroupIbp.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to fetch the usergroup", response = String.class) })

	public Response getAllowedUserGroup(@Context HttpServletRequest request) {
		try {
			List<UserGroupIbp> result = docService.getAllowedUserGroupList(request);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.ADD + ApiConstants.COMMENT)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Adds a comment", notes = "Return the current activity", response = Activity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to log a comment", response = String.class) })

	public Response addCommnet(@Context HttpServletRequest request,
			@ApiParam(name = "commentData") CommentLoggingData commentData) {
		try {
			Activity result = docService.addDocumentComment(request, commentData);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@POST
	@Path(ApiConstants.DELETE + ApiConstants.COMMENT + "/{commentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Deletes a comment", notes = "Return the current activity", response = Activity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to log a comment", response = String.class) })

	public Response deleteCommnet(@Context HttpServletRequest request,
			@ApiParam(name = "commentData") CommentLoggingData commentDatas, @PathParam("commentId") String commentId) {
		try {
			Activity result = docService.removeDocumentComment(request, commentDatas, commentId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@GET
	@Path(ApiConstants.LANGUAGE)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find all the Languages based on IsDirty field", notes = "Returns all the Languages Details", response = Language.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Languages Not Found", response = String.class) })

	public Response getLanguaes(@QueryParam("isDirty") Boolean isDirty) {
		try {
			List<Language> result = docService.getLanguages(isDirty);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.TAGS + ApiConstants.AUTOCOMPLETE)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find the Sugguestion for tags", notes = "Return list of Top 10 tags matching the phrase", response = Tags.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to fetch the tags", response = String.class) })

	public Response getTagsSuggetion(@QueryParam("phrase") String phrase) {
		try {
			List<Tags> result = docService.getTagsSuggestion(phrase);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.TAGS)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update tags for the Document", notes = "Returns Tags list", response = Tags.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to update the tags", response = String.class) })

	public Response updateTags(@Context HttpServletRequest request,
			@ApiParam(name = "tagsMapping") TagsMapping tagsMapping) {
		try {
			List<Tags> result = docService.updateTags(request, tagsMapping);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.SPECIESGROUP + "/{documentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update the speciesGroup ids", notes = "return list of speciesGroup ids", response = Long.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to update", response = String.class) })

	public Response udpateSpeciesGroup(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			@ApiParam("speciesGroupList") List<Long> speciesGroupList) {
		try {
			Long docId = Long.parseLong(documentId);
			List<Long> result = docService.updateSpeciesGroup(request, docId, speciesGroupList);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.HABITAT + ApiConstants.ALL)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Get all the Habitat", notes = "Returns all the habitat in habitat order", response = Habitat.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to get the habitat", response = String.class) })

	public Response getAllHabitat() {
		try {
			List<Habitat> result = docService.getAllHabitat();
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.HABITAT + "/{documentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update the habitat ids", notes = "return list of habitat ids", response = Long.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to update", response = String.class) })

	public Response updateHabitat(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			@ApiParam("habitatList") List<Long> habitatList) {
		try {
			Long docId = Long.parseLong(documentId);
			List<Long> result = docService.updateHabitat(request, docId, habitatList);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.USERGROUP + "/{documentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Update the UserGroup linked with a Document", notes = "Returns all the current userGroup Linked", response = UserGroupIbp.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Unable to updated the userGroup of Document", response = String.class) })

	public Response updateUserGroup(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			@ApiParam(name = "userGroupList") List<Long> userGroupList) {
		try {
			List<UserGroupIbp> result = docService.updateUserGroup(request, documentId, userGroupList);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.FEATURED)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser

	@ApiOperation(value = "Posting of Featured to a Group", notes = "Returns the Details of Featured", response = Featured.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Unable to Feature in a Group", response = String.class) })

	public Response createFeatured(@Context HttpServletRequest request,
			@ApiParam(name = "featuredCreate") FeaturedCreate featuredCreate) {
		try {
			List<Featured> result = docService.createFeatured(request, featuredCreate);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UNFEATURED + "/{documentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser
	@ApiOperation(value = "UnFeatures a Object from a UserGroup", notes = "Returns the Current Featured", response = Featured.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Unable to Unfeature", response = String.class) })

	public Response unFeatured(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			@ApiParam(name = "userGroupList") List<Long> userGroupList) {
		try {
			List<Featured> result = docService.unFeatured(request, documentId, userGroupList);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.FLAG + "/{documentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser

	@ApiOperation(value = "Flag a Document", notes = "Return a list of flag to the document", response = FlagShow.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to flag a Document", response = String.class),
			@ApiResponse(code = 406, message = "User has already flagged", response = String.class) })

	public Response createFlag(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			@ApiParam(name = "flagIbp") FlagIbp flagIbp) {
		try {
			Long docId = Long.parseLong(documentId);
			List<FlagShow> result = docService.createFlag(request, docId, flagIbp);
			if (result.isEmpty())
				return Response.status(Status.NOT_ACCEPTABLE).entity("User Allowed Flagged").build();
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UNFLAG + "/{documentId}/{flagId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser

	@ApiOperation(value = "Unflag a document", notes = "Return a list of flag to the Document", response = FlagShow.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to unflag a document", response = String.class),
			@ApiResponse(code = 406, message = "User is not allowed to unflag", response = String.class) })

	public Response unFlag(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			@PathParam("flagId") String flagId) {
		try {
			Long docId = Long.parseLong(documentId);
			List<FlagShow> result = docService.unFlag(request, docId, flagId);
			if (result == null)
				return Response.status(Status.NOT_ACCEPTABLE).entity("User not allowed to Unflag").build();
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.FOLLOW + "/{documentId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser

	@ApiOperation(value = "Marks follow for a User", notes = "Returnt the follow details", response = Follow.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to mark follow", response = String.class) })

	public Response followObservation(@Context HttpServletRequest request, @PathParam("documentId") String documentId) {
		try {
			Long docId = Long.parseLong(documentId);
			Follow result = docService.followRequest(request, docId);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.UNFOLLOW + "/{documentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser

	@ApiOperation(value = "Marks unfollow for a User", notes = "Returnt the unfollow details", response = Follow.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to mark unfollow", response = String.class) })

	public Response unfollow(@Context HttpServletRequest request, @PathParam("documentId") String documentId) {

		try {
			Long docId = Long.parseLong(documentId);
			Follow result = docService.unFollowRequest(request, docId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.LOG + ApiConstants.DOWNLOAD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)

	@ValidateUser

	@ApiOperation(value = "log the document download", notes = "return true incase of logging", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to log the download", response = String.class) })
	public Response logDocumentDownload(@Context HttpServletRequest request,
			@ApiParam("documentDownloadData") DownloadLogData downloadLogData) {
		try {
			Boolean result = docService.documentDownloadLog(request, downloadLogData);

			if (result != null && result)
				return Response.status(Status.OK).entity("Download logged").build();
			return Response.status(Status.NOT_ACCEPTABLE).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.TAXONOMY + "/{taxonConceptId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "fetch document on basis of taxonConceptId", notes = "Return the document meta data list", response = DocumentMeta.class, responseContainer = "List")
	@ApiResponses(value = {

			@ApiResponse(code = 400, message = "unable to return the data", response = String.class) })

	public Response getDocumentByTaxonConceptId(@PathParam("taxonConceptId") String taxonConceptId) {
		try {
			Long taxonomyConceptId = Long.parseLong(taxonConceptId);
			List<DocumentMeta> result = docService.getDocumentByTaxonId(taxonomyConceptId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.LIST + "/{index}/{type}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	public Response DocumentList(@PathParam("index") String index, @PathParam("type") String type,
			@DefaultValue("10") @QueryParam("max") Integer max, @DefaultValue("0") @QueryParam("offset") Integer offset,
			@DefaultValue("list") @QueryParam("view") String view,
			@DefaultValue("document.lastRevised") @QueryParam("sort") String sortOn, @QueryParam("tags") String tags,
			@QueryParam("createdOnMaxDate") String createdOnMaxDate,
			@QueryParam("createdOnMinDate") String createdOnMinDate,
			@QueryParam("revisedOnMaxDate") String revisedOnMaxDate,
			@QueryParam("revisedOnMinDate") String revisedOnMinDate,
			@DefaultValue("") @QueryParam("isFlagged") String isFlagged,
			@DefaultValue("") @QueryParam("user") String user, @DefaultValue("") @QueryParam("sGroup") String sGroup,
			@DefaultValue("") @QueryParam("habitatIds") String habitatIds,
			@DefaultValue("") @QueryParam("flags") String flags,
			@DefaultValue("") @QueryParam("featured") String featured, @QueryParam("left") Double left,
			@QueryParam("right") Double right, @QueryParam("top") Double top, @QueryParam("bottom") Double bottom,
			@QueryParam("state") String state, @DefaultValue("") @QueryParam("userGroupList") String userGroupList,
			@QueryParam("geoAggregationField") String geoAggregationField,
			@QueryParam("geoShapeFilterField") String geoShapeFilterField,
			@QueryParam("nestedField") String nestedField,

			@QueryParam("itemType") String itemType, @QueryParam("year") String year,
			@QueryParam("author") String author, @QueryParam("publisher") String publisher,
			@QueryParam("title") String title,

			@DefaultValue("1") @QueryParam("geoAggegationPrecision") Integer geoAggegationPrecision,
			@QueryParam("onlyFilteredAggregation") Boolean onlyFilteredAggregation,
			@ApiParam(name = "location") DocumentListParams location,
			
			@QueryParam("bulkAction") String bulkAction,
			@QueryParam("selectAll") Boolean selectAll, @QueryParam("bulkUsergroupIds") String bulkUsergroupIds,
			@QueryParam("bulkSpeciesIds") String bulkDocumentsIds,
			@Context HttpServletRequest request) {
		try {

			if (max > 50) {
				max = 50;
			}

			MapBounds bounds = null;
			if (top != null || bottom != null || left != null || right != null) {
				bounds = new MapBounds();
				bounds.setBottom(bottom);
				bounds.setLeft(left);
				bounds.setRight(right);
				bounds.setTop(top);
			}

			MapBoundParams mapBoundsParams = new MapBoundParams();
			MapSearchParams mapSearchParams = new MapSearchParams();
			mapSearchParams.setFrom(offset);
			mapBoundsParams.setBounds(bounds);
			mapSearchParams.setLimit(max);
			mapSearchParams.setSortOn(sortOn);
			mapSearchParams.setSortType(SortTypeEnum.DESC);
			mapSearchParams.setMapBoundParams(mapBoundsParams);

			String loc = location.getLocation();
			if (loc != null) {
				if (loc.contains("/")) {
					String[] locationArray = loc.split("/");
					List<List<MapGeoPoint>> multiPolygonPoint = esUtility.multiPolygonGenerator(locationArray);
					mapBoundsParams.setMultipolygon(multiPolygonPoint);
				} else {
					mapBoundsParams.setPolygon(esUtility.polygonGenerator(loc));
				}
			}

			MapAggregationResponse aggregationResult = null;

			if (offset == 0) {
				aggregationResult = docListService.mapAggregate(index, type, sGroup, habitatIds, tags, user, flags,
						createdOnMaxDate, createdOnMinDate, featured, userGroupList, isFlagged, revisedOnMaxDate,
						revisedOnMinDate, state, itemType, year, author, publisher, title, geoShapeFilterField,
						mapSearchParams);
			}

			MapSearchQuery mapSearchQuery = esUtility.getMapSearchQuery(sGroup, habitatIds, tags, user, flags,
					createdOnMaxDate, createdOnMinDate, featured, userGroupList, isFlagged, revisedOnMaxDate,
					revisedOnMinDate, state, itemType, year, author, publisher, title, mapSearchParams);
			
			if (view.equalsIgnoreCase("list")){
				DocumentListData result = docListService.getDocumentList(index, type, geoAggregationField,
						geoShapeFilterField, nestedField, aggregationResult, mapSearchQuery);

				return Response.status(Status.OK).entity(result).build();
			} else if ((Boolean.FALSE.equals(selectAll) && bulkDocumentsIds != null && !bulkAction.isEmpty()
					&& !bulkDocumentsIds.isEmpty() && bulkUsergroupIds != null && !bulkUsergroupIds.isEmpty()
					&& view.equalsIgnoreCase("bulkMapping"))
					|| (Boolean.TRUE.equals(selectAll) && bulkUsergroupIds != null && !bulkUsergroupIds.isEmpty()
							&& !bulkAction.isEmpty() && view.equalsIgnoreCase("bulkMapping"))) {
				mapSearchParams.setFrom(0);
				mapSearchParams.setLimit(100000);
				
				if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
					return Response.status(Status.BAD_REQUEST).build();
				}
				
				DocumentBulkMappingThread bulkMappingThread = new DocumentBulkMappingThread(selectAll, bulkAction,
						bulkDocumentsIds, bulkUsergroupIds, mapSearchQuery, ugService, index, type, esService, request,
						headers, objectMapper, esUpdate);

				Thread thread = new Thread(bulkMappingThread);
				thread.start();
				return Response.status(Status.OK).build();
				
				
			}
			return Response.status(Status.OK).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

}
