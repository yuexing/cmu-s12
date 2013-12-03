package beauty.web.dao;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;

import beauty.web.model.Comment;

public class CommentDao extends GenericDAO<Comment>{

	public CommentDao( String tableName,
			ConnectionPool connectionPool) throws DAOException {
		super(Comment.class, tableName, connectionPool);
	}

}
