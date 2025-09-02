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
     * Counts total records that match the search criteria.
     *
     * @param whereClause SQL WHERE clause
     * @param params      SQL parameters
     * @return total matching records
     * @throws SQLException if query execution fails
     */
    private int countCustomers(StringBuilder whereClause, List<Object> params) throws SQLException {
        StringBuilder countSql = new StringBuilder()
                .append("SELECT COUNT(*) FROM ").append(TableConstants.TABLE_MSTCUSTOMER)
                .append(whereClause);

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement psCount = conn.prepareStatement(countSql.toString())) {
            setParameters(psCount, params);
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
    private List<T002Dto> fetchCustomers(StringBuilder whereClause, List<Object> params,
                                         int offset, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder()
                .append("SELECT ")
                .append(TableConstants.CUST_CUSTOMER_ID).append(", ")
                .append(TableConstants.CUST_CUSTOMER_NAME).append(", ")
                .append("CASE WHEN ").append(TableConstants.CUST_SEX).append(" = '0' THEN 'Male' ")
                .append("WHEN ").append(TableConstants.CUST_SEX).append(" = '1' THEN 'Female' END AS ")
                .append(TableConstants.CUST_SEX).append(", ")
                .append(TableConstants.CUST_BIRTHDAY).append(", ")
                .append(TableConstants.CUST_EMAIL).append(", ")
                .append(TableConstants.CUST_ADDRESS).append(" ")
                .append("FROM ").append(TableConstants.TABLE_MSTCUSTOMER)
                .append(whereClause)
                .append(" ORDER BY ").append(TableConstants.CUST_CUSTOMER_ID)
                .append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        // Clone params and add pagination values
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(offset);
        queryParams.add(limit);

        List<T002Dto> customers = new ArrayList<>();
        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            setParameters(ps, queryParams);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRow(rs));
                }
            }
        }
        return customers;
    }

    /**
     * Marks customers as deleted by setting {@code DELETE_YMD} to current date.
     *
     * @param customerIds list of customer IDs to delete
     * @throws SQLException if update operation fails
     */
    public void deleteCustomer(List<Integer> customerIds) throws SQLException {
        if (customerIds == null || customerIds.isEmpty()) return;

        // Build dynamic SQL with placeholders for all IDs
        StringBuilder sql = new StringBuilder()
                .append("UPDATE ").append(TableConstants.TABLE_MSTCUSTOMER)
                .append(" SET ").append(TableConstants.CUST_DELETE_YMD).append(" = GETDATE()")
                .append(" WHERE ").append(TableConstants.CUST_CUSTOMER_ID).append(" IN (")
                .append(customerIds.stream().map(id -> "?").collect(Collectors.joining(",")))
                .append(")");

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            // Set all ID parameters
            for (int i = 0; i < customerIds.size(); i++) {
                ps.setInt(i + 1, customerIds.get(i));
            }
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
