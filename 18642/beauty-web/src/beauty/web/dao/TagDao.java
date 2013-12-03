package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.Tag;

public class TagDao extends GenericDAO<Tag>{

	public TagDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(Tag.class, tableName, connectionPool);
	}
}
