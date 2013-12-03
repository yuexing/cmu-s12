package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.Category;

public class CategoryDao extends GenericDAO<Category> {
	
	public CategoryDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(Category.class, tableName, connectionPool);
	}

}
