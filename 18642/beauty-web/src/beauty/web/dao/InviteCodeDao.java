package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.*;

public class InviteCodeDao extends GenericDAO<InviteCode>{

	public InviteCodeDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(InviteCode.class, tableName, connectionPool);
	}

}
