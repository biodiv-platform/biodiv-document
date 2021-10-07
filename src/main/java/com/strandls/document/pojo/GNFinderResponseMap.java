/**
 * 
 */
package com.strandls.document.pojo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GNFinderResponseMap {
	@JsonProperty(value = "metadata")
	private Metadata metadata;
	@JsonProperty(value = "names")
	private List<GnFinderResponseNames> names;

	public GNFinderResponseMap() {
		super();
	}

	public GNFinderResponseMap(Metadata metadata, List<GnFinderResponseNames> names) {
		this.metadata = metadata;
		this.names = names;
	}

	public void setNames(List<GnFinderResponseNames> names) {
		this.names = names;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public List<GnFinderResponseNames> getNames() {
		return names;
	}

}

@JsonIgnoreProperties(ignoreUnknown = true)
class Metadata {
	Date date;
	String language;
	Long totalWords;
	Long totalCandidates;
	Long totalNames;

	public Metadata() {
		super();
	}

	public Metadata(Date date, String language, Long totalWords, Long totalCandidates, Long totalNames) {
		this.date = date;
		this.language = language;
		this.totalWords = totalWords;
		this.totalCandidates = totalCandidates;
		this.totalNames = totalNames;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Long getTotalWords() {
		return totalWords;
	}

	public void setTotalWords(Long totalWords) {
		this.totalWords = totalWords;
	}

	public Long getTotalCandidates() {
		return totalCandidates;
	}

	public void setTotalCandidates(Long totalCandidates) {
		this.totalCandidates = totalCandidates;
	}

	public Long getTotalNames() {
		return totalNames;
	}

	public void setTotalNames(Long totalNames) {
		this.totalNames = totalNames;
	}

}
