package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.T002Dto;
import utils.DBUtils;

/**
 * Data Access Object (DAO) for handling operations on {@code MSTCUSTOMER}.
 * <p>
 * Provides methods to retrieve, insert, and update customer records.
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
        String sql = "SELECT CUSTOMER_ID, CUSTOMER_NAME, SEX, BIRTHDAY, EMAIL, ADDRESS " +
                     "FROM MSTCUSTOMER WHERE CUSTOMER_ID = ? AND DELETE_YMD IS NULL";

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs); // map ResultSet row to DTO
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
     * @param customer customer data to insert
     * @param psnCd    personal code of the user performing the operation
     * @return {@code true} if insert was successful, otherwise {@code false}
     */
    public void insertCustomer(T002Dto customer, Integer psnCd) throws SQLException {
        String sql = "INSERT INTO MSTCUSTOMER (CUSTOMER_ID, CUSTOMER_NAME, SEX, BIRTHDAY, EMAIL, ADDRESS, " +
                     "DELETE_YMD, INSERT_YMD, INSERT_PSN_CD, UPDATE_YMD, UPDATE_PSN_CD) " +
                     "VALUES (NEXT VALUE FOR SEQ_CUSTOMER_ID, ?, ?, ?, ?, ?, NULL, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)";

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setCustomerParams(stmt, customer);
            stmt.setInt(6, psnCd); // INSERT_PSN_CD
            stmt.setInt(7, psnCd); // UPDATE_PSN_CD

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting customer", e);
        }
    }


    public void updateCustomer(T002Dto customer, Integer psnCd) throws SQLException {
        String sql = "UPDATE MSTCUSTOMER " +
                     "SET CUSTOMER_NAME = ?, SEX = ?, BIRTHDAY = ?, EMAIL = ?, ADDRESS = ?, " +
                     "DELETE_YMD = NULL, UPDATE_YMD = CURRENT_TIMESTAMP, UPDATE_PSN_CD = ? " +
                     "WHERE CUSTOMER_ID = ?";

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setCustomerParams(stmt, customer);
            stmt.setInt(6, psnCd);                   // UPDATE_PSN_CD
            stmt.setInt(7, customer.getCustomerID()); // WHERE clause

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
        dto.setCustomerID(rs.getInt("CUSTOMER_ID"));
        dto.setCustomerName(rs.getString("CUSTOMER_NAME"));
        dto.setSex(rs.getString("SEX"));
        dto.setBirthday(rs.getString("BIRTHDAY"));
        dto.setEmail(rs.getString("EMAIL"));
        dto.setAddress(rs.getString("ADDRESS"));
        return dto;
    }

    /**
     * Sets common customer fields in a {@link PreparedStatement}.
     *
     * @param stmt     prepared statement
     * @param customer customer data
     * @throws SQLException if setting parameters fails
     */
    private void setCustomerParams(PreparedStatement stmt, T002Dto customer) throws SQLException {
        stmt.setString(1, customer.getCustomerName());
        stmt.setString(2, customer.getSex());
        stmt.setString(3, customer.getBirthday());
        stmt.setString(4, customer.getEmail());
        stmt.setString(5, customer.getAddress());
    }
}
