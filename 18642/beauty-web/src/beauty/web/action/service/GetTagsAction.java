package beauty.web.action.service;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.model.*;

public class GetTagsAction extends Action {
	

	public GetTagsAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "gettags.d";
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		Tag[] tags = new Tag[0];
		try {
			tags = ds.getTags();
			if(tags == null){
				tags = new Tag[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Gson().toJson(tags);
	}
}
