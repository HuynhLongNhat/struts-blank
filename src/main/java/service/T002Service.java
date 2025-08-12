package service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dao.T002Dao;
import dto.T002Dto;
import dto.T002SCO;

/**
 * Service class for handling business logic related to customers.
 *
 * <p>This class acts as a bridge between the controller layer and
 * the {@link T002Dao} data access layer. It provides methods for retrieving
 * and managing customer records, including search with filters, pagination,
 * and marking records as deleted.</p>
 *
 * <p>Implements the Singleton pattern to ensure a single instance is used
 * throughout the application.</p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-07-21
 */
public class T002Service {

    /** Singleton instance of {@code T002Service}. */
    private static final T002Service INSTANCE = new T002Service();

    /** DAO instance for accessing customer data. */
    private final T002Dao t002Dao = T002Dao.getInstance();

    /** Private constructor to enforce Singleton pattern. */
    private T002Service() {}

    /**
     * Returns the singleton instance of {@code T002Service}.
     *
     * @return singleton instance
     */
    public static T002Service getInstance() {
        return INSTANCE;
    }

    /**
     * Searches customers using search condition object and pagination.
     *
     * @param sco    search condition object containing filters
     * @param offset starting row for pagination
     * @param limit  number of rows per page
     * @return a map with keys:
     *         - "customers": list of {@link T002Dto}
     *         - "totalCount": total matching records
     * @throws SQLException if database access fails
     */
	public Map<String, Object> searchCustomers(T002SCO sco, int offset, int limit) throws SQLException {
		return t002Dao.searchCustomers(sco, offset, limit);
	}
    /**
     * Marks customers as deleted based on the provided list of IDs.
     *
     * @param customerIds list of customer IDs to be marked as deleted
     * @throws SQLException if a database access error occurs
     */
    public void deleteCustomers(List<String> customerIds) throws SQLException {
        t002Dao.deleteCustomer(customerIds);
    }
}
