package com.strandls.document.es.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.esmodule.pojo.MapAndBoolQuery;
import com.strandls.esmodule.pojo.MapAndMatchPhraseQuery;
import com.strandls.esmodule.pojo.MapAndRangeQuery;
import com.strandls.esmodule.pojo.MapExistQuery;
import com.strandls.esmodule.pojo.MapGeoPoint;
import com.strandls.esmodule.pojo.MapOrBoolQuery;
import com.strandls.esmodule.pojo.MapOrMatchPhraseQuery;
import com.strandls.esmodule.pojo.MapOrRangeQuery;
import com.strandls.esmodule.pojo.MapSearchParams;
import com.strandls.esmodule.pojo.MapSearchQuery;

/**
 * 
 * @author arun
 *
 */
public class ESUtility {
	private final Logger logger = LoggerFactory.getLogger(ESUtility.class);

	private List<Object> cSTSOT(String str) {
		if (str == null || str.isBlank())
			return new ArrayList<>();

		String[] y = str.split(",");
		Set<Object> strSet1 = Arrays.stream(y).filter(s -> !s.isBlank()).collect(Collectors.toSet());
		return new ArrayList<>(strSet1);
	}

	private MapAndBoolQuery assignBoolAndQuery(String key, List<Object> values) {
		MapAndBoolQuery andBool = new MapAndBoolQuery();
		andBool.setKey(key);
		andBool.setValues(values);
		return andBool;
	}

	private MapAndMatchPhraseQuery assignAndMatchPhrase(String key, String value) {
		MapAndMatchPhraseQuery andMatchPhrase = new MapAndMatchPhraseQuery();
		andMatchPhrase.setKey(key);
		andMatchPhrase.setValue(value);
		return andMatchPhrase;
	}

	private MapOrMatchPhraseQuery assignOrMatchPhrase(String key, String value) {
		MapOrMatchPhraseQuery orMatchPhrase = new MapOrMatchPhraseQuery();
		orMatchPhrase.setKey(key);
		orMatchPhrase.setValue(value);
		return orMatchPhrase;
	}

	private MapAndRangeQuery assignAndRange(String key, Object start, Object end, String path) {
		MapAndRangeQuery andRange = new MapAndRangeQuery();
		andRange.setKey(key);
		andRange.setStart(start);
		andRange.setEnd(end);
		andRange.setPath(path);
		return andRange;
	}

	private void assignOrMatchPhraseArray(String ids, String key, List<MapOrMatchPhraseQuery> queries) {
		if (ids == null || ids.isBlank())
			return;
		String[] list = ids.split(",");
		for (String o : list) {
			queries.add(assignOrMatchPhrase(key, o.trim()));
		}
	}

	public List<MapGeoPoint> polygonGenerator(String locationArray) {
		List<MapGeoPoint> polygon = new ArrayList<>();
		try {
			double[] points = Stream.of(locationArray.split(",")).mapToDouble(Double::parseDouble).toArray();
			for (int i = 0; i < points.length; i = i + 2) {
				MapGeoPoint geoPoint = new MapGeoPoint();
				geoPoint.setLat(points[i + 1]);
				geoPoint.setLon(points[i]);
				polygon.add(geoPoint);
			}
		} catch (Exception e) {
			logger.error("Error generating polygon: {}", e.getMessage());
		}
		return polygon;
	}

	public List<List<MapGeoPoint>> multiPolygonGenerator(String[] locationArray) {
		List<List<MapGeoPoint>> multipolygon = new ArrayList<>();
		for (String loc : locationArray) {
			multipolygon.add(polygonGenerator(loc));
		}
		return multipolygon;
	}

	public MapSearchQuery getMapSearchQuery(String sGroup, String habitatIds, String tags, String user, String flags,
			String createdOnMaxDate, String createdOnMinDate, String featured, String userGroupList, String isFlagged,
			String revisedOnMinDate, String revisedOnMaxDate, String state, String itemType, String year, String author,
			String publisher, String title, MapSearchParams mapSearchParams) {

		MapSearchQuery mapSearchQuery = new MapSearchQuery();
		List<MapAndBoolQuery> boolAndLists = new ArrayList<>();
		List<MapOrBoolQuery> boolOrLists = new ArrayList<>();
		List<MapOrRangeQuery> rangeOrLists = new ArrayList<>();
		List<MapAndRangeQuery> rangeAndLists = new ArrayList<>();
		List<MapExistQuery> andMapExistQueries = new ArrayList<>();
		List<MapAndMatchPhraseQuery> andMatchPhraseQueries = new ArrayList<>();
		List<MapOrMatchPhraseQuery> orMatchPhraseQueriesnew = new ArrayList<>();

		try {
//			tags
			List<Object> tagsList = cSTSOT(tags);
			if (!tagsList.isEmpty()) {
				List<Object> lowerCaseTags = tagsList.stream().map(o -> o.toString().toLowerCase())
						.collect(Collectors.toList());
				boolAndLists.add(assignBoolAndQuery(DocumentIndex.TAGS.getValue(), lowerCaseTags));
			}

//			userGroupList
			List<Object> ugList = cSTSOT(userGroupList);
			if (!ugList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(DocumentIndex.USERGROUPID.getValue(), ugList));
			}

//			speciesGroupList
			if (sGroup != null && sGroup.length() >= 1) {
				assignOrMatchPhraseArray(sGroup, DocumentIndex.SGROUP.getValue(), orMatchPhraseQueriesnew);
			}
//			habitatId List
			if (habitatIds != null && habitatIds.length() >= 1) {
				assignOrMatchPhraseArray(habitatIds, DocumentIndex.HABITATIDS.getValue(), orMatchPhraseQueriesnew);
			}

			// Featured & Flags
			addSimpleBool(boolAndLists, DocumentIndex.FEATURED.getValue(), featured);
			addSimpleBool(boolAndLists, DocumentIndex.FLAG.getValue(), flags);

//			user
			List<Object> authorId = cSTSOT(user);
			if (!authorId.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(DocumentIndex.USER.getValue(), authorId));
			}

//			Data Quality:- Flagged
			List<Object> flaggedList = cSTSOT(isFlagged);
			if (!flaggedList.isEmpty() && flaggedList.size() < 2) {
				String first = (String) flaggedList.get(0);
				if (first.equalsIgnoreCase("1")) {
					rangeAndLists.add(assignAndRange(DocumentIndex.FLAGCOUNT.getValue(), 1, null, null));
				} else if (first.equalsIgnoreCase("0")) {
					rangeAndLists.add(assignAndRange(DocumentIndex.FLAGCOUNT.getValue(), 0, 0, null));
				}
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today = sdf.format(new Date());

			// Created On
			handleDateRange(rangeAndLists, DocumentIndex.CREATEDON.getValue(), createdOnMinDate, createdOnMaxDate,
					today);

			// Revised On
			handleDateRange(rangeAndLists, DocumentIndex.LASTREVISED.getValue(), revisedOnMinDate, revisedOnMaxDate,
					today);

			// State (Normalization)
			List<Object> stateList = cSTSOT(state);
			if (!stateList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(DocumentIndex.STATE.getValue(), stateList));
			}

			// Title, Year, Publisher, Author, ItemType (Match Phrases)
			addAndMatchPhrase(andMatchPhraseQueries, DocumentIndex.TITLE.getValue(), title);
			addAndMatchPhrase(andMatchPhraseQueries, DocumentIndex.YEAROFPUBLICATION.getValue(), year);
			addOrMatchPhrase(orMatchPhraseQueriesnew, DocumentIndex.PUBLISHER.getValue(), publisher);
			addOrMatchPhrase(orMatchPhraseQueriesnew, DocumentIndex.AUTHOR.getValue(), author);
			addOrMatchPhrase(orMatchPhraseQueriesnew, DocumentIndex.ITEMTYPE.getValue(), itemType);

			// Finalize
			mapSearchQuery.setAndBoolQueries(boolAndLists);
			mapSearchQuery.setOrBoolQueries(boolOrLists);
			mapSearchQuery.setAndRangeQueries(rangeAndLists);
			mapSearchQuery.setOrRangeQueries(rangeOrLists);
			mapSearchQuery.setAndExistQueries(andMapExistQueries);
			mapSearchQuery.setAndMatchPhraseQueries(andMatchPhraseQueries);
			mapSearchQuery.setOrMatchPhraseQueries(orMatchPhraseQueriesnew);

		} catch (Exception e) {
			logger.error("Error in getMapSearchQuery: ", e);
		}
		mapSearchQuery.setSearchParams(mapSearchParams);
		return mapSearchQuery;
	}

	// Helper Methods for Cleaner Logic
	private void addSimpleBool(List<MapAndBoolQuery> list, String key, String rawValues) {
		List<Object> values = cSTSOT(rawValues);
		if (!values.isEmpty()) {
			list.add(assignBoolAndQuery(key, values));
		}
	}

	private void addAndMatchPhrase(List<MapAndMatchPhraseQuery> list, String key, String rawValues) {
		List<Object> values = cSTSOT(rawValues);
		for (Object o : values) {
			list.add(assignAndMatchPhrase(key, o.toString().toLowerCase()));
		}
	}

	private void addOrMatchPhrase(List<MapOrMatchPhraseQuery> list, String key, String rawValues) {
		List<Object> values = cSTSOT(rawValues);
		for (Object o : values) {
			list.add(assignOrMatchPhrase(key, o.toString().toLowerCase()));
		}
	}

	private void handleDateRange(List<MapAndRangeQuery> list, String key, String min, String max, String fallback) {
		if (min != null || max != null) {
			list.add(assignAndRange(key, (min != null ? min : fallback), (max != null ? max : fallback), null));
		}
	}
}