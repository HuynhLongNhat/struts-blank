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
     * Retrieves a user matching the given login credentials.
     *
     * <p>This method validates login by querying the {@code MSTUSER} table:
     * <ul>
     *   <li>Ensures the account is not marked as deleted ({@code DELETE_YMD IS NULL}).</li>
     *   <li>Checks if {@code USERID} and {@code PASSWORD} match the given input.</li>
     *   <li>If a match is found, maps user details into {@link T001Dto}.</li>
     *   <li>If no record matches, returns {@code null}.</li>
     * </ul>
     *
     * <p>Security note: The password is compared in plain text here.
     * In a production system, consider storing hashed passwords and 
     * verifying with a secure hash function (e.g., bcrypt, PBKDF2, Argon2).</p>
     *
     * @param t001Form the {@link T001Form} containing {@code userId} and {@code password}
     * @return a {@link T001Dto} with user details if credentials are valid; otherwise {@code null}
     * @throws SQLException if a database access error occurs
     */
    public T001Dto getUserLogin(T001Form t001Form) throws SQLException {
        // Build SQL query dynamically using constants for column and table names
        StringBuilder sql = new StringBuilder()
            .append("SELECT ")
            .append(TableConstants.USER_USERID).append(", ")
            .append(TableConstants.USER_USERNAME).append(", ")
            .append(TableConstants.USER_PSN_CD)
            .append(" FROM ").append(TableConstants.TABLE_MSTUSER)
            .append(" WHERE ").append(TableConstants.USER_DELETE_YMD).append(" IS NULL ")
            .append(" AND ").append(TableConstants.USER_USERID).append(" = ?")
            .append(" AND ").append(TableConstants.USER_PASSWORD).append(" = ?");

        // Use try-with-resources: auto-closes connection, statement, and result set
        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Bind parameters: userId & password
            ps.setString(1, t001Form.getUserId());
            ps.setString(2, t001Form.getPassword());

            // Execute query
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Map DB row into DTO
                    T001Dto userDto = new T001Dto();
                    userDto.setUserId(rs.getString(TableConstants.USER_USERID));
                    userDto.setUserName(rs.getString(TableConstants.USER_USERNAME));
                    userDto.setPsnCd(rs.getInt(TableConstants.USER_PSN_CD));
                    return userDto;
                }
            }
        }

        // No user found
        return null;
    }


}
