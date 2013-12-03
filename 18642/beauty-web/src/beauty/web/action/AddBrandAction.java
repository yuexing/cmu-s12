package beauty.web.action;

import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybeans.form.FileProperty;
import org.mybeans.form.FormBeanFactory;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.*;
import beauty.web.model.*;
import beauty.web.model.User.Type;

public class AddBrandAction extends Action {
	private FormBeanFactory<BrandPostForm> postFormFactory = FormBeanFactory
			.getInstance(BrandPostForm.class);

	private FormBeanFactory<BrandGetForm> getFormFactory = FormBeanFactory
			.getInstance(BrandGetForm.class);

	public AddBrandAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "addbrand.do";
	}

	public Type[] getTypes() {
		return new Type[] { Type.admin, Type.manufacturer };
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		List<String> errors = new ArrayList<String>();
		BrandGetForm getform = null;
		BrandPostForm postform = null;

		try {
			User u = (User) request.getSession().getAttribute("user");
			request.setAttribute("brands", ds.getBrands());
			Brand b = null;

			if (request.getMethod().equals("POST")) {
				postform = postFormFactory.create(request);

				errors.addAll(postform.getValidationErrors());

				if (errors.size() > 0) {
					request.setAttribute("form", postform);
					request.setAttribute("errors", errors);
					return "addbrand.jsp";
				}

				FileProperty file = postform.getFile();

				if (postform.isEdit()) {
					b = ds.getBrand(Integer.parseInt(postform.getId()));
					if (b.getOwner() == u.getId() || u.isAdmin()) {
						postform.populateBrand(b);
						ds.updateBrand(b, file);
					} else {
						errors.add("Permission denied.");
						request.setAttribute("errors", errors);
						return "addbrand.jsp";
					}
				} else {
					b = new Brand();
					postform.populateBrand(b);

					b.setOwner(u.getId());

					if (postform.isForce()) {
						ds.saveBrandForce(b, file);
					} else {
						ds.saveBrand(b, file);
					}
				}
				// success!
				return "index.jsp";

			} else {
				getform = getFormFactory.create(request);
				getform.setGet(true);

				errors.addAll(getform.getValidationErrors());

				if (errors.size() > 0) {
					request.setAttribute("form", getform);
					request.setAttribute("errors", errors);
					return "addbrand.jsp";
				}

				if (getform.isEdit()) {
					b = ds.getBrand(Integer.parseInt(getform.getId()));
					getform.setName(b.getName());
				}

				request.setAttribute("form", getform);
				return "addbrand.jsp";
			}

		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			if (getform != null) {
				request.setAttribute("form", getform);
			} else {
				request.setAttribute("form", postform);
			}
			request.setAttribute("errors", errors);
			return "addbrand.jsp";
		}
	}
}
