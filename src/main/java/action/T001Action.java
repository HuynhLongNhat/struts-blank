package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import dto.T001Dto;
import form.T001Form;
import service.T001Service;

public class T001Action extends Action {

	private final T001Service t001Service = T001Service.getInstance();

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		T001Form loginForm = (T001Form) form;
		T001Dto user = t001Service.getUserLogin(loginForm);

		if (user != null) {
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			return mapping.findForward("T002");
		} else {
			ActionErrors errors = new ActionErrors();
			errors.add("errorMessage", new ActionMessage("error.login.failed"));
			saveErrors(request, errors);
			return mapping.findForward("T001");
		}
	}
}