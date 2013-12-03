package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mybeans.form.FormBeanFactory;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.RetailForm;
import beauty.web.model.Retail;
import beauty.web.model.User;
import beauty.web.model.User.Type;

public class AddRetailAction extends Action {
	
	private static final Logger log = Logger.getLogger(AddRetailAction.class);

	private FormBeanFactory<RetailForm> formBeanFactory = FormBeanFactory
			.getInstance(RetailForm.class);

	/**
	 * @param ds
	 */
	public AddRetailAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "addretail.do";

	}

	public Type[] getTypes() {
		return new Type[] { Type.admin, Type.retail };
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		List<String> errors = new ArrayList<String>();
		RetailForm form = null;
		try {
			request.setAttribute("retails", ds.getRetails());

			form = formBeanFactory.create(request);

			if (request.getMethod().equals("POST")) {
				form.setGet(false);
			} else {
				form.setGet(true);
			}

			User u = (User) request.getSession().getAttribute("user");
			if (u.getRid() != -1 && !form.isEdit()) {
				errors.add("Can not add more than one retails.");
				request.setAttribute("errors", errors);
				return "addretail.jsp";
			}
			errors.addAll(form.getValidationErrors());
			if (errors.size() > 0) {
				request.setAttribute("form", form);
				request.setAttribute("errors", errors);
				return "addretail.jsp";
			}

			Retail r = null;
			if (form.isGet()) {
				if (form.isEdit()) {
					r = ds.getRetail(Integer.parseInt(form.getId()));
					form.populateForm(r);
				}
				request.setAttribute("form", form);
				return "addretail.jsp";
			} else {
				if (form.isEdit()) {
					r = ds.getRetail(Integer.parseInt(form.getId()));
					if (r.getOwner() == u.getId() || u.isAdmin()) {
						form.populateRetail(r);
						// it is force by default
						ds.updateRetail(r);
					} else {
						errors.add("Permission denied.");
						request.setAttribute("errors", errors);
						return "addretail.jsp";
					}
				} else {
					r = new Retail();
					form.populateRetail(r);
					r.setOwner(u.getId());
					
					if(u.isRetail()){
						r.setUid(u.getId());
					}	
					
					ds.saveRetail(r);
					
					log.debug("save a retail with r.getId : " + r.getId());
					
					if(u.isRetail()){
						u.setRid(r.getId());
						request.getSession().setAttribute("user", u);
						ds.updateUser(u);
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
			return "addretail.jsp";
		}

	}
}
