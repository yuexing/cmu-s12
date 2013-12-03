package amixyue.webapp.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import amixyue.webapp.action.*;
import amixyue.webapp.dao.Model;
import amixyue.webapp.model.*;

/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Controller.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Controller() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		Model model = new Model(getServletConfig());
		Action.add(new ChkUserAction(model));
		Action.add(new CommentAction(model));
		Action.add(new DeleteAction(model));
		Action.add(new FollowAction(model));
		Action.add(new HomeAction(model));
		Action.add(new LoginAction(model));
		Action.add(new LogoutAction(model));
		Action.add(new ProfileAction(model));
		Action.add(new PrefileAction(model));
		Action.add(new RegisterAction(model));
		Action.add(new StoryAction(model));
		Action.add(new SearchAction(model));
		Action.add(new WelcomeAction(model));
		Action.add(new ViewAction(model));
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String nextPage = performTheAction(request);
		
		//add cookie now
		HttpSession session = request.getSession();
		if(session.getAttribute("uidCookie")!=null){
			Cookie uidCookie= (Cookie)session.getAttribute("uidCookie");
			response.addCookie(uidCookie);
			log.debug("set cookie: uidCookie " + uidCookie.getValue());
			session.setAttribute("uidCookie", null);
		}
		if(session.getAttribute("pwdCookie")!=null){
			Cookie pwdCookie= (Cookie)session.getAttribute("pwdCookie");
			response.addCookie(pwdCookie);
			log.debug("set cookie: pwdCookie " + pwdCookie.getValue());
			session.setAttribute("pwdCookie", null);
		}
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
	private String performTheAction(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String servletPath = request.getServletPath();
		
		User user = (User) session.getAttribute("user");
		String action = getActionName(servletPath);

		if (user == null
				&& !(action.equals("register.do") || action.equals("login.do") || action
						.equals("chkuser.do") || action.equals("view.do"))) {
			log.debug("performTheAction: welcome.do");
			return Action.perform("welcome.do", request);
		}

		if(user != null && action.equals("register.do")){
			return Action.perform("welcome.do", request);
		}
		// Let the logged in user run his chosen action
		log.debug("performTheAction: " + action);
		return Action.perform(action, request);
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
		
		if(nextPage.equals("welcome.do")){
			RequestDispatcher d = request.getRequestDispatcher(nextPage);
			d.forward(request, response);
			return;
		}else if (nextPage.endsWith(".do")) {
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
