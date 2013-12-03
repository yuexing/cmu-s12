package beauty.web.action;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;

public class LogoutAction extends Action {

	public LogoutAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "logout.do";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		HttpSession session = request.getSession(false);
		session.setAttribute("user", null);
		return "login.jsp";
	}
}
