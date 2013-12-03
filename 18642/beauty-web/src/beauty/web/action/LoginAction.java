package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.mybeans.form.*;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.LoginForm;
import beauty.web.model.User;

public class LoginAction extends Action {

	private static final Logger log = Logger.getLogger(LoginAction.class);

	private FormBeanFactory<LoginForm> formBeanFactory = FormBeanFactory
			.getInstance(LoginForm.class);

	public LoginAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "login.do";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		List<String> errors = new ArrayList<String>();
		LoginForm form = null;
		try {
			if (!request.getMethod().equals("POST")) {
				return "login.jsp";
			}
			
			form = formBeanFactory.create(request);
			errors.addAll(form.getValidationErrors());
			if (errors.size() > 0) {
				request.setAttribute("form", form);
				request.setAttribute("errors", errors);
				return "login.jsp";
			}

			User u = ds.getUserByEmail(form.getEmail());
			
			if (u == null) {
				errors.add("No such user");
			} else if (!u.getPassword().equals(form.getPassword())) {
				errors.add("password error!");
			} else {
				request.getSession().setAttribute("user", u);				
				log.debug("Login user " + u.getEmail()+ " with id (" + u.getId() + ")");			
				return "index.jsp";
			}
			// error !
			request.setAttribute("form", form);
			request.setAttribute("errors", errors);
			return "login.jsp";
		} catch (Exception e) {
			errors.add(e.getMessage());
			request.setAttribute("form", form);
			request.setAttribute("errors", errors);
			return "login.jsp";
		} 
	}
}