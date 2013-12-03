package amixyue.webapp.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import amixyue.webapp.dao.Model;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class LogoutAction extends Action {

	public LogoutAction(Model model) {

	}

	@Override
	public String getName() {
		return "logout.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		request.getSession().setAttribute("user", null);
		
		HttpSession session = request.getSession();
		
		Cookie uidCookie = new Cookie("uid", "");
        uidCookie.setMaxAge(0);
        uidCookie.setPath("/");
        
        Cookie pwdCookie = new Cookie("pwd", "");
        pwdCookie.setMaxAge(0);
        pwdCookie.setPath("/");
        
        session.setAttribute("uidCookie", uidCookie);
        session.setAttribute("pwdCookie", pwdCookie);
        request.setAttribute("skip", 1);
		return "welcome.do";
	}

}
