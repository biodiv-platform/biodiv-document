/**
 *
 */
package com.strandls.document;

import com.strandls.activity.controller.ActivityServiceApi;
import com.strandls.file.api.UploadApi;
import com.strandls.resource.controllers.ResourceServicesApi;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.userGroup.controller.UserGroupServiceApi;
import com.strandls.utility.controller.UtilityServiceApi;

import jakarta.ws.rs.core.HttpHeaders;

/**
 * @author Abhishek Rudra
 *
 */
public class Headers {

	public ActivityServiceApi addActivityHeaders(ActivityServiceApi activityService, String authHeader) {
		activityService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return activityService;
	}

	public ResourceServicesApi addResourceHeaders(ResourceServicesApi resourceService, String authHeader) {
		resourceService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return resourceService;
	}

	public UserGroupServiceApi addUserGroupHeader(UserGroupServiceApi ugService, String authHeader) {
		ugService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return ugService;
	}

	public UtilityServiceApi addUtilityHeaders(UtilityServiceApi utilityServices, String authHeader) {
		utilityServices.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return utilityServices;
	}

	public UserServiceApi addUserHeaders(UserServiceApi userService, String authHeader) {
		userService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return userService;
	}

	public UploadApi addFileUploadHeader(UploadApi uploadService, String authHeader) {
		uploadService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return uploadService;
	}

}
