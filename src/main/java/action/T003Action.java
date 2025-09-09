package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.MappingDispatchAction;

import common.Constants;
import dto.T001Dto;
import form.T003Form;
import service.T003Service;
import utils.Helper;

/**
 * Struts Action for T003 screen. Handles loading customer form (edit/add) and
 * saving customer data.
 */
public class T003Action extends MappingDispatchAction {

	private static final T003Service t003Service = T003Service.getInstance();
  
    /** 
     * Executes the action for handling customer-related requests.
     * <p>
     * This method validates the session first. If the user is not logged in,
     * it forwards to the login page. Otherwise, it checks the {@code action}
     * parameter from the request and dispatches to the appropriate handler
     * (e.g., {@code save} or {@code load}).
     * </p>
     *
     * @param mapping   the action mapping configuration
     * @param form      the form bean associated with the request
     * @param request   the HTTP servlet request
     * @param response  the HTTP servlet response
     * @return          the next forward (either login, save, or load)
     * @throws Exception if processing fails
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Validate user session, redirect to login if not authenticated
        if (!Helper.isLogin(request)) {
            return mapping.findForward(Constants.T001_LOGIN);
        }

        // Extract action parameter from request
        String action = request.getParameter(Constants.PARAM_ACTION);

        // Route request to correct handler based on action type
        if (Constants.ACTION_SAVE.equals(action)) {
            return save(mapping, form, request, response);
        }

        // Default action → load data
        return load(mapping, form, request, response);
    }

    /**
     * Loads the customer form for editing.
     * <p>
     * Retrieves the customer data by ID and populates the {@link T003Form}.
     * After loading, forwards the user to the customer edit page.
     * </p>
     *
     * @param mapping   the action mapping configuration
     * @param form      the form bean containing customer data
     * @param request   the HTTP servlet request
     * @param response  the HTTP servlet response
     * @return          forward to the edit page
     * @throws Exception if loading customer data fails
     */
    public ActionForward load(ActionMapping mapping, ActionForm form,
                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Cast the generic form to T003Form
        T003Form t003Form = (T003Form) form;

        // Populate the form with customer data from the service
        t003Service.getCustomerById(t003Form);

        // Forward to the edit page
        return mapping.findForward(Constants.T003_EDIT);
    }

    /**
     * Saves customer information (insert or update).
     * <p>
     * Uses the logged-in user information (if available) to associate the
     * save operation with a user. If save succeeds, forward to the search page;
     * otherwise, remain on the edit page.
     * </p>
     *
     * @param mapping   the action mapping configuration
     * @param form      the form bean containing customer data
     * @param request   the HTTP servlet request
     * @param response  the HTTP servlet response
     * @return          forward to the search page if successful, edit page otherwise
     * @throws Exception if saving customer data fails
     */
    public ActionForward save(ActionMapping mapping, ActionForm form,
                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Cast the generic form to T003Form
        T003Form t003Form = (T003Form) form;

        // Get the current session (false → do not create a new one if absent)
        HttpSession session = request.getSession(false);

        // Retrieve the logged-in user from session
        T001Dto loggedInUser = (T001Dto) session.getAttribute(Constants.SESSION_USER);

        // Extract the personal code if user exists, otherwise null
        Integer psnCd = (loggedInUser != null) ? loggedInUser.getPsnCd() : null;

        // Save customer data (insert/update) through service
        boolean success = t003Service.saveCustomer(t003Form, psnCd);

        // Redirect based on save result
        if (success) {
            return mapping.findForward(Constants.T002_SEARCH); // success → back to search page
        } else {
            return mapping.findForward(Constants.T003_EDIT);   // failure → stay on edit page
        }
    }

}
