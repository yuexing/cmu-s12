package beauty.web.action;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybeans.form.FormBeanFactory;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.SearchForm;
import beauty.web.model.Product;
import beauty.web.util.*;

import java.util.*;

public class SearchAction extends Action {
	

	private FormBeanFactory<SearchForm> formBeanFactory = FormBeanFactory
			.getInstance(SearchForm.class);

	public SearchAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "search.do";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		List<String> errors = new ArrayList<String>();
		SearchForm form = null;
		
		try {
			request.setAttribute("brands", ds.getBrands());
			request.setAttribute("cates", ds.getCategories());
			request.setAttribute("tags", ds.getTags());
			request.setAttribute("benefits", ds.getBenefits());
			
			if (!request.getMethod().equals("POST")) {
				return "search.jsp";
			} else {
				form = formBeanFactory.create(request);
				errors.addAll(form.getValidationErrors());

				if (errors.size() > 0) {
					request.setAttribute("form", form);
					request.setAttribute("errors", errors);
					return "search.jsp";

				} else {
					List<Product> tmpProducts = new LinkedList<Product>();
					Product[] products = null;
					Type type = form.getTypeType();
					int id = form.getIdInt();

					if (type == null) {
						products = ds.getProducts();
					} else {
						switch (type) {
						case category:
							products = ds.getProductsByCategory(id);
							break;
						case brand:
							products = ds.getProductsByBrand(id);
							break;
						case benefit:
							products = ds.getProductsByBenefit(id);
							break;
						case tag:
							products = ds.getProductsByTag(id);
							break;
						default:
							products = ds.getProducts();
						}
					}

					if (LCS.parseProduct(tmpProducts, products, StringUtil.getStdFrom(form.getQ()))) {
						products = new Product[tmpProducts.size()];
						tmpProducts.toArray(products);
					}

					request.setAttribute("products", products);
					return "detail.jsp";
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			request.setAttribute("form", form);
			request.setAttribute("errors", errors);
			return "search.jsp";
		}
	}

}
