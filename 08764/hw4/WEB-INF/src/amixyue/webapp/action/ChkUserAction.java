package amixyue.webapp.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.genericdao.MatchArg;
import org.genericdao.RollbackException;

import amixyue.webapp.dao.Model;
import amixyue.webapp.dao.UserDao;
import amixyue.webapp.model.User;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class ChkUserAction extends Action {

	private static final Logger log = Logger.getLogger(ChkUserAction.class);
	private UserDao userDao;
	
	public ChkUserAction(Model model) {
		userDao = model.getUserDao();
	}
	@Override
	public String getName() {
		return "chkuser.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		User[] users = null;
		try {
			users = userDao.match(MatchArg.equals("email", (String)request.getParameter("email")));
		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(users.length > 0){
			log.debug("user exist: ok-0");
			request.setAttribute("ok", 0);
		}else{
			log.debug("user exist: ok-1");
			request.setAttribute("ok", 1);
		}
		return "ajax";
	}

}
