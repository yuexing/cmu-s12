package amixyue.webapp.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import amixyue.webapp.model.*;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class CommentDao extends GenericDAO<Comment> {
	
	public CommentDao(String tableName, ConnectionPool connectionPool) throws DAOException {
		super(Comment.class, tableName, connectionPool);
	}
}
