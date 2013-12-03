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
import beauty.web.model.*;

public class DetailAction extends Action {
	

	private FormBeanFactory<TypeForm> formBeanFactory = FormBeanFactory
			.getInstance(TypeForm.class);

	public DetailAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "detail.do";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		Product product = null;
		Comment comment = null;
		Brand brand = null;
		Category category = null;
		Tag tag = null;
		Benefit benefit = null;
		Retail retail = null;
		Tag[] tags = null;
		Benefit[] benefits = null;
		Comment[] comments = null;
		Product[] products = null;

		List<String> errors = new ArrayList<String>();

		try {
			TypeForm form = formBeanFactory.create(request);
			errors.addAll(form.getValidationErrors());

			if (errors.size() > 0)
				return this.getReferer(request);

			Type type = form.getTypeType();
			int id = form.getIdInt();
			switch (type) {
			case product:
				product = ds.getProduct(id);
				brand = ds.getBrand(product.getBrandId());
				category = ds.getCategory(product.getCategoryId());
				tags = ds.getTagsByProduct(id);
				benefits = ds.getBenefitsByProduct(id);
				comments = ds.getCommentsByProduct(id);
				break;
			case comment:
				comment = ds.getComment(id);
				comments = ds.getCommentsByOrigin(id);
				break;
			case brand:
				brand = ds.getBrand(id);
				products = ds.getProductsByBrand(id);
				break;
			case category:
				category = ds.getCategory(id);
				products = ds.getProductsByCategory(id);
				break;
			case tag:
				tag = ds.getTag(id);
				products = ds.getProductsByTag(id);
				break;
			case benefit:
				benefit = ds.getBenefit(id);
				products = ds.getProductsByBenefit(id);
				break;
			case retail:
				retail = ds.getRetail(id);
				products = ds.getProductsByRetail(id);
				break;
			default:
				break; // can this happen?
			}

			request.setAttribute("retail", retail);
			request.setAttribute("benefit", benefit);
			request.setAttribute("tag", tag);
			request.setAttribute("comment", comment);
			request.setAttribute("product", product);
			request.setAttribute("brand", brand);
			request.setAttribute("category", category);
			request.setAttribute("products", products);
			request.setAttribute("comments", comments);
			request.setAttribute("tags", tags);
			request.setAttribute("benefits", benefits);

			return "detail.jsp";
		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			request.setAttribute("errors", errors);
			return this.getReferer(request);
		}
	}
}
