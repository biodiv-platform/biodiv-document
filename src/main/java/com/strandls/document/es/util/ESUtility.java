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
 * @author vishnu
 *
 */

public class ESUtility {
	private Logger logger = LoggerFactory.getLogger(ESUtility.class);

	private List<Object> cSTSOT(String str) {
		if (str == null || str.matches("") || str.isEmpty())
			return new ArrayList<Object>();

		String[] y = str.split(",");
		Set<Object> strSet1 = Arrays.stream(y).collect(Collectors.toSet());
		List<Object> strList = new ArrayList<Object>();
		strList.addAll(strSet1);
		return strList;

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

	// for comma separated string ids
	private void assignOrMatchPhraseArray(String ids, String key, List<MapOrMatchPhraseQuery> orMatchPhraseQueriesnew) {
		String[] list = ids.split(",");
		for (String o : list) {
			orMatchPhraseQueriesnew.add(assignOrMatchPhrase(key, o));
		}
	}

	public List<MapGeoPoint> polygonGenerator(String locationArray) {
		List<MapGeoPoint> polygon = new ArrayList<MapGeoPoint>();
		double[] point = Stream.of(locationArray.split(",")).mapToDouble(Double::parseDouble).toArray();
		for (int i = 0; i < point.length; i = i + 2) {
			String singlePoint = point[i + 1] + "," + point[i];
			int comma = singlePoint.indexOf(',');
			if (comma != -1) {
				MapGeoPoint geoPoint = new MapGeoPoint();
				geoPoint.setLat(Double.parseDouble(singlePoint.substring(0, comma).trim()));
				geoPoint.setLon(Double.parseDouble(singlePoint.substring(comma + 1).trim()));
				polygon.add(geoPoint);
			}
		}
		return polygon;
	}

	public List<List<MapGeoPoint>> multiPolygonGenerator(String[] locationArray) {
		List<List<MapGeoPoint>> mutlipolygon = new ArrayList<>();
		for (int j = 0; j < locationArray.length; j++) {
			mutlipolygon.add(polygonGenerator(locationArray[j]));
		}
		return mutlipolygon;
	}

	public MapSearchQuery getMapSearchQuery(String sGroup, String habitatIds, String tags, String user, String flags,
			String createdOnMaxDate, String createdOnMinDate, String featured, String userGroupList, String isFlagged,
			String revisedOnMinDate, String revisedOnMaxDate, String state, String itemType, String year, String author,
			String publisher, String title, MapSearchParams mapSearchParams) {

		MapSearchQuery mapSearchQuery = new MapSearchQuery();
		List<MapAndBoolQuery> boolAndLists = new ArrayList<MapAndBoolQuery>();
		List<MapOrBoolQuery> boolOrLists = new ArrayList<MapOrBoolQuery>();
		List<MapOrRangeQuery> rangeOrLists = new ArrayList<MapOrRangeQuery>();
		List<MapAndRangeQuery> rangeAndLists = new ArrayList<MapAndRangeQuery>();
		List<MapExistQuery> andMapExistQueries = new ArrayList<MapExistQuery>();
		List<MapAndMatchPhraseQuery> andMatchPhraseQueries = new ArrayList<MapAndMatchPhraseQuery>();
		List<MapOrMatchPhraseQuery> orMatchPhraseQueriesnew = new ArrayList<MapOrMatchPhraseQuery>();

		try {

//			tags
			List<Object> tagsList = cSTSOT(tags);
			if (!tagsList.isEmpty()) {
				List<Object> lowerCaseList = new ArrayList<Object>();
				for (Object o : tagsList) {
					String result = o.toString().toLowerCase();
					lowerCaseList.add(result);
				}
				boolAndLists.add(assignBoolAndQuery(DocumentIndex.TAGS.getValue(), lowerCaseList));
			}

//			userGroupList
			List<Object> ugList = cSTSOT(userGroupList);
			if (!ugList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(DocumentIndex.USERGROUPID.getValue(), ugList));
			}

//			speciesGroupList
			if (sGroup.length() >= 1) {
				assignOrMatchPhraseArray(sGroup, DocumentIndex.SGROUP.getValue(), orMatchPhraseQueriesnew);
			}
//			habitatId List
			if (habitatIds.length() >= 1) {
				assignOrMatchPhraseArray(habitatIds, DocumentIndex.HABITATIDS.getValue(), orMatchPhraseQueriesnew);
			}
//			tags
			List<Object> tagList = cSTSOT(tags);
			if (!tagList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(DocumentIndex.TAGS.getValue(), tagList));
			}

//			featured
			List<Object> featuredList = cSTSOT(featured);
			if (!featuredList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(DocumentIndex.FEATURED.getValue(), featuredList));
			}

//			flags
			List<Object> flagList = cSTSOT(flags);
			if (!flagList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(DocumentIndex.FLAG.getValue(), flagList));
			}
//			user
			List<Object> authorId = cSTSOT(user);
			if (!authorId.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(DocumentIndex.USER.getValue(), authorId));
			}

//			Data Quality:- Flagged
			List<Object> flagged = cSTSOT(isFlagged);
			if (!flagged.isEmpty()) {

				if (flagged.size() < 2) {
					String first = (String) flagged.toArray()[0];
					if (first.equalsIgnoreCase("1")) {
						rangeAndLists
								.add(assignAndRange(DocumentIndex.FLAGCOUNT.getValue(), first, Long.MAX_VALUE, null));
					}
					if (first.equalsIgnoreCase("0")) {
						rangeAndLists.add(assignAndRange(DocumentIndex.FLAGCOUNT.getValue(), first, first, null));
					}

				}
			}

//			Created on
			String createdOnMaxDateValue = null;
			String createdOnMinDateValue = null;
			Date date = new Date();
			SimpleDateFormat out = new SimpleDateFormat("YYYY-MM-dd");
			try {
				if (createdOnMinDate != null) {
					createdOnMinDateValue = createdOnMinDate;
				}
				if (createdOnMaxDate != null) {
					createdOnMaxDateValue = createdOnMaxDate;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			if (createdOnMinDateValue != null && createdOnMaxDateValue != null) {

				rangeAndLists.add(assignAndRange(DocumentIndex.CREATEDON.getValue(), createdOnMinDateValue,
						createdOnMaxDateValue, null));
			}
			if (createdOnMinDateValue != null && createdOnMaxDateValue == null) {
				rangeAndLists.add(assignAndRange(DocumentIndex.CREATEDON.getValue(), createdOnMinDateValue,
						out.format(date), null));
			}
			if (createdOnMinDateValue == null && createdOnMaxDateValue != null) {
				rangeAndLists.add(assignAndRange(DocumentIndex.CREATEDON.getValue(), out.format(date),
						createdOnMaxDateValue, null));
			}

//			revised on

			String revisedOnMaxDateValue = null;
			String revisedOnMinDateValue = null;

			try {
				if (revisedOnMinDate != null) {
					revisedOnMinDateValue = revisedOnMinDate;
				}
				if (revisedOnMaxDate != null) {
					revisedOnMaxDateValue = revisedOnMaxDate;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			if (revisedOnMinDateValue != null && revisedOnMaxDateValue != null) {

				rangeAndLists.add(assignAndRange(DocumentIndex.LASTREVISED.getValue(), revisedOnMaxDateValue,
						revisedOnMinDateValue, null));
			}
			if (revisedOnMinDateValue != null && revisedOnMaxDateValue == null) {
				rangeAndLists.add(assignAndRange(DocumentIndex.LASTREVISED.getValue(), out.format(date),
						revisedOnMinDateValue, null));
			}
			if (revisedOnMinDateValue == null && revisedOnMaxDateValue != null) {
				rangeAndLists.add(assignAndRange(DocumentIndex.LASTREVISED.getValue(), revisedOnMaxDateValue,
						out.format(date), null));
			}

//			state
			List<Object> stateList = cSTSOT(state);
			if (!stateList.isEmpty()) {
				List<Object> lowerCaseList = new ArrayList<Object>();
				for (Object o : stateList) {
					String result = o.toString();
					lowerCaseList.add(result);
				}

				boolAndLists.add(assignBoolAndQuery(DocumentIndex.STATE.getValue(), lowerCaseList));
			}

			// title
			List<Object> titleList = cSTSOT(title);
			if (!titleList.isEmpty()) {
				for (Object o : titleList) {
					String result = o.toString().toLowerCase();
					andMatchPhraseQueries.add(assignAndMatchPhrase(DocumentIndex.TITLE.getValue(), result));
				}
			}

			List<Object> yearList = cSTSOT(year);
			if (!yearList.isEmpty()) {
				for (Object o : yearList) {
					String result = o.toString().toLowerCase();
					andMatchPhraseQueries.add(assignAndMatchPhrase(DocumentIndex.YEAROFPUBLICATION.getValue(), result));
				}
			}

			List<Object> publisherList = cSTSOT(publisher);
			if (!publisherList.isEmpty()) {
				for (Object o : publisherList) {
					String result = o.toString().toLowerCase();
					orMatchPhraseQueriesnew.add(assignOrMatchPhrase(DocumentIndex.PUBLISHER.getValue(), result));
				}
			}

			List<Object> authorList = cSTSOT(author);
			if (!authorList.isEmpty()) {
				for (Object o : authorList) {
					String result = o.toString().toLowerCase();
					orMatchPhraseQueriesnew.add(assignOrMatchPhrase(DocumentIndex.AUTHOR.getValue(), result));
				}
			}

			List<Object> itemTypeList = cSTSOT(itemType);
			if (!itemTypeList.isEmpty()) {
				for (Object o : itemTypeList) {
					String result = o.toString().toLowerCase();
					orMatchPhraseQueriesnew.add(assignOrMatchPhrase(DocumentIndex.ITEMTYPE.getValue(), result));
				}
			}

			/**
			 * combine all the queries
			 * 
			 */
			mapSearchQuery.setAndBoolQueries(boolAndLists);
			mapSearchQuery.setOrBoolQueries(boolOrLists);
			mapSearchQuery.setAndRangeQueries(rangeAndLists);
			mapSearchQuery.setOrRangeQueries(rangeOrLists);
			mapSearchQuery.setAndExistQueries(andMapExistQueries);
			mapSearchQuery.setAndMatchPhraseQueries(andMatchPhraseQueries);
			mapSearchQuery.setOrMatchPhraseQueries(orMatchPhraseQueriesnew);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		mapSearchQuery.setSearchParams(mapSearchParams);
		return mapSearchQuery;

	}
}
