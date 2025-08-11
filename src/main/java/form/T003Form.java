package form;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Struts Form bean for T003 screen. Holds customer data and performs
 * server-side validation.
 */
public class T003Form extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer customerId;
	private String customerName;
	private String sex;
	private String birthday;
	private String email;
	private String address;
	private String mode; // "ADD" or "EDIT"

	// Getters & Setters
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
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

		// Birthday validation
		if (birthday == null || birthday.trim().isEmpty()) {
			errors.add("birthday", new ActionMessage("error.birthday.invalid"));
			return errors; // Dừng xử lý luôn
		} else {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				LocalDate.parse(birthday.trim(), formatter);
			} catch (DateTimeParseException e) {
				errors.add("birthday", new ActionMessage("error.birthday.invalid"));
				return errors; // Dừng xử lý luôn
			}
		}

		// Email validation
		if (email == null || email.trim().isEmpty()) {
			errors.add("email", new ActionMessage("error.email.invalid"));
			return errors;
		} else {
			String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
			if (!email.matches(emailRegex)) {
				errors.add("email", new ActionMessage("error.email.invalid"));
				return errors;
			}
		}

		return errors;
	}

}
