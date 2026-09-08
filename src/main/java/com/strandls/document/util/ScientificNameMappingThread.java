package com.strandls.document.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.document.dao.DocSciNameDao;
import com.strandls.document.es.util.ESUpdate;
import com.strandls.document.pojo.DocSciName;
import com.strandls.esmodule.ApiException;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.ExtendedTaxonDefinition;

public class ScientificNameMappingThread implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(ScientificNameMappingThread.class);

	private ESUpdate esUpdate;
	private DocSciNameDao docSciNameDao;
	private EsServicesApi esService;

	public ScientificNameMappingThread(ESUpdate esUpdate, DocSciNameDao docSciNameDao, EsServicesApi esService) {
		super();
		this.esUpdate = esUpdate;
		this.docSciNameDao = docSciNameDao;
		this.esService = esService;
	}

	@Override
	public void run() {
		List<Long> documentIds = new ArrayList<>();

		List<DocSciName> unLinkedSciName = docSciNameDao.findAllScientificNames();
		List<DocSciName> LinkedNames = new ArrayList<>();
		Map<String, Long> taxonMap = new HashMap<>();

		for (DocSciName sciName : unLinkedSciName) {
			String canonicalName = sciName.getScientificName();
			try {
				if (taxonMap.containsKey(canonicalName)) {
					if (taxonMap.get(canonicalName) != null) {
						sciName.setTaxonConceptId(new Long(taxonMap.get(canonicalName)));
					} else {
						sciName.setTaxonConceptId(null);
					}
					LinkedNames.add(sciName);
					if (!documentIds.contains(sciName.getDocumentId())) {
						documentIds.add(sciName.getDocumentId());
					}
				} else {
					ExtendedTaxonDefinition taxonMapped = esService.matchPhrase("etd", "er", "", "", "canonical_form",
							canonicalName);

					if (taxonMapped != null) {
						taxonMap.put(canonicalName, new Long(taxonMapped.getId()));
						sciName.setTaxonConceptId(new Long(taxonMapped.getId()));
					} else {
						taxonMap.put(canonicalName, null);
						sciName.setTaxonConceptId(null);
					}
					LinkedNames.add(sciName);
					if (!documentIds.contains(sciName.getDocumentId())) {
						documentIds.add(sciName.getDocumentId());
					}
				}
			} catch (ApiException e) {
				logger.error(e.getMessage());
			}
		}
		docSciNameDao.updateAll(LinkedNames);
		esUpdate.esBulkScientificNamesUpdate(StringUtils.join(documentIds, ','));
	}
}