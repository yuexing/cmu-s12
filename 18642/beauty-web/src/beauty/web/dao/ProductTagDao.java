package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.ProductTag;

public class ProductTagDao extends GenericDAO<ProductTag>{

	public ProductTagDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(ProductTag.class, tableName, connectionPool);
	}

}
