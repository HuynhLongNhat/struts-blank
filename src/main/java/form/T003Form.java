package form;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import common.Constants;
import utils.Helper;

import javax.servlet.http.HttpServletRequest;

/**
 * Struts Form bean for T003 screen. Holds customer data and performs
 * server-side validation.
 */
public class T003Form extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int customerId;
	private String customerName;
	private String sex;
	private String birthday;
	private String email;
	private String address;
	private String mode; // "ADD" or "EDIT"

	// Getters & Setters
	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String action = request.getParameter(Constants.PARAM_ACTION);
		if (Constants.ACTION_SAVE.equals(action)) {
			 if (!Helper.isValidDate(birthday)) {
		            errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_BIRTHDAY_INVALID));
		            return errors;
		        }
		        // Email validation
		        if (!Helper.isValidEmail(email)) {
		            errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_EMAIL_INVALID));
		            return errors;
		        }
		}
		return errors;
	}

}
