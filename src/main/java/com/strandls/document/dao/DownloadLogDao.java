/**
 * 
 */
package com.strandls.document.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.document.pojo.DownloadLog;
import com.strandls.document.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 *
 */
public class DownloadLogDao extends AbstractDAO<DownloadLog, Long> {

	private final Logger logger = LoggerFactory.getLogger(DownloadLogDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected DownloadLogDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public DownloadLog findById(Long id) {
		Session session = sessionFactory.openSession();
		DownloadLog result = null;
		try {
			result = session.get(DownloadLog.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
