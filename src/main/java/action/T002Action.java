package action;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.MappingDispatchAction;

import common.Constants;
import dto.T002Dto;
import dto.T002SCO;
import form.T002Form;
import service.T002Service;
import utils.Helper;

/**
 * Action class responsible for handling customer search, listing, deletion, and
 * logout operations on the T002 screen.
 * <p>
 * This class extends {@link MappingDispatchAction} to support multiple methods
 * being mapped to different request parameters.
 * </p>
 */
public class T002Action extends MappingDispatchAction {

	/** Service layer instance for customer operations */
	private final T002Service t002Service = T002Service.getInstance();

	/** Number of records per page for pagination */
	private static final int PAGE_SIZE = 15;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String action = request.getParameter("action");
		if (action == null) {
	        // Có thể redirect về trang mặc định hoặc search chẳng hạn
	        return findCustomer(mapping, form, request, response);
	    }
		switch (action) {
        case Constants.ACTION_REMOVE:
            return deleteCustomer(mapping, form, request, response);

        case Constants.ACTION_LOGOUT:
            return logout(mapping, form, request, response);

        case Constants.ACTION_SEARCH:
            return findCustomer(mapping, form, request, response);

        default:
            // Nếu action không hợp lệ thì cũng có thể cho về search
            return findCustomer(mapping, form, request, response);
    }
	}

	/**
	 * Deletes selected customers based on the IDs submitted from the T002 screen.
	 *
	 * @param mapping  The ActionMapping used to select this instance.
	 * @param form     The ActionForm bean containing selected customer IDs.
	 * @param request  The HTTP request we are processing.
	 * @param response The HTTP response we are creating.
	 * @return An ActionForward to refresh the T002 JSP page after deletion.
	 * @throws Exception If an application-level error occurs.
	 */
	public ActionForward deleteCustomer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		T002Form t002Form = (T002Form) form;
		ActionErrors errors = t002Form.validate(mapping, request);
		// If no rows are selected, return with an error message
		if (!errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			return findCustomer(mapping, form, request, response);
		}
		int[] ids = t002Form.getCustomerIds();
		// Perform deletion using the service layer
		try {
			 t002Service.deleteCustomers(Arrays.stream(ids)
                                               .boxed()
                                               .collect(Collectors.toList()));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return findCustomer(mapping, form, request, response);
	}

	/**
	 * Core method that performs customer listing and searching with pagination.
	 *
	 * @param mapping  The ActionMapping used to select this instance.
	 * @param form     The ActionForm bean containing search criteria.
	 * @param request  The HTTP request we are processing.
	 * @param response The HTTP response we are creating.
	 * @return An ActionForward to the T002 JSP page with customer data.
	 * @throws Exception If an application-level error occurs.
	 */
	private ActionForward findCustomer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// Validate session
		if (!Helper.isLogin(request)) {
			return mapping.findForward(Constants.T001_LOGIN);
		}
		HttpSession session = request.getSession();
		T002Form t002Form = (T002Form) form;
		T002SCO sco = getSCO(request);
		// Handle search action
		String actionType = request.getParameter("actionType");
		if (Constants.ACTION_SEARCH.equals(actionType) || sco == null) {
			sco.setCustomerName(t002Form.getCustomerName());
			sco.setSex(t002Form.getSex());
			// Validate input
			ActionErrors errors = t002Form.validate(mapping, request);
			if (!errors.isEmpty()) {
				request.setAttribute(Globals.ERROR_KEY, errors);
			} else {
				sco.setBirthdayFrom(t002Form.getBirthdayFrom());
				sco.setBirthdayTo(t002Form.getBirthdayTo());
			}
			// Save SCO into session
			session.setAttribute("T002SCO", sco);
		}
		// Handle pagination
		int currentPage = 1;
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (NumberFormatException e) {
			currentPage = 1;
		}
		t002Form.setCurrentPage(currentPage);
		int offset = (currentPage - 1) * PAGE_SIZE;
		// Fetch customers from service and prepare data for JSP
		try {
			Map<String, Object> data = t002Service.searchCustomers(sco, offset, PAGE_SIZE);
			@SuppressWarnings("unchecked")
			List<T002Dto> customers = (List<T002Dto>) data.get("customers");
			int totalRecords = (int) data.get("totalCount");
			int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
			if (currentPage > totalPages && totalPages > 0) {
				currentPage = totalPages;
			}
			request.setAttribute("customers", customers);
			t002Form.setCurrentPage(currentPage);
			t002Form.setPrevPage((currentPage > 1) ? currentPage - 1 : 1);
			t002Form.setNextPage((currentPage < totalPages) ? currentPage + 1 : totalPages);
			t002Form.setTotalPages(totalPages);

			request.setAttribute("disableFirst", currentPage == 1);
			request.setAttribute("disablePrevious", currentPage == 1);
			request.setAttribute("disableNext", currentPage == totalPages || totalPages == 0);
			request.setAttribute("disableLast", currentPage == totalPages || totalPages == 0);

			t002Form.setCustomerName(sco.getCustomerName());
			t002Form.setSex(sco.getSex());
			t002Form.setBirthdayFrom(sco.getBirthdayFrom());
			t002Form.setBirthdayTo(sco.getBirthdayTo());

			return mapping.findForward(Constants.T002_SEARCH);
		} catch (Exception e) {
			e.printStackTrace();
			return mapping.findForward(Constants.T002_SEARCH);
			}
	}

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
	public ActionForward logout(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute("user");
		}
		return mapping.findForward("T001");
	}

	private T002SCO getSCO(HttpServletRequest request) {
		HttpSession session = request.getSession();
		T002SCO sco = (T002SCO) session.getAttribute("T002SCO");
		if (sco == null) {
			sco = new T002SCO();
			session.setAttribute("T002SCO", sco);
		}
		return sco;
	}
}
