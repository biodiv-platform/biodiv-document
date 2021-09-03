package com.strandls.document.service.Impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.strandls.document.es.util.DocumentIndex;
import com.strandls.document.es.util.ESUtility;
import com.strandls.document.pojo.DocumentListData;
import com.strandls.document.pojo.DocumentMappingList;
import com.strandls.document.pojo.MapAggregationResponse;
import com.strandls.document.service.DocumentListService;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.AggregationResponse;
import com.strandls.esmodule.pojo.MapDocument;
import com.strandls.esmodule.pojo.MapResponse;
import com.strandls.esmodule.pojo.MapSearchParams;
import com.strandls.esmodule.pojo.MapSearchQuery;

public class DocumentListServiceImpl implements DocumentListService {

	private final Logger logger = LoggerFactory.getLogger(DocumentListServiceImpl.class);

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private EsServicesApi esService;

	@Inject
	private ESUtility esUtility;

	@Override
	public DocumentListData getDocumentList(String index, String type, String geoAggregationField,
			String geoShapeFilterField, String nestedField, MapAggregationResponse aggregationResult,
			MapSearchQuery querys) {

		DocumentListData listData = null;

		try {
			MapResponse result = esService.search(index, type, geoAggregationField, null, false, null,
					geoShapeFilterField, querys);
			List<MapDocument> documents = result.getDocuments();
			Long totalCount = result.getTotalDocuments();
			List<DocumentMappingList> DocumentList = new ArrayList<DocumentMappingList>();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			objectMapper.setDateFormat(df);
			for (MapDocument document : documents) {
				JsonNode rootNode = objectMapper.readTree(document.getDocument().toString());
				((ObjectNode) rootNode).replace("documentCoverages", null);
				try {

					DocumentList.add(objectMapper.readValue(String.valueOf(rootNode), DocumentMappingList.class));
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}

			listData = new DocumentListData(DocumentList, aggregationResult, totalCount);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return listData;
	}

	private void getAggregateLatch(String index, String type, String filter, String geoAggregationField,
			MapSearchQuery searchQuery, Map<String, AggregationResponse> mapResponse, CountDownLatch latch,
			String geoShapeFilterField,String namedAgg) {

		LatchThreadWorker worker = new LatchThreadWorker(index, type, filter, geoAggregationField, searchQuery,
				mapResponse, namedAgg, latch, geoShapeFilterField,esService);
		worker.start();

	}

	@Override
	public MapAggregationResponse mapAggregate(String index, String type, String sGroup, String habitatIds, String tags,
			String user, String flags, String createdOnMaxDate, String createdOnMinDate, String featured,
			String userGroupList, String isFlagged, String revisedOnMaxDate, String revisedOnMinDate, String state,
			String itemType, String year, String author, String publisher, String title,
			String geoShapeFilterField,MapSearchParams mapSearchParams) {

		MapSearchQuery mapSearchQuery = esUtility.getMapSearchQuery(sGroup, habitatIds, tags, user, flags,
				createdOnMaxDate, createdOnMinDate, featured, userGroupList, isFlagged, revisedOnMaxDate,
				revisedOnMinDate, state, itemType, year, author, publisher, title, mapSearchParams);

		MapSearchQuery mapSearchQueryFilter;

		String omiter = null;
		MapAggregationResponse aggregationResponse = new MapAggregationResponse();

		Map<String, AggregationResponse> mapAggResponse = new HashMap<String, AggregationResponse>();

//		filter panel data

//      number refers to total field to aggregate
		int totalLatch = 5;

//		latch count down
		CountDownLatch latch = new CountDownLatch(totalLatch);

		if (sGroup != null && !sGroup.isEmpty()) {

			mapSearchQueryFilter = esUtility.getMapSearchQuery(omiter, habitatIds, tags, user, flags, createdOnMaxDate,
					createdOnMinDate, featured, userGroupList, isFlagged, revisedOnMaxDate, revisedOnMinDate, state,
					itemType, year, author, publisher, title, mapSearchParams);

			getAggregateLatch(index, type, DocumentIndex.SGROUP.getValue(), null, mapSearchQueryFilter, mapAggResponse,
					latch, geoShapeFilterField,null);

		} else {
			getAggregateLatch(index, type, DocumentIndex.SGROUP.getValue(), null, mapSearchQuery, mapAggResponse, latch,
					geoShapeFilterField,null);
		}

		if (state != null && !state.isEmpty()) {

			mapSearchQueryFilter = esUtility.getMapSearchQuery(sGroup, habitatIds, tags, user, flags, createdOnMaxDate,
					createdOnMinDate, featured, userGroupList, isFlagged, revisedOnMaxDate, revisedOnMinDate, omiter,
					itemType, year, author, publisher, title, mapSearchParams);

			getAggregateLatch(index, type, DocumentIndex.STATE.getValue(), null, mapSearchQueryFilter, mapAggResponse,
					latch, geoShapeFilterField,null);

		} else {
			getAggregateLatch(index, type, DocumentIndex.STATE.getValue(), null, mapSearchQuery, mapAggResponse, latch,
					geoShapeFilterField,null);
		}

		if (itemType != null && !itemType.isEmpty()) {

			mapSearchQueryFilter = esUtility.getMapSearchQuery(sGroup, habitatIds, tags, user, flags, createdOnMaxDate,
					createdOnMinDate, featured, userGroupList, isFlagged, revisedOnMaxDate, revisedOnMinDate, state,
					omiter, year, author, publisher, title, mapSearchParams);

			getAggregateLatch(index, type, "document.itemtype.raw", null, mapSearchQueryFilter, mapAggResponse, latch,
					geoShapeFilterField,null);

		} else {
			getAggregateLatch(index, type, "document.itemtype.raw", null, mapSearchQuery, mapAggResponse, latch, geoShapeFilterField,null);
		}
		
		if (userGroupList != null && !userGroupList.isEmpty()) {

			mapSearchQueryFilter = esUtility.getMapSearchQuery(sGroup, habitatIds, tags, user, flags, createdOnMaxDate,
					createdOnMinDate, featured, omiter, isFlagged, revisedOnMaxDate, revisedOnMinDate, state,
					omiter, year, author, publisher, title, mapSearchParams);

			getAggregateLatch(index, type, DocumentIndex.USERGROUPID.getValue(), null, mapSearchQueryFilter, mapAggResponse, latch,
					geoShapeFilterField,null);

		} else {
			getAggregateLatch(index, type, DocumentIndex.USERGROUPID.getValue(), null, mapSearchQuery, mapAggResponse, latch, geoShapeFilterField,null);
		}

		if (year != null && !year.isEmpty()) {

			mapSearchQueryFilter = esUtility.getMapSearchQuery(sGroup, habitatIds, tags, user, flags, createdOnMaxDate,
					createdOnMinDate, featured, userGroupList, isFlagged, revisedOnMaxDate, revisedOnMinDate, state,
					itemType, omiter, author, publisher, title, mapSearchParams);

			getAggregateLatch(index, type, "document.year.keyword", null, mapSearchQueryFilter, mapAggResponse, latch,
					geoShapeFilterField,null);

		} else {
			getAggregateLatch(index, type, "document.year.keyword", null, mapSearchQuery, mapAggResponse, latch,geoShapeFilterField,null);
		}

		try {
			latch.await();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		aggregationResponse
				.setGroupSpeciesName(mapAggResponse.get(DocumentIndex.SGROUP.getValue()).getGroupAggregation());

		aggregationResponse.setGroupState(mapAggResponse.get(DocumentIndex.STATE.getValue()).getGroupAggregation());

		
		aggregationResponse.setGroupUserGroupName(mapAggResponse.get(DocumentIndex.USERGROUPID.getValue()).getGroupAggregation());
		
		aggregationResponse.setGroupTypeOfDocument(mapAggResponse.get("document.itemtype.raw").getGroupAggregation());

		aggregationResponse
				.setGroupYearofPublication(mapAggResponse.get("document.year.keyword").getGroupAggregation());

		return aggregationResponse;
	}
}
