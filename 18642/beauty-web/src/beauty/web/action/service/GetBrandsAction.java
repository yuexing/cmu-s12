package beauty.web.action.service;


import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gson.Gson;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.model.*;

public class GetBrandsAction extends Action {
	

	public GetBrandsAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "getbrands.d";
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		Brand[] brands = new Brand[0];
		try {
			brands = ds.getBrands();
			if(brands == null){
				brands = new Brand[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Gson().toJson(brands);
	}
}
