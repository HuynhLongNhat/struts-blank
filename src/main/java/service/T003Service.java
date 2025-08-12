package service;

import java.sql.SQLException;

import dao.T003Dao;
import dto.T002Dto;

/**
 * Service class for handling business logic related to a single customer record.
 *
 * <p>This class serves as the intermediary between the controller layer
 * and the {@link T003Dao} data access layer. It provides methods for retrieving,
 * creating, and updating individual customer records.</p>
 *
 * <p>Implements the Singleton pattern to ensure that only one instance
 * exists throughout the application lifecycle.</p>
 * 
 * @author YourName
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
     * Retrieves a customer by their ID.
     *
     * @param customerId unique ID of the customer
     * @return a populated {@link T002Dto} if the customer exists,
     *         otherwise {@code null}
     */
    public T002Dto getCustomerById(Integer customerId) throws SQLException{
        return t003Dao.getCustomerById(customerId);
    }

    /**
     * Inserts a new customer record into the database.
     *
     * @param customer customer data to insert
     * @param psnCd    personal code of the user performing the operation
     * @return {@code true} if the insertion was successful; {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public void insertCustomer(T002Dto customer, Integer psnCd) throws SQLException {
         t003Dao.insertCustomer(customer, psnCd);
    }

    /**
     * Updates an existing customer record in the database.
     *
     * @param customer customer data with updated fields
     * @param psnCd    personal code of the user performing the update
     * @return {@code true} if the update was successful; {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */
    public void updateCustomer(T002Dto customer, Integer psnCd) throws SQLException {
        t003Dao.updateCustomer(customer, psnCd);
    }
}
