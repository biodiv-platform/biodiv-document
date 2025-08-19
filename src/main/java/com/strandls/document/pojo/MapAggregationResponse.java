package com.strandls.document.pojo;

import java.util.Map;

public class MapAggregationResponse {

	private Map<String, Long> groupSpeciesName;
	private Map<String, Long> groupState;
	private Map<String, Long> groupYearofPublication;
	private Map<String, Long> groupTypeOfDocument;
	private Map<String, Long> groupUserGroupName;

	public Map<String, Long> getGroupSpeciesName() {
		return groupSpeciesName;
	}

	public void setGroupSpeciesName(Map<String, Long> groupSpeciesName) {
		this.groupSpeciesName = groupSpeciesName;
	}

	public Map<String, Long> getGroupState() {
		return groupState;
	}

	public void setGroupState(Map<String, Long> groupState) {
		this.groupState = groupState;
	}

	public Map<String, Long> getGroupYearofPublication() {
		return groupYearofPublication;
	}

	public void setGroupYearofPublication(Map<String, Long> groupYearofPublication) {
		this.groupYearofPublication = groupYearofPublication;
	}

	public Map<String, Long> getGroupTypeOfDocument() {
		return groupTypeOfDocument;
	}

	public void setGroupTypeOfDocument(Map<String, Long> groupTypeOfDocument) {
		this.groupTypeOfDocument = groupTypeOfDocument;
	}

	public Map<String, Long> getGroupUserGroupName() {
		return groupUserGroupName;
	}

	public void setGroupUserGroupName(Map<String, Long> groupUserGroupName) {
		this.groupUserGroupName = groupUserGroupName;
	}

}
