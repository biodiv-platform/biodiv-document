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

import com.strandls.document.pojo.Document;
import com.strandls.document.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 *
 */
public class DocumentDao extends AbstractDAO<Document, Long> {

	private final Logger logger = LoggerFactory.getLogger(DocumentDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected DocumentDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Document findById(Long id) {
		Session session = sessionFactory.openSession();
		Document result = null;
		try {
			result = session.get(Document.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	public List<Document> findByBulkIds(List<Long> bulkIds) {

		Session session = sessionFactory.openSession();
		List<Document> results = null;
		try {
			String hql = "FROM Document WHERE id IN :bulkIds";
			Query<Document> query = session.createQuery(hql, Document.class);
			query.setParameter("bulkIds", bulkIds);
			results = query.list();
		} catch (Exception e) {
			logger.error("Error fetching documents by bulkIds: {} - {}", bulkIds, e.getMessage(), e);
		} finally {
			session.close();
		}
		return results;
	}

}
