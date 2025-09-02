package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.TableConstants;
import dto.T002Dto;
import form.T003Form;
import utils.DBUtils;

/**
 * Data Access Object (DAO) for handling operations on {@code MSTCUSTOMER}.
 * <p>
 * Provides methods to retrieve, insert, and update customer records.
 * </p>
 *
 * <p>
 * Implements the Singleton pattern to ensure only one instance is used
 * throughout the application lifecycle.
 * </p>
 */
public class T003Dao {

    /** Singleton eager instance */
    private static final T003Dao instance = new T003Dao();

    /** Private constructor to prevent external instantiation */
    private T003Dao() {}

    /**
     * Returns the singleton instance of {@code T003Dao}.
     *
     * @return the singleton instance
     */
    public static T003Dao getInstance() {
        return instance;
    }

    /**
     * Retrieves a customer by ID if not marked as deleted.
     *
     * @param customerId ID of the customer to retrieve
     * @return a {@link T002Dto} object if found, otherwise {@code null}
     */
    public T002Dto getCustomerById(Integer customerId) {
        StringBuilder sql = new StringBuilder()
            .append("SELECT ")
            .append(TableConstants.CUST_CUSTOMER_ID).append(", ")
            .append(TableConstants.CUST_CUSTOMER_NAME).append(", ")
            .append(TableConstants.CUST_SEX).append(", ")
            .append(TableConstants.CUST_BIRTHDAY).append(", ")
            .append(TableConstants.CUST_EMAIL).append(", ")
            .append(TableConstants.CUST_ADDRESS)
            .append(" FROM ").append(TableConstants.TABLE_MSTCUSTOMER)
            .append(" WHERE ").append(TableConstants.CUST_CUSTOMER_ID).append(" = ?")
            .append(" AND ").append(TableConstants.CUST_DELETE_YMD).append(" IS NULL");

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs); // Map ResultSet row to DTO
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inserts a new customer record.
     *
     * @param editForm customer data to insert
     * @param psnCd    personal code of the user performing the operation
     * @throws SQLException if insert fails
     */
    public void insertCustomer(T003Form editForm, Integer psnCd) throws SQLException {
        StringBuilder sql = new StringBuilder()
            .append("INSERT INTO ").append(TableConstants.TABLE_MSTCUSTOMER).append(" (")
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
            .append("VALUES (NEXT VALUE FOR SEQ_CUSTOMER_ID, ?, ?, ?, ?, ?, ")
            .append("NULL, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            setCustomerParams(stmt, editForm);
            stmt.setInt(6, psnCd); // INSERT_PSN_CD
            stmt.setInt(7, psnCd); // UPDATE_PSN_CD

            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing customer record.
     *
     * @param editForm customer data to update
     * @param psnCd    personal code of the user performing the operation
     * @throws SQLException if update fails
     */
    public void updateCustomer(T003Form editForm, Integer psnCd) throws SQLException {
        StringBuilder sql = new StringBuilder()
            .append("UPDATE ").append(TableConstants.TABLE_MSTCUSTOMER).append(" SET ")
            .append(TableConstants.CUST_CUSTOMER_NAME).append(" = ?, ")
            .append(TableConstants.CUST_SEX).append(" = ?, ")
            .append(TableConstants.CUST_BIRTHDAY).append(" = ?, ")
            .append(TableConstants.CUST_EMAIL).append(" = ?, ")
            .append(TableConstants.CUST_ADDRESS).append(" = ?, ")
            .append(TableConstants.CUST_DELETE_YMD).append(" = NULL, ")
            .append(TableConstants.CUST_UPDATE_YMD).append(" = CURRENT_TIMESTAMP, ")
            .append(TableConstants.CUST_UPDATE_PSN_CD).append(" = ? ")
            .append("WHERE ").append(TableConstants.CUST_CUSTOMER_ID).append(" = ?");

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            setCustomerParams(stmt, editForm);
            stmt.setInt(6, psnCd); // UPDATE_PSN_CD
            stmt.setInt(7, editForm.getCustomerId()); // WHERE clause

            stmt.executeUpdate();
        }
    }

    /**
     * Maps the current row of a {@link ResultSet} to a {@link T002Dto}.
     *
     * @param rs result set positioned at a row
     * @return populated {@link T002Dto}
     * @throws SQLException if column retrieval fails
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

    /**
     * Sets common customer fields in a {@link PreparedStatement}.
     *
     * @param stmt     prepared statement
     * @param editForm customer data
     * @throws SQLException if setting parameters fails
     */
    private void setCustomerParams(PreparedStatement stmt, T003Form editForm) throws SQLException {
        stmt.setString(1, editForm.getCustomerName());
        stmt.setString(2, editForm.getSex());
        stmt.setString(3, editForm.getBirthday());
        stmt.setString(4, editForm.getEmail());
        stmt.setString(5, editForm.getAddress());
    }
}
