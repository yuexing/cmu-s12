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
import beauty.web.formbean.*;
import beauty.web.model.ProductRetail;
import beauty.web.model.User;
import beauty.web.model.User.Type;

public class AddPRAction extends Action {
	
	private static final Logger log = Logger.getLogger(AddPRAction.class);

	private FormBeanFactory<PRForm> formBeanFactory = FormBeanFactory
			.getInstance(PRForm.class);

	public AddPRAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "addpr.do";
	}

	public Type[] getTypes() {
		return new Type[] { Type.retail };
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		List<String> errors = new ArrayList<String>();
		PRForm form = null;

		try {
			User u = (User) request.getSession().getAttribute("user");
			
			if (ds.getRetail(u.getRid()) == null) {
				errors.add(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>add a retail to you first");
				request.setAttribute("errors", errors);
				log.debug(errors);
				return super.getReferer(request);
			}
			form = formBeanFactory.create(request);
			errors.addAll(form.getValidationErrors());
			if (errors.size() > 0) {
				request.setAttribute("form", form);
				request.setAttribute("errors", errors);
				return super.getReferer(request);
			}
			String del = request.getParameter("del");
			if (del != null && del.equals("1")) {
				ds.deletePR(form.getPid(), u.getRid());
			} else {
				// this is the dummy retail
				ProductRetail pr = new ProductRetail();
				pr.setProductId(form.getPid());
				pr.setRetailId(u.getRid());
				ds.saveProductRetail(pr);
			}
			return super.getReferer(request);
		} catch (Exception e) {
			e.printStackTrace();
			errors.add(e.getMessage());
			request.setAttribute("form", form);
			request.setAttribute("errors", errors);
			return super.getReferer(request);
		}
	}

}
