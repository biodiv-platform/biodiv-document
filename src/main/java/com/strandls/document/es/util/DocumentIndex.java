package com.strandls.document.es.util;

public enum DocumentIndex {

	INDEX("document"), TYPE("document_records"), SGROUP("speciesGroupIds"), USER("document.authorId"),
	FEATURED("featured.id"), FLAGCOUNT("document.flagCount"), FLAG("flag.id"), USERGROUPID("userGroupIbp.id"),
	CREATEDON("document.createdOn"), LASTREVISED("document.lastRevised"), STATE("documentCoverages.placename.keyword"),
	TAGS("tags.name.raw"), HABITATIDS("habitatIds"), ITEMTYPE("document.itemtype"), // all below match_phrase_prefix
	YEAROFPUBLICATION("document.year"), //
	AUTHOR("document.author"), //
	PUBLISHER("document.publisher"), //
	TITLE("document.title"),//
	;

	private String field;

	private DocumentIndex(String field) {
		this.field = field;
	}

	public String getValue() {
		return field;
	}
}
