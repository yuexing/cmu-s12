package control.hw2.amixyue;

import impl.dao.hw2.amixyue.*;

import java.io.IOException;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.hw2.amixyue.User;

import dao.hw2.amixyue.*;

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
        userDao = new ArrayUserDao();
        //this.getServletContext().setAttribute("db", ArrayDB.getDB());
        //no need for context
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
		user.setEmail(request.getParameter("email"));
		user.setFname(request.getParameter("fname"));
		user.setLname(request.getParameter("lname"));
		user.setPassword(request.getParameter("password"));
		user.setTzone(TimeZone.getTimeZone(request.getParameter("tzone")));
		userDao.insert(user);
		request.getSession().setAttribute("user", user);
		response.sendRedirect("home.jsp");
	}

}
