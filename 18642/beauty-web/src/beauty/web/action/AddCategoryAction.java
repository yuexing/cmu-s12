package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybeans.form.FormBeanFactory;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.CategoryForm;
import beauty.web.model.Category;
import beauty.web.model.User;
import beauty.web.model.User.Type;

public class AddCategoryAction extends Action {

	private FormBeanFactory<CategoryForm> formBeanFactory = FormBeanFactory
			.getInstance(CategoryForm.class);

	public AddCategoryAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "addcate.do";
	}

	public Type[] getTypes() {
		return new Type[] { Type.admin, Type.manufacturer };
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		List<String> errors = new ArrayList<String>();
		CategoryForm form = null;

		try {
			User u = (User) request.getSession().getAttribute("user");
			request.setAttribute("categories", ds.getCategories());
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
				return "addcate.jsp";
			}
			Category c = null;
			if (form.isGet()) {
				if (form.isEdit()) {
					c = ds.getCategory(Integer.parseInt(form.getId()));
					form.setName(c.getName());
				}
				request.setAttribute("form", form);
				return "addcate.jsp";
			} else {
				if (form.isEdit()) {
					c = ds.getCategory(Integer.parseInt(form.getId()));
					if (c.getOwner() == u.getId() || u.isAdmin()) {
						form.populateCategory(c);
						ds.updateCategory(c);
					} else {
						errors.add("Permission denied.");
						request.setAttribute("errors", errors);
						return "addcate.jsp";
					}
				} else {
					c = new Category();
					c.setOwner(u.getId());
					form.populateCategory(c);
					if (form.isForce()) {
						ds.saveCategoryForce(c);
					} else {
						ds.saveCategory(c);
					}

				}

				return "index.jsp";
			}

		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			request.setAttribute("form", form);
			request.setAttribute("errors", errors);
			return "addcate.jsp";
		}

	}
}
