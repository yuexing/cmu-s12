package amixyue.webapp.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import org.genericdao.RollbackException;

import amixyue.webapp.dao.*;
import amixyue.webapp.model.*;
import amixyue.webapp.util.Util;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class StoryAction extends Action {

	private StoryDao storyDao;
	private UserDao userDao;
	
	public StoryAction(Model model) {
		storyDao = model.getStoryDao();
		userDao = model.getUserDao();
	}

	@Override
	public String getName() {
		return "story.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		String form = (String) request.getParameter("form");
		User su = (User) request.getSession().getAttribute("user");
		if(form == null){
			return "story.jsp";
		}
		
		Story s = new Story();
		s.setContent(Util.sanitize((String)request.getParameter("story")));
		s.setDate(new Date());
		s.setFname(su.getFname());
		s.setLname(su.getLname());
		s.setUid(su.getUid());
		
		//update user
		su.setScount(su.getScount()+1);
		try {
			storyDao.createAutoIncrement(s);
			userDao.update(su);
		} catch (RollbackException e) {
			e.printStackTrace();
		}
		return "home.do";
	}

}
