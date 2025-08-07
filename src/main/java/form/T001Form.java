package form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import utils.Helper;

/**
 * Represents the login form in a Struts 1.x application.
 * <p>
 * This form is used to capture user credentials and related information
 * submitted from a JSP page for authentication purposes.
 * </p>
 * 
 * <p>
 * <b>Fields include:</b>
 * </p>
 * <ul>
 * <li>{@code psnCd} - Unique personal code of the user.</li>
 * <li>{@code userId} - User ID for login authentication.</li>
 * <li>{@code password} - Password associated with the user account.</li>
 * <li>{@code userName} - Display name of the user.</li>
 * </ul>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-07-21
 */
public class T001Form extends ActionForm {

	/** Serial version UID for serialization. */
	private static final long serialVersionUID = 1L;

	/** Unique personal code of the user. */
	private Integer psnCd;

	/** User ID used for login authentication. */
	private String userId;

	/** Password associated with this user account. */
	private String password;

	/** Display name of the user. */
	private String userName;

	/**
	 * Gets the personal code of the user.
	 *
	 * @return the personal code of the user
	 */
	public Integer getPsnCd() {
		return psnCd;
	}

	/**
	 * Sets the personal code of the user.
	 *
	 * @param psnCd the personal code to set
	 */
	public void setPsnCd(Integer psnCd) {
		this.psnCd = psnCd;
	}

	/**
	 * Gets the user ID.
	 *
	 * @return the user ID
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user ID.
	 *
	 * @param userId the user ID to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Gets the password of the user.
	 *
	 * @return the password of the user
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password of the user.
	 *
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the display name of the user.
	 *
	 * @return the display name of the user
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the display name of the user.
	 *
	 * @param userName the display name to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Validates the user input when the form is submitted.
	 * <p>
	 * Ensures that both {@code userId} and {@code password} are not empty.
	 * </p>
	 *
	 * @param mapping the action mapping used to select this instance
	 * @param request the HTTP request being processed
	 * @return an {@link ActionErrors} object containing any validation errors;
	 *         returns an empty {@code ActionErrors} object if validation passes
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if (Helper.isEmpty(userId)) {
			errors.add("errorMessage", new ActionMessage("error.userId.required"));
		} else if (Helper.isEmpty(password)) {
			errors.add("errorMessage", new ActionMessage("error.password.required"));

		}

		return errors;
	}
}
