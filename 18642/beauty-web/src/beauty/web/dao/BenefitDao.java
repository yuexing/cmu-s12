package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.*;

public class BenefitDao extends GenericDAO<Benefit>{

	public BenefitDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(Benefit.class, tableName, connectionPool);
	}

}
