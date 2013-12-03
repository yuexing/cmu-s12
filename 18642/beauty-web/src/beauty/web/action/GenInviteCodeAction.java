package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.invite.*;
import beauty.web.model.User.Type;

public class GenInviteCodeAction extends Action {

	private InviteService inviteService;

	public GenInviteCodeAction(DataService ds) {
		this.ds = ds;
		inviteService = new InviteServiceImpl(ds);
	}

	@Override
	public String getName() {
		return "gencode.do";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		if (!request.getMethod().equals("POST")) {
			return "gencode.jsp";
		}

		List<String> errors = new ArrayList<String>();
		String type = null;
		Type typeType = null;

		if ((type = request.getParameter("type")) == null
				|| (type = type.trim()).length() == 0) {
			errors.add("type is empty");
			request.setAttribute("errors", errors);
			return "gencode.jsp";
		}

		try {
			typeType = Type.valueOf(type);
		} catch (Exception e) {
			errors.add("type is invalid");
			request.setAttribute("errors", errors);
			return "gencode.jsp";
		}

		errors.add("The Code For A " + type + " is: "
				+ this.inviteService.genInviteCode(typeType));
		request.setAttribute("errors", errors);
		return "gencode.jsp";
	}

	public Type[] getTypes() {
		return new Type[] { Type.admin };
	}
}
