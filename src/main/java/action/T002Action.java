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

	/** Service layer instance for CSV export operations */
	private static final T005Service t005Service = T005Service.getInstance();

	/**
	 * Executes customer-related actions (search, remove, export).
	 * <p>
	 * - Validates session first (redirect to login if not logged in).  
	 * - Reads the action from {@link T002Form}.  
	 * - Routes request to the corresponding handler:  
	 *   - {@code ACTION_REMOVE}: delete a customer.  
	 *   - {@code ACTION_SEARCH}: search for customers.  
	 *   - {@code ACTION_EXPORT}: export customers to CSV.  
	 *   - Default: perform search.  
	 * </p>
	 *
	 * @param mapping   the {@link ActionMapping} used to select this instance
	 * @param form      the {@link ActionForm} containing customer search input
	 * @param request   the {@link HttpServletRequest} object
	 * @param response  the {@link HttpServletResponse} object
	 * @return the {@link ActionForward} indicating the next view or action
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) throws Exception {

	    // Validate that the user is logged in; otherwise, redirect to login page
	    if (!Helper.isLogin(request)) {
	        return mapping.findForward(Constants.T001_LOGIN);
	    }

	    // Cast generic form to T002Form (customer search form)
	    T002Form searchForm = (T002Form) form;

	    // Retrieve requested action from the form
	    String action = searchForm.getAction();

	    // If no action specified, default to searching customers
	    if (action == null) {
	        return findCustomer(mapping, form, request, response);
	    }

	    // Handle specific actions based on user input
	    switch (action) {
	        case Constants.ACTION_REMOVE:
	            // Remove customer
	            return deleteCustomer(mapping, form, request, response);
	        case Constants.ACTION_SEARCH:
	            // Search customers
	            return findCustomer(mapping, form, request, response);
	        case Constants.ACTION_EXPORT:
	            // Export customer list to CSV
	            return exportCSV(mapping, form, request, response);
	        default:
	            // Fallback to searching customers
	            return findCustomer(mapping, form, request, response);
	    }
	}


	/**
	 * Handles customer deletion action.
	 * <p>
	 * - Retrieves selected customer IDs from the {@link T002Form}.  
	 * - Calls the service layer to delete those customers.  
	 * - After deletion, redirects to the customer search screen to refresh the list.  
	 * </p>
	 *
	 * @param mapping   the {@link ActionMapping} used to select this instance
	 * @param form      the {@link ActionForm} containing customer deletion input
	 * @param request   the {@link HttpServletRequest} object
	 * @param response  the {@link HttpServletResponse} object
	 * @return the {@link ActionForward} pointing to the refreshed customer search view
	 * @throws Exception if an error occurs during deletion
	 */
	public ActionForward deleteCustomer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) throws Exception {

	    // Cast generic ActionForm to T002Form (customer form)
	    T002Form t002Form = (T002Form) form;

	    // Call service layer to delete customers by their IDs
	    t002Service.deleteCustomers(t002Form.getCustomerIds());

	    // After deletion, forward to search action to refresh customer list
	    return findCustomer(mapping, form, request, response);
	}


	/**
	 * Core method that performs customer listing and searching with pagination.
	 * <p>
	 * - Retrieves or initializes the search condition object (SCO) from session.  
	 * - Calls the service layer to perform search based on criteria in {@link T002Form}.  
	 * - Restores or initializes column header configuration for the search results.  
	 * - Updates the session with the latest search condition.  
	 * - Forwards to the T002 JSP page displaying customer data.  
	 * </p>
	 *
	 * @param mapping   the {@link ActionMapping} used to select this instance
	 * @param form      the {@link ActionForm} containing search criteria
	 * @param request   the {@link HttpServletRequest} being processed
	 * @param response  the {@link HttpServletResponse} being created
	 * @return an {@link ActionForward} pointing to the T002 search results page
	 * @throws Exception if an application-level error occurs
	 */
	private ActionForward findCustomer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) throws Exception {

	    // Retrieve session object
	    HttpSession session = request.getSession();

	    // Cast generic ActionForm to T002Form (customer search form)
	    T002Form t002Form = (T002Form) form;

	    // Retrieve previously stored search conditions (SCO) from session
	    T002SCO sco = (T002SCO) session.getAttribute(Constants.SESSION_T002_SCO);

	    // Perform search with criteria from form and previous SCO
	    sco = t002Service.searchCustomers(t002Form, sco);

	    // Try to restore column headers from session if available
	    T005Form sessionForm = (T005Form) session.getAttribute("columnHeader");
	    if (sessionForm != null) {
	        // Use saved custom column headers
	        List<ColumnHeader> headers = sessionForm.getRightHeaders();
	        t002Form.setColumnHeaders(headers);
	    } else {
	        // Fallback: use default column headers from service
	        t002Form.setColumnHeaders(t005Service.getDefaultRightHeaders());
	    }

	    // Save updated search condition back to session
	    session.setAttribute(Constants.SESSION_T002_SCO, sco);

	    // Forward to T002 search results page
	    return mapping.findForward(Constants.T002_SEARCH);
	}

	/**
	 * Exports customer data to a CSV file based on search conditions.
	 * <p>
	 * - Retrieves the current search condition object (SCO) from session.  
	 * - Calls the service layer to generate CSV data string.  
	 * - Builds a dynamic file name for the export file.  
	 * - Configures HTTP response headers to trigger a CSV file download.  
	 * - Writes the CSV content directly to the response output stream.  
	 * - Returns {@code null} because the response is already committed.  
	 * </p>
	 *
	 * @param mapping   the {@link ActionMapping} used to select this instance
	 * @param form      the {@link ActionForm} containing search criteria
	 * @param request   the {@link HttpServletRequest} being processed
	 * @param response  the {@link HttpServletResponse} to which CSV data is written
	 * @return {@code null} since the response is directly written to output stream
	 * @throws Exception if an error occurs while generating or writing the CSV
	 */
	public ActionForward exportCSV(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) throws Exception {

	    // Cast the generic ActionForm to T002Form (customer search form)
	    T002Form t002Form = (T002Form) form;

	    // Retrieve session to access stored search condition object (SCO)
	    HttpSession session = request.getSession();
	    T002SCO sco = (T002SCO) session.getAttribute(Constants.SESSION_T002_SCO);

	    // Generate CSV data string from service layer using form criteria and SCO
	    String csvData = t002Service.exportCustomersToCSV(t002Form, sco);

	    // Generate a file name for the CSV export
	    String fileName = t002Service.generateFileName();

	    // Configure HTTP response headers for CSV file download
	    response.setContentType("text/csv; charset=UTF-8");
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
	    response.setCharacterEncoding("UTF-8");

	    // Write CSV data to response output stream using UTF-8 encoding
	    try (PrintWriter writer = new PrintWriter(
	            new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
	        writer.write(csvData);
	    }

	    // Return null since response is already committed (download triggered)
	    return null;
	}


}