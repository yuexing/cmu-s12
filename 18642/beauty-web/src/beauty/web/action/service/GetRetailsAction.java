package beauty.web.action.service;


import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gson.Gson;

import beauty.web.action.service.msg.RetailMsg;
import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.model.*;
/**
 * Get retails according to pid. when pid is null or
 * invalid, return errors. 
 * @author amixyue
 *
 */
public class GetRetailsAction extends Action {	

	public GetRetailsAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "getretails.d";
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		Retail[] retails = null;
		RetailMsg rmsg = new RetailMsg();
		
		try {
			String pidStr = null;
			int pid = -1;
			List<String> errors = new ArrayList<String>();
			if((pidStr = request.getParameter("pid")) == null){
				errors.add("empty pid!");
			} else {
				try{
					pid = Integer.parseInt(pidStr);
				} catch(Exception e){
					errors.add("invalid pid " + pidStr);
				}
			}
			
			if(errors.size() > 0){
				rmsg.setErrors(errors);
				return new Gson().toJson(rmsg);
			}
			
			retails = ds.getRetailsByProduct(pid);
			if(retails == null){
				retails = new Retail[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		rmsg.setRetails(parse(retails));
		return new Gson().toJson(rmsg);
	}
}
