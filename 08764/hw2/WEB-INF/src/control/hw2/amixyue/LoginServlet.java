package control.hw2.amixyue;

import impl.dao.hw2.amixyue.ArrayUserDao;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.hw2.amixyue.User;

import dao.hw2.amixyue.UserDao;

/**
 * Servlet implementation class LoginServlet
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDao userDao;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
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
//		request.getSession().setAttribute("errorMsg", null);
//		request.getSession().setAttribute("errPwdUsr", null);
		request.setAttribute("errorMsg", null);
		request.setAttribute("errPwdUsr", null);
		User user = userDao.findByEmail(request.getParameter("email"));
		String errorMsg;
		if(user != null && user.getPassword().equals(request.getParameter("password"))){
			request.getSession().setAttribute("user", user);
			response.sendRedirect("home.jsp");
		}else{
			if(user == null){
				errorMsg = "No such user!";
				//request.getSession().setAttribute("errorMsg", errorMsg);
				request.setAttribute("errorMsg", errorMsg);
				request.getRequestDispatcher("welcome.jsp").forward(request, response);
			}else if(!user.getPassword().equals(request.getParameter("password"))){
				errorMsg = "incorrect password!";
//				request.getSession().setAttribute("errorMsg", errorMsg);
//				request.getSession().setAttribute("errPwdUsr", user.getEmail());
				request.setAttribute("errorMsg", errorMsg);
				request.setAttribute("errPwdUsr", user.getEmail());
				request.getRequestDispatcher("welcome.jsp").forward(request, response);
			}
		}			
		}
}
