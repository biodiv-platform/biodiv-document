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

import com.strandls.document.pojo.DocumentSpeciesGroup;
import com.strandls.document.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 *
 */
public class DocumentSpeciesGroupDao extends AbstractDAO<DocumentSpeciesGroup, Long> {

	private final Logger logger = LoggerFactory.getLogger(DocumentSpeciesGroupDao.class);

	/**
	 * @param sessionFactory
	 */

	@Inject
	protected DocumentSpeciesGroupDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public DocumentSpeciesGroup findById(Long id) {
		DocumentSpeciesGroup result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(DocumentSpeciesGroup.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<DocumentSpeciesGroup> findByDocumentId(Long documentId) {
		List<DocumentSpeciesGroup> result = null;
		String qry = "from DocumentSpeciesGroup where documentId = :docId";
		Session session = sessionFactory.openSession();
		try {
			Query<DocumentSpeciesGroup> query = session.createQuery(qry);
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
