package com.strandls.document.service;

import javax.servlet.http.HttpServletRequest;

import com.strandls.document.pojo.DocumentListData;
import com.strandls.document.pojo.MapAggregationResponse;
import com.strandls.esmodule.pojo.MapSearchParams;
import com.strandls.esmodule.pojo.MapSearchQuery;

public interface DocumentListService {

	public DocumentListData getDocumentList(String index, String type, String geoAggregationField,
			String geoShapeFilterField, String nestedField, MapAggregationResponse aggregationResult,
			MapSearchQuery querys);

	public MapAggregationResponse mapAggregate(String index, String type, String sGroup, String habitatIds, String tags,
			String user, String flags, String createdOnMaxDate, String createdOnMinDate, String featured,
			String userGroupList, String isFlagged, String revisedOnMaxDate, String revisedOnMinDate, String state,
			String itemType, String year, String author, String publisher, String title, String geoShapeFilterField,
			MapSearchParams mapSearchParams);

	public void bulkAction(Boolean selectAll, String bulkAction, String bulkDocumentsIds, String bulkUsergroupIds,
			MapSearchQuery mapSearchQuery, String index, String type, HttpServletRequest request);

}
