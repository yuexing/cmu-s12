package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.mybeans.form.*;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.RateForm;
import beauty.web.model.*;
import beauty.web.model.User.Type;

public class AddRateAction extends Action {	

	private FormBeanFactory<RateForm> formBeanFactory = FormBeanFactory
			.getInstance(RateForm.class);

	public AddRateAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "addrate.do";
	}
	
	public Type[] getTypes(){
		return new Type[]{Type.admin, Type.manufacturer, Type.retail};
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		List<String> errors = new ArrayList<String>();

		try {
			if (!request.getMethod().equals("POST")) {
				int pid = Integer.parseInt(request.getParameter("id"));
				request.setAttribute("product", ds.getProduct(pid));
				return "addrate.jsp";
			} else {
				RateForm form = null;
				form = formBeanFactory.create(request);
				errors = form.getValidationErrors();

				if (errors.size() > 0) {
					request.setAttribute("form", form);
					request.setAttribute("errors", errors);
					return "addrate.jsp";
				}
				Product p = ds
						.getProduct(Integer.parseInt(form.getProductId()));

				if (p == null) {
					errors.add("Product "
							+ Integer.parseInt(form.getProductId())
							+ " is null");
				} else {
					p.setRate((p.getRate() * p.getRateNum() + Integer
							.parseInt(form.getThisRate()))
							/ (p.getRateNum() + 1));
					p.setRateNum(p.getRateNum() + 1);
					ds.updateProduct(p);
				}

				request.setAttribute("errors", errors);
				return "index.jsp";
			}

		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			request.setAttribute("errors", errors);
			return "addrate.jsp";
		}
	}
}
