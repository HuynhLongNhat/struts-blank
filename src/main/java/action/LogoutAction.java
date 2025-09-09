package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import common.Constants;

public class LogoutAction extends Action{

	/**
	 * Logs out the current user and redirects to the login page.
	 *
	 * @param mapping  The ActionMapping used to select this instance.
	 * @param form     The optional ActionForm bean for this request.
	 * @param request  The HTTP request we are processing.
	 * @param response The HTTP response we are creating.
	 * @return A redirect ActionForward to the login page.
	 * @throws Exception If an application-level error occurs.
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
	    // Retrieve the current session, do not create a new one if it doesn't exist
	    HttpSession session = request.getSession(false);
	    // If session exists, clear user-related data
	    if (session != null) {
	        // Remove the logged-in user information
	        session.removeAttribute("user");
	    }
	    // After logout, forward the user to the login page
	    return mapping.findForward(Constants.T001_LOGIN);
    }
}
