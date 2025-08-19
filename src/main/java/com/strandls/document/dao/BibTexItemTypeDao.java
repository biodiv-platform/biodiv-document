/**
 * 
 */
package com.strandls.document.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.document.pojo.BibTexItemType;
import com.strandls.document.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 *
 */
public class BibTexItemTypeDao extends AbstractDAO<BibTexItemType, Long> {

	private final Logger logger = LoggerFactory.getLogger(BibTexItemTypeDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected BibTexItemTypeDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public BibTexItemType findById(Long id) {
		Session session = sessionFactory.openSession();
		BibTexItemType result = null;
		try {
			result = session.get(BibTexItemType.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public BibTexItemType findByName(String phrase) {
		BibTexItemType result = null;
		Session session = sessionFactory.openSession();
		String qry = "from BibTexItemType where lower(itemType) = lower(:phrase)";
		try {
			Query<BibTexItemType> query = session.createQuery(qry);
			query.setParameter("phrase", phrase);
			result = query.getSingleResult();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
