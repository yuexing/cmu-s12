package amixyue.webapp.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;
import amixyue.webapp.model.*;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class FollowDao extends GenericDAO<Follow> {
	
	public FollowDao(String tableName, ConnectionPool connectionPool) throws DAOException {
		super(Follow.class, tableName, connectionPool);
	}
}
