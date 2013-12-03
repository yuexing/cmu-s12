package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.Retail;


public class RetailDao extends GenericDAO<Retail>{
	
	public RetailDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(Retail.class, tableName, connectionPool);
	}

}
