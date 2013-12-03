package beauty.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybeans.form.FormBeanFactory;

import beauty.web.controller.Action;
import beauty.web.dataservice.DataService;
import beauty.web.formbean.TagForm;
import beauty.web.model.Tag;
import beauty.web.model.User;
import beauty.web.model.User.Type;

public class AddTagAction extends Action {

	

	private FormBeanFactory<TagForm> formBeanFactory = FormBeanFactory
			.getInstance(TagForm.class);

	public AddTagAction(DataService ds) {
		this.ds = ds;
	}

	@Override
	public String getName() {
		return "addtag.do";
	}
	
	public Type[] getTypes(){
		return new Type[]{Type.admin, Type.manufacturer};
	}

	@Override
	public String perform(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		List<String> errors = new ArrayList<String>();
		TagForm form = null;

		try {
			User u = (User) request.getSession().getAttribute("user");
			request.setAttribute("tags", ds.getTags());
			form = formBeanFactory.create(request);
			if (request.getMethod().equals("POST")) {
				form.setGet(false);
			} else {
				form.setGet(true);
			}
			errors.addAll(form.getValidationErrors());
			if (errors.size() > 0) {
				request.setAttribute("form", form);
				request.setAttribute("errors", errors);
				return "addtag.jsp";
			}
			Tag t = null;
			if (form.isGet()) {
				if (form.isEdit()) {
					t = ds.getTag(Integer.parseInt(form.getId()));
					form.setName(t.getName());
				}
				request.setAttribute("form", form);
				return "addtag.jsp";

			} else {
				if (form.isEdit() || u.isAdmin()) {
					t = ds.getTag(Integer.parseInt(form.getId()));
					if(t.getOwner() == u.getId()){
					form.populateTag(t);
					// it is force by default
					ds.updateTag(t);
					} else {
						errors.add("Permission denied.");
						request.setAttribute("errors", errors);
						return "addtag.jsp";
					}
				} else {
					t = new Tag();
					form.populateTag(t);
					t.setOwner(u.getId());					
					if (form.isForce()) {
						ds.saveTagForce(t);
					} else {
						ds.saveTag(t);
					}
				}
				// success!
				return "index.jsp";
			}
		} catch (Exception e) {

			e.printStackTrace(); // for test
			errors.add(e.getMessage());
			request.setAttribute("form", form);
			request.setAttribute("errors", errors);
			return "addtag.jsp";
		}

	}

}
