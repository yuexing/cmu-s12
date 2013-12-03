package beauty.web.action.service;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.model.*;

public class GetCategorysAction extends Action {
	

	public GetCategorysAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "getcates.d";
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		Category[] cates = new Category[0];
		try {
			cates = ds.getCategories();
			if(cates == null){
				cates = new Category[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Gson().toJson(cates);
	}
}
