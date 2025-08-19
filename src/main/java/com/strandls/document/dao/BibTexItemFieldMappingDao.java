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

import com.strandls.document.pojo.BibTexItemFieldMapping;
import com.strandls.document.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 *
 */
public class BibTexItemFieldMappingDao extends AbstractDAO<BibTexItemFieldMapping, Long> {

	private final Logger logger = LoggerFactory.getLogger(BibTexItemFieldMapping.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected BibTexItemFieldMappingDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public BibTexItemFieldMapping findById(Long id) {
		Session session = sessionFactory.openSession();
		BibTexItemFieldMapping result = null;
		try {
			result = session.get(BibTexItemFieldMapping.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<BibTexItemFieldMapping> findByItemTypeId(Long itemTypeId) {
		List<BibTexItemFieldMapping> result = null;
		String qry = "from BibTexItemFieldMapping where itemTypeId = :itemTypeId";
		Session session = sessionFactory.openSession();
		try {
			Query<BibTexItemFieldMapping> query = session.createQuery(qry);
			query.setParameter("itemTypeId", itemTypeId);
			result = query.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
