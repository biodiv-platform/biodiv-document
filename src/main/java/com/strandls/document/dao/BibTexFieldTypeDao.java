/**
 * 
 */
package com.strandls.document.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.document.pojo.BibTexFieldType;
import com.strandls.document.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 *
 */
public class BibTexFieldTypeDao extends AbstractDAO<BibTexFieldType, Long> {

	private final Logger logger = LoggerFactory.getLogger(BibTexFieldTypeDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected BibTexFieldTypeDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public BibTexFieldType findById(Long id) {
		Session session = sessionFactory.openSession();
		BibTexFieldType result = null;
		try {
			result = session.get(BibTexFieldType.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
