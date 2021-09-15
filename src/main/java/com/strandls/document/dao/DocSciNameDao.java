/**
 * 
 */
package com.strandls.document.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.document.pojo.DocSciName;
import com.strandls.document.util.AbstractDAO;

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
	public List<DocSciName> findByDocId(Long documentId) {
		String qry = "from DocSciName where documentId = :documentId and isDeleted = false order by frequency desc";
		Session session = sessionFactory.openSession();
		List<DocSciName> result = null;
		try {
			Query<DocSciName> query = session.createQuery(qry);
			query.setParameter("documentId", documentId);
			result = query.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
