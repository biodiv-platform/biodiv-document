/**
 * 
 */
package com.strandls.document.controllers;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
import com.strandls.esmodule.pojo.MapBoundParams;
import com.strandls.esmodule.pojo.MapBounds;
import com.strandls.esmodule.pojo.MapGeoPoint;
import com.strandls.esmodule.pojo.MapSearchParams;
import com.strandls.esmodule.pojo.MapSearchParams.SortTypeEnum;
import com.strandls.esmodule.pojo.MapSearchQuery;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * @author Abhishek Rudra
 *
 */

@Tag(name = "Document Service", description = "APIs for managing document metadata and content")
@Path(ApiConstants.V1 + ApiConstants.SERVICES)
public class DocumentController {

	@Inject
	private DocumentService docService;

	@Inject
	private DocumentListService docListService;

	@Inject
	private ESUtility esUtility;

	@GET
	@Path(ApiConstants.PING)
	@Produces(MediaType.TEXT_PLAIN)
	@Operation(summary = "Ping check", description = "Service health endpoint")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Service is up", content = @Content(schema = @Schema(type = "string"))) })
	public Response getPong() {
		return Response.status(Status.OK).entity("PONG").build();
	}

	@PUT
	@Path(ApiConstants.DELETENAME + "/{nameId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Delete scientific name detected for a document by gnfinder", description = "Returns the object with updated column", responses = {
			@ApiResponse(responseCode = "200", description = "Name deleted", content = @Content(schema = @Schema(implementation = DocSciName.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data", content = @Content(schema = @Schema(type = "string"))) })
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
	@Operation(summary = "Detect scientific names in pdf files through gnfinder", description = "Returns the scientific names details detected by gnfinder", responses = {
			@ApiResponse(responseCode = "200", description = "Scientific names detected", content = @Content(schema = @Schema(implementation = GNFinderResponseMap.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data", content = @Content(schema = @Schema(type = "string"))) })
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
	@Operation(summary = "Fetch scientific names in pdf documents", description = "Returns the scientific names detected by gnfinder", responses = {
			@ApiResponse(responseCode = "200", description = "List of detected names", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocSciName.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data", content = @Content(schema = @Schema(type = "string"))) })
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
	@Operation(summary = "Fetch the document show page data", description = "Returns the document show page data", responses = {
			@ApiResponse(responseCode = "200", description = "Document show page", content = @Content(schema = @Schema(implementation = ShowDocument.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data", content = @Content(schema = @Schema(type = "string"))) })
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
	@Operation(summary = "Create the document", description = "Returns the document show page data", requestBody = @RequestBody(description = "Document data", required = true, content = @Content(schema = @Schema(implementation = DocumentCreateData.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Document created", content = @Content(schema = @Schema(implementation = ShowDocument.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data") })
	public Response createDocument(@Context HttpServletRequest request, DocumentCreateData documentCreate) {
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
	@Operation(summary = "Fetch the document for edit page", description = "Returns the document edit data", responses = {
			@ApiResponse(responseCode = "200", description = "Edit data fetched", content = @Content(schema = @Schema(implementation = DocumentEditData.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data") })
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
	@Operation(summary = "Update the document", description = "Returns the updated document show page data", requestBody = @RequestBody(description = "Document edit data", required = true, content = @Content(schema = @Schema(implementation = DocumentEditData.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Document updated", content = @Content(schema = @Schema(implementation = ShowDocument.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data") })
	public Response updateDocument(@Context HttpServletRequest request, DocumentEditData docEditData) {
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
	@Operation(summary = "Upload the bib file", description = "Returns the bib file data in a object", responses = {
			@ApiResponse(responseCode = "200", description = "Parsed successfully", content = @Content(schema = @Schema(implementation = BibFieldsData.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data") })
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
	@Operation(summary = "Bulk upload Bib file", description = "Processes bulk BibTeX upload", responses = {
			@ApiResponse(responseCode = "200", description = "Upload accepted"), })
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
	@Operation(summary = "Bulk upload the Excel file", description = "Starts the process of bulk upload", requestBody = @RequestBody(description = "Bulk upload Excel data", required = true, content = @Content(schema = @Schema(implementation = BulkUploadExcelData.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Process completed", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data") })
	public Response bulkUploadExcel(@Context HttpServletRequest request, BulkUploadExcelData bulkUploadData) {
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
	@Operation(summary = "Fetch all the BibTeX item types", description = "Returns all the item types with IDs", responses = {
			@ApiResponse(responseCode = "200", description = "List of item types", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BibTexItemType.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the bib item types") })
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
	@Operation(summary = "Fetch all the fields based on item type", description = "Returns all the relevant field based on item type", responses = {
			@ApiResponse(responseCode = "200", description = "Field map returned", content = @Content(schema = @Schema(implementation = Map.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data") })
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
	@Operation(summary = "Delete the document", description = "Confirms if the document got deleted", responses = {
			@ApiResponse(responseCode = "200", description = "Document deleted", content = @Content(schema = @Schema(implementation = Boolean.class))),
			@ApiResponse(responseCode = "400", description = "Unable to delete the document") })
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
	@Operation(summary = "Fetch user permission on document show page", description = "Returns document show page permission", responses = {
			@ApiResponse(responseCode = "200", description = "Permission details returned", content = @Content(schema = @Schema(implementation = DocumentUserPermission.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch user permission") })
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
	@Operation(summary = "Fetch all the usergroup associated with a user", description = "Returns usergroup list associated with user", responses = {
			@ApiResponse(responseCode = "200", description = "User groups returned", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserGroupIbp.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the user groups") })
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
	@Operation(summary = "Add a comment", description = "Returns the current activity", requestBody = @RequestBody(description = "Comment data", required = true, content = @Content(schema = @Schema(implementation = CommentLoggingData.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Comment added", content = @Content(schema = @Schema(implementation = Activity.class))),
			@ApiResponse(responseCode = "400", description = "Unable to log a comment") })
	public Response addComment(@Context HttpServletRequest request, CommentLoggingData commentData) {
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
	@Operation(summary = "Deletes a comment", description = "Return the current activity", parameters = {
			@Parameter(name = "commentId", description = "Comment ID", required = true) }, requestBody = @RequestBody(description = "Comment data", required = true, content = @Content(schema = @Schema(implementation = CommentLoggingData.class))), responses = {
					@ApiResponse(responseCode = "200", description = "Comment deleted", content = @Content(schema = @Schema(implementation = Activity.class))),
					@ApiResponse(responseCode = "400", description = "Unable to log a comment") })
	public Response deleteCommnet(@Context HttpServletRequest request, CommentLoggingData commentDatas,
			@PathParam("commentId") String commentId) {
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
	@Operation(summary = "Find all the Languages based on IsDirty field", description = "Returns all the Languages Details", responses = {
			@ApiResponse(responseCode = "200", description = "Languages fetched", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Language.class)))),
			@ApiResponse(responseCode = "400", description = "Languages not found") })
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
	@Operation(summary = "Find the Sugguestion for tags", description = "Return list of Top 10 tags matching the phrase", responses = {
			@ApiResponse(responseCode = "200", description = "Tag suggestions fetched", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tags.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the tags") })
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
	@Operation(summary = "Update tags for the document", description = "Returns tag list", requestBody = @RequestBody(description = "Tags mapping data", required = true, content = @Content(schema = @Schema(implementation = TagsMapping.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Tags updated", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Tags.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to update tags") })
	public Response updateTags(@Context HttpServletRequest request, TagsMapping tagsMapping) {
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
	@Operation(summary = "Update species group IDs", description = "Returns list of species group IDs", parameters = {
			@Parameter(name = "documentId", description = "Document ID", required = true) }, requestBody = @RequestBody(description = "List of species group IDs", required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))), responses = {
					@ApiResponse(responseCode = "200", description = "Species group updated", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))),
					@ApiResponse(responseCode = "400", description = "Unable to update") })
	public Response udpateSpeciesGroup(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			List<Long> speciesGroupList) {
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
	@Operation(summary = "Get all the habitats", description = "Returns all habitat in habitat order", responses = {
			@ApiResponse(responseCode = "200", description = "Habitats returned", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Habitat.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to get the habitat") })
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
	@Operation(summary = "Update habitat ids", description = "Return list of habitat ids", parameters = {
			@Parameter(name = "documentId", description = "Document ID", required = true) }, requestBody = @RequestBody(description = "List of habitat IDs", required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))), responses = {
					@ApiResponse(responseCode = "200", description = "Habitats updated", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))),
					@ApiResponse(responseCode = "400", description = "Unable to update") })
	public Response updateHabitat(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			List<Long> habitatList) {
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
	@Operation(summary = "Update the UserGroup linked with a Document", description = "Returns all the current userGroup Linked", parameters = {
			@Parameter(name = "documentId", description = "Document ID", required = true) }, requestBody = @RequestBody(description = "List of user group IDs", required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))), responses = {
					@ApiResponse(responseCode = "200", description = "User groups updated", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserGroupIbp.class)))),
					@ApiResponse(responseCode = "400", description = "Unable to updated the userGroup of Document") })
	public Response updateUserGroup(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			List<Long> userGroupList) {
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
	@Operation(summary = "Posting of Featured to a Group", description = "Marks a document as featured in specified user groups", requestBody = @RequestBody(description = "Returns the Details of Featured", required = true, content = @Content(schema = @Schema(implementation = FeaturedCreate.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Document featured", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Featured.class)))),
			@ApiResponse(responseCode = "404", description = "Unable to feature in a group") })
	public Response createFeatured(@Context HttpServletRequest request, FeaturedCreate featuredCreate) {
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
	@Operation(summary = "Unfeatures a Object from a UserGroup", description = "Returns the Current Featured", parameters = {
			@Parameter(name = "documentId", description = "Document ID", required = true) }, requestBody = @RequestBody(description = "List of user group IDs", required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))), responses = {
					@ApiResponse(responseCode = "200", description = "Document unfeatured", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Featured.class)))),
					@ApiResponse(responseCode = "404", description = "Unable to unfeature") })
	public Response unFeatured(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			List<Long> userGroupList) {
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
	@Operation(summary = "Flag a document", description = "Return a list of flag to the document", parameters = {
			@Parameter(name = "documentId", description = "ID of the document to flag", required = true) }, requestBody = @RequestBody(description = "Flag data", required = true, content = @Content(schema = @Schema(implementation = FlagIbp.class))), responses = {
					@ApiResponse(responseCode = "200", description = "Flagged successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FlagShow.class)))),
					@ApiResponse(responseCode = "406", description = "User has already flagged") })
	public Response createFlag(@Context HttpServletRequest request, @PathParam("documentId") String documentId,
			FlagIbp flagIbp) {
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
	@Operation(summary = "Unflag a document", description = "Return a list of flag to the Document", parameters = {
			@Parameter(name = "documentId", description = "Document ID", required = true),
			@Parameter(name = "flagId", description = "Flag ID", required = true) }, responses = {
					@ApiResponse(responseCode = "200", description = "Unflagged successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FlagShow.class)))),
					@ApiResponse(responseCode = "400", description = "Unable to unflag the document") })
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
	@Operation(summary = "Marks follow for a User", description = "Returns the follow details", parameters = {
			@Parameter(name = "documentId", description = "Document ID to follow", required = true) }, responses = {
					@ApiResponse(responseCode = "200", description = "Followed successfully", content = @Content(schema = @Schema(implementation = Follow.class))),
					@ApiResponse(responseCode = "400", description = "Unable to mark follow") })
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
	@Operation(summary = "Marks unfollow for a User", description = "Returns the unfollow details", parameters = {
			@Parameter(name = "documentId", description = "Document ID to unfollow", required = true) }, responses = {
					@ApiResponse(responseCode = "200", description = "Unfollowed successfully", content = @Content(schema = @Schema(implementation = Follow.class))),
					@ApiResponse(responseCode = "400", description = "Unable to mark unfollow") })
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
	@Operation(summary = "Log the document download", description = "Return true in case of logging", requestBody = @RequestBody(description = "Download log data", required = true, content = @Content(schema = @Schema(implementation = DownloadLogData.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Download logged", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Unable to log the download") })
	public Response logDocumentDownload(@Context HttpServletRequest request, DownloadLogData downloadLogData) {
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
	@Operation(summary = "Fetch document on basis of taxonConceptId", description = "Return the document meta data list", parameters = {
			@Parameter(name = "taxonConceptId", description = "Taxon concept ID", required = true) }, responses = {
					@ApiResponse(responseCode = "200", description = "Documents returned", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentMeta.class)))),
					@ApiResponse(responseCode = "400", description = "Unable to return the data") })
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
	@Operation(summary = "Fetch document list with filters", description = "Returns a list of documents based on various filters and pagination", parameters = {
			@Parameter(name = "index", description = "Index to query", required = true),
			@Parameter(name = "type", description = "Type to query", required = true),
			@Parameter(name = "max", description = "Max results", example = "10"),
			@Parameter(name = "offset", description = "Pagination offset", example = "0"),
			@Parameter(name = "view", description = "View type (list/grid)", example = "list"),
			@Parameter(name = "sort", description = "Field to sort on", example = "document.lastRevised"),
			@Parameter(name = "tags", description = "Comma-separated tag filters"),
			@Parameter(name = "createdOnMaxDate", description = "Max creation date"),
			@Parameter(name = "createdOnMinDate", description = "Min creation date"),
			@Parameter(name = "revisedOnMaxDate", description = "Max revised date"),
			@Parameter(name = "revisedOnMinDate", description = "Min revised date"),
			@Parameter(name = "isFlagged", description = "Flag filter"),
			@Parameter(name = "user", description = "User ID filter"),
			@Parameter(name = "sGroup", description = "Species group filter"),
			@Parameter(name = "habitatIds", description = "Habitat filter"),
			@Parameter(name = "flags", description = "Flags filter"),
			@Parameter(name = "featured", description = "Featured filter"),
			@Parameter(name = "left", description = "Bounding box left (longitude)"),
			@Parameter(name = "right", description = "Bounding box right (longitude)"),
			@Parameter(name = "top", description = "Bounding box top (latitude)"),
			@Parameter(name = "bottom", description = "Bounding box bottom (latitude)"),
			@Parameter(name = "state", description = "State filter"),
			@Parameter(name = "userGroupList", description = "User group filter"),
			@Parameter(name = "geoAggregationField", description = "Field for geo aggregation"),
			@Parameter(name = "geoShapeFilterField", description = "Geo shape filter field"),
			@Parameter(name = "nestedField", description = "Nested field to query"),
			@Parameter(name = "itemType", description = "Item type filter"),
			@Parameter(name = "year", description = "Year filter"),
			@Parameter(name = "author", description = "Author filter"),
			@Parameter(name = "publisher", description = "Publisher filter"),
			@Parameter(name = "title", description = "Title filter"),
			@Parameter(name = "geoAggegationPrecision", description = "Precision for geo aggregation", example = "1"),
			@Parameter(name = "onlyFilteredAggregation", description = "Only filtered aggregation", example = "false") }, requestBody = @RequestBody(required = false, content = @Content(schema = @Schema(implementation = DocumentListParams.class))), responses = {
					@ApiResponse(responseCode = "200", description = "List of documents returned", content = @Content(schema = @Schema(implementation = DocumentListData.class))),
					@ApiResponse(responseCode = "400", description = "Error occurred during document list fetch") })
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
			@QueryParam("onlyFilteredAggregation") Boolean onlyFilteredAggregation, DocumentListParams location) {
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

			DocumentListData result = docListService.getDocumentList(index, type, geoAggregationField,
					geoShapeFilterField, nestedField, aggregationResult, mapSearchQuery);

			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

}
