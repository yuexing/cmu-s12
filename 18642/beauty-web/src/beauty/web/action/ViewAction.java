package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.model.*;

/**
 * view.do actually looks up the photo bean by "id" and then passes it (via
 * request attribute) to the ImageServlet. See also the mapping of /image in the
 * web.xml file. for security and for dao
 */
public class ViewAction extends Action {

	

	public ViewAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "view.do";
	}

	@SuppressWarnings("finally")
	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		
		try {
			int id = Integer.parseInt(request.getParameter("id"));
			Photo p = ds.readPhoto(id);
			request.setAttribute("photo", p);			
		} catch (Exception e) {
			List<String> errors = new ArrayList<String>();
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			request.setAttribute("errors", errors);
		} finally{
			return "image";
		}
	}

}