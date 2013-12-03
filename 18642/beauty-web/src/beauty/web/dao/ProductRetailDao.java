package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.*;

public class ProductRetailDao extends GenericDAO<ProductRetail>{

	public ProductRetailDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(ProductRetail.class, tableName, connectionPool);
	}

}