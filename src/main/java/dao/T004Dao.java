package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dto.T002Dto;
import utils.DBUtils;

public class T004Dao {

    public boolean checkCustomerExists(int customerId) throws SQLException {
    	StringBuilder stringBuilder = new StringBuilder();
    	stringBuilder.append("SELECT COUNT(*) FROM MSTCUSTOMER ");
    	stringBuilder.append("WHERE CUSTOMER_ID = ? ");
    	stringBuilder.append("AND DELETE_YMD IS NULL");
        String sql = stringBuilder.toString();
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
    public Map<String, List<Integer>> importCustomerData(List<T002Dto> customers, Integer psnCd) throws SQLException {
        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement checkStmt = null;

        // Map kết quả trả về
        Map<String, List<Integer>> resultMap = new HashMap<>();
        List<Integer> insertedIndexes = new ArrayList<>();
        List<Integer> updatedIndexes = new ArrayList<>();

        try {
            conn = DBUtils.getInstance().getConnection();
            conn.setAutoCommit(false);

            String insertSql = "INSERT INTO MSTCUSTOMER " +
                    "(CUSTOMER_ID, CUSTOMER_NAME, SEX, BIRTHDAY, EMAIL, ADDRESS, DELETE_YMD, INSERT_YMD, INSERT_PSN_CD, UPDATE_YMD, UPDATE_PSN_CD) " +
                    "VALUES (NEXT VALUE FOR SEQ_CUSTOMER_ID, ?, ?, ?, ?, ?, NULL, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)";

            String updateSql = "UPDATE MSTCUSTOMER SET " +
                    "CUSTOMER_NAME = ?, SEX = ?, BIRTHDAY = ?, EMAIL = ?, ADDRESS = ?, " +
                    "UPDATE_YMD = CURRENT_TIMESTAMP, UPDATE_PSN_CD = ? " +
                    "WHERE CUSTOMER_ID = ?";

            String checkExistingSql = "SELECT CUSTOMER_NAME, SEX, BIRTHDAY, EMAIL, ADDRESS FROM MSTCUSTOMER WHERE CUSTOMER_ID = ? AND DELETE_YMD IS NULL";

            insertStmt = conn.prepareStatement(insertSql);
            updateStmt = conn.prepareStatement(updateSql);
            checkStmt = conn.prepareStatement(checkExistingSql);

            for (int i = 0; i < customers.size(); i++) {
                T002Dto customer = customers.get(i);

                // Convert sex string to numeric
                int sexValue = mapSex(customer.getSex());

                if (customer.getCustomerID() == 0) {
                    // Insert new customer
                    prepareInsertStatement(insertStmt, customer, sexValue, psnCd);
                    insertStmt.addBatch();
                    insertedIndexes.add(i + 1); // index dòng (1-based)
                } else {
                    // Check if update is needed (only update if data has changed)
                    if (isUpdateNeeded(checkStmt, customer, sexValue)) {
                        prepareUpdateStatement(updateStmt, customer, sexValue, psnCd);
                        updateStmt.addBatch();
                        updatedIndexes.add(i + 1); // index dòng (1-based)
                    }
                }
            }

            if (!insertedIndexes.isEmpty()) insertStmt.executeBatch();
            if (!updatedIndexes.isEmpty()) updateStmt.executeBatch();

            conn.commit();

            // Gán vào map trả về
            resultMap.put("inserted", insertedIndexes);
            resultMap.put("updated", updatedIndexes);

            return resultMap;

        } catch (SQLException e) {
            if (conn != null) {
                try { 
                    conn.rollback(); 
                } catch (SQLException rollbackEx) { 
                    System.err.println("Rollback error: " + rollbackEx.getMessage()); 
                }
            }
            throw e;
        } finally {
            closeResource(insertStmt);
            closeResource(updateStmt);
            closeResource(checkStmt);
            closeConnection(conn);
        }
    }

    private void prepareInsertStatement(PreparedStatement stmt, T002Dto customer, int sexValue, Integer psnCd) throws SQLException {
        stmt.setString(1, customer.getCustomerName());
        stmt.setInt(2, sexValue);
        stmt.setString(3, customer.getBirthday());
        stmt.setString(4, customer.getEmail());
        stmt.setString(5, customer.getAddress());
        stmt.setInt(6, psnCd); 
        stmt.setInt(7, psnCd); 
    }

    private void prepareUpdateStatement(PreparedStatement stmt, T002Dto customer, int sexValue, Integer psnCd) throws SQLException {
        stmt.setString(1, customer.getCustomerName());
        stmt.setInt(2, sexValue);
        stmt.setString(3, customer.getBirthday());
        stmt.setString(4, customer.getEmail());
        stmt.setString(5, customer.getAddress());
        stmt.setInt(6, psnCd); // UPDATE_PSN_CD
        stmt.setInt(7, customer.getCustomerID());
    }

    private boolean isUpdateNeeded(PreparedStatement checkStmt, T002Dto newCustomer, int newSexValue) throws SQLException {
        checkStmt.setInt(1, newCustomer.getCustomerID());
        
        try (ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next()) {
                String existingName = rs.getString("CUSTOMER_NAME");
                int existingSex = rs.getInt("SEX");
                Date existingBirthday = rs.getDate("BIRTHDAY");
                String existingEmail = rs.getString("EMAIL");
                String existingAddress = rs.getString("ADDRESS");
                
                // Convert dates to comparable format
                java.sql.Date newBirthday = java.sql.Date.valueOf(newCustomer.getBirthday().replace("/", "-"));
                
                // Check if any field has changed
                boolean nameChanged = !existingName.equals(newCustomer.getCustomerName());
                boolean sexChanged = existingSex != newSexValue;
                boolean birthdayChanged = !existingBirthday.equals(newBirthday);
                boolean emailChanged = !existingEmail.equals(newCustomer.getEmail());
                boolean addressChanged = !existingAddress.equals(newCustomer.getAddress());
                
                return nameChanged || sexChanged || birthdayChanged || emailChanged || addressChanged;
            }
        }
        
        // If customer not found, should update to handle potential data inconsistency
        return true;
    }

    private void closeResource(AutoCloseable resource) {
        if (resource != null) {
            try { 
                resource.close(); 
            } catch (Exception e) { 
                // Log at debug level if needed
            }
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) { 
                // Log at debug level if needed
            }
        }
    }
    
    private int mapSex(String sex) {
        if ("Male".equalsIgnoreCase(sex)) return 0;
        if ("Female".equalsIgnoreCase(sex)) return 1;
        throw new IllegalArgumentException("Invalid sex value: " + sex);
    }

}
