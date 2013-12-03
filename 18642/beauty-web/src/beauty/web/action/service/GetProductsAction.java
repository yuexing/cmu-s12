package beauty.web.action.service;

import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.mybeans.form.*;

import com.google.gson.Gson;
import beauty.web.action.Type;
import beauty.web.action.service.msg.ProductMsg;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.TypeForm;
import beauty.web.model.*;

/**
 * Get products according to type. when type is null or invalid return errors.
 * when type is none, there is no need for pid, otherwise, return errors.
 * 
 * @author amixyue
 * 
 */
public class GetProductsAction extends Action {

//	private static final Logger log = Logger.getLogger(GetProductsAction.class);
	
	private FormBeanFactory<TypeForm> formBeanFactory = FormBeanFactory
			.getInstance(TypeForm.class);

	public GetProductsAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "getproducts.d";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ProductMsg pmsg = new ProductMsg();

		Product[] products = null;

		List<String> errors = new ArrayList<String>();

		try {
			TypeForm form = formBeanFactory.create(request);

			errors.addAll(form.getValidationErrors());

			if (errors.size() > 0) {
				pmsg.setErrors(errors);
				return new Gson().toJson(pmsg);
			}

			Type type = form.getTypeType();
			int id = form.getIdInt();

			switch (type) {
			case brand:
				products = ds.getProductsByBrand(id);
				break;
			case category:
				products = ds.getProductsByCategory(id);
				break;
			case tag:
				products = ds.getProductsByTag(id);
				break;
			case benefit:
				products = ds.getProductsByBenefit(id);
				break;
			case none:
				products = ds.getProducts();
				break;
			case one:
				Product tmp = ds.getProduct(id);
				if (tmp == null)
					products = new Product[0];
				else 
					products = new Product[]{tmp};
				break;
			default:
				errors.add("Unknown type");
				pmsg.setErrors(errors);
				return new Gson().toJson(pmsg);
			}
			pmsg.setProducts(this.parse(products));
			return new Gson().toJson(pmsg);
		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			pmsg.setErrors(errors);
			return new Gson().toJson(pmsg);
		}
	}
}
