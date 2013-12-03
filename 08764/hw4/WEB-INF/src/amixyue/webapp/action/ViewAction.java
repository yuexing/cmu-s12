package amixyue.webapp.action;

import javax.servlet.http.HttpServletRequest;

import org.genericdao.RollbackException;

import amixyue.webapp.dao.Model;
import amixyue.webapp.dao.PhotoDao;
import amixyue.webapp.model.Photo;
/**
 * view.do actually looks up the photo bean by "id" and then passes it
 * (via request attribute) to the ImageServlet.  See also the mapping
 * of /image in the web.xml file.
 * for security and for dao
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class ViewAction extends Action {

	private PhotoDao photoDao;
	
	public ViewAction(Model model) {
		photoDao = model.getPhotoDao();
	}

	@Override
	public String getName() {
		return "view.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		int uid = Integer.parseInt(request.getParameter("uid"));
		Photo p = null;
		try {
			p = photoDao.read(uid);
		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("photo", p);
		return "image";
	}

}