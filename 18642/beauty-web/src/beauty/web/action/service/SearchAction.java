package beauty.web.action.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mybeans.form.FormBeanFactory;

import com.google.gson.Gson;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.SearchForm;
import beauty.web.model.Product;
import beauty.web.util.*;
import beauty.web.action.Type;
import beauty.web.action.service.msg.ProductMsg;

import java.util.*;

public class SearchAction extends Action {

	private static final Logger log = Logger.getLogger(SearchAction.class);

	private FormBeanFactory<SearchForm> formBeanFactory = FormBeanFactory
			.getInstance(SearchForm.class);

	public SearchAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "search.d";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ProductMsg pmsg = new ProductMsg();
		Product[] products = null;
		List<String> errors = new ArrayList<String>();
		SearchForm form = null;

		try {
			form = formBeanFactory.create(request);
			errors.addAll(form.getValidationErrors());
			if (errors.size() > 0) {
				pmsg.setErrors(errors);
				return new Gson().toJson(pmsg);
			}

			List<Product> tmpProducts = new LinkedList<Product>();
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

			log.warn(products);
			if (LCS.parseProduct(tmpProducts, products, StringUtil.getStdFrom(form.getQ()))) {
				products = new Product[tmpProducts.size()];
				tmpProducts.toArray(products);
			}

			pmsg.setProducts(parse(products));
			return new Gson().toJson(pmsg);
		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			pmsg.setErrors(errors);
			return new Gson().toJson(pmsg);
		}
	}
}
