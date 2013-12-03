package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.ProductBenefit;

public class ProductBenefitDao extends GenericDAO<ProductBenefit>{

	public ProductBenefitDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(ProductBenefit.class, tableName, connectionPool);
	}

}
