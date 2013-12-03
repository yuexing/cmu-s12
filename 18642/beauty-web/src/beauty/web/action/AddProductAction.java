package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

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

public class AddProductAction extends Action {

	private FormBeanFactory<ProductGetForm> getformFactory = FormBeanFactory
			.getInstance(ProductGetForm.class);

	private FormBeanFactory<ProductPostForm> postformFactory = FormBeanFactory
			.getInstance(ProductPostForm.class);

	public AddProductAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "addproduct.do";
	}

	public Type[] getTypes() {
		return new Type[] { Type.admin, Type.manufacturer };
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		List<String> errors = new ArrayList<String>();
		ProductGetForm getform = null;
		ProductPostForm postform = null;

		try {
			User u = (User) request.getSession().getAttribute("user");

			request.setAttribute("brands", ds.getBrands());
			request.setAttribute("cates", ds.getCategories());
			request.setAttribute("tags", ds.getTags());
			request.setAttribute("benefits", ds.getBenefits());

			// before product calculate added arr
			// added array will not allow add
			Product[] products = ds.getProducts();
			request.setAttribute("products", products);
			
			boolean[] added = null;
			if (products != null) {
				added = new boolean[products.length];
				for (int i = 0; i < products.length; i++) {
					added[i] = ds.existPR(products[i].getId(), u.getRid());
				}
			}
			request.setAttribute("added", added);
			Product p = null;

			if (request.getMethod().equals("POST")) {
				postform = postformFactory.create(request);
				postform.setGet(false);
				errors.addAll(postform.getValidationErrors());
				if (errors.size() > 0) {
					request.setAttribute("form", postform);
					request.setAttribute("errors", errors);
					return "addproduct.jsp";
				}

				FileProperty file = postform.getFile();
				if (postform.isEdit()) {
					p = ds.getProduct(Integer.parseInt(postform.getId()));
					if (p.getOwner() == u.getId() || u.isAdmin()) {
						postform.populateProduct(p);
						ds.updateProduct(p, file, postform.getBenefitIds(),
								postform.getTagIds());
					} else {
						errors.add("Permission denied.");
						request.setAttribute("errors", errors);
						return "addproduct.jsp";
					}
				} else {
					p = new Product();
					postform.populateProduct(p);
					p.setOwner(u.getId());

					if (postform.isForce()) {
						ds.saveProductForce(p, file, postform.getBenefitIds(),
								postform.getTagIds());
					} else {
						ds.saveProduct(p, file, postform.getBenefitIds(),
								postform.getTagIds());
					}
				}
				// save product
				return "index.jsp";
			} else {
				getform = getformFactory.create(request);
				getform.setGet(true);

				errors.addAll(getform.getValidationErrors());
				if (errors.size() > 0) {
					request.setAttribute("form", getform);
					request.setAttribute("errors", errors);
					return "addproduct.jsp";
				}

				if (getform.isEdit()) {
					p = ds.getProduct(Integer.parseInt(getform.getId()));
					getform.populateForm(p);
				}
				request.setAttribute("form", getform);
				return "addproduct.jsp";
			}

		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			if (postform != null) {
				request.setAttribute("form", postform);
			} else {
				request.setAttribute("form", getform);
			}
			request.setAttribute("errors", errors);
			return "addproduct.jsp";
		}
	}
}
