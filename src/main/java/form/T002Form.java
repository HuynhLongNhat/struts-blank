package form;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import common.Constants;
import dto.T002Dto;
import utils.Helper;

/**
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
	private int[] customerIds;

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
    
	private boolean selectAll;
	
	private String action ;
	
	private List<T002Dto> customers;

	private boolean disabledFirst;
	private boolean disabledPrevious;
	public boolean isDisabledFirst() {
		return disabledFirst;
	}

	public void setDisabledFirst(boolean disabledFirst) {
		this.disabledFirst = disabledFirst;
	}

	public boolean isDisabledPrevious() {
		return disabledPrevious;
	}

	public void setDisabledPrevious(boolean disabledPrevious) {
		this.disabledPrevious = disabledPrevious;
	}

	public boolean isDisabledNext() {
		return disabledNext;
	}

	public void setDisabledNext(boolean disabledNext) {
		this.disabledNext = disabledNext;
	}

	public boolean isDisabledLast() {
		return disabledLast;
	}

	public void setDisabledLast(boolean disabledLast) {
		this.disabledLast = disabledLast;
	}

	private boolean disabledNext;
	private boolean disabledLast;
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

	public int[] getCustomerIds() {
		return customerIds;
	}

	public void setCustomerIds(int[] customerIds) {
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
	
	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * @return the customers
	 */
	public List<T002Dto> getCustomers() {
		return customers;
	}

	/**
	 * @param customers the customers to set
	 */
	public void setCustomers(List<T002Dto> customers) {
		this.customers = customers;
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
		String action = getAction();
		if (Constants.ACTION_REMOVE.equals(action)) {
			if (customerIds == null || customerIds.length == 0) {
				errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_CUSTOMER_ID_REQUIRED));
				return errors;
			}
		} else if (Constants.ACTION_SEARCH.equals(action)) {
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
				errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_BIRTHDAY_RANGE));
			}
		} catch (DateTimeParseException e) {
			if (e.getParsedString().equals(birthdayFrom)) {
				errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_BIRTHDAY_FROM_FORMAT));
			} else {
				errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_BIRTHDAY_TO_FORMAT));
			}
		}
	}



}
