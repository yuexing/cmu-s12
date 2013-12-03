package beauty.web.action.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybeans.form.FileProperty;
import org.mybeans.form.FormBeanFactory;

import com.google.gson.Gson;

import beauty.web.action.service.msg.BaseMsg;
import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.*;
import beauty.web.model.*;

public class AddCommentAction extends Action {

	private FormBeanFactory<CommentPostForm> postformBeanFactory = FormBeanFactory
			.getInstance(CommentPostForm.class);

	public AddCommentAction(DataService ds) {
		this.ds = ds;
	}

	public String getName() {
		return "addcomment.d";
	}

	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		List<String> errors = new ArrayList<String>();
		CommentPostForm postform = null;
		
		BaseMsg bmsg = new BaseMsg();

		try {
			Comment co = null;

			postform = postformBeanFactory.create(request);

			errors.addAll(postform.getValidationErrors());

			if (errors.size() > 0) {
				bmsg.setErrors(errors);
				return new Gson().toJson(bmsg);
			}

			FileProperty file = postform.getFile();

			if (postform.isEdit()) {
				co = ds.getComment(Integer.parseInt(postform.getId()));
				postform.populateComment(co);
				ds.updateComment(co, file);
			} else {
				co = new Comment();
				postform.populateComment(co);
				ds.saveComment(co, file);
			}
			// success!
			return new Gson().toJson(bmsg);

		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			bmsg.setErrors(errors);
			return new Gson().toJson(bmsg);
		}
	}

}
