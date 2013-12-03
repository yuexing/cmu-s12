package amixyue.webapp.dao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class Model {
	
	private UserDao userDao;
	private StoryDao storyDao;
	private CommentDao commentDao;
	private PhotoDao photoDao;
	private FollowDao friendShipDao;
	
	public Model(ServletConfig config) throws ServletException {
		try {
			String jdbcDriver = config.getInitParameter("jdbcDriverName");
			String jdbcURL    = config.getInitParameter("jdbcURL");
			
			ConnectionPool pool = new ConnectionPool(jdbcDriver,jdbcURL);
			userDao = new UserDao("yuexing_user", pool);
			storyDao = new StoryDao("yuexing_story", pool);
			commentDao = new CommentDao("yuexing_comment", pool);
			friendShipDao = new FollowDao("yuexing_follow", pool);
			photoDao = new PhotoDao("yuexing_photo", pool);
			
		} catch (DAOException e) {
			throw new ServletException(e);
		}
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public StoryDao getStoryDao() {
		return storyDao;
	}

	public CommentDao getCommentDao() {
		return commentDao;
	}

	public PhotoDao getPhotoDao() {
		return photoDao;
	}

	public FollowDao getFollowDao() {
		return friendShipDao;
	}
	
}
