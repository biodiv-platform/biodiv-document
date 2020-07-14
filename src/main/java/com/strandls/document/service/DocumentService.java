/**
 * 
 */
package com.strandls.document.service;

import javax.servlet.http.HttpServletRequest;

import com.strandls.document.pojo.DocumentCreateData;
import com.strandls.document.pojo.ShowDocument;

/**
 * @author Abhishek Rudra
 *
 */
public interface DocumentService {

	public ShowDocument show(Long documentId);

	public ShowDocument createDocument(HttpServletRequest request, DocumentCreateData documentCreateData);

}
