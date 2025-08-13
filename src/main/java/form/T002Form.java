package form;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import utils.Helper;

/**
 * Form bean for the T002 screen (Customer Search).
 * <p>
 * This class captures user input for customer search criteria, selected
 * customer IDs, and pagination data for listing results.
 * </p>
 *
 * <p>
 * <b>Main responsibilities:</b>
 * </p>
 * <ul>
 * <li>Bind search criteria fields from the JSP (name, gender, birthday
 * range).</li>
 * <li>Capture selected customer IDs for bulk actions (e.g., delete).</li>
 * <li>Maintain pagination state (current page, total pages, etc.) for
 * consistent navigation.</li>
 * <li>Provide input validation logic for date formats and birthday ranges.</li>
 * </ul>
 *
 * <p>
 * <b>Pagination fields:</b>
 * </p>
 * <ul>
 * <li>{@code currentPage} - The currently displayed page number.</li>
 * <li>{@code prevPage} - The previous page number for navigation.</li>
 * <li>{@code nextPage} - The next page number for navigation.</li>
 * <li>{@code totalPages} - The total number of pages calculated from search
 * results.</li>
 * </ul>
 *
 * @author YourName
 * @version 1.1
 * @since 2025-07-21
 */
public class T002Form extends ActionForm {

	/** Serial version UID for serialization. */
	private static final long serialVersionUID = 1L;

	// =========================
	// Search criteria fields
	// =========================

	/** Name of the customer used for search filtering. */
	private String customerName;

	/** Gender of the customer ("0" for male, "1" for female, empty for all). */
	private String sex;

	/** Start date of the birthday range (format: yyyy/MM/dd). */
	private String birthdayFrom;

	/** End date of the birthday range (format: yyyy/MM/dd). */
	private String birthdayTo;

	// =========================
	// Selected customers (bulk actions)
	// =========================

	/** Array of selected customer IDs (e.g., for bulk delete). */
	private String[] customerIds;

	// =========================
	// Pagination fields
	// =========================

	/** The currently displayed page number. */
	private int currentPage = 1;

	/** The previous page number for navigation. */
	private int prevPage = 1;

	/** The next page number for navigation. */
	private int nextPage = 1;

	/** The total number of pages. */
	private int totalPages = 1;

	// =========================
	// Getters and Setters
	// =========================

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthdayFrom() {
		return birthdayFrom;
	}

	public void setBirthdayFrom(String birthdayFrom) {
		this.birthdayFrom = birthdayFrom;
	}

	public String getBirthdayTo() {
		return birthdayTo;
	}

	public void setBirthdayTo(String birthdayTo) {
		this.birthdayTo = birthdayTo;
	}

	public String[] getCustomerIds() {
		return customerIds;
	}

	public void setCustomerIds(String[] customerIds) {
		this.customerIds = customerIds;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(int prevPage) {
		this.prevPage = prevPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	// =========================
	// Validation logic
	// =========================

	/**
	 * Validates user input for the T002 screen.
	 * <p>
	 * Ensures that:
	 * <ul>
	 * <li>Birthday fields follow the yyyy/MM/dd format (if provided).</li>
	 * <li>If both birthdayFrom and birthdayTo are provided, birthdayTo is not
	 * earlier than birthdayFrom.</li>
	 * </ul>
	 * </p>
	 *
	 * @param mapping The action mapping used to select this instance.
	 * @param request The HTTP request being processed.
	 * @return An ActionErrors object containing any validation errors; empty if
	 *         validation passes.
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
	    ActionErrors errors = new ActionErrors();

	    String actionType = request.getParameter("actionType"); // hoặc actionType
	    if ("delete".equals(actionType)) {
	        if (customerIds == null || customerIds.length == 0) {
	            errors.add("customerId", new ActionMessage("error.customerId.required"));
	            return errors;
	        }
	    }
	    if ("search".equals(actionType)) {
	        validateBirthday(errors);
	    }

	    return errors;
	}

	private void validateBirthday(ActionErrors errors) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	    try {
	        LocalDate from = null;
	        LocalDate to = null;

	        if (!Helper.isEmpty(birthdayFrom)) {
	            from = LocalDate.parse(birthdayFrom.trim(), formatter);
	        }
	        if (!Helper.isEmpty(birthdayTo)) {
	            to = LocalDate.parse(birthdayTo.trim(), formatter);
	        }
	        if (from != null && to != null && to.isBefore(from)) {
	            errors.add("birthdayRange", new ActionMessage("error.birthday.range"));
	        }
	    } catch (DateTimeParseException e) {
	        if (e.getParsedString().equals(birthdayFrom)) {
	            errors.add("birthdayFrom", new ActionMessage("error.birthdayFrom.format"));
	        } else {
	            errors.add("birthdayTo", new ActionMessage("error.birthdayTo.format"));
	        }
	    }
	}

}
