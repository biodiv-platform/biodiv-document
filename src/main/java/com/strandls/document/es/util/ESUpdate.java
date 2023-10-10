package com.strandls.document.es.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.esmodule.ApiException;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.MapDocument;
import com.strandls.esmodule.pojo.MapQueryResponse;
import com.strandls.species.es.util.SpeciesIndex;
import com.strandls.species.service.SpeciesServices;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.UserGroupIbp;

public class ESUpdate {

	private final Logger logger = LoggerFactory.getLogger(ESUpdate.class);

	@Inject
	private UserGroupSerivceApi ugService;

	@Inject
	private EsServicesApi esService;

	public void updateESInstance(String documentId, String documentData) {
		try {
			System.out.println("--------------------document es Update---------");
			System.out.println();
			System.out.println("------started----------");
			System.out.println("Document getting UPDATED to elastic,ID:" + documentId);
			MapDocument doc = new MapDocument();
			doc.setDocument(documentData);
			MapQueryResponse response = esService.create(DocumentIndex.INDEX.getValue(), DocumentIndex.TYPE.getValue(),
					documentId, doc);
			System.out.println();
			System.out.println();
			System.out.println("-----------updated----------");
			System.out.println(response.getResult());
			System.out.println("--------------completed-------------documentId :" + documentId);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public void esBulkUpload(String documentIds) {

		if (documentIds == null || documentIds.isEmpty()) {
			return;
		}

		List<Map<String, Object>> ESDocumentShowList = new ArrayList<>();

		try {

			for (String id : documentIds.split(",")) {
				List<UserGroupIbp> userGroupList = new ArrayList<>();

				try {
					userGroupList = ugService.getUserGroupByDocId(id);
				} catch (com.strandls.userGroup.ApiException e) {
					logger.error(e.getMessage());
				}

				Map<String, Object> payload = new HashMap<>();
				payload.put("id", id);
				payload.put("userGroups", userGroupList);
				ESDocumentShowList.add(payload);

			}

			if (!ESDocumentShowList.isEmpty()) {

				esService.bulkUpdate(DocumentIndex.INDEX.getValue(), DocumentIndex.TYPE.getValue(), ESDocumentShowList);

			}

		} catch (ApiException e) {
			logger.error(e.getMessage());
		}
	}
}
