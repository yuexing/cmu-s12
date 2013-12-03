package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybeans.form.FormBeanFactory;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.DealForm;
import beauty.web.model.Deal;
import beauty.web.model.User;
import beauty.web.model.User.Type;

public class AddDealAction extends Action {

	private FormBeanFactory<DealForm> formBeanFactory = FormBeanFactory
			.getInstance(DealForm.class);

	/**
	 * @param ds
	 */
	public AddDealAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "adddeal.do";
	}

	public Type[] getTypes() {
		return new Type[] { Type.retail };
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		List<String> errors = new ArrayList<String>();
		DealForm form = null;

		try {
			User u = (User) request.getSession().getAttribute("user");
			if (ds.getRetail(u.getRid()) == null) {
				errors.add("add a retail to you first");
				request.setAttribute("errors", errors);
				return "adddeal.jsp";
			}

			request.setAttribute("deals", ds.getDeals());
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
				return "adddeal.jsp";
			}
			Deal d = null;
			if (form.isGet()) {
				if (form.isEdit()) {
					d = ds.getDeal(Integer.parseInt(form.getId()));
					form.setContent(d.getContent());
				}
				request.setAttribute("form", form);
				return "adddeal.jsp";
			} else {
				if (form.isEdit()) {
					d = ds.getDeal(Integer.parseInt(form.getId()));
					if (d.getOwner() == u.getId() || u.isAdmin()) {
						form.populateDeal(d);
						// it is force by default
						ds.updateDeal(d);
					} else {
						errors.add("Permission denied.");
						request.setAttribute("errors", errors);
						return "adddeal.jsp";
					}
				} else {
					d = new Deal();
					form.populateDeal(d);
					d.setRetailId(u.getRid()); // collect with retail
					d.setRetailName(ds.getRetail(u.getRid()).getName());
					d.setOwner(u.getId());
					ds.saveDeal(d);
				}
				return "index.jsp";
			}
		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			request.setAttribute("form", form);
			request.setAttribute("errors", errors);
			return "adddeal.jsp";
		}
	}

}
