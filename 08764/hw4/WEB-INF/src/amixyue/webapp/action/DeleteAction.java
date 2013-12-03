package amixyue.webapp.action;

import javax.servlet.http.HttpServletRequest;

import org.genericdao.MatchArg;
import org.genericdao.RollbackException;

import amixyue.webapp.dao.*;
import amixyue.webapp.model.*;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class DeleteAction extends Action {

	static enum Type {
		fr, cm, st
	};

	private StoryDao storyDao;
	private CommentDao commentDao;
	private FollowDao followDao;
	private UserDao userDao;

	public DeleteAction(Model model) {
		storyDao = model.getStoryDao();
		commentDao = model.getCommentDao();
		followDao = model.getFollowDao();
		userDao = model.getUserDao();
	}

	@Override
	public String getName() {
		return "delete.do";
	}

	@SuppressWarnings("static-access")
	@Override
	public String perform(HttpServletRequest request) {
		// type fr cm st
		// sid/ uid/ cid
		Type type = Type.valueOf((String) request.getParameter("type"));
		// session user
		User su = (User) request.getSession().getAttribute("user");
		int suid = su.getUid();
		switch (type) {
		case fr:
			int uid = Integer.parseInt(request.getParameter("uid"));
			Follow[] f;
			try {
				f = followDao.match(MatchArg.equals("uid1", uid).and(
						MatchArg.equals("uid2", suid)));
				followDao.delete(f[0].getFid());
				User u = userDao.read(uid);
				u.setFollower(u.getFollower()-1);
				su.setFollowing(su.getFollowing()-1);
				userDao.update(u);
				userDao.update(su);
				request.setAttribute("ok", 1);
			} catch (RollbackException e) {
				request.setAttribute("ok", 0);
				e.printStackTrace();
			}
			break;
		case cm:
			int cid = Integer.parseInt(request.getParameter("cid"));
			try {
				commentDao.delete(cid);
				su.setCcount(su.getCcount()-1);
				userDao.update(su);
				request.setAttribute("ok", 1);
			} catch (RollbackException e) {
				request.setAttribute("ok", 0);
				e.printStackTrace();
			}
			break;
		case st:
			int sid = Integer.parseInt(request.getParameter("sid"));
			try {
				storyDao.delete(sid);
				su.setScount(su.getScount()-1);
				userDao.update(su);
				request.setAttribute("ok", 1);
			} catch (RollbackException e) {
				request.setAttribute("ok", 0);
				e.printStackTrace();
			}
			break;
		}		
		return "ajax";
	}

}
