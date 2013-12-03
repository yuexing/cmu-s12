package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.Product;

public class ProductDao extends GenericDAO<Product>{

	public ProductDao( String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(Product.class, tableName, connectionPool);
	}

}
