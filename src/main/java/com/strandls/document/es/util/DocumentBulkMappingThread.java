package com.strandls.document.es.util;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.esmodule.ApiException;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.MapDocument;
import com.strandls.esmodule.pojo.MapResponse;
import com.strandls.esmodule.pojo.MapSearchQuery;
//import com.strandls.integrator.controllers.IntergratorServicesApi;
//import com.strandls.integrator.pojo.CheckFilterRule;
//import com.strandls.integrator.pojo.UserGroupObvRuleData;
import com.strandls.document.Headers;
import com.strandls.document.pojo.ShowDocument;
//import com.strandls.observation.dao.ObservationDAO;
//import com.strandls.observation.pojo.MapAggregationResponse;
//import com.strandls.observation.pojo.MapAggregationStatsResponse;
//import com.strandls.observation.pojo.Observation;
//import com.strandls.observation.service.Impl.ObservationMapperHelper;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.BulkGroupPostingData;
import com.strandls.userGroup.pojo.BulkGroupUnPostingData;
import com.strandls.userGroup.pojo.UserGroupObvFilterData;

public class DocumentBulkMappingThread implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(DocumentBulkMappingThread.class);

	private Boolean selectAll;
	private String bulkAction;
	private String bulkObservationIds;
	private String bulkUsergroupIds;
	private MapSearchQuery mapSearchQuery;
	private UserGroupSerivceApi ugService;
	private String index;
	private String type;
	private String geoAggregationField;
	private Integer geoAggegationPrecision;
	private Boolean onlyFilteredAggregation;
//	private String termsAggregationField;
	private String geoShapeFilterField;
	private EsServicesApi esService;
	// private ObservationMapperHelper observationMapperHelper;
	// private ObservationDAO observationDao;
	private ObjectMapper objectMapper;
	private final HttpServletRequest request;
	private final Headers headers;
	private final String requestAuthHeader;
	private final ESUpdate esUpdate;
	// private IntergratorServicesApi intergratorService;

	public DocumentBulkMappingThread(Boolean selectAll, String bulkAction, String bulkObservationIds,
			String bulkUsergroupIds, MapSearchQuery mapSearchQuery, UserGroupSerivceApi ugService, String index,
			String type, String geoAggregationField, Integer geoAggegationPrecision, Boolean onlyFilteredAggregation,
			String geoShapeFilterField, String view, EsServicesApi esService, HttpServletRequest request,
			Headers headers, ObjectMapper objectMapper, ESUpdate esUpdate) {
		super();
		this.selectAll = selectAll;
		this.bulkAction = bulkAction;
		this.bulkObservationIds = bulkObservationIds;
		this.bulkUsergroupIds = bulkUsergroupIds;
		this.mapSearchQuery = mapSearchQuery;
		this.ugService = ugService;
		this.index = index;
		this.type = type;
		this.geoAggregationField = geoAggregationField;
		this.geoAggegationPrecision = geoAggegationPrecision;
		this.onlyFilteredAggregation = onlyFilteredAggregation;
		this.geoShapeFilterField = geoShapeFilterField;
		this.esService = esService;
		this.request = request;
		this.headers = headers;
		this.objectMapper = objectMapper;
		this.requestAuthHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		this.esUpdate = esUpdate;
	}

	@Override
	public void run() {

		try {

			List<UserGroupObvFilterData> list = new ArrayList<UserGroupObvFilterData>();
			List<Long> oservationIds = new ArrayList<Long>();
			List<Long> ugIds = new ArrayList<Long>();

			if (bulkObservationIds != null && !bulkObservationIds.isEmpty() && Boolean.FALSE.equals(selectAll)) {
				oservationIds.addAll(
						Arrays.stream(bulkObservationIds.split(",")).map(Long::valueOf).collect(Collectors.toList()));
			}

			if (bulkUsergroupIds != null && !bulkUsergroupIds.isEmpty()) {
				ugIds.addAll(
						Arrays.stream(bulkUsergroupIds.split(",")).map(Long::valueOf).collect(Collectors.toList()));
			}

//			if (!oservationIds.isEmpty()) {
//				List<Observation> obsDataList = observationDao.fecthByListOfIds(oservationIds);
//
//				for (Observation obs : obsDataList) {
//					UserGroupObvRuleData data = observationMapperHelper.getUGObvRuleData(obs);
//					CheckFilterRule checkFilterRule = new CheckFilterRule();
//					checkFilterRule.setUserGroupId(ugIds);
//					checkFilterRule.setUgObvFilterData(data);
//					intergratorService = headers.addIntergratorHeader(intergratorService, requestAuthHeader);
//					List<Long> filterUGId = intergratorService.checkUserGroupEligiblity(checkFilterRule);
//					if (filterUGId != null && !filterUGId.isEmpty()) {
//						list.add(observationMapperHelper.getUGFilterObvData(obs));
//					}
//
//				}
//
//			}

			if (!oservationIds.isEmpty()) {

				for (Long obs : oservationIds) {
					UserGroupObvFilterData ugFilterData = new UserGroupObvFilterData();
					ugFilterData.setObservationId(obs);
					list.add(ugFilterData);
				}

			}

			if (Boolean.TRUE.equals(selectAll)) {

				List<ShowDocument> documentList = new ArrayList<>();

				try {
//					MapResponse result = esService.search(index, type, geoAggregationField, geoAggegationPrecision,
//					onlyFilteredAggregation, termsAggregationField, geoShapeFilterField, mapSearchQuery);

					MapResponse result = esService.search(index, type, geoAggregationField, null, false, null,
							geoShapeFilterField, mapSearchQuery);

					List<MapDocument> documents = result.getDocuments();

					for (MapDocument document : documents) {
						try {
							documentList.add(objectMapper.readValue(String.valueOf(document), ShowDocument.class));
						} catch (IOException e) {
							logger.error(e.getMessage());
						}
					}

					documentList.forEach(item -> {
						UserGroupObvFilterData ugFilterData = new UserGroupObvFilterData();
						ugFilterData.setObservationId(item.getDocument().getId());
						list.add(ugFilterData);
					});

				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());

				}

//				for (MapDocument document : documents) {
//					ObservationListMinimalData data = objectMapper.readValue(String.valueOf(document.getDocument()),
//							ObservationListMinimalData.class);
//					UserGroupObvFilterData ugFilterData = new UserGroupObvFilterData();
//					ugFilterData.setObservationId(data.getObservationId());
//					ugFilterData.setCreatedOnDate(data.getCreatedOn());
//					ugFilterData.setLatitude(data.getLatitude());
//					ugFilterData.setLongitude(data.getLongitude());
//					ugFilterData.setObservedOnDate(data.getObservedOn() != null ? data.getObservedOn() : null);
//					ugFilterData.setAuthorId(data.getUser() != null ? data.getUser().getId() : null);
//					ugFilterData.setTaxonomyId(data.getRecoIbp() != null ? data.getRecoIbp().getTaxonId() : null);
//
//					UserGroupObvRuleData filterData = observationMapperHelper
//							.getUGObvRuleData(observationDao.findById(data.getObservationId()));
//					CheckFilterRule checkFilterRule = new CheckFilterRule();
//					checkFilterRule.setUserGroupId(ugIds);
//					checkFilterRule.setUgObvFilterData(filterData);
//					intergratorService = headers.addIntergratorHeader(intergratorService, requestAuthHeader);
//					List<Long> filterUGId = intergratorService.checkUserGroupEligiblity(checkFilterRule);
//					if (filterUGId != null && !filterUGId.isEmpty()) {
//						list.add(observationMapperHelper
//								.getUGFilterObvData(observationDao.findById(data.getObservationId())));
//					}
//
////					list.add(ugFilterData);
//				}

			}

			if (!list.isEmpty() && !bulkAction.isEmpty()
					&& (bulkAction.contains("ugBulkPosting") || bulkAction.contains("ugBulkUnPosting"))) {

				List<UserGroupObvFilterData> ugObsList = new ArrayList<UserGroupObvFilterData>();
				;
				Integer count = 0;

				while (count < list.size()) {
					ugObsList.add(list.get(count));

					if (ugObsList.size() >= 200) {
						bulkGroupAction(ugObsList, ugIds);
						ugObsList.clear();
					}
					count++;
				}

				bulkGroupAction(ugObsList, ugIds);
				ugObsList.clear();
			}

		} catch (Exception e) {
			logger.error(e.getMessage());

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
				ugBulkPostingData.setRecordType("document");
				ugBulkPostingData.setUgObvFilterDataList(ugObsList);
				ugBulkPostingData.setUserGroupList(ugIds);
			} else if (ugBulkUnPostingData != null) {
				ugBulkUnPostingData.setRecordType("document");
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
