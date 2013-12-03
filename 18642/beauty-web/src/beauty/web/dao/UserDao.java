package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.User;

public class UserDao extends GenericDAO<User>{

	public UserDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(User.class, tableName, connectionPool);
	}

}
