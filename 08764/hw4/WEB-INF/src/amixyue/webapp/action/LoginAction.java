package amixyue.webapp.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.genericdao.MatchArg;
import org.genericdao.RollbackException;

import amixyue.webapp.dao.*;
import amixyue.webapp.model.User;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class LoginAction extends Action {
	private static final Logger log = Logger.getLogger(LoginAction.class);
	private UserDao userDao;
	
	public LoginAction(Model model) {
		userDao = model.getUserDao();
	}

	@Override
	public String getName() {
		return "login.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		List<String> errors = new ArrayList<String>();
        request.setAttribute("errors",errors);
        
        // Look up the user
        User[] users = null;
        User user = null;
		try {
			String email = request.getParameter("email");
			users = userDao.match(MatchArg.equals("email", email));
			if(users.length > 0){
				user = users[0];
			}
		} catch (RollbackException e) {
			e.printStackTrace();
		}
        
        if (user == null) {
            errors.add("User Email not found");
            return "welcome.do";
        }
        
        log.debug("salt: " + user.getSalt() + " pwd: " + (String)request.getParameter("password"));
        
        // Check the password
        if (!user.checkPassword((String)(request.getParameter("password")))) {
            errors.add("Incorrect password");
            return "welcome.do";
        }

        // Attach (this copy of) the user bean to the session
        HttpSession session = request.getSession();
        session.setAttribute("user",user);
        
        //add cookie 30 days
        if(request.getParameter("remember")!=null){
        	int autoExpire = (60 * 60 * 24) * 30;
            Cookie uidCookie = new Cookie("uid", new Integer(user.getUid()).toString());
            uidCookie.setMaxAge(autoExpire);
            uidCookie.setPath("/");
            
            Cookie pwdCookie = new Cookie("pwd", user.getHashedPassword());
            pwdCookie.setMaxAge(autoExpire);
            pwdCookie.setPath("/");
            
            session.setAttribute("uidCookie", uidCookie);
            session.setAttribute("pwdCookie", pwdCookie);
        }
		return "home.do";
	}

}
