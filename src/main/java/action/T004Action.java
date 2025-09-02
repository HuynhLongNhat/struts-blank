package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import common.Constants;
import dto.T001Dto;
import form.T004Form;
import service.T004Service;
import utils.Helper;

/**
 * Action class for handling T004 import functionality.
 * Handles import requests, session validation, and message handling.
 */
public class T004Action extends Action {

    private T004Service t004Service = new T004Service();

    /**
     * Entry point for T004 action.
     * Validates user session and delegates to specific actions.
     *
     * @param mapping  ActionMapping for this request
     * @param form     ActionForm submitted by the user
     * @param request  HttpServletRequest object
     * @param response HttpServletResponse object
     * @return ActionForward destination after processing
     * @throws Exception if any error occurs during processing
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // Validate user session
        if (!Helper.isLogin(request)) {
            return mapping.findForward(Constants.T001_LOGIN);
        }
        T004Form t004Form = (T004Form) form;
        String action = t004Form.getAction();
        // Check if the action is import
        if (Constants.ACTION_IMPORT.equals(action)) {
            return processImport(mapping, t004Form, request);
        }
        // Default forward to import page
        return mapping.findForward(Constants.T004_IMPORT);
    }

    /**
     * Processes the import action.
     * Retrieves logged-in user, calls service to import file, and handles success/errors messages.
     *
     * @param mapping ActionMapping for this request
     * @param form    T004Form containing uploaded file and other form data
     * @param request HttpServletRequest object
     * @return ActionForward destination after import processing
     * @throws Exception if any error occurs during import
     */
    private ActionForward processImport(ActionMapping mapping, T004Form form, HttpServletRequest request)
            throws Exception {
        // Get current session and logged-in user
        HttpSession session = request.getSession(false);
        T001Dto loggedInUser = (T001Dto) session.getAttribute(Constants.SESSION_USER);
        Integer psnCd = (loggedInUser != null) ? loggedInUser.getPsnCd() : null;
        // Prepare ActionMessages for success messages
        ActionMessages messages = new ActionMessages();
        // Call service to import file; returns errors if any
        ActionMessages errors = t004Service.importFile(form.getUploadFile(), psnCd, messages);
        if (!errors.isEmpty()) {
            // If there are errors, save them into request scope
            saveErrors(request, errors);
        } else {
            // If successful, save success messages into request scope
            saveErrors(request, messages);
        }
        // Forward to the import page
        return mapping.findForward(Constants.T004_IMPORT);
    }
}
