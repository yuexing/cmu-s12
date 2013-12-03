package amixyue.webapp.action;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.genericdao.MatchArg;
import org.genericdao.RollbackException;

import amixyue.webapp.controller.Controller;
import amixyue.webapp.dao.*;
import amixyue.webapp.model.*;

/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class WelcomeAction extends Action {
	private static final Logger log = Logger.getLogger(WelcomeAction.class);
	private StoryDao storyDao;
	private UserDao userDao;

	public WelcomeAction(Model model) {
		userDao = model.getUserDao();
		storyDao = model.getStoryDao();
	}

	@Override
	public String getName() {
		return "welcome.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		try {
			int count = userDao.getCount();
			request.setAttribute("count", count);
			ArrayList<User> users = userDao.getUsersByScount(10);
			ArrayList<Story> storys = storyDao.getStorysOrderByTime(10);
			request.setAttribute("users", users.toArray());
			request.setAttribute("storys", storys.toArray());

			if (request.getAttribute("skip") == null) {
				// check cookie first
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					int uid = 0;
					String pwd = null;
					for (Cookie c : cookies) {
						if (c.getName().equals("uid")) {
							uid = Integer.parseInt(c.getValue());
							log.debug("read cookie: " + uid);
						}
						if (c.getName().equals("pwd")) {
							pwd = c.getValue();
							log.debug("read cookie: " + pwd);
						}
					}

					@SuppressWarnings("static-access")
					User[] us = userDao.match(MatchArg.equals("uid", uid).and(
							MatchArg.equals("hashedPassword", pwd)));
					if (us.length > 0) {
						request.getSession().setAttribute("user", us[0]);
						log.debug("set user");
					}
				}
			}
		} catch (RollbackException e) {
			e.printStackTrace();
		}
		return "welcome.jsp";
	}

}
