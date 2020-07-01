/**
 * 
 */
package com.strandls.document.service.Impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.document.service.DocumentService;

/**
 * @author Abhishek Rudra
 *
 */
public class DocumentServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DocumentService.class).to(DocumentServiceImpl.class).in(Scopes.SINGLETON);
	}
}