package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.T001Dto;
import utils.DBUtils;

/**
 * DAO class responsible for handling login operations.
 * <p>
 * Provides methods to verify user credentials against the {@code MSTUSER} table.
 * </p>
 */
public class T001Dao {

    /** Singleton instance of T001Dao */
    private static final T001Dao instance = new T001Dao();

    /** Private constructor to prevent external instantiation */
    private T001Dao() {
    }

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
     * @param inputDto {@link T001Dto} containing {@code userId} and {@code password}
     * @return {@link T001Dto} with user details if credentials match, otherwise {@code null}
     * @throws SQLException if a database access error occurs
     */
    public T001Dto getUserLogin(T001Dto inputDto) throws SQLException {
        // SQL query to validate user credentials
        String sql = """
                SELECT USERID, USERNAME, PSN_CD
                FROM MSTUSER
                WHERE DELETE_YMD IS NULL AND USERID = ? AND PASSWORD = ?
                """;

        // Try-with-resources to ensure connection and statement are closed properly
        try (Connection conn = DBUtils.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set query parameters from input DTO
            ps.setString(1, inputDto.getUserId());
            ps.setString(2, inputDto.getPassword());

            // Execute query and process result
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Map result set to DTO
                    T001Dto userDto = new T001Dto();
                    userDto.setUserId(rs.getString("USERID"));
                    userDto.setUserName(rs.getString("USERNAME"));
                    userDto.setPsnCd(rs.getInt("PSN_CD"));
                    return userDto;
                }
            }
        }

        // Return null if no matching user found
        return null;
    }
}
