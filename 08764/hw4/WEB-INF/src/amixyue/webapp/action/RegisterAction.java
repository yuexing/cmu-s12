package amixyue.webapp.action;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.genericdao.RollbackException;

import amixyue.webapp.dao.Model;
import amixyue.webapp.dao.UserDao;
import amixyue.webapp.model.User;
import amixyue.webapp.util.Util;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class RegisterAction extends Action {

	private static final Logger log = Logger.getLogger(RegisterAction.class);
	private UserDao userDao;
	
	public RegisterAction(Model model) {
		userDao = model.getUserDao();
	}

	@Override
	public String getName() {
		return "register.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		String form = (String) request.getParameter("form");
		//redirect to register.jsp with time zone
		if(form == null){
			String[] tzones = TimeZone.getAvailableIDs();
			request.setAttribute("tzones", tzones);
			return "register.jsp";
		}
		User user = new User();
		user.setEmail(Util.sanitize((String)request.getParameter("email")));
		user.setFname(Util.sanitize((String)request.getParameter("fname")));
		user.setLname(Util.sanitize((String)request.getParameter("lname")));
		user.setPassword((String)request.getParameter("password"));
		String tzone = (String)request.getParameter("tzone");
		int offset = TimeZone.getTimeZone(tzone).getRawOffset();
		user.setTzone(tzone);
		user.setOffset(offset);
		log.debug("salt: " + user.getSalt() + " pwd: " + (String)request.getParameter("password"));
		try {
			userDao.createAutoIncrement(user);
		} catch (RollbackException e) {
			e.printStackTrace();
		}
		request.getSession().setAttribute("user", user);
		return "home.do";
	}

}
