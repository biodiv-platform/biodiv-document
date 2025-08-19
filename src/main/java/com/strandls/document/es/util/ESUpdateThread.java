/**
 * 
 */
package com.strandls.document.es.util;

/**
 * @author vishnu
 *
 */
public class ESUpdateThread implements Runnable {

	private ESUpdate esUpdate;
	private String documentId;
	private String documentData;

	/**
	 * 
	 */
	public ESUpdateThread() {
		super();
	}

	/**
	 * @param esUpdate
	 * @param observationId
	 */
	public ESUpdateThread(ESUpdate esUpdate, String documentData, String documentId) {
		super();
		this.esUpdate = esUpdate;
		this.documentData = documentData;
		this.documentId = documentId;
	}

	@Override
	public void run() {
		esUpdate.updateESInstance(documentId, documentData);
	}

}
