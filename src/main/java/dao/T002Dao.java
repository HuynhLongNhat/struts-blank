package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dto.T002Dto;
import dto.T002SCO;
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
	 * @param sco    search criteria (name, sex, birthday range in yyyy/MM/dd)
	 * @param offset start index for pagination (zero-based)
	 * @param limit  max number of records to return
	 * @return map containing "customers" (List of T002Dto) and "totalCount" (total
	 *         matching records)
	 * @throws SQLException if DB error or invalid date format occurs
	 */
	public Map<String, Object> searchCustomers(T002SCO sco, int offset, int limit) throws SQLException {
		List<Object> params = new ArrayList<>();
		StringBuilder whereClause = new StringBuilder(" WHERE DELETE_YMD IS NULL");

		// Lấy tham số từ sco
		String userName = sco.getCustomerName();
		String sex = sco.getSex();
		String birthdayFromStr = sco.getBirthdayFrom();
		String birthdayToStr = sco.getBirthdayTo();

		if (userName != null && !userName.trim().isEmpty()) {
			whereClause.append(" AND CUSTOMER_NAME LIKE ?");
			params.add("%" + userName.trim() + "%");
		}
		if (sex != null && !sex.trim().isEmpty()) {
			whereClause.append(" AND SEX = ?");
			params.add(sex.trim());
		}

		// Format ngày sinh yyyy/MM/dd
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		java.sql.Date birthdayFrom = null;
		java.sql.Date birthdayTo = null;

		if (birthdayFromStr != null && !birthdayFromStr.trim().isEmpty()) {
			try {
				LocalDate localDateFrom = LocalDate.parse(birthdayFromStr.trim(), formatter);
				birthdayFrom = java.sql.Date.valueOf(localDateFrom);
				whereClause.append(" AND BIRTHDAY >= ?");
				params.add(birthdayFrom);
			} catch (DateTimeParseException e) {
				e.printStackTrace();
			}
		}

		if (birthdayToStr != null && !birthdayToStr.trim().isEmpty()) {
			try {
				LocalDate localDateTo = LocalDate.parse(birthdayToStr.trim(), formatter);
				birthdayTo = java.sql.Date.valueOf(localDateTo);
				whereClause.append(" AND BIRTHDAY <= ?");
				params.add(birthdayTo);
			} catch (DateTimeParseException e) {
				e.printStackTrace();
			}
		}
		// Đếm tổng bản ghi
		int totalCount;
		String countSql = "SELECT COUNT(*) FROM MSTCUSTOMER" + whereClause;
		try (Connection conn = DBUtils.getInstance().getConnection();
				PreparedStatement psCount = conn.prepareStatement(countSql)) {
			setParameters(psCount, params);
			try (ResultSet rs = psCount.executeQuery()) {
				totalCount = rs.next() ? rs.getInt(1) : 0;
			}
		}

		// Truy vấn phân trang (SQL Server OFFSET FETCH)
		String sql = "SELECT CUSTOMER_ID, CUSTOMER_NAME, "
				+ "CASE WHEN SEX = '0' THEN 'Male' WHEN SEX = '1' THEN 'Female' END AS SEX, " + "BIRTHDAY, ADDRESS "
				+ "FROM MSTCUSTOMER" + whereClause + " ORDER BY CUSTOMER_ID " + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

		List<Object> queryParams = new ArrayList<>(params);
		queryParams.add(offset);
		queryParams.add(limit);

		List<T002Dto> customers = new ArrayList<>();
		try (Connection conn = DBUtils.getInstance().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			setParameters(ps, queryParams);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					customers.add(mapRow(rs));
				}
			}
		}

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
