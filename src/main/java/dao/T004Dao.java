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
  

    /** Singleton instance of T001Dao */
    private static final T004Dao instance = new T004Dao();

    /** Private constructor to prevent external instantiation */
    private T004Dao() {}

    /**
     * Returns the singleton instance of {@code T001Dao}.
     *
     * @return singleton {@code T001Dao} instance
     */
    public static T004Dao getInstance() {
        return instance;
    }
    
    /**
     * Checks whether a customer exists in the database and is not marked as deleted.
     *
     * <p>This method queries the {@code MSTCUSTOMER} table by {@code CUSTOMER_ID} 
     * and ensures that the {@code DELETE_YMD} field is NULL 
     * (i.e., the customer is still active and not logically deleted).</p>
     *
     * @param customerId the unique ID of the customer to check
     * @return {@code true} if the customer exists and is active, {@code false} otherwise
     * @throws SQLException if any database access error occurs
     */
    public boolean checkCustomerExists(int customerId) throws SQLException {
        // SQL query to check customer existence where DELETE_YMD is NULL
        String sql = "SELECT COUNT(*) FROM " + TableConstants.TABLE_MSTCUSTOMER +
                     " WHERE " + TableConstants.CUST_CUSTOMER_ID + " = ? " +
                     "AND " + TableConstants.CUST_DELETE_YMD + " IS NULL";

        // Open DB connection and prepare statement
        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Bind parameter: set the customer ID
            pstmt.setInt(1, customerId);

            // Execute query and check if result count > 0
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }


    /**
     * Imports a list of customers: inserts new customers or updates existing ones.
     *
     * <p>This method runs inside a transaction. If any insert/update fails, 
     * all changes are rolled back. Customers without ID are treated as new inserts. 
     * Existing customers are updated only if their data has changed.</p>
     *
     * @param customers list of customer DTOs to be imported
     * @param psnCd     personal code of the operator performing the action
     * @return a result map containing:
     *         <ul>
     *           <li>"inserted" → list of row indexes that were inserted</li>
     *           <li>"updated" → list of row indexes that were updated</li>
     *         </ul>
     * @throws SQLException if any database error occurs (insertion, update, or rollback)
     */
    public Map<String, List<Integer>> importCustomerData(List<T002Dto> customers, Integer psnCd) throws SQLException {
        Map<String, List<Integer>> resultMap = new HashMap<>();
        List<Integer> insertedIndexes = new ArrayList<>();
        List<Integer> updatedIndexes = new ArrayList<>();

        // Build SQL templates for insert, update, and check
        String insertSql = buildInsertSql();
        String updateSql = buildUpdateSql();
        String checkSql = buildCheckSql();

        // Get DB connection
        try (Connection conn = DBUtils.getInstance().getConnection()) {
            conn.setAutoCommit(false); // start transaction manually

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

                // Iterate over customers and decide insert/update
                for (int i = 0; i < customers.size(); i++) {
                    T002Dto customer = customers.get(i);
                    int sexValue = mapSex(customer.getSex()); // map sex from string to int (0/1)

                    if (customer.getCustomerID() == 0) {
                        // Case 1: Insert new customer
                        prepareInsertStatement(insertStmt, customer, sexValue, psnCd);
                        insertStmt.addBatch();
                        insertedIndexes.add(i + 1); // store line number (1-based index)
                    } else if (isUpdateNeeded(checkStmt, customer, sexValue)) {
                        // Case 2: Update existing customer only if data is different
                        prepareUpdateStatement(updateStmt, customer, sexValue, psnCd);
                        updateStmt.addBatch();
                        updatedIndexes.add(i + 1);
                    }
                }

                // Execute batch for insert and update separately
                executeBatch(insertStmt, insertedIndexes);
                executeBatch(updateStmt, updatedIndexes);

                conn.commit(); // commit all if success

                // Return result summary
                resultMap.put("inserted", insertedIndexes);
                resultMap.put("updated", updatedIndexes);
                return resultMap;

            } catch (SQLException e) {
                conn.rollback(); // rollback all changes if any error
                throw e;
            } finally {
                conn.setAutoCommit(true); // reset autocommit back to default
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
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(TableConstants.TABLE_MSTCUSTOMER).append(" (")
          .append(TableConstants.CUST_CUSTOMER_ID).append(", ")
          .append(TableConstants.CUST_CUSTOMER_NAME).append(", ")
          .append(TableConstants.CUST_SEX).append(", ")
          .append(TableConstants.CUST_BIRTHDAY).append(", ")
          .append(TableConstants.CUST_EMAIL).append(", ")
          .append(TableConstants.CUST_ADDRESS).append(", ")
          .append(TableConstants.CUST_DELETE_YMD).append(", ")
          .append(TableConstants.CUST_INSERT_YMD).append(", ")
          .append(TableConstants.CUST_INSERT_PSN_CD).append(", ")
          .append(TableConstants.CUST_UPDATE_YMD).append(", ")
          .append(TableConstants.CUST_UPDATE_PSN_CD).append(") ")
          .append("VALUES (NEXT VALUE FOR SEQ_CUSTOMER_ID, ?, ?, ?, ?, ?, NULL, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");
        return sb.toString();
    }
    /**
     * Build SQL for updating a customer.
     *
     * @return SQL string
     */
    private String buildUpdateSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(TableConstants.TABLE_MSTCUSTOMER).append(" SET ")
          .append(TableConstants.CUST_CUSTOMER_NAME).append(" = ?, ")
          .append(TableConstants.CUST_SEX).append(" = ?, ")
          .append(TableConstants.CUST_BIRTHDAY).append(" = ?, ")
          .append(TableConstants.CUST_EMAIL).append(" = ?, ")
          .append(TableConstants.CUST_ADDRESS).append(" = ?, ")
          .append(TableConstants.CUST_UPDATE_YMD).append(" = CURRENT_TIMESTAMP, ")
          .append(TableConstants.CUST_UPDATE_PSN_CD).append(" = ? ")
          .append("WHERE ").append(TableConstants.CUST_CUSTOMER_ID).append(" = ?");
        return sb.toString();
    }

    /**
     * Build SQL for checking existing customer.
     *
     * @return SQL string
     */
    private String buildCheckSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ")
          .append(TableConstants.CUST_CUSTOMER_NAME).append(", ")
          .append(TableConstants.CUST_SEX).append(", ")
          .append(TableConstants.CUST_BIRTHDAY).append(", ")
          .append(TableConstants.CUST_EMAIL).append(", ")
          .append(TableConstants.CUST_ADDRESS).append(" ")
          .append("FROM ").append(TableConstants.TABLE_MSTCUSTOMER).append(" ")
          .append("WHERE ").append(TableConstants.CUST_CUSTOMER_ID).append(" = ? ")
          .append("AND ").append(TableConstants.CUST_DELETE_YMD).append(" IS NULL");
        return sb.toString();
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
