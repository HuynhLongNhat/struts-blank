package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.TableConstants;
import dto.T001Dto;
import form.T001Form;
import utils.DBUtils;

/**
 * DAO class responsible for handling login operations.
 * <p>
 * Provides methods to verify user credentials against the MSTUSER table.
 * </p>
 */
public class T001Dao {

    /** Singleton instance of T001Dao */
    private static final T001Dao instance = new T001Dao();

    /** Private constructor to prevent external instantiation */
    private T001Dao() {}

    /**
     * Returns the singleton instance of {@code T001Dao}.
     *
     * @return singleton {@code T001Dao} instance
     */
    public static T001Dao getInstance() {
        return instance;
    }

    /**
     * Retrieves a user matching the given credentials.
     * <p>
     * Executes a query against {@code MSTUSER} table to validate login.
     * </p>
     *
     * @param t001Form {@link T001Form} containing {@code userId} and {@code password}
     * @return {@link T001Dto} with user details if credentials match, otherwise {@code null}
     * @throws SQLException if a database access error occurs
     */
    public T001Dto getUserLogin(T001Form t001Form) throws SQLException {
        // Build SQL dynamically using constants
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append(TableConstants.USER_USERID).append(", ")
           .append(TableConstants.USER_USERNAME).append(", ")
           .append(TableConstants.USER_PSN_CD)
           .append(" FROM ").append(TableConstants.TABLE_MSTUSER)
           .append(" WHERE ").append(TableConstants.USER_DELETE_YMD).append(" IS NULL ")
           .append(" AND ").append(TableConstants.USER_USERID).append(" = ?")
           .append(" AND ").append(TableConstants.USER_PASSWORD).append(" = ?");

        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Set query parameters
            ps.setString(1, t001Form.getUserId());
            ps.setString(2, t001Form.getPassword());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Map result set to DTO
                    T001Dto userDto = new T001Dto();
                    userDto.setUserId(rs.getString(TableConstants.USER_USERID));
                    userDto.setUserName(rs.getString(TableConstants.USER_USERNAME));
                    userDto.setPsnCd(rs.getInt(TableConstants.USER_PSN_CD));
                    return userDto;
                }
            }
        }
        return null;
    }
}
