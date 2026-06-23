/**
 * 
 */
package com.strandls.document.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.document.pojo.DocSciName;
import com.strandls.document.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class DocSciNameDao extends AbstractDAO<DocSciName, Long> {

	private final Logger logger = LoggerFactory.getLogger(DocSciNameDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected DocSciNameDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public DocSciName findById(Long id) {
		DocSciName result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(DocSciName.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<DocSciName> findByTaxonConceptId(Long taxonConceptId) {
		String qry = "from DocSciName where taxonConceptId = :taxonConceptId and isDeleted = false order by displayOrder";
		Session session = sessionFactory.openSession();
		List<DocSciName> result = null;
		try {
			Query<DocSciName> query = session.createQuery(qry);
			query.setParameter("taxonConceptId", taxonConceptId);
			result = query.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<DocSciName> findByDocId(Long documentId, Integer offset) {
		String qry = "from DocSciName where documentId = :documentId and isDeleted = false order by frequency desc";
		Session session = sessionFactory.openSession();
		List<DocSciName> result = null;
		try {
			Query<DocSciName> query = session.createQuery(qry);
			query.setParameter("documentId", documentId);

			if (offset != null) {
				query.setFirstResult(offset);
				query.setMaxResults(10);
			}

			result = query.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	public List<Long> getDocumentIdsByTaxonConceptIds(List<Long> taxonConceptIds) {

		Session session = sessionFactory.openSession();
		List<Long> documentIds = null;
		try {
			String sql = "SELECT DISTINCT docSciName.documentId FROM DocSciName docSciName "
					+ "WHERE docSciName.taxonConceptId IN :taxonConceptIds";
			Query<Long> query = session.createQuery(sql, Long.class);
			query.setParameter("taxonConceptIds", taxonConceptIds);
			documentIds = query.list();

		} catch (Exception e) {
			logger.error("Error fetching unique documentIds by taxonConceptIds: {} - {}", taxonConceptIds,
					e.getMessage(), e);
		} finally {
			session.close();
		}
		return documentIds;
	}

	public void deleteByDocumentId(Long documentId) {

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String sql = "UPDATE DocSciName SET isDeleted = true WHERE documentId = :documentId";
			Query query = session.createQuery(sql);
			query.setParameter("documentId", documentId);

			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			logger.error("Error deleting entries by documentId: {} - {}", documentId, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

}
