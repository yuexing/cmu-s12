package beauty.web.action;

import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mybeans.form.FormBeanFactory;

import beauty.web.controller.*;
import beauty.web.dataservice.*;
import beauty.web.formbean.*;
import beauty.web.invite.*;
import beauty.web.model.User;

public class RegisterAction extends Action {
	
	private static final Logger log = Logger.getLogger(RegisterAction.class);
	
	private FormBeanFactory<RegisterForm> formBeanFactory = FormBeanFactory
			.getInstance(RegisterForm.class);

	private InviteService inviteService;
	
	public RegisterAction(DataService ds) {
		this.ds = ds;
		inviteService = new InviteServiceImpl(ds);
	}

	public String getName() {
		return "register.do";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		List<String> errors = new ArrayList<String>();
		RegisterForm form = null;

		try {
			if (!request.getMethod().equals("POST")) {
				return "register.jsp";
			}
			
			form = formBeanFactory.create(request);
			errors.addAll(form.getValidationErrors());
			if (errors.size() > 0) {
				request.setAttribute("form", form);
				request.setAttribute("errors", errors);
				return "register.jsp";
			}
			
			// invite or not
			if(Action.enableInvite && (!this.inviteService.isInvited(form.getCode(), form.getT()))){
				errors.add("The code '" + form.getCode() + "' is not invited");
				request.setAttribute("form", form);
				request.setAttribute("errors", errors);
				return "register.jsp";
			}
			
			if(Action.enableInvite){
				this.inviteService.consumeCode(form.getCode());
			}
			
			User u = ds.getUserByEmail(form.getEmail());
			if (u != null) {
				errors.add("Email address has existed");
				request.setAttribute("form", form);
				request.setAttribute("errors", errors);
				return "register.jsp";
			} else {
				// check invite here
				u = new User();
				u.setEmail(form.getEmail());
				u.setPassword(form.getPassword());
				u.setType(form.getT());
				ds.saveUser(u);
				
				request.getSession().setAttribute("user", u);
				log.debug("register user " + u.getEmail()+ " with id (" + u.getId() + ")");
				return "index.jsp";
			}
		} catch (Exception e) {
			errors.add(e.getMessage());
			request.setAttribute("form", form);
			request.setAttribute("errors", errors);
			return "register.jsp";
		}
	}
}
