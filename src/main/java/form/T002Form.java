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
 * Form bean for T002 screen.
 * <p>
 * This class holds user input data, manages pagination, selection,
 * and validation logic for searching and managing customers.
 * </p>
 *
 * @author  
 * @version 1.1
 * @since 2025-07-21
 */
public class T002Form extends ActionForm {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    // =========================
    // Search criteria fields
    // =========================

    /** Name of the customer used as a search filter. */
    private String customerName;

    /** Gender of the customer ("0" = male, "1" = female, empty = all). */
    private String sex;

    /** Start date of the birthday range (format: yyyy/MM/dd). */
    private String birthdayFrom;

    /** End date of the birthday range (format: yyyy/MM/dd). */
    private String birthdayTo;

    // =========================
    // Selected customers (bulk actions)
    // =========================

    /** Array of selected customer IDs (used for bulk actions such as delete). */
    private int[] customerIds;

    // =========================
    // Pagination fields
    // =========================

    /** Current page number being displayed. */
    private int currentPage = 1;

    /** Previous page number (used in pagination navigation). */
    private int prevPage = 1;

    /** Next page number (used in pagination navigation). */
    private int nextPage = 1;

    /** Total number of available pages. */
    private int totalPages = 1;

    /** Indicates whether the "Select All" checkbox is checked. */
    private boolean selectAll;

    /** The action being performed (e.g., search, remove, etc.). */
    private String action;

    /** List of customers returned from the search query. */
    private List<T002Dto> customers;

    // =========================
    // Pagination button states
    // =========================

    /** True if the "First" page button should be disabled. */
    private boolean disabledFirst;

    /** True if the "Previous" page button should be disabled. */
    private boolean disabledPrevious;

    /** True if the "Next" page button should be disabled. */
    private boolean disabledNext;

    /** True if the "Last" page button should be disabled. */
    private boolean disabledLast;

    // =========================
    // Table header fields
    // =========================

    /** List of column headers to be displayed in the result table. */
    private List<ColumnHeader> columnHeaders;

    // =========================
    // Getters and Setters
    // =========================

    public List<ColumnHeader> getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnHeaders(List<ColumnHeader> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

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
     * Gets the list of customers.
     * 
     * @return customers list
     */
    public List<T002Dto> getCustomers() {
        return customers;
    }

    /**
     * Sets the list of customers.
     * 
     * @param customers List of T002Dto
     */
    public void setCustomers(List<T002Dto> customers) {
        this.customers = customers;
    }
    
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
       this.action = null ;
       this.customerIds = null ;
    }

    // =========================
    // Validation logic
    // =========================

    /**
     * Validates user input for the T002 screen.
     * <p>
     * Ensures that:
     * <ul>
     *   <li>If action is "remove", at least one customer must be selected.</li>
     *   <li>If action is "search", birthday fields must follow yyyy/MM/dd format
     *       and the range must be valid.</li>
     * </ul>
     * </p>
     *
     * @param mapping The action mapping used to select this instance.
     * @param request The HTTP request being processed.
     * @return ActionErrors object containing validation errors (empty if valid).
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String action = getAction();

        // Validate bulk delete action
        if (Constants.ACTION_REMOVE.equals(action)) {
            if (customerIds == null || customerIds.length == 0) {
                errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_CUSTOMER_ID_REQUIRED));
                return errors;
            }
        }
        // Validate search action
        else if (Constants.ACTION_SEARCH.equals(action)) {
            validateBirthday(errors);
        }
        return errors;
    }

    /**
     * Validates the birthday range inputs.
     * <p>
     * Ensures that:
     * <ul>
     *   <li>BirthdayFrom and BirthdayTo (if provided) follow yyyy/MM/dd format.</li>
     *   <li>BirthdayTo is not earlier than BirthdayFrom.</li>
     * </ul>
     * </p>
     *
     * @param errors the ActionErrors object to collect validation issues
     */
    private void validateBirthday(ActionErrors errors) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        try {
            LocalDate from = null;
            LocalDate to = null;

            // Parse birthdayFrom if provided
            if (!Helper.isEmpty(birthdayFrom)) {
                from = LocalDate.parse(birthdayFrom.trim(), formatter);
            }

            // Parse birthdayTo if provided
            if (!Helper.isEmpty(birthdayTo)) {
                to = LocalDate.parse(birthdayTo.trim(), formatter);
            }

            // Validate date range consistency
            if (from != null && to != null && to.isBefore(from)) {
                errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_BIRTHDAY_RANGE));
            }
        } catch (DateTimeParseException e) {
            // Determine which field caused the error
            if (e.getParsedString().equals(birthdayFrom)) {
                errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_BIRTHDAY_FROM_FORMAT));
            } else {
                errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_BIRTHDAY_TO_FORMAT));
            }
        }
    }

}
