package hw2.amixyue.control;

import hw2.amixyue.dao.*;
import hw2.amixyue.dao.imp.*;
import hw2.amixyue.model.User;
import hw2.amixyue.util.Util;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;



/**
 * Servlet implementation class LoginServlet
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class LoginServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(LoginServlet.class);
	private static final long serialVersionUID = 1L;
	private UserDao userDao;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        userDao = new MysqlUserDao();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("welcome.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("errorMsg", null);
		request.setAttribute("errPwdUsr", null);
		User user = userDao.findByEmail(Util.sanitize(request.getParameter("email")));
		String errorMsg;
		if(user != null && user.getPassword().equals(request.getParameter("password"))){
			//add cookie
//			logger.debug(request.getParameter("remember"));
//			if(request.getParameter("remember").equals("on")){
//				//rememberIdendificationKey should be different for per user
//				Cookie remember = new Cookie("remember", "rememberIdendificationKey");
//				remember.setMaxAge(30* 24 * 60 * 60);
//				response.addCookie(remember);
//			}
			request.getSession().setAttribute("user", user);
			response.sendRedirect("home.jsp");
		}else{
			if(user == null){
				errorMsg = "No such user!";
				request.setAttribute("errorMsg", errorMsg);
				request.getRequestDispatcher("welcome.jsp").forward(request, response);
			}else if(!user.getPassword().equals(request.getParameter("password"))){
				errorMsg = "incorrect password!";
				request.setAttribute("errorMsg", errorMsg);
				request.setAttribute("errPwdUsr", user.getEmail());
				request.getRequestDispatcher("welcome.jsp").forward(request, response);
			}
		}			
		}
}
