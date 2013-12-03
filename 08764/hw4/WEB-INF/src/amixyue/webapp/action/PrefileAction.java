package amixyue.webapp.action;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import amixyue.webapp.dao.Model;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class PrefileAction extends Action {

	public PrefileAction(Model model) {
	}

	@Override
	public String getName() {
		return "prefile.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		String[] tzones = TimeZone.getAvailableIDs();
		request.setAttribute("tzones", tzones);
        return "profile.jsp";
	}
}
