package control.hw2.amixyue;

import impl.dao.hw2.amixyue.ArrayUserDao;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.hw2.amixyue.UserDao;

/**
 * Servlet implementation class ChkUserServlet
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class ChkUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDao userDao;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChkUserServlet() {
        super();
        userDao = new ArrayUserDao();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pw = response.getWriter();
		if(userDao.findByEmail(request.getParameter("email")) != null){
			//has one and return false
			pw.println(0);
		}else{
			pw.println(1);
		}
		pw.flush();
		pw.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
