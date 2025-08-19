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

import com.strandls.document.pojo.DocumentCoverage;
import com.strandls.document.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 *
 */
public class DocumentCoverageDao extends AbstractDAO<DocumentCoverage, Long> {

	private final Logger logger = LoggerFactory.getLogger(DocumentCoverageDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected DocumentCoverageDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public DocumentCoverage findById(Long id) {
		DocumentCoverage result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(DocumentCoverage.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<DocumentCoverage> findByDocumentId(Long documentId) {
		String qry = "from DocumentCoverage where documentId = :docId";
		Session session = sessionFactory.openSession();
		List<DocumentCoverage> result = null;
		try {
			Query<DocumentCoverage> query = session.createQuery(qry);
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
