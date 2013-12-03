package beauty.web.action;

import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybeans.form.FormBeanFactory;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.BenefitForm;
import beauty.web.model.Benefit;
import beauty.web.model.User;
import beauty.web.model.User.Type;

public class AddBenefitAction extends Action {

	private FormBeanFactory<BenefitForm> formBeanFactory = FormBeanFactory
			.getInstance(BenefitForm.class);

	public AddBenefitAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "addbenefit.do";
	}

	public Type[] getTypes() {
		return new Type[] { Type.admin, Type.manufacturer};
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		List<String> errors = new ArrayList<String>();
		BenefitForm form = null;
		try {
			User u = (User) request.getSession().getAttribute("user");
			request.setAttribute("benefits", ds.getBenefits());
			form = formBeanFactory.create(request);
			if (request.getMethod().equals("POST")) {
				form.setGet(false);
			} else {
				form.setGet(true);
			}
			errors.addAll(form.getValidationErrors());
			if (errors.size() > 0) {
				request.setAttribute("form", form);
				request.setAttribute("errors", errors);
				return "addbenefit.jsp";
			}
			Benefit be = null;
			if (form.isGet()) {
				if (form.isEdit()) {
					be = ds.getBenefit(Integer.parseInt(form.getId()));
					form.setName(be.getName());
				}
				request.setAttribute("form", form);
				return "addbenefit.jsp";
			} else {
				if (form.isEdit()) {
					be = ds.getBenefit(Integer.parseInt(form.getId()));
					if (be.getOwner() == u.getId() || u.isAdmin()) {
						form.populateBenefit(be);
						// it is force by default
						ds.updateBenefit(be);
					} else {
						errors.add("Permission denied.");
						request.setAttribute("errors", errors);
						return "addbenefit.jsp";
					}
				} else {
					be = new Benefit();
					form.populateBenefit(be);

					be.setOwner(u.getId());

					if (form.isForce()) {
						ds.saveBenefitForce(be);
					} else {
						ds.saveBenefit(be);
					}
				}
				// success!
				return "index.jsp";
			}
		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			request.setAttribute("form", form);
			request.setAttribute("errors", errors);
			return "addbenefit.jsp";
		}
	}
}
