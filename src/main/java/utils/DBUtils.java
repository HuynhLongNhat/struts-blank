package utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBUtils - Utility class for managing database connections using the Singleton
 * pattern. This class loads database configurations from a properties file and
 * provides JDBC connections when needed. It ensures thread-safety and proper
 * resource management.
 *
 * @author YourName
 * @version 1.0
 * @since 2025-07-21
 */
public class DBUtils {

	/** Eager Singleton instance */
	private static final DBUtils instance = new DBUtils();

	private final String url;
	private final String username;
	private final String password;

	/**
	 * Private constructor for eager Singleton. Loads database configuration from
	 * dbConfig.properties.
	 *
	 * @throws RuntimeException
	 */
	private DBUtils() {
		try (InputStream input = DBUtils.class.getClassLoader().getResourceAsStream("dbConfig.properties")) {
			if (input == null) {
				throw new RuntimeException("dbConfig.properties not found in classpath.");
			}

			Properties prop = new Properties();
			prop.load(input);

			String serverName = prop.getProperty("db.serverName");
			String port = prop.getProperty("db.port", "1433");
			String databaseName = prop.getProperty("db.databaseName");
			username = prop.getProperty("db.username");
			password = prop.getProperty("db.password");

			url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s", serverName, port, databaseName);

			// Load JDBC driver once
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Failed to load DB configuration", e);
		}
	}

	/**
	 * Provides access to the Singleton instance.
	 *
	 * @return the singleton instance of  DBUtils
	 */
	public static DBUtils getInstance() {
		return instance;
	}

	/**
	 * Creates and returns a new database connection. Caller is responsible for
	 * closing the connection after use.
	 *
	 * @return a new Connection to the configured database
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
	public static void main(String[] args) {
	    try {
	        DBUtils dbUtils = DBUtils.getInstance();

	        try (Connection conn = dbUtils.getConnection()) {
	            if (conn != null && !conn.isClosed()) {
	                System.out.println("Kết nối thành công tới database!");
	            } else {
	                System.out.println("Kết nối thất bại.");
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}