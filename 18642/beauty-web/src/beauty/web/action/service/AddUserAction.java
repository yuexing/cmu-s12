package beauty.web.action.service;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gson.Gson;

import beauty.web.action.service.msg.BaseMsg;
import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.model.User;

import java.util.*;

/**
 * Add user from android to database
 * 
 * @author amixyue
 * 
 */
public class AddUserAction extends Action {

	public AddUserAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "adduser.d";
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		List<String> errors = new ArrayList<String>();

		String uidStr = request.getParameter("id");
		String name = request.getParameter("name");

		int id = -1;

		if (name == null || (name = name.trim()).length() == 0) {
			errors.add("name is empty");
		}
		if (uidStr == null || (uidStr = uidStr.trim()).length() == 0) {
			errors.add("id is empty");
		} else {
			try {
				id = Integer.parseInt(uidStr);
			} catch (Exception e) {
				errors.add("id is not an integer");
			}
		}

		BaseMsg bmsg = new BaseMsg();
		if (errors.size() > 0) {
			bmsg.setErrors(errors);
			return new Gson().toJson(bmsg);
		}

		try {
			if (this.ds.getUser(id) == null) {
				User u = new User();
				u.setType(User.Type.user);
				u.setEmail(name);
				u.setId(id);

				this.ds.saveUserN(u);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errors.add(e.getMessage());
			bmsg.setErrors(errors);
		}
		return new Gson().toJson(bmsg);
	}
}
