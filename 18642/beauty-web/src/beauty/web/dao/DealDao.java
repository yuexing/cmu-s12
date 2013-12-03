package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.Deal;

public class DealDao extends GenericDAO<Deal> {

	public DealDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(Deal.class, tableName, connectionPool);
	}
	

}
