package dto;

/**
 * T002Dto - representing a customer.
 * Used for transferring customer information across application layers.
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-07-21
 */
public class T002Dto {

    /** Unique identifier of this customer */
    private int customerID;

    /** Full name of this customer */
    private String customerName;

    /** Gender of this customer */
    private String sex;

    /** Birth date of this customer (format: YYYY/MM/DD) */
    private String birthday;

    /** Email address of this customer */
    private String email;

    /** Residential address of this customer */
    private String address;

    /**
     * Default constructor.
     */
    public T002Dto() {
    }

    /**
     * Gets the customer's ID.
     *
     * @return the customer ID
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * Sets the customer's ID.
     *
     * @param customerID the customer ID to set
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * Gets the customer's name.
     *
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customer's name.
     *
     * @param customerName the customer name to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the customer's gender.
     *
     * @return the customer gender
     */
    public String getSex() {
        return sex;
    }

    /**
     * Sets the customer's gender.
     *
     * @param sex the customer gender to set
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * Gets the customer's birth date.
     *
     * @return the customer's birth date
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * Sets the customer's birth date.
     *
     * @param birthday the customer's birth date to set
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * Gets the customer's email address.
     *
     * @return the customer's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the customer's email address.
     *
     * @param email the customer's email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the customer's residential address.
     *
     * @return the customer's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the customer's residential address.
     *
     * @param address the customer's address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
