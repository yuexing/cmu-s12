package hw2.amixyue.control;

import hw2.amixyue.dao.*;
import hw2.amixyue.dao.imp.*;
import hw2.amixyue.model.User;
import hw2.amixyue.util.Util;

import java.io.IOException;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Servlet implementation class RegisterServlet
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private UserDao userDao;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
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
		User user = new User();
		//sanitize
		user.setEmail(Util.sanitize(request.getParameter("email")));
		user.setFname(Util.sanitize(request.getParameter("fname")));
		user.setLname(Util.sanitize(request.getParameter("lname")));
		user.setPassword(request.getParameter("password"));
		user.setTzone(TimeZone.getTimeZone(request.getParameter("tzone")));
		userDao.create(user);
		request.getSession().setAttribute("user", user);
		response.sendRedirect("home.jsp");
	}

}
