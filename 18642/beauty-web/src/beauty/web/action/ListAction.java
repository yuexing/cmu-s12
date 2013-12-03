package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.exception.DataException;
import beauty.web.model.Product;
import beauty.web.model.User;

public class ListAction extends Action {

	

	public ListAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "list.do";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		List<String> errors = new ArrayList<String>();
		try {
			User u = (User) request.getSession().getAttribute("user");
			Product[] products = ds.getProducts();
			
			boolean[] added = null;
			if (products != null) {
				added = new boolean[products.length];
				for (int i = 0; i < products.length; i++) {
					added[i] = ds.existPR(products[i].getId(), u.getRid());
				}
			}
			
			request.setAttribute("added", added);
			request.setAttribute("brands", ds.getBrands());
			request.setAttribute("cates", ds.getCategories());
			request.setAttribute("tags", ds.getTags());
			request.setAttribute("benefits", ds.getBenefits());
			request.setAttribute("products", products);
			request.setAttribute("comments", ds.getComments());
			request.setAttribute("deals", ds.getDeals());
			request.setAttribute("retails", ds.getRetails());

			
		} catch (DataException e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			request.setAttribute("errors", errors);
			return "list.jsp";
		}
		return "list.jsp";
	}

}
