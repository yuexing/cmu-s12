package amixyue.webapp.servlet;

import java.io.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import amixyue.webapp.model.Photo;

/**
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 * Servlet implementation class ImageServlet
 */
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Photo photo = (Photo) request.getAttribute("photo");
		
		if (photo == null) {
			//default pic
			String path = "/images/avarta.jpg";
			ServletContext context = getServletContext();
			InputStream is = context.getResourceAsStream(path);
			//log.debug(context.getRealPath(path));
			response.setContentType("image/jpg");
			ServletOutputStream out = response.getOutputStream();
	        int num = is.available();
	        byte[] bytes = new byte[num];
	        is.read(bytes);
	        out.write(bytes);
	        is.close();
        	return;
        }
        
        response.setContentType(photo.getContentType());

        ServletOutputStream out = response.getOutputStream();
        out.write(photo.getBytes());
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
