/**
 * 
 */
package com.strandls.document.service.Impl;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.activity.controller.ActivitySerivceApi;
import com.strandls.activity.pojo.DocumentActivityLogging;
import com.strandls.activity.pojo.MailData;
import com.strandls.document.Headers;

/**
 * @author Abhishek Rudra
 *
 */
public class LogActivities {

	private final Logger logger = LoggerFactory.getLogger(LogActivities.class);

	@Inject
	private ActivitySerivceApi activityService;

	@Inject
	private Headers headers;

	public void LogDocumentActivities(String authHeader, String activityDescription, Long rootObjectId,
			Long subRootObjectId, String rootObjectType, Long activityId, String activityType, MailData mailData) {
		try {

			DocumentActivityLogging loggingData = new DocumentActivityLogging();
			loggingData.setActivityDescription(activityDescription);
			loggingData.setActivityId(activityId);
			loggingData.setActivityType(activityType);
			loggingData.setRootObjectId(rootObjectId);
			loggingData.setRootObjectType(rootObjectType);
			loggingData.setSubRootObjectId(subRootObjectId);
			loggingData.setMailData(mailData);

			activityService = headers.addActivityHeaders(activityService, authHeader);
			activityService.logDocumentActivity(loggingData);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

}
