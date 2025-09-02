package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.MappingDispatchAction;

import common.Constants;
import dto.T001Dto;
import form.T001Form;
import service.T001Service;
import utils.Helper;

/**
 * Action class for handling login-related operations (Login, Logout).
 * <p>
 * This class uses {@link MappingDispatchAction} to handle multiple actions
 * (showing login form, processing login, and logout) within a single Action
 * class.
 * </p>
 */
public class T001Action extends Action {

	/** Service class responsible for login operations. */
	private final T001Service t001Service = T001Service.getInstance();

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (Helper.isLogin(request)) {
			// User already logged in, redirect to T002
			return mapping.findForward(Constants.T002_SEARCH);
		} 
		String action = request.getParameter(Constants.PARAM_ACTION);
		if (Constants.ACTION_LOGIN.equals(action)) {
			return getUserLogin(mapping, form, request, response);
		}
		return showLoginForm(mapping, form, request, response);
	}

	/**
	 * Displays the login form.
	 * <p>
	 * If the user is already logged in (validated by
	 * {@link Helper#isLogin(HttpServletRequest)}), it redirects to the T002 action;
	 * otherwise, it forwards to the login page.
	 * </p>
	 *
	 * @param mapping  the action mapping used to select this instance
	 * @param form     the optional ActionForm bean for this request
	 * @param request  the HTTP request we are processing
	 * @param response the HTTP response we are creating
	 * @return an {@link ActionForward} to either T002.do (redirect) or login page
	 * @throws Exception if any application-level error occurs
	 */
	public ActionForward showLoginForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
			// Show login page
			return mapping.findForward(Constants.T001_LOGIN);
	}

	/**
	 * Processes user login.
	 * <p>
	 * Validates user credentials by calling
	 * {@link T001Service#getUserLogin(T001Dto)}. If authentication is successful,
	 * the user information is stored in the session and the request is redirected
	 * to T002.do. Otherwise, an error message is added and the user is returned to
	 * the login page.
	 * </p>
	 *
	 * @param mapping  the action mapping used to select this instance
	 * @param form     the login form containing user credentials
	 * @param request  the HTTP request we are processing
	 * @param response the HTTP response we are creating
	 * @return an {@link ActionForward} to either T002.do (redirect) or login page
	 * @throws Exception if any application-level error occurs
	 */
	public ActionForward getUserLogin(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		T001Form loginForm = (T001Form) form;
		T001Dto user = t001Service.getUserLogin(loginForm);
		if (user != null) {
			// Store user in session after successful login
			HttpSession session = request.getSession();
			session.setAttribute(Constants.SESSION_USER, user);
			return mapping.findForward(Constants.T002_SEARCH);
		} else {
			ActionMessages errors = new ActionMessages();
			// Login failed, return to login page with error message
			errors.add(
				    Constants.GLOBAL, 
				    new ActionMessage(Constants.ERROR_MSG_LOGIN_FAILED)
				);
			saveErrors(request, errors);
			return mapping.findForward(Constants.T001_LOGIN);
		}
	}
}
