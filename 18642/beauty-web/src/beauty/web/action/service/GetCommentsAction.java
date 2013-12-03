package beauty.web.action.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.mybeans.form.*;

import beauty.web.action.Type;
import beauty.web.action.service.msg.CommentMsg;
import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.TypeForm;
import beauty.web.model.*;

import com.google.gson.Gson;

/**
 * Get comments according to product and id
 * or user and id
 * 
 * @author amixyue
 *
 */
public class GetCommentsAction extends Action {	

	private FormBeanFactory<TypeForm> formBeanFactory = FormBeanFactory
			.getInstance(TypeForm.class);

	public GetCommentsAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "getcomments.d";
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		CommentMsg cmsg = new CommentMsg();

		Comment[] comments = null;

		List<String> errors = new ArrayList<String>();

		try {
			TypeForm form = formBeanFactory.create(request);

			errors.addAll(form.getValidationErrors());

			if (errors.size() > 0) {
				cmsg.setErrors(errors);
				return new Gson().toJson(cmsg);
			}

			Type type = form.getTypeType();
			int id = form.getIdInt();

			switch (type) {
			case product:
				comments = this.ds.getCommentsByProduct(id);
				break;
			case user:
				comments = this.ds.getCommentsByUser(id);
				break;
			default:
				errors.add("Unknown type");
				cmsg.setErrors(errors);
				return new Gson().toJson(cmsg);
			}

			cmsg.setComments(parse(comments));
			return new Gson().toJson(cmsg);
		} catch (Exception e) {
			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			cmsg.setErrors(errors);
			return new Gson().toJson(cmsg);
		}
	}	
}
