package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybeans.form.FileProperty;
import org.mybeans.form.FormBeanFactory;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.*;
import beauty.web.model.*;
import beauty.web.model.User.Type;

public class AddCommentAction extends Action {

	private FormBeanFactory<CommentGetForm> getformBeanFactory = FormBeanFactory
			.getInstance(CommentGetForm.class);

	private FormBeanFactory<CommentPostForm> postformBeanFactory = FormBeanFactory
			.getInstance(CommentPostForm.class);

	public AddCommentAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "addcomment.do";
	}

	public Type[] getTypes() {
		return new Type[] { Type.admin, Type.manufacturer, Type.retail };
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		List<String> errors = new ArrayList<String>();
		CommentGetForm getform = null;
		CommentPostForm postform = null;
		try {
			User u = (User) request.getSession().getAttribute("user");
			request.setAttribute("comments", ds.getComments());
			Comment co = null;
			if (request.getMethod().equals("POST")) {
				postform = postformBeanFactory.create(request);
				errors.addAll(postform.getValidationErrors());
				if (errors.size() > 0) {
					request.setAttribute("form", postform);
					request.setAttribute("errors", errors);
					return "addcomment.jsp";
				}
				FileProperty file = postform.getFile();
				if (postform.isEdit()) {
					co = ds.getComment(Integer.parseInt(postform.getId()));
					if (co.getUserId() == u.getId() || u.isAdmin()) {
						postform.populateComment(co);
						ds.updateComment(co, file);
					} else {
						errors.add("Permission denied.");
						request.setAttribute("errors", errors);
						return "addcomment.jsp";
					}
				} else {
					co = new Comment();
					postform.populateComment(co);
					co.setUserId(u.getId());
					ds.saveComment(co, file);
				}
				// success!
				return "index.jsp";
			} else {
				getform = getformBeanFactory.create(request);

				errors.addAll(getform.getValidationErrors());

				if (errors.size() > 0) {
					request.setAttribute("form", getform);
					request.setAttribute("errors", errors);
					return "addcomment.jsp";
				}

				if (getform.isEdit()) {
					co = ds.getComment(Integer.parseInt(getform.getId()));
					getform.populateForm(co);
				}

				request.setAttribute("form", getform);
				return "addcomment.jsp";
			}

		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			if (getform != null) {
				request.setAttribute("form", getform);
			} else {
				request.setAttribute("form", postform);
			}
			request.setAttribute("errors", errors);
			return "addcomment.jsp";
		}
	}

}
