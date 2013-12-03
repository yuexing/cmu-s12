package amixyue.webapp.action;

import javax.servlet.http.HttpServletRequest;
import org.genericdao.RollbackException;

import amixyue.webapp.dao.FollowDao;
import amixyue.webapp.dao.Model;
import amixyue.webapp.dao.UserDao;
import amixyue.webapp.model.Follow;
import amixyue.webapp.model.User;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class FollowAction extends Action {

	FollowDao friendShipDao;
	private UserDao userDao;

	public FollowAction(Model model) {
		friendShipDao = model.getFollowDao();
		userDao = model.getUserDao();
	}

	@Override
	public String getName() {
		return "follow.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		User su = (User) request.getSession().getAttribute("user");
		int suid = su.getUid();
		int uid = Integer.parseInt(request.getParameter("uid"));
		User u = null;
		try {
			u = userDao.read((Integer)uid);
		} catch (RollbackException e1) {
			e1.printStackTrace();
		}
		try {
			Follow fs = new Follow();
			fs.setUid1(uid);
			fs.setUid2(suid);
			friendShipDao.createAutoIncrement(fs);
			
			u.setFollower(u.getFollower()+1);
			su.setFollowing(u.getFollowing()+1);
			userDao.update(u);
			userDao.update(su);
			request.setAttribute("ok", 1);
		} catch (RollbackException e) {
			e.printStackTrace();
			request.setAttribute("ok", 0);
		}

		return "ajax";
	}

}