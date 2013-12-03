package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybeans.form.FormBeanFactory;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.TypeForm;
import beauty.web.model.User;

public class DeleteAction extends Action {

	private FormBeanFactory<TypeForm> formBeanFactory = FormBeanFactory
			.getInstance(TypeForm.class);

	public DeleteAction(DataService ds2) {
		this.ds = ds2;
	}

	@Override
	public String getName() {
		return "delete.do";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		List<String> errors = new ArrayList<String>();

		Type type = null;
		int id = -1;
		try {
			User u = (User) request.getSession().getAttribute("user");

			TypeForm form = formBeanFactory.create(request);
			errors.addAll(form.getValidationErrors());

			if (errors.size() > 0)
				return this.getReferer(request);

			type = form.getTypeType();
			id = form.getIdInt();

			boolean success = false;
			switch (type) {
			case product:
				if (ds.getProduct(id).getOwner() == u.getId() || u.isAdmin()) {
					ds.deleteProduct(id);
					success = true;
				}
				break;
			case comment:
				if (ds.getComment(id).getUserId() == u.getId() || u.isAdmin()) {
					ds.deleteComment(id);
					success = true;
				}
				break;
			case brand:
				if (ds.getBrand(id).getOwner() == u.getId() || u.isAdmin()) {
					ds.deleteBrand(id);
					success = true;
				}
				break;
			case category:
				if (ds.getCategory(id).getOwner() == u.getId() || u.isAdmin()) {
					ds.deleteCategory(id);
					success = true;
				}
				break;
			case tag:
				if (ds.getTag(id).getOwner() == u.getId() || u.isAdmin()) {
					ds.deleteTag(id);
					success = true;
				}
				break;
			case benefit:
				if (ds.getBenefit(id).getOwner() == u.getId() || u.isAdmin()) {
					ds.deleteBenefit(id);
					success = true;
				}
				break;
			case deal:
				if (ds.getDeal(id).getOwner() == u.getId() || u.isAdmin()) {
					ds.deleteDeal(id);
					success = true;
				}
				break;
			case retail:
				if (ds.getRetail(id).getOwner() == u.getId() || u.isAdmin()) {
					ds.deleteRetail(id);
					
					if (u.isRetail()) {
						u.setRid(-1);
						ds.updateUser(u);
					}					
					
					success = true;
				}
				break;
			default:
				break; // can this happen?
			}
			if (!success) {
				errors.add("Permission Denied");
				request.setAttribute("errors", errors);
			}
			return this.getReferer(request);
		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add("Type " + type + " with ID " + id + "doesn't exist");
			request.setAttribute("errors", errors);
			return this.getReferer(request);
		}
	}
}
