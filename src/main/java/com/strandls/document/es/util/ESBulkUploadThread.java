package com.strandls.document.es.util;

public class ESBulkUploadThread implements Runnable {

	private ESUpdate esUpdate;
	private String documentIds;

	public ESBulkUploadThread() {
		super();
	}

	public ESBulkUploadThread(ESUpdate esUpdate, String documentIds) {
		super();
		this.esUpdate = esUpdate;
		this.documentIds = documentIds;
	}

	@Override
	public void run() {
		esUpdate.esBulkUpload(documentIds);

	}

}
