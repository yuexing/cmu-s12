package beauty.web.controller;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;

import beauty.web.action.*;
import beauty.web.dao.Model;
import beauty.web.dataservice.DataService;
import beauty.web.model.User;

@SuppressWarnings("serial")
public class Controller extends HttpServlet {

	protected Logger log = Logger.getLogger(Controller.class);
	protected ActionDispatcher dispatcher = new ActionDispatcher();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Controller() {
		super();

	}

	public void init() throws ServletException {
		Model model = new Model(getServletConfig());
		DataService ds = new DataService(model);
		dispatcher.add(new LoginAction(ds));
		dispatcher.add(new LogoutAction(ds));
		dispatcher.add(new RegisterAction(ds));
		dispatcher.add(new AddBrandAction(ds));
		dispatcher.add(new AddCategoryAction(ds));
		dispatcher.add(new AddCommentAction(ds));
		dispatcher.add(new AddProductAction(ds));
		dispatcher.add(new AddTagAction(ds));
		dispatcher.add(new AddBenefitAction(ds));
		dispatcher.add(new AddDealAction(ds));
		dispatcher.add(new AddRateAction(ds));
		dispatcher.add(new DetailAction(ds));
		dispatcher.add(new DeleteAction(ds));
		dispatcher.add(new SearchAction(ds));
		dispatcher.add(new ViewAction(ds));
		dispatcher.add(new ListAction(ds));
		dispatcher.add(new LoadZipProductAction(ds));
		dispatcher.add(new AddRetailAction(ds));
		dispatcher.add(new AddPRAction(ds));
		dispatcher.add(new GenInviteCodeAction(ds));
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String nextPage = performTheAction(request, response);
		sendToNextPage(nextPage, request, response);
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
		log.debug(request.getMethod() + ": " + servletPath + " preform: "
				+ action);

		List<String> errors = new ArrayList<String>();
		User u = (User) request.getSession().getAttribute("user");

		if (!action.equals("register.do") && !action.equals("login.do")
				&& !action.equals("view.do")) {
			if (u == null) {
				return "login.jsp";
			}

			if (!dispatcher.isAllow(u.getType(), action, errors)) {
				request.setAttribute("errors", errors);
				return "index.jsp";
			}
		}
		return preform(action, request, response);
	}

	protected String preform(String action, HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		return dispatcher.perform(action, request, response);
	}

	/*
	 * If nextPage is null, send back 404 If nextPage ends with ".do", redirect
	 * to this page. If nextPage ends with ".jsp", dispatch (forward) to the
	 * page (the view) This is the common case
	 */
	private void sendToNextPage(String nextPage, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		log.debug("sendToNextPage: " + nextPage);

		if (nextPage == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					request.getServletPath());
			return;
		}

		/*
		 * RequestDispatcher d = request.getRequestDispatcher(nextPage);
		 * d.forward(request, response);
		 */
		if (nextPage.endsWith(".do")) {
			response.sendRedirect(nextPage);
			return;
		}

		if (nextPage.endsWith(".jsp")) {
			RequestDispatcher d = request.getRequestDispatcher(nextPage);
			d.forward(request, response);
			return;
		}

		if (nextPage.charAt(0) == '/') {
			String host = request.getServerName();
			String port = ":" + String.valueOf(request.getServerPort());
			if (port.equals(":80"))
				port = "";
			response.sendRedirect("http://" + host + port + nextPage);
			return;
		}

		RequestDispatcher d = request.getRequestDispatcher("/" + nextPage);
		d.forward(request, response);

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
