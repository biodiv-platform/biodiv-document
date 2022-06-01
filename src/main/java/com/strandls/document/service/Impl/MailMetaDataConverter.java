package com.strandls.document.service.Impl;

import java.util.ArrayList;
import java.util.List;

import com.strandls.activity.pojo.UserGroupMailData;
import com.strandls.utility.pojo.DocumentMailData;

public class MailMetaDataConverter {

	public com.strandls.userGroup.pojo.MailData userGroupMetadata(com.strandls.activity.pojo.MailData mailData) {
		com.strandls.userGroup.pojo.MailData metaData = new com.strandls.userGroup.pojo.MailData();
		com.strandls.userGroup.pojo.DocumentMailData documentMailData = new com.strandls.userGroup.pojo.DocumentMailData();
		documentMailData.setAuthorId(mailData.getDocumentMailData().getAuthorId());
		documentMailData.setCreatedOn(mailData.getDocumentMailData().getCreatedOn());
		documentMailData.setDocumentId(mailData.getDocumentMailData().getDocumentId());
		documentMailData.setTitle(mailData.getDocumentMailData().getTitle());

		List<com.strandls.userGroup.pojo.UserGroupMailData> userGroupMeta = new ArrayList<com.strandls.userGroup.pojo.UserGroupMailData>();
		for (UserGroupMailData userGroupData : mailData.getUserGroupData()) {
			com.strandls.userGroup.pojo.UserGroupMailData ugMeta = new com.strandls.userGroup.pojo.UserGroupMailData();
			ugMeta.setIcon(userGroupData.getIcon());
			ugMeta.setId(userGroupData.getId());
			ugMeta.setName(userGroupData.getName());
			ugMeta.setWebAddress(userGroupData.getWebAddress());
			userGroupMeta.add(ugMeta);
		}
		metaData.setDocumentMailData(documentMailData);
		metaData.setUserGroupData(userGroupMeta);
		return metaData;

	}

	public com.strandls.utility.pojo.MailData utilityMetaData(com.strandls.activity.pojo.MailData mailData) {
		com.strandls.utility.pojo.MailData metaData = new com.strandls.utility.pojo.MailData();

		DocumentMailData documentMailData = new DocumentMailData();
		documentMailData.setAuthorId(mailData.getDocumentMailData().getAuthorId());
		documentMailData.setCreatedOn(mailData.getDocumentMailData().getCreatedOn());
		documentMailData.setDocumentId(mailData.getDocumentMailData().getDocumentId());
		documentMailData.setTitle(mailData.getDocumentMailData().getTitle());


		List<com.strandls.utility.pojo.UserGroupMailData> userGroupMeta = new ArrayList<com.strandls.utility.pojo.UserGroupMailData>();
		for (UserGroupMailData userGroupData : mailData.getUserGroupData()) {
			com.strandls.utility.pojo.UserGroupMailData ugMeta = new com.strandls.utility.pojo.UserGroupMailData();
			ugMeta.setIcon(userGroupData.getIcon());
			ugMeta.setId(userGroupData.getId());
			ugMeta.setName(userGroupData.getName());
			ugMeta.setWebAddress(userGroupData.getWebAddress());
			userGroupMeta.add(ugMeta);
		}
		metaData.setDocumentMailData(documentMailData);
		metaData.setUserGroupData(userGroupMeta);
		return metaData;
	}

}
