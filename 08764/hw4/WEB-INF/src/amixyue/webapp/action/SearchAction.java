package amixyue.webapp.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.genericdao.MatchArg;
import org.genericdao.RollbackException;

import amixyue.webapp.dao.FollowDao;
import amixyue.webapp.dao.Model;
import amixyue.webapp.dao.UserDao;
import amixyue.webapp.model.Follow;
import amixyue.webapp.model.User;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class SearchAction extends Action {

	private UserDao userDao;
	private FollowDao followDao;
	
	public SearchAction(Model model) {
		userDao = model.getUserDao();
		followDao = model.getFollowDao();
	}

	@Override
	public String getName() {
		return "search.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		String frname = (String) request.getParameter("frname");
		//session user
		User su = (User) request.getSession().getAttribute("user");
		int suid = su.getUid();	
		
		// following	
		ArrayList<User> friends = new ArrayList<User>();
		try {		
			//search everyone
			@SuppressWarnings("static-access")
			User[] us = userDao.match(MatchArg.containsIgnoreCase("fname", frname).or(MatchArg.containsIgnoreCase("lname", frname)));
			
			for(User u : us){
				friends.add(u);
			}
		} catch (RollbackException e) {
			e.printStackTrace();
		}
		request.setAttribute("friends", friends);		
		return "search.jsp";
	}

}
