package action;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.MappingDispatchAction;

import common.Constants;
import dto.T002SCO;
import form.ColumnHeader;
import form.T002Form;
import form.T005Form;
import service.T002Service;
import service.T005Service;
import utils.Helper;

/**
 * Action class responsible for handling customer search, listing, deletion, and
 * logout operations on the T002 screen.
 * <p>
 * This class extends {@link MappingDispatchAction} to support multiple methods
 * being mapped to different request parameters.
 * </p>
 */
public class T002Action extends Action {

	/** Service layer instance for customer operations */
	private static final T002Service t002Service = T002Service.getInstance();
	private static final T005Service t005Service = T005Service.getInstance();

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// Validate session
		if (!Helper.isLogin(request)) {
			return mapping.findForward(Constants.T001_LOGIN);
		}
		T002Form searchForm = (T002Form) form;
		String action = searchForm.getAction();
		if (action == null) {
			return findCustomer(mapping, form, request, response);
		}
		switch (action) {
		case Constants.ACTION_REMOVE:
			return deleteCustomer(mapping, form, request, response);
		case Constants.ACTION_SEARCH:
			return findCustomer(mapping, form, request, response);
		case Constants.ACTION_EXPORT:
			return exportCSV(mapping, form, request, response);
		default:
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
		t002Service.deleteCustomers(t002Form.getCustomerIds());
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
		HttpSession session = request.getSession();
		T002Form t002Form = (T002Form) form;
		T002SCO sco = (T002SCO) session.getAttribute(Constants.SESSION_T002_SCO);
		sco = t002Service.searchCustomers(t002Form, sco);
		T005Form sessionForm = (T005Form) session.getAttribute("columnHeader");
		if (sessionForm != null) {
			List<ColumnHeader> headers = sessionForm.getRightHeaders();
			t002Form.setColumnHeaders(headers);
		} else {
			t002Form.setColumnHeaders(t005Service.getDefaultRightHeaders());
		}

		session.setAttribute(Constants.SESSION_T002_SCO, sco);
		return mapping.findForward(Constants.T002_SEARCH);
	}

	/**
	 * Exports customer data to CSV file based on search conditions.
	 *
	 * @param mapping  The ActionMapping used to select this instance.
	 * @param form     The ActionForm bean containing search criteria.
	 * @param request  The HTTP request we are processing.
	 * @param response The HTTP response we are creating.
	 * @return null as the response is directly written to output stream.
	 * @throws Exception If an application-level error occurs.
	 */
	public ActionForward exportCSV(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		T002Form t002Form = (T002Form) form;
		HttpSession session = request.getSession();
		T002SCO sco = (T002SCO) session.getAttribute(Constants.SESSION_T002_SCO);
		String csvData = t002Service.exportCustomersToCSV(t002Form, sco);
		String fileName = t002Service.generateFileName();
		response.setContentType("text/csv; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		response.setCharacterEncoding("UTF-8");

		try (PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
			writer.write(csvData);
		}
		// Response is committed
		return null;

	}

}