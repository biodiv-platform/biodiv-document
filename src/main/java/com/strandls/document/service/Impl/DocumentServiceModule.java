/**
 * 
 */
package com.strandls.document.service.Impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.document.service.DocumentListService;
import com.strandls.document.service.DocumentService;

/**
 * @author Abhishek Rudra
 *
 */
public class DocumentServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DocumentService.class).to(DocumentServiceImpl.class).in(Scopes.SINGLETON);
		bind(DocumentListService.class).to(DocumentListServiceImpl.class).in(Scopes.SINGLETON);
		bind(MailMetaDataConverter.class).in(Scopes.SINGLETON);
		bind(DocumentHelper.class).in(Scopes.SINGLETON);
		bind(LogActivities.class).in(Scopes.SINGLETON);
	}
}
