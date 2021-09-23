/**
 * 
 */
package com.strandls.document.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.document.pojo.GNFinderResponseMap;
import com.strandls.document.pojo.GnFinderResponseNames;
import com.strandls.esmodule.ApiException;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.ExtendedTaxonDefinition;

public class MicroServicesUtils {

	private final static Logger logger = LoggerFactory.getLogger(MicroServicesUtils.class);

	public static GNFinderResponseMap fetchSpeciesDetails(GNFinderResponseMap response, EsServicesApi esServiceApi) {
		List<GnFinderResponseNames> names = response.getNames();
		List<GnFinderResponseNames> detailedNames = new ArrayList<GnFinderResponseNames>();
		for (GnFinderResponseNames name : names) {
			String canonicalName = name.getName();
			try {
				ExtendedTaxonDefinition taxonMapped = esServiceApi.matchPhrase("etd", "er", null, null,
						"canonical_form", canonicalName);

				if (taxonMapped != null) {
					name.setTaxonId(new Long(taxonMapped.getId()));
				}
				detailedNames.add(name);
			} catch (ApiException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		response.setNames(detailedNames);
		return response;
	}

}
