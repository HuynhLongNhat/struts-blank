package dto;

/**
 * Search Condition Object (SCO) for searching customer information.
 * 
 * <p>This class holds the filtering criteria such as customer name, sex, and birthday range.
 * It is typically used to store user search input in session and reused across screen navigation.</p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-08-02
 */
public class T002SCO {

    /** Customer's full name used for search. */
    private String customerName;

    /** Customer's sex (e.g., "Male", "Female", etc.) */
    private String sex;

    /** Start of birthday range for filtering (format: yyyy/MM/dd). */
    private String birthdayFrom;

    /** End of birthday range for filtering (format: yyyy/MM/dd). */
    private String birthdayTo;

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
     * Gets the sex.
     * 
     * @return the sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * Sets the sex.
     * 
     * @param sex the sex to set
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * Gets the starting date of the birthday range.
     * 
     * @return the birthdayFrom
     */
    public String getBirthdayFrom() {
        return birthdayFrom;
    }

    /**
     * Sets the starting date of the birthday range.
     * 
     * @param birthdayFrom the birthdayFrom to set
     */
    public void setBirthdayFrom(String birthdayFrom) {
        this.birthdayFrom = birthdayFrom;
    }

    /**
     * Gets the ending date of the birthday range.
     * 
     * @return the birthdayTo
     */
    public String getBirthdayTo() {
        return birthdayTo;
    }

    /**
     * Sets the ending date of the birthday range.
     * 
     * @param birthdayTo the birthdayTo to set
     */
    public void setBirthdayTo(String birthdayTo) {
        this.birthdayTo = birthdayTo;
    }
}
