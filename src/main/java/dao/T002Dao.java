package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import common.TableConstants;
import dto.T002Dto;
import dto.T002SCO;
import utils.DBUtils;
import utils.Helper;

/**
 * DAO class for handling customer information in the {@code MSTCUSTOMER} table.
 * <p>
 * Supports searching customers with filters, pagination, 
 * and marking customers as logically deleted.
 * </p>
 */
public class T002Dao {

    /** Singleton instance */
    private static final T002Dao instance = new T002Dao();

    /** Private constructor to prevent external instantiation */
    private T002Dao() {}

    /**
     * Returns the singleton instance of {@code T002Dao}.
     *
     * @return singleton instance
     */
    public static T002Dao getInstance() {
        return instance;
    }

    /**
     * Searches customers with optional filters and pagination.
     *
     * @param sco    search criteria (name, sex, birthday range in yyyy/MM/dd)
     * @param offset start index for pagination (zero-based)
     * @param limit  max number of records to return
     * @return map containing:
     *         <ul>
     *           <li>"customers" - List of {@link T002Dto}</li>
     *           <li>"totalCount" - total matching records</li>
     *         </ul>
     * @throws SQLException if database error occurs
     */
    public Map<String, Object> searchCustomers(T002SCO sco, int offset, int limit) throws SQLException {
        // Build WHERE clause + collect parameters
        List<Object> params = new ArrayList<>();
        StringBuilder whereClause = buildWhereClause(sco, params);

        // Count total records for pagination
        int totalCount = countCustomers(whereClause, params);

        // Fetch paginated customers
        List<T002Dto> customers = fetchCustomers(whereClause, params, offset, limit);

        // Prepare result map
        Map<String, Object> result = new HashMap<>();
        result.put("customers", customers);
        result.put("totalCount", totalCount);
        return result;
    }

    /**
     * Builds dynamic WHERE clause and collects SQL parameters.
     *
     * @param sco    search criteria
     * @param params list to collect prepared statement parameters
     * @return WHERE clause as {@link StringBuilder}
     */
    private StringBuilder buildWhereClause(T002SCO sco, List<Object> params) {
        StringBuilder whereClause = new StringBuilder()
                .append(" WHERE ").append(TableConstants.CUST_DELETE_YMD).append(" IS NULL");

        // Filter by customer name (LIKE)
        if (!Helper.isEmpty(sco.getCustomerName())) {
            whereClause.append(" AND ").append(TableConstants.CUST_CUSTOMER_NAME).append(" LIKE ?");
            params.add("%" + sco.getCustomerName().trim() + "%");
        }
        // Filter by gender
        if (!Helper.isEmpty(sco.getSex())) {
            whereClause.append(" AND ").append(TableConstants.CUST_SEX).append(" = ?");
            params.add(sco.getSex().trim());
        }
        // Filter by birthday range
        if (!Helper.isEmpty(sco.getBirthdayFrom())) {
            whereClause.append(" AND ").append(TableConstants.CUST_BIRTHDAY).append(" >= ?");
            params.add(sco.getBirthdayFrom());
        }
        if (!Helper.isEmpty(sco.getBirthdayTo())) {
            whereClause.append(" AND ").append(TableConstants.CUST_BIRTHDAY).append(" <= ?");
            params.add(sco.getBirthdayTo());
        }
        return whereClause;
    }
    /**
     * Counts the total number of customers that match the given WHERE clause and parameters.
     * <p>
     * Builds and executes a SQL {@code SELECT COUNT(*)} query dynamically
     * using the provided conditions and parameters.
     * </p>
     *
     * @param whereClause The SQL WHERE clause (including leading "WHERE" if applicable).
     * @param params      The list of parameter values to bind in the prepared statement.
     * @return The total number of matching customers.
     * @throws SQLException if a database access error occurs.
     */
    private int countCustomers(StringBuilder whereClause, List<Object> params) throws SQLException {
        // Build COUNT query dynamically
        StringBuilder countSql = new StringBuilder()
                .append("SELECT COUNT(*) FROM ")
                .append(TableConstants.TABLE_MSTCUSTOMER)
                .append(whereClause);

        // Use try-with-resources to ensure connection, statement, and result set are closed
        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement psCount = conn.prepareStatement(countSql.toString())) {

            // Bind query parameters
            setParameters(psCount, params);

            // Execute query and return the count if available
            try (ResultSet rs = psCount.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }


    /**
     * Fetches customer records with pagination.
     *
     * @param whereClause SQL WHERE clause
     * @param params      SQL parameters
     * @param offset      starting record index
     * @param limit       max number of records
     * @return list of customers
     * @throws SQLException if query execution fails
     */
    /**
     * Fetches a paginated list of customers based on dynamic search conditions.
     * <p>
     * Builds and executes a SQL query with filtering, ordering, and pagination.
     * Supports mapping gender values ('0' → Male, '1' → Female) into readable text.
     * </p>
     *
     * @param whereClause The SQL WHERE clause (including leading "WHERE" if applicable).
     * @param params      The list of parameter values to bind in the prepared statement.
     * @param offset      The starting row for pagination.
     * @param limit       The maximum number of rows to retrieve.
     * @return A list of {@link T002Dto} customers matching the search conditions.
     * @throws SQLException if a database access error occurs.
     */
    private List<T002Dto> fetchCustomers(StringBuilder whereClause, List<Object> params,
                                         int offset, int limit) throws SQLException {
        // Build SELECT query with filtering, ordering, and pagination
        StringBuilder sql = new StringBuilder()
                .append("SELECT ")
                .append(TableConstants.CUST_CUSTOMER_ID).append(", ")
                .append(TableConstants.CUST_CUSTOMER_NAME).append(", ")
                // Map numeric gender values into readable strings
                .append("CASE WHEN ").append(TableConstants.CUST_SEX).append(" = '0' THEN 'Male' ")
                .append("WHEN ").append(TableConstants.CUST_SEX).append(" = '1' THEN 'Female' END AS ")
                .append(TableConstants.CUST_SEX).append(", ")
                .append(TableConstants.CUST_BIRTHDAY).append(", ")
                .append(TableConstants.CUST_EMAIL).append(", ")
                .append(TableConstants.CUST_ADDRESS).append(" ")
                .append("FROM ").append(TableConstants.TABLE_MSTCUSTOMER)
                .append(whereClause)
                .append(" ORDER BY ").append(TableConstants.CUST_CUSTOMER_ID)
                .append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"); // Pagination

        // Clone parameter list and add pagination values
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(offset);
        queryParams.add(limit);

        List<T002Dto> customers = new ArrayList<>();
        // Try-with-resources ensures connection, statement, and result set are closed
        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Bind all parameters, including filters and pagination
            setParameters(ps, queryParams);

            // Execute query and map each row into a DTO
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRow(rs));
                }
            }
        }
        return customers;
    }
    /**
     * Soft deletes customers by marking them with the current date in {@code DELETE_YMD}.
     * <p>
     * Instead of removing records physically, this method updates the 
     * {@code DELETE_YMD} column to the current date using {@code GETDATE()}.
     * </p>
     *
     * @param customerIds list of customer IDs to mark as deleted
     * @throws SQLException if the update operation fails
     */
    public void deleteCustomer(List<Integer> customerIds) throws SQLException {
        if (customerIds == null || customerIds.isEmpty()) return;

        // Build dynamic SQL with placeholders (?) for each customer ID
        StringBuilder sql = new StringBuilder()
                .append("UPDATE ").append(TableConstants.TABLE_MSTCUSTOMER)
                .append(" SET ").append(TableConstants.CUST_DELETE_YMD).append(" = GETDATE()")
                .append(" WHERE ").append(TableConstants.CUST_CUSTOMER_ID).append(" IN (")
                .append(customerIds.stream().map(id -> "?").collect(Collectors.joining(",")))
                .append(")");

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // Assign values to each placeholder (customer ID)
            for (int i = 0; i < customerIds.size(); i++) {
                ps.setInt(i + 1, customerIds.get(i));
            }

            // Execute the update query
            ps.executeUpdate();
        }
    }

    /**
     * Sets all parameters for a prepared statement.
     *
     * @param ps     prepared statement
     * @param params list of parameters
     * @throws SQLException if setting any parameter fails
     */
    private void setParameters(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    /**
     * Maps a result set row to a {@link T002Dto}.
     *
     * @param rs result set pointing to the current row
     * @return DTO with mapped data
     * @throws SQLException if result set access fails
     */
    private T002Dto mapRow(ResultSet rs) throws SQLException {
        T002Dto dto = new T002Dto();
        dto.setCustomerID(rs.getInt(TableConstants.CUST_CUSTOMER_ID));
        dto.setCustomerName(rs.getString(TableConstants.CUST_CUSTOMER_NAME));
        dto.setSex(rs.getString(TableConstants.CUST_SEX));
        dto.setBirthday(rs.getString(TableConstants.CUST_BIRTHDAY));
        dto.setEmail(rs.getString(TableConstants.CUST_EMAIL));
        dto.setAddress(rs.getString(TableConstants.CUST_ADDRESS));
        return dto;
    }
}
