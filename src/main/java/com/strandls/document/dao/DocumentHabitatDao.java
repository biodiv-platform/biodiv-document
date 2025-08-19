/**
 * 
 */
package com.strandls.document.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.document.pojo.DocumentHabitat;
import com.strandls.document.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 *
 */
public class DocumentHabitatDao extends AbstractDAO<DocumentHabitat, Long> {

	private final Logger logger = LoggerFactory.getLogger(DocumentHabitatDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected DocumentHabitatDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public DocumentHabitat findById(Long id) {
		DocumentHabitat result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(DocumentHabitat.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<DocumentHabitat> findByDocumentId(Long documentId) {
		List<DocumentHabitat> result = null;
		String qry = "from DocumentHabitat where documentId = :docId";
		Session session = sessionFactory.openSession();
		try {
			Query<DocumentHabitat> query = session.createQuery(qry);
			query.setParameter("docId", documentId);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
