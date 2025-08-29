package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import common.Constants;

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

	/**
     * Check if birthday is valid format yyyy/MM/dd
     */
    public static boolean isValidDate(String dateStr) {
        if (isEmpty(dateStr)) {
            return false;
        }
        try {       
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN);
            LocalDate.parse(dateStr.trim(), formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Check if email is valid format
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return Pattern.compile(Constants.EMAIL_REGEX).matcher(email).matches();
    }

}