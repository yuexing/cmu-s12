package hw2.amixyue.control;

import hw2.amixyue.dao.UserDao;
import hw2.amixyue.dao.imp.ArrayUserDao;
import hw2.amixyue.dao.imp.MysqlUserDao;
import hw2.amixyue.util.Util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
        userDao = new MysqlUserDao();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter pw = response.getWriter();
		if(userDao.findByEmail(Util.sanitize(request.getParameter("email"))) != null){
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
