package beauty.web.action.service;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.model.Benefit;

public class GetBenefitsAction extends Action {	

	public GetBenefitsAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "getbenefits.d";
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		Benefit[] benefits = new Benefit[0];
		
		try {
			benefits = ds.getBenefits();
			if(benefits == null){
				benefits = new Benefit[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new Gson().toJson(benefits);
	}
}
