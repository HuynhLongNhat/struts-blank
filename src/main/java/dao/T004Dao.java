package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import common.TableConstants;
import dto.T002Dto;
import utils.DBUtils;

/**
 * DAO class for handling MSTCUSTOMER table operations.
 * Supports checking customer existence, batch insert, batch update, 
 * and transactional import of customer data.
 */
public class T004Dao {

    /**
     * Check if a customer exists and is not deleted.
     *
     * @param customerId the customer ID
     * @return true if customer exists, false otherwise
     * @throws SQLException database error
     */
    public boolean checkCustomerExists(int customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TableConstants.TABLE_MSTCUSTOMER +
                     " WHERE " + TableConstants.CUST_CUSTOMER_ID + " = ? " +
                     "AND " + TableConstants.CUST_DELETE_YMD + " IS NULL";

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Import a list of customers: insert new customers or update existing ones.
     * All operations are transactional: success or rollback.
     *
     * @param customers list of customer DTOs
     * @param psnCd personal code of the operator
     * @return map with inserted and updated line numbers
     * @throws SQLException database error
     */
    public Map<String, List<Integer>> importCustomerData(List<T002Dto> customers, Integer psnCd) throws SQLException {
        Map<String, List<Integer>> resultMap = new HashMap<>();
        List<Integer> insertedIndexes = new ArrayList<>();
        List<Integer> updatedIndexes = new ArrayList<>();

        String insertSql = buildInsertSql();
        String updateSql = buildUpdateSql();
        String checkSql = buildCheckSql();

        try (Connection conn = DBUtils.getInstance().getConnection()) {
            conn.setAutoCommit(false); // start transaction

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

                for (int i = 0; i < customers.size(); i++) {
                    T002Dto customer = customers.get(i);
                    int sexValue = mapSex(customer.getSex());

                    if (customer.getCustomerID() == 0) {
                        // Insert new customer
                        prepareInsertStatement(insertStmt, customer, sexValue, psnCd);
                        insertStmt.addBatch();
                        insertedIndexes.add(i + 1);
                    } else if (isUpdateNeeded(checkStmt, customer, sexValue)) {
                        // Update existing customer if data changed
                        prepareUpdateStatement(updateStmt, customer, sexValue, psnCd);
                        updateStmt.addBatch();
                        updatedIndexes.add(i + 1);
                    }
                }

                // Execute batch statements
                executeBatch(insertStmt, insertedIndexes);
                executeBatch(updateStmt, updatedIndexes);

                conn.commit(); // commit transaction

                // Put result into map
                resultMap.put("inserted", insertedIndexes);
                resultMap.put("updated", updatedIndexes);
                return resultMap;

            } catch (SQLException e) {
                conn.rollback(); // rollback on error
                throw e;
            } finally {
                conn.setAutoCommit(true); // reset autocommit
            }
        }
    }

    // ================= Helper Methods =================

    /**
     * Execute batch if list is not empty.
     *
     * @param stmt prepared statement
     * @param indexes list of line numbers
     * @throws SQLException database error
     */
    private void executeBatch(PreparedStatement stmt, List<Integer> indexes) throws SQLException {
        if (!indexes.isEmpty()) stmt.executeBatch();
    }

    /**
     * Build SQL for inserting a customer.
     *
     * @return SQL string
     */
    private String buildInsertSql() {
        return "INSERT INTO " + TableConstants.TABLE_MSTCUSTOMER + " (" +
               TableConstants.CUST_CUSTOMER_ID + ", " +
               TableConstants.CUST_CUSTOMER_NAME + ", " +
               TableConstants.CUST_SEX + ", " +
               TableConstants.CUST_BIRTHDAY + ", " +
               TableConstants.CUST_EMAIL + ", " +
               TableConstants.CUST_ADDRESS + ", " +
               TableConstants.CUST_DELETE_YMD + ", " +
               TableConstants.CUST_INSERT_YMD + ", " +
               TableConstants.CUST_INSERT_PSN_CD + ", " +
               TableConstants.CUST_UPDATE_YMD + ", " +
               TableConstants.CUST_UPDATE_PSN_CD + ") " +
               "VALUES (NEXT VALUE FOR SEQ_CUSTOMER_ID, ?, ?, ?, ?, ?, NULL, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)";
    }

    /**
     * Build SQL for updating a customer.
     *
     * @return SQL string
     */
    private String buildUpdateSql() {
        return "UPDATE " + TableConstants.TABLE_MSTCUSTOMER + " SET " +
               TableConstants.CUST_CUSTOMER_NAME + " = ?, " +
               TableConstants.CUST_SEX + " = ?, " +
               TableConstants.CUST_BIRTHDAY + " = ?, " +
               TableConstants.CUST_EMAIL + " = ?, " +
               TableConstants.CUST_ADDRESS + " = ?, " +
               TableConstants.CUST_UPDATE_YMD + " = CURRENT_TIMESTAMP, " +
               TableConstants.CUST_UPDATE_PSN_CD + " = ? " +
               "WHERE " + TableConstants.CUST_CUSTOMER_ID + " = ?";
    }

    /**
     * Build SQL for checking existing customer.
     *
     * @return SQL string
     */
    private String buildCheckSql() {
        return "SELECT " +
               TableConstants.CUST_CUSTOMER_NAME + ", " +
               TableConstants.CUST_SEX + ", " +
               TableConstants.CUST_BIRTHDAY + ", " +
               TableConstants.CUST_EMAIL + ", " +
               TableConstants.CUST_ADDRESS +
               " FROM " + TableConstants.TABLE_MSTCUSTOMER +
               " WHERE " + TableConstants.CUST_CUSTOMER_ID + " = ? " +
               "AND " + TableConstants.CUST_DELETE_YMD + " IS NULL";
    }

    /**
     * Prepare insert statement parameters.
     *
     * @param stmt prepared statement
     * @param customer DTO
     * @param sexValue mapped sex
     * @param psnCd personal code
     * @throws SQLException database error
     */
    private void prepareInsertStatement(PreparedStatement stmt, T002Dto customer, int sexValue, Integer psnCd) throws SQLException {
        stmt.setString(1, customer.getCustomerName());
        stmt.setInt(2, sexValue);
        stmt.setString(3, customer.getBirthday());
        stmt.setString(4, customer.getEmail());
        stmt.setString(5, customer.getAddress());
        stmt.setInt(6, psnCd);
        stmt.setInt(7, psnCd);
    }

    /**
     * Prepare update statement parameters.
     *
     * @param stmt prepared statement
     * @param customer DTO
     * @param sexValue mapped sex
     * @param psnCd personal code
     * @throws SQLException database error
     */
    private void prepareUpdateStatement(PreparedStatement stmt, T002Dto customer, int sexValue, Integer psnCd) throws SQLException {
        stmt.setString(1, customer.getCustomerName());
        stmt.setInt(2, sexValue);
        stmt.setString(3, customer.getBirthday());
        stmt.setString(4, customer.getEmail());
        stmt.setString(5, customer.getAddress());
        stmt.setInt(6, psnCd);
        stmt.setInt(7, customer.getCustomerID());
    }

    /**
     * Determine if update is needed by comparing with existing record.
     *
     * @param checkStmt prepared statement for checking
     * @param newCustomer new customer DTO
     * @param newSexValue mapped sex
     * @return true if update required
     * @throws SQLException database error
     */
    private boolean isUpdateNeeded(PreparedStatement checkStmt, T002Dto newCustomer, int newSexValue) throws SQLException {
        checkStmt.setInt(1, newCustomer.getCustomerID());
        try (ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next()) {
                boolean nameChanged = !rs.getString(TableConstants.CUST_CUSTOMER_NAME).equals(newCustomer.getCustomerName());
                boolean sexChanged = rs.getInt(TableConstants.CUST_SEX) != newSexValue;
                boolean birthdayChanged = !rs.getString(TableConstants.CUST_BIRTHDAY).equals(newCustomer.getBirthday());
                boolean emailChanged = !rs.getString(TableConstants.CUST_EMAIL).equals(newCustomer.getEmail());
                boolean addressChanged = !rs.getString(TableConstants.CUST_ADDRESS).equals(newCustomer.getAddress());
                return nameChanged || sexChanged || birthdayChanged || emailChanged || addressChanged;
            }
        }
        return true; // update if record not found
    }

    /**
     * Map sex string to database value.
     *
     * @param sex "Male"/"Female"
     * @return 0 for male, 1 for female
     */
    private int mapSex(String sex) {
        if ("Male".equalsIgnoreCase(sex)) return 0;
        if ("Female".equalsIgnoreCase(sex)) return 1;
        throw new IllegalArgumentException("Invalid sex value: " + sex);
    }
}
