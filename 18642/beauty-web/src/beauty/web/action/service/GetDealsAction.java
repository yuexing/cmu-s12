package beauty.web.action.service;


import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gson.Gson;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.model.*;

public class GetDealsAction extends Action {
	

	public GetDealsAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "getdeals.d";
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		Deal[] deals = new Deal[0];
		try {
			deals = ds.getDeals();
			if(deals == null){
				deals = new Deal[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Gson().toJson(deals);
	}
}
