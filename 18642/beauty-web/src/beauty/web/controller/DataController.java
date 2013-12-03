package beauty.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import beauty.web.action.service.*;
import beauty.web.dao.Model;
import beauty.web.dataservice.DataService;

public class DataController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Logger log = Logger.getLogger(DataController.class);
	protected ActionDispatcher dispatcher = new ActionDispatcher();	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DataController() {
		super();

	}
	
	public void init() throws ServletException {
		Model model = new Model(getServletConfig());
		DataService ds = new DataService(model);
		dispatcher.add(new GetBenefitsAction(ds));
		dispatcher.add(new GetBrandsAction(ds));
		dispatcher.add(new GetTagsAction(ds));
		dispatcher.add(new GetCategorysAction(ds));
		dispatcher.add(new GetDealsAction(ds));
		dispatcher.add(new GetProductsAction(ds));
		dispatcher.add(new GetRetailsAction(ds));
		dispatcher.add(new GetCommentsAction(ds));
		dispatcher.add(new AddCommentAction(ds));
		dispatcher.add(new AddUserAction(ds));
		dispatcher.add(new AddRateAction(ds));
		dispatcher.add(new SearchAction(ds));
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String resp = performTheAction(request, response);
		response.getWriter().write(resp);
	}

	/*
	 * Extracts the requested action and (depending on whether the user is
	 * logged in) perform it (or make the user login).
	 * 
	 * @param request
	 * 
	 * @return the next page (the view)
	 */
	private String performTheAction(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		String servletPath = request.getServletPath();
		String action = getActionName(servletPath);
		log.debug( request.getMethod() + ": " + servletPath + " preform: " + action);
		
		return preform(action, request, response);
	}

	protected String preform(String action, HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		return dispatcher.perform(action, request, response);
	}

	/*
	 * Returns the path component after the last slash removing any "extension"
	 * if present.
	 */
	private String getActionName(String path) {
		// We're guaranteed that the path will start with a slash
		// so it can be *.do or index.html
		int slash = path.lastIndexOf('/');
		return path.substring(slash + 1);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
