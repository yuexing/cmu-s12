package amixyue.webapp.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.genericdao.RollbackException;
import org.mybeans.form.FileProperty;
import org.mybeans.form.FormBeanException;
import org.mybeans.form.FormBeanFactory;
import amixyue.webapp.dao.*;
import amixyue.webapp.formbean.ProfileForm;
import amixyue.webapp.model.*;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class ProfileAction extends Action {

	private FormBeanFactory<ProfileForm> formBeanFactory = FormBeanFactory
			.getInstance(ProfileForm.class);
	
	private PhotoDao photoDao;
	private UserDao userDao;

	public ProfileAction(Model model) {
		photoDao = model.getPhotoDao();
		userDao = model.getUserDao();
	}

	@Override
	public String getName() {
		return "profile.do";
	}

	@Override
	public String perform(HttpServletRequest request) {
		// Set up the errors list
		List<String> errors = new ArrayList<String>();
		request.setAttribute("errors", errors);
		
		ProfileForm form = null;
		try {
			form = formBeanFactory.create(request);
		} catch (FormBeanException e) {
			e.printStackTrace();
		}
        
		User user = (User) request.getSession(false).getAttribute("user");
		FileProperty fileProp = form.getFile();
		if (fileProp != null) {
			Photo photo = new Photo(); 
			photo.setBytes(fileProp.getBytes());
			photo.setContentType(fileProp.getContentType());
			photo.setUid(user.getUid());
			//create or update
			try {
				if (photoDao.read(photo.getUid()) != null) {
					photoDao.update(photo);
				} else {
					photoDao.create(photo);
				}
				form.populateUser(user);
				userDao.update(user);
			} catch (RollbackException e) {
				e.printStackTrace();
			}
		}
		
		request.getSession(false).setAttribute("user", user);
		return "home.do";
	}

}
