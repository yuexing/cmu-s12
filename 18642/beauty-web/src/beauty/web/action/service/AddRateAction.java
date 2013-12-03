package beauty.web.action.service;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gson.Gson;

import beauty.web.action.service.msg.BaseMsg;
import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.model.Product;

import java.util.*;

/**
 * Add rate for a product
 * 
 * @author amixyue
 * 
 */
public class AddRateAction extends Action {

	public AddRateAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "addrate.d";
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		List<String> errors = new ArrayList<String>();

		String pidStr = request.getParameter("pid");
		String rateStr = request.getParameter("rate");

		int pid = -1;
		int rate = -1;

		if (pidStr == null || (pidStr = pidStr.trim()).length() == 0) {
			errors.add("pid is empty");
		} else {
			try {
				pid = Integer.parseInt(pidStr);
			} catch (Exception e) {
				errors.add("id is not an integer");
			}
		}
		if (rateStr == null || (rateStr = rateStr.trim()).length() == 0) {
			errors.add("rate is empty");
		} else {
			try {
				rate = Integer.parseInt(rateStr);
			} catch (Exception e) {
				errors.add("rate is not an integer");
			}
			if(rate < 0 || rate > 5){
				errors.add("rate is not valid");
			}
		}

		BaseMsg bmsg = new BaseMsg();
		if (errors.size() > 0) {
			bmsg.setErrors(errors);
			return new Gson().toJson(bmsg);
		}

		try {
			Product tmp = null;
			if ((tmp = this.ds.getProduct(pid))== null) {
				errors.add("The product doesn't exist");
				bmsg.setErrors(errors);		
			} else {
				tmp.setRate((tmp.getRate() * tmp.getRateNum() + rate) / (tmp.getRateNum() + 1));
				tmp.setRateNum(tmp.getRateNum() + 1);
				this.ds.updateProduct(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errors.add(e.getMessage());
			bmsg.setErrors(errors);
		}
		return new Gson().toJson(bmsg);
	}
}
