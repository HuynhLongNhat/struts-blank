package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dto.T002Dto;
import utils.DBUtils;

public class T004Dao {

    public boolean checkCustomerExists(int customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM MSTCUSTOMER WHERE CUSTOMER_ID = ? AND DELETE_YMD IS NULL";

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean importCustomerData(List<T002Dto> customers) throws SQLException {
        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;

        try {
            conn = DBUtils.getInstance().getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Prepare SQL statements
            String insertSql = "INSERT INTO MSTCUSTOMER " +
                    "(CUSTOMER_ID, CUSTOMER_NAME, SEX, BIRTHDAY, EMAIL, ADDRESS, DELETE_YMD, INSERT_YMD, INSERT_PSN_CD, UPDATE_YMD, UPDATE_PSN_CD) " +
                    "VALUES (?, ?, ?, ?, ?, ?, NULL, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)";

            String updateSql = "UPDATE MSTCUSTOMER " +
                    "SET CUSTOMER_NAME = ?, SEX = ?, BIRTHDAY = ?, EMAIL = ?, ADDRESS = ?, " +
                    "UPDATE_YMD = CURRENT_TIMESTAMP, UPDATE_PSN_CD = ? " +
                    "WHERE CUSTOMER_ID = ?";

            insertStmt = conn.prepareStatement(insertSql);
            updateStmt = conn.prepareStatement(updateSql);

            int insertCount = 0;
            int updateCount = 0;

            for (T002Dto customer : customers) {
                if (customer.getCustomerID() == 0) {
                    // Insert new customer with auto-generated ID
                    String newCustomerId = generateNewCustomerId(conn);
                    insertStmt.setInt(1, Integer.parseInt(newCustomerId));
                    insertStmt.setString(2, customer.getCustomerName());
                    insertStmt.setString(3, customer.getSex());
                    insertStmt.setDate(4, java.sql.Date.valueOf(customer.getBirthday().replace("/", "-")));
                    insertStmt.setString(5, customer.getEmail());
                    insertStmt.setString(6, customer.getAddress());
                    insertStmt.setString(7, "SYSTEM"); // INSERT_PSN_CD
                    insertStmt.setString(8, "SYSTEM"); // UPDATE_PSN_CD
                    insertStmt.addBatch();
                    insertCount++;
                } else {
                    // Update existing customer
                    updateStmt.setString(1, customer.getCustomerName());
                    updateStmt.setString(2, customer.getSex());
                    updateStmt.setDate(3, java.sql.Date.valueOf(customer.getBirthday().replace("/", "-")));
                    updateStmt.setString(4, customer.getEmail());
                    updateStmt.setString(5, customer.getAddress());
                    updateStmt.setString(6, "SYSTEM"); // UPDATE_PSN_CD
                    updateStmt.setInt(7, customer.getCustomerID());
                    updateStmt.addBatch();
                    updateCount++;
                }
            }

            // Execute batch operations
            if (insertCount > 0) {
                insertStmt.executeBatch();
            }
            if (updateCount > 0) {
                updateStmt.executeBatch();
            }

            conn.commit(); // Commit transaction

            // Log the results
            System.out.println("Import completed successfully:");
            System.out.println("Inserted line(s): " + insertCount);
            System.out.println("Updated line(s): " + updateCount);

            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error during import: " + e.getMessage());
            throw e;
        } finally {
            // Close resources
            if (insertStmt != null) {
                try { insertStmt.close(); } catch (SQLException e) { /* ignore */ }
            }
            if (updateStmt != null) {
                try { updateStmt.close(); } catch (SQLException e) { /* ignore */ }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) { /* ignore */ }
            }
        }
    }

    private String generateNewCustomerId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(CUSTOMER_ID AS INTEGER)) FROM MSTCUSTOMER WHERE CUSTOMER_ID REGEXP '^[0-9]+$'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int maxId = rs.getInt(1);
                return String.valueOf(maxId + 1);
            }
        }

        return "1"; // Default if no numeric IDs found
    }
}
