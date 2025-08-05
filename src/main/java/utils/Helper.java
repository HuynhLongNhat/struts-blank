package utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Helper - Utility class providing common helper methods used across the
 * application.
 */
public class Helper {

	/**
	 * Check if a string is null or empty after trimming.
	 *
	 * @param value the string to check
	 * @return true if the string is null or empty, false otherwise
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	/**
	 * Checks if the user is logged in.
	 *
	 * @param request HttpServletRequest
	 * @return true if the "user" attribute exists in session, false otherwise
	 */
	public static boolean isLogin(HttpServletRequest request) {
	    HttpSession session = request.getSession(false);
	    return session != null && session.getAttribute("user") != null;
	}


}