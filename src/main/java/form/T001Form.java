package form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import utils.Helper;

/**
 * ActionForm for user login and related information. Used for capturing user
 * input from JSP in Struts.
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-07-21
 */
public class T001Form extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Unique personal code of this user */
	private Integer psnCd;

	/** User ID used for login authentication */
	private String userId;

	/** Password associated with this user account */
	private String password;

	/** Display name of this user */
	private String userName;

	// Getters and Setters
	public Integer getPsnCd() {
		return psnCd;
	}

	public void setPsnCd(Integer psnCd) {
		this.psnCd = psnCd;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
  
	/**
	 * Validate input fields when form is submitted.
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if ("POST".equalsIgnoreCase(request.getMethod())) {
	        if (Helper.isEmpty(userId)) {
	            errors.add("errorMessage", new ActionMessage("error.userId.required"));
	            return errors;
	        } else if (Helper.isEmpty(password)) {
	            errors.add("errorMessage", new ActionMessage("error.password.required"));
	            return errors;
	        }
	    }

	    return errors;
	}

}