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
			String hql = "UPDATE DocSciName SET isDeleted = true WHERE documentId = :documentId";
			Query query = session.createQuery(hql);
			query.setParameter("documentId", documentId);

			int rowsUpdated = query.executeUpdate();
			logger.info("Soft-deleted {} DocSciName rows for documentId {}", rowsUpdated, documentId);

			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			logger.error("Error deleting entries by documentId: {} - {}", documentId, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

	public void unlinkTaxonIds(List<Long> taxonConceptIds) {

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String hql = "UPDATE DocSciName SET taxonConceptId = null WHERE taxonConceptId IN :taxonConceptIds";
			Query query = session.createQuery(hql);
			query.setParameter("taxonConceptIds", taxonConceptIds);

			int rowsUpdated = query.executeUpdate();
			logger.info("Unlinked {} DocSciName rows for taxonConceptIds {}", rowsUpdated, taxonConceptIds);

			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			logger.error("Error unlinking entries by taxonConceptIds: {} - {}", taxonConceptIds, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

	public void linkTaxonIds(Long taxonConceptId, String name) {

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String hql = "UPDATE DocSciName SET taxonConceptId = :taxonConceptId WHERE taxonConceptId IS NULL AND scientificName = :name";
			Query query = session.createQuery(hql);
			query.setParameter("taxonConceptId", taxonConceptId);
			query.setParameter("name", name);

			int rowsUpdated = query.executeUpdate();
			logger.info("Linked {} DocSciName rows for taxonConceptIds {}", rowsUpdated, taxonConceptId);

			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			logger.error("Error Linking entries by taxonConceptIds: {} - {}", taxonConceptId, e.getMessage(), e);
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<DocSciName> findAllScientificNames() {
		String qry = "from DocSciName where isDeleted = false";
		Session session = sessionFactory.openSession();
		List<DocSciName> result = null;
		try {
			Query<DocSciName> query = session.createQuery(qry);

			result = query.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	public void updateAll(List<DocSciName> docSciNames) {

		if (docSciNames == null || docSciNames.isEmpty()) {
			return;
		}

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		final int batchSize = 50; // tune based on hibernate.jdbc.batch_size in config

		try {
			tx = session.beginTransaction();

			for (int i = 0; i < docSciNames.size(); i++) {
				DocSciName docSciName = docSciNames.get(i);
				session.merge(docSciName); // use merge() if entities are detached; update() if they're guaranteed
											// transient/new to this session

				if (i > 0 && i % batchSize == 0) {
					session.flush();
					session.clear();
				}
			}

			tx.commit();
			logger.info("Bulk updated {} DocSciName rows", docSciNames.size());

		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			logger.error("Error bulk updating DocSciName rows: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to bulk update DocSciName rows", e);
		} finally {
			session.close();
		}
	}

}
