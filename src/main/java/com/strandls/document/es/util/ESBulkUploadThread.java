package com.strandls.document.es.util;

public class ESBulkUploadThread implements Runnable {

	private ESUpdate esUpdate;
	private String documentIds;

	/**
	 * 
	 */
	public ESBulkUploadThread() {
		super();
	}

	/**
	 * @param esUpdate
	 * @param documentIds
	 */
	public ESBulkUploadThread(ESUpdate esUpdate, String speciesIds) {
		super();
		this.esUpdate = esUpdate;
		this.documentIds = speciesIds;
	}

	@Override
	public void run() {

		esUpdate.esBulkUpload(documentIds);

	}

}