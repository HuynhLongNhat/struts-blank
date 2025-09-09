package service;

import java.sql.SQLException;
import common.Constants;
import dao.T003Dao;
import dto.T002Dto;
import form.T003Form;

/**
 * Service class for handling business logic related to a single customer record.
 *
 * <p>
 * This class acts as a bridge between the controller (Action layer) and the
 * {@link T003Dao} data access layer. It manages retrieving, inserting, and
 * updating a single customer in the system.
 * </p>
 *
 * <p>
 * Implements the Singleton pattern to ensure only one service instance is used
 * throughout the application lifecycle.
 * </p>
 * 
 * @author  
 * @version 1.0
 * @since 2025-07-21
 */
public class T003Service {

    /** Singleton instance of {@code T003Service}. */
    private static final T003Service INSTANCE = new T003Service();

    /** DAO instance for interacting with the MSTCUSTOMER table. */
    private final T003Dao t003Dao = T003Dao.getInstance();

    /** Private constructor to prevent external instantiation. */
    private T003Service() {}

    /**
     * Returns the singleton instance of {@code T003Service}.
     *
     * @return singleton instance
     */
    public static T003Service getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves customer information by ID and populates the given form.
     * <p>
     * If a valid customer ID exists in the form, the method attempts to
     * fetch the corresponding customer data from the database. If found,
     * the form is populated with the customer's details and switched to
     * {@code EDIT} mode. If the customer does not exist or an error occurs,
     * the form defaults to {@code ADD} mode. If no customer ID is provided
     * (zero), the form is also set to {@code ADD} mode.
     * </p>
     *
     * @param form the form object to populate with customer data
     */
    public void getCustomerById(T003Form form) {
        int customerId = form.getCustomerId();

        if (customerId != 0) {
            try {
                // Fetch customer data from DAO
                T002Dto customer = t003Dao.getCustomerById(customerId);

                if (customer != null) {
                    // Customer found → populate form with details
                    form.setCustomerId(customer.getCustomerID());
                    form.setCustomerName(customer.getCustomerName());
                    form.setSex(customer.getSex());
                    form.setBirthday(customer.getBirthday());
                    form.setEmail(customer.getEmail());
                    form.setAddress(customer.getAddress());

                    // Switch to EDIT mode
                    form.setMode(Constants.MODE_EDIT);
                } else {
                    // Customer not found → switch to ADD mode
                    form.setMode(Constants.MODE_ADD);
                }
            } catch (Exception e) {
                // Any error → default to ADD mode
                form.setMode(Constants.MODE_ADD);
            }
        } else {
            // No ID (new entry) → ADD mode
            form.setMode(Constants.MODE_ADD);
        }
    }

    /**
     * Saves a customer record (insert or update).
     * <p>
     * If the form is in {@code ADD} mode, a new customer is created. If in
     * {@code EDIT} mode, the existing record is updated.
     * </p>
     *
     * @param editForm form containing customer input data
     * @param request  current HTTP request (reserved for audit/logging if needed)
     * @param psnCd    person code of the user performing the operation (for audit)
     * @return {@code true} if operation succeeded, {@code false} otherwise
     */
    public boolean saveCustomer(T003Form editForm, Integer psnCd) {
        try {
            if (Constants.MODE_ADD.equals(editForm.getMode())) {
                // Insert new customer
                t003Dao.insertCustomer(editForm, psnCd);
            } else {
                // Update existing customer
                t003Dao.updateCustomer(editForm, psnCd);
            }
            return true;
        } catch (SQLException e) {
            // Handle database errors gracefully
            return false;
        }
    }
}
