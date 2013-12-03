package beauty.web.action;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybeans.form.FileProperty;
import org.mybeans.form.FormBeanFactory;

import java.util.*;

import beauty.web.controller.Action;
import beauty.web.dataservice.*;
import beauty.web.formbean.*;
import beauty.web.model.User;
import beauty.web.model.User.Type;
import beauty.web.util.bean.*;
import beauty.web.util.*;

public class LoadZipProductAction extends Action {

	private FormBeanFactory<ZipProductForm> formBeanFactory = FormBeanFactory
			.getInstance(ZipProductForm.class);
	
	public LoadZipProductAction(DataService ds){
		this.ds = ds;
	}
	
	@Override
	public String getName() {
		return "loadzip.do";
	}
	
	public Type[] getTypes(){
		return new Type[]{Type.admin, Type.manufacturer};
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		List<String> errors = new ArrayList<String>();
		ZipProductForm form = null;

		try {
			User u = (User) request.getSession().getAttribute("user");
			
			if (!request.getMethod().equals("POST")) {
				return "loadzip.jsp";
			} else {
				form = formBeanFactory.create(request);
				errors.addAll(form.getValidationErrors());
				if (errors.size() > 0) {
					request.setAttribute("errors", errors);
					return "loadzip.jsp";
				}
				FileProperty file = form.getFile();
				ArrayList<ParsedProduct> products = new ArrayList<ParsedProduct>();
				new XMLParser().loadZipByteProduct(file.getBytes(), products);
				for(ParsedProduct p : products){
					ds.saveParsedProduct(p, u.getId());
				}					
				errors.add("success");
				request.setAttribute("errors", errors);
				return "loadzip.jsp";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errors.add(e.getMessage());
			request.setAttribute("errors", errors);
			return "loadzip.jsp";	
		}
	}

}
