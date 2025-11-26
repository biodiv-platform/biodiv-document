package com.strandls.document.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.strandls.esmodule.ApiException;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.MapDocument;
import com.strandls.esmodule.pojo.MapResponse;
import com.strandls.esmodule.pojo.MapSearchQuery;
import com.strandls.document.Headers;
import com.strandls.document.es.util.ESBulkUploadThread;
import com.strandls.document.es.util.ESUpdate;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.BulkGroupPostingData;
import com.strandls.userGroup.pojo.BulkGroupUnPostingData;
import com.strandls.userGroup.pojo.UserGroupObvFilterData;

public class DocumentBulkMappingThread implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(DocumentBulkMappingThread.class);

	private Boolean selectAll;
	private String bulkAction;
	private String bulkDocumentIds;
	private String bulkUsergroupIds;
	private MapSearchQuery mapSearchQuery;
	private UserGroupSerivceApi ugService;
	private String index;
	private String type;
	private EsServicesApi esService;
	private ESUpdate esUpdate;
	private ObjectMapper objectMapper;
	private final HttpServletRequest request;
	private final Headers headers;
	private final String requestAuthHeader;

	public DocumentBulkMappingThread(Boolean selectAll, String bulkAction, String bulkDocumentIds,
			String bulkUsergroupIds, MapSearchQuery mapSearchQuery, UserGroupSerivceApi ugService, String index,
			String type, EsServicesApi esService, HttpServletRequest request, Headers headers,
			ObjectMapper objectMapper, ESUpdate esUpdate) {
		super();
		this.selectAll = selectAll;
		this.bulkAction = bulkAction;
		this.bulkDocumentIds = bulkDocumentIds;
		this.bulkUsergroupIds = bulkUsergroupIds;
		this.mapSearchQuery = mapSearchQuery;
		this.ugService = ugService;
		this.index = index;
		this.type = type;
		this.esService = esService;
		this.request = request;
		this.headers = headers;
		this.objectMapper = objectMapper;
		this.requestAuthHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		this.esUpdate = esUpdate;
	}

	@Override
	public void run() {
		List<UserGroupObvFilterData> list = new ArrayList<UserGroupObvFilterData>();
		List<Long> documentIds = new ArrayList<Long>();
		List<Long> ugIds = new ArrayList<Long>();

		if (bulkDocumentIds != null && !bulkDocumentIds.isEmpty() && Boolean.FALSE.equals(selectAll)) {
			documentIds.addAll(
					Arrays.stream(bulkDocumentIds.split(",")).map(Long::valueOf).collect(Collectors.toList()));
		}

		if (bulkUsergroupIds != null && !bulkUsergroupIds.isEmpty()) {
			ugIds.addAll(Arrays.stream(bulkUsergroupIds.split(",")).map(Long::valueOf).collect(Collectors.toList()));
		}

		if (!documentIds.isEmpty()) {

			for (Long doc : documentIds) {
				UserGroupObvFilterData ugFilterData = new UserGroupObvFilterData();
				ugFilterData.setObservationId(doc);
				list.add(ugFilterData);
			}

		}

		/*if (Boolean.TRUE.equals(selectAll)) {
			List<ShowSpeciesPage> specieList = new ArrayList<ShowSpeciesPage>();

			try {

				MapResponse result = esService.search(index, type, null, null, false, null, null, mapSearchQuery);
				List<MapDocument> documents = result.getDocuments();

				for (MapDocument document : documents) {
					JsonNode rootNode = objectMapper.readTree(document.getDocument().toString());
					((ObjectNode) rootNode).remove("id");
					((ObjectNode) rootNode).replace("featured", null);
					((ObjectNode) rootNode).replace("facts", null);
					((ObjectNode) rootNode).replace("fieldData", null);
					JsonNode child = ((ObjectNode) rootNode).get("taxonomyDefinition");
					((ObjectNode) child).replace("defaultHierarchy", null);

					try {

						specieList.add(objectMapper.readValue(String.valueOf(rootNode), ShowSpeciesPage.class));
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}

				specieList.forEach(item -> {
					UserGroupObvFilterData ugFilterData = new UserGroupObvFilterData();
					ugFilterData.setObservationId(item.getSpecies().getId());
					list.add(ugFilterData);
				});


			} catch (IOException | ApiException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}

		}*/

		if (!list.isEmpty() && !bulkAction.isEmpty()
				&& (bulkAction.contains("ugBulkPosting") || bulkAction.contains("ugBulkUnPosting"))) {

			List<UserGroupObvFilterData> ugObsList = new ArrayList<UserGroupObvFilterData>();
			;
			Integer count = 0;

			while (count < list.size()) {
				ugObsList.add(list.get(count));

				if (ugObsList.size() >= 20) {
					bulkGroupAction(ugObsList, ugIds);
					ugObsList.clear();
				}
				count++;
			}

			bulkGroupAction(ugObsList, ugIds);
			ugObsList.clear();
		}

	}

	private void bulkGroupAction(List<UserGroupObvFilterData> ugObsList, List<Long> ugIds) {
		if (!ugObsList.isEmpty()) {
			BulkGroupPostingData ugBulkPostingData = bulkAction.contains("ugBulkPosting") ? new BulkGroupPostingData()
					: null;
			BulkGroupUnPostingData ugBulkUnPostingData = bulkAction.contains("ugBulkUnPosting")
					? new BulkGroupUnPostingData()
					: null;
			if (ugBulkPostingData != null) {
				ugBulkPostingData.setRecordType("species");
				ugBulkPostingData.setUgObvFilterDataList(ugObsList);
				ugBulkPostingData.setUserGroupList(ugIds);
			} else if (ugBulkUnPostingData != null) {
				ugBulkUnPostingData.setRecordType("species");
				ugBulkUnPostingData.setUgFilterDataList(ugObsList);
				ugBulkUnPostingData.setUserGroupList(ugIds);
			}

			ugService = headers.addUserGroupHeader(ugService, requestAuthHeader);
			try {
				if (ugBulkPostingData != null) {
					ugService.bulkPostingObservationUG(ugBulkPostingData);
				} else if (ugBulkUnPostingData != null) {
					ugService.bulkRemovingObservation(ugBulkUnPostingData);
				}

			} catch (com.strandls.userGroup.ApiException e) {
				logger.error(e.getMessage());
			}

			List<Long> obsIds = ugObsList.stream().map(item -> item.getObservationId()).collect(Collectors.toList());
			String observationList = StringUtils.join(obsIds, ',');
			ESBulkUploadThread updateThread = new ESBulkUploadThread(esUpdate, observationList);
			Thread esThreadUpdate = new Thread(updateThread);
			esThreadUpdate.start();

		}
	}
}