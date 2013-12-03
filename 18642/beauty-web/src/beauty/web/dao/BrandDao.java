package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.Brand;

public class BrandDao extends GenericDAO<Brand>{

	public BrandDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(Brand.class, tableName, connectionPool);
	}

}
