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
 * Represents the customer search form in a Struts 1.x application.
 * <p>
 * This form captures search criteria such as customer name, gender,
 * birthday range, and selected customer IDs from a JSP page.
 * </p>
 * 
 * <p><b>Fields include:</b></p>
 * <ul>
 *   <li>{@code customerName} - Name of the customer for search filtering.</li>
 *   <li>{@code sex} - Gender of the customer ("M", "F", etc.).</li>
 *   <li>{@code birthdayFrom} - Start date of the birthday range (format: yyyy/MM/dd).</li>
 *   <li>{@code birthdayTo} - End date of the birthday range (format: yyyy/MM/dd).</li>
 *   <li>{@code customerIds} - Array of selected customer IDs (e.g., for bulk actions).</li>
 * </ul>
 * 
 * <p><b>Validation rules:</b></p>
 * <ul>
 *   <li>Birthday fields must follow the {@code yyyy/MM/dd} format.</li>
 *   <li>If both birthday range fields are provided, {@code birthdayTo} cannot be earlier than {@code birthdayFrom}.</li>
 * </ul>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-07-21
 */
public class T002Form extends ActionForm {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /** Name of the customer used for search filtering. */
    private String customerName;

    /** Gender of the customer ("M", "F", etc.). */
    private String sex;

    /** Start date of the birthday range (format: yyyy/MM/dd). */
    private String birthdayFrom;

    /** End date of the birthday range (format: yyyy/MM/dd). */
    private String birthdayTo;

    /** Array of selected customer IDs (e.g., for bulk actions). */
    private String[] customerIds;

    /**
     * Gets the customer name.
     *
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customer name.
     *
     * @param customerName the customer name to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the gender of the customer.
     *
     * @return the gender of the customer
     */
    public String getSex() {
        return sex;
    }

    /**
     * Sets the gender of the customer.
     *
     * @param sex the gender to set
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * Gets the starting date of the birthday range.
     *
     * @return the starting birthday date in yyyy/MM/dd format
     */
    public String getBirthdayFrom() {
        return birthdayFrom;
    }

    /**
     * Sets the starting date of the birthday range.
     *
     * @param birthdayFrom the starting birthday date to set
     */
    public void setBirthdayFrom(String birthdayFrom) {
        this.birthdayFrom = birthdayFrom;
    }

    /**
     * Gets the ending date of the birthday range.
     *
     * @return the ending birthday date in yyyy/MM/dd format
     */
    public String getBirthdayTo() {
        return birthdayTo;
    }

    /**
     * Sets the ending date of the birthday range.
     *
     * @param birthdayTo the ending birthday date to set
     */
    public void setBirthdayTo(String birthdayTo) {
        this.birthdayTo = birthdayTo;
    }

    /**
     * Gets the selected customer IDs.
     *
     * @return an array of selected customer IDs
     */
    public String[] getCustomerIds() {
        return customerIds;
    }

    /**
     * Sets the selected customer IDs.
     *
     * @param customerIds an array of selected customer IDs to set
     */
    public void setCustomerIds(String[] customerIds) {
        this.customerIds = customerIds;
    }

    /**
     * Validates the input fields when the form is submitted.
     * <p>
     * This validation checks:
     * <ul>
     *   <li>Birthday fields follow the {@code yyyy/MM/dd} format.</li>
     *   <li>If both birthday range fields are present, {@code birthdayTo} is not earlier than {@code birthdayFrom}.</li>
     * </ul>
     * </p>
     *
     * @param mapping the action mapping used to select this instance
     * @param request the HTTP request being processed
     * @return an {@link ActionErrors} object containing any validation errors;
     *         returns an empty {@code ActionErrors} object if validation passes
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

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

        return errors;
    }
}
