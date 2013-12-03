package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.Photo;

public class PhotoDao extends GenericDAO<Photo>{

	public PhotoDao(String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(Photo.class, tableName, connectionPool);
	}

}
