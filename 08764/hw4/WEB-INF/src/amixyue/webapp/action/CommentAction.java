package amixyue.webapp.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.genericdao.RollbackException;

import amixyue.webapp.dao.CommentDao;
import amixyue.webapp.dao.Model;
import amixyue.webapp.dao.UserDao;
import amixyue.webapp.model.Comment;
import amixyue.webapp.model.User;
import amixyue.webapp.util.Util;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class CommentAction  extends Action {

	private CommentDao commentDao;
	private UserDao userDao;
	
	public CommentAction(Model model) {
		commentDao = model.getCommentDao();
		userDao = model.getUserDao();
	}

	@Override
	public String getName() {
		return "comment.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		String form = (String) request.getParameter("form");
		User su = (User) request.getSession().getAttribute("user");
		if(form == null){
			request.setAttribute("sid", Integer.parseInt(request.getParameter("sid")));
			return "comment.jsp";
		}
		
		Comment c = new Comment();
		c.setContent(Util.sanitize((String)request.getParameter("comment")));
		c.setFname(su.getFname());
		c.setLname(su.getLname());
		c.setSid(Integer.parseInt(request.getParameter("sid")));
		c.setUid(su.getUid());
		c.setDate(new Date());
		
		su.setCcount(su.getCcount()+1);
		try {
			commentDao.createAutoIncrement(c);
			userDao.update(su);
		} catch (RollbackException e) {
			e.printStackTrace();
		}
		return "home.do";
	}

}
