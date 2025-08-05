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

import dto.T002Dto;
import utils.DBUtils;

/**
 * DAO for handling customer information in the {@code MSTCUSTOMER} table.
 * <p>
 * Provides methods for searching customers with filters, pagination, and marking customers as deleted.
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
     * @return singleton {@code T002Dao} instance
     */
    public static T002Dao getInstance() {
        return instance;
    }

    /**
     * Searches customers with optional filters and pagination.
     *
     * @param userName      optional customer name filter (nullable)
     * @param sex           optional gender filter: "0" = Male, "1" = Female (nullable)
     * @param birthdayFrom  optional birthday start date (nullable)
     * @param birthdayTo    optional birthday end date (nullable)
     * @param offset        starting row index for pagination
     * @param limit         maximum number of rows to return
     * @return map with keys:
     *         - {@code "customers"}: list of {@link T002Dto} matching the filters
     *         - {@code "totalCount"}: total number of matching records
     * @throws SQLException if a database access error occurs
     */
    public Map<String, Object> searchCustomers(String userName, String sex,
                                               String birthdayFrom, String birthdayTo,
                                               int offset, int limit) throws SQLException {

        // Build dynamic WHERE clause based on provided filters
        List<Object> params = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder(" WHERE DELETE_YMD IS NULL");

        if (userName != null && !userName.trim().isEmpty()) {
            whereClause.append(" AND CUSTOMER_NAME LIKE ?");
            params.add("%" + userName.trim() + "%");
        }
        if (sex != null && !sex.trim().isEmpty()) {
            whereClause.append(" AND SEX = ?");
            params.add(sex.trim());
        }
        if (birthdayFrom != null && !birthdayFrom.trim().isEmpty()) {
            whereClause.append(" AND BIRTHDAY >= ?");
            params.add(birthdayFrom.trim());
        }
        if (birthdayTo != null && !birthdayTo.trim().isEmpty()) {
            whereClause.append(" AND BIRTHDAY <= ?");
            params.add(birthdayTo.trim());
        }

        // Get total count of matching records
        int totalCount;
        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement psCount = conn.prepareStatement("SELECT COUNT(*) FROM MSTCUSTOMER" + whereClause)) {

            setParameters(psCount, params);
            try (ResultSet rs = psCount.executeQuery()) {
                totalCount = rs.next() ? rs.getInt(1) : 0;
            }
        }

        // Prepare SQL for paginated results
        String sql = """
                SELECT CUSTOMER_ID, CUSTOMER_NAME,
                       CASE WHEN SEX = '0' THEN 'Male'
                            WHEN SEX = '1' THEN 'Female' END AS SEX,
                       BIRTHDAY, ADDRESS
                FROM MSTCUSTOMER""" + whereClause + " ORDER BY CUSTOMER_ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        List<Object> listParams = new ArrayList<>(params);
        listParams.add(offset);
        listParams.add(limit);

        // Retrieve paginated list of customers
        List<T002Dto> customers = new ArrayList<>();
        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setParameters(ps, listParams);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRow(rs));
                }
            }
        }

        // Combine total count and customer list into result map
        Map<String, Object> result = new HashMap<>();
        result.put("customers", customers);
        result.put("totalCount", totalCount);
        return result;
    }

    /**
     * Marks customers as deleted by setting {@code DELETE_YMD} to the current date.
     *
     * @param customerIds list of customer IDs to mark as deleted
     * @throws SQLException if the update operation fails
     */
    public void deleteCustomer(List<String> customerIds) throws SQLException {
        if (customerIds == null || customerIds.isEmpty()) return;

        // Build SQL query with placeholders for all IDs
        String sql = "UPDATE MSTCUSTOMER SET DELETE_YMD = GETDATE() WHERE CUSTOMER_ID IN (" +
                     customerIds.stream().map(id -> "?").collect(Collectors.joining(",")) + ")";

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set each ID in the prepared statement
            for (int i = 0; i < customerIds.size(); i++) {
                ps.setString(i + 1, customerIds.get(i));
            }
            ps.executeUpdate();
        }
    }

    /**
     * Sets all parameters for a prepared statement.
     *
     * @param ps     prepared statement
     * @param params list of parameter values
     * @throws SQLException if setting any parameter fails
     */
    private void setParameters(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    /**
     * Maps a {@link ResultSet} row to a {@link T002Dto}.
     *
     * @param rs result set pointing to the current row
     * @return DTO populated with customer data
     * @throws SQLException if reading from the result set fails
     */
    private T002Dto mapRow(ResultSet rs) throws SQLException {
        T002Dto dto = new T002Dto();
        dto.setCustomerID(rs.getInt("CUSTOMER_ID"));
        dto.setCustomerName(rs.getString("CUSTOMER_NAME"));
        dto.setSex(rs.getString("SEX"));
        dto.setBirthday(rs.getString("BIRTHDAY"));
        dto.setAddress(rs.getString("ADDRESS"));
        return dto;
    }
}
