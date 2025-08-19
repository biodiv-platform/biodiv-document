package com.strandls.document.es.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.MapDocument;
import com.strandls.esmodule.pojo.MapQueryResponse;

import jakarta.inject.Inject;

public class ESUpdate {

	private final Logger logger = LoggerFactory.getLogger(ESUpdate.class);

	@Inject
	private EsServicesApi esService;

	public void updateESInstance(String documentId, String documentData) {
		try {
			System.out.println("--------------------document es Update---------");
			System.out.println();
			System.out.println("------started----------");
			System.out.println("Document getting UPDATED to elastic,ID:" + documentId);
			MapDocument doc = new MapDocument();
			doc.setDocument(documentData);
			MapQueryResponse response = esService.create(DocumentIndex.INDEX.getValue(), DocumentIndex.TYPE.getValue(),
					documentId, doc);
			System.out.println();
			System.out.println();
			System.out.println("-----------updated----------");
			System.out.println(response.getResult());
			System.out.println("--------------completed-------------documentId :" + documentId);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

}
