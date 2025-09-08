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
  
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// Validate session
		if (!Helper.isLogin(request)) {
			return mapping.findForward(Constants.T001_LOGIN);
		}
		String action = request.getParameter(Constants.PARAM_ACTION);
		if (Constants.ACTION_SAVE.equals(action)) {
			return save(mapping, form, request, response);
		}
		return load(mapping, form, request, response);
	}
	/**
	 * Loads the customer form.
	 */
	public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
	    T003Form t003Form = (T003Form) form;
	    t003Service.getCustomerById(t003Form);
	    return mapping.findForward(Constants.T003_EDIT);
	}

	/**
	 * Saves the customer (insert or update).
	 */
	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	                           HttpServletResponse response) throws Exception {
	    T003Form t003Form = (T003Form) form;
	    HttpSession session = request.getSession(false);
		T001Dto loggedInUser = (T001Dto) session.getAttribute(Constants.SESSION_USER);
		Integer psnCd = (loggedInUser != null) ? loggedInUser.getPsnCd() : null;
	    boolean success = t003Service.saveCustomer(t003Form, psnCd);
        if (success) {
            return mapping.findForward(Constants.T002_SEARCH);
        } else {
            return mapping.findForward(Constants.T003_EDIT);
        }
	}
}
