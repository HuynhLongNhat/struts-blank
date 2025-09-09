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
	private static final T001Service t001Service = T001Service.getInstance();

	/**
	 * Executes the login action logic.
	 *
	 * @param mapping   the {@link ActionMapping} used to select this instance
	 * @param form      the {@link ActionForm} bean for this request
	 * @param request   the {@link HttpServletRequest} object
	 * @param response  the {@link HttpServletResponse} object
	 * @return the {@link ActionForward} indicating the next view or action
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
	    // Check if the user is already logged in
	    if (Helper.isLogin(request)) {
	        // User already logged in, redirect to the T002 search screen
	        return mapping.findForward(Constants.T002_SEARCH);
	    }
	    // Cast the generic ActionForm to T001Form (login form)
	    T001Form loginForm = (T001Form) form;
	    // Get the action submitted from the login form
	    String action = loginForm.getAction();
	    // If the action is "login", process user authentication
	    if (Constants.ACTION_LOGIN.equals(action)) {
	        return getUserLogin(mapping, form, request, response);
	    }
	    // If no action or another action, display the login form
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
	 * - Retrieves the user login information from the submitted form.  
	 * - Calls the service layer to validate credentials.  
	 * - If login is successful, store the user in session and redirect to the search screen (T002).  
	 * - If login fails, add an error message and return to the login page.  
	 * </p>
	 *
	 * @param mapping   the {@link ActionMapping} used to select this instance
	 * @param form      the {@link ActionForm} bean containing login input
	 * @param request   the {@link HttpServletRequest} object
	 * @param response  the {@link HttpServletResponse} object
	 * @return the {@link ActionForward} indicating the next view or action
	 * @throws Exception if an error occurs during execution
	 */
	public ActionForward getUserLogin(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
	    // Cast the generic ActionForm to the specific T001Form (login form)
	    T001Form loginForm = (T001Form) form;
	    // Call service to validate user credentials
	    T001Dto user = t001Service.getUserLogin(loginForm);
	    // If user is valid (login success)
	    if (user != null) {
	        // Create or retrieve session
	        HttpSession session = request.getSession();
	        // Store user information in session for later use
	        session.setAttribute(Constants.SESSION_USER, user);
	        // Redirect to T002 search screen after successful login
	        return mapping.findForward(Constants.T002_SEARCH);
	    } else {
	        // Create an ActionMessages object to hold error messages
	        ActionMessages errors = new ActionMessages();
	        // Add a global login failed error message
	        errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_MSG_LOGIN_FAILED));
	        // Save error messages so they can be displayed on the login page
	        saveErrors(request, errors);
	        // Forward back to login page after failed login attempt
	        return mapping.findForward(Constants.T001_LOGIN);
	    }
	}

}
