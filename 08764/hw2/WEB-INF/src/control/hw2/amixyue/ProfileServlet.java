package control.hw2.amixyue;

import impl.dao.hw2.amixyue.ArrayUserDao;

import java.io.IOException;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.hw2.amixyue.User;

import dao.hw2.amixyue.UserDao;

/**
 * Servlet implementation class ProfileServlet
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDao userDao;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProfileServlet() {
        super();
        userDao = new ArrayUserDao();
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
		User user = (User) request.getSession().getAttribute("user");
		user.setEmail(request.getParameter("email"));
		user.setFname(request.getParameter("fname"));
		user.setLname(request.getParameter("lname"));
		//user.setPassword(request.getParameter("password"));
		user.setTzone(TimeZone.getTimeZone(request.getParameter("tzone")));
		user.setSignature(request.getParameter("signature"));
		User currentUser = (User)request.getSession().getAttribute("user");
		userDao.update(currentUser.getId(), user);
		response.sendRedirect("home.jsp");
	}

}
