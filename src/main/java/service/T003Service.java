package service;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import common.Constants;
import dao.T003Dao;
import dto.T002Dto;
import form.T003Form;

/**
 * Service class for handling business logic related to a single customer
 * record.
 *
 * <p>
 * This class serves as the intermediary between the controller layer and the
 * {@link T003Dao} data access layer. It provides methods for retrieving,
 * creating, and updating individual customer records.
 * </p>
 *
 * <p>
 * Implements the Singleton pattern to ensure that only one instance exists
 * throughout the application lifecycle.
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-07-21
 */
public class T003Service {

	/** Singleton instance of {@code T003Service}. */
	private static final T003Service INSTANCE = new T003Service();

	/** DAO instance for interacting with the MSTCUSTOMER table. */
	private final T003Dao t003Dao = T003Dao.getInstance();

	/** Private constructor to prevent external instantiation. */
	private T003Service() {
	}

	/**
	 * Returns the singleton instance of {@code T003Service}.
	 *
	 * @return singleton instance
	 */
	public static T003Service getInstance() {
		return INSTANCE;
	}

	/**
	 * Retrieves a customer by their ID.
	 *
	 * @param customerId unique ID of the customer
	 * @return a populated {@link T002Dto} if the customer exists, otherwise
	 *         {@code null}
	 */
	public void getCustomerById(T003Form form, HttpServletRequest request) {
		int customerId = form.getCustomerId();
		if (customerId != 0) {
			try {
				T002Dto customer = t003Dao.getCustomerById(customerId);
				if (customer != null) {
					form.setCustomerId(customer.getCustomerID());
					form.setCustomerName(customer.getCustomerName());
					form.setSex(customer.getSex());
					form.setBirthday(customer.getBirthday());
					form.setEmail(customer.getEmail());
					form.setAddress(customer.getAddress());
					form.setMode(Constants.MODE_EDIT);
				} else {
					form.setMode(Constants.MODE_ADD);
				}
			} catch (Exception e) {
				form.setMode(Constants.MODE_ADD);
			}
		} else {
			form.setMode(Constants.MODE_ADD);
		}
	}

	public boolean saveCustomer(T003Form editForm, HttpServletRequest request, Integer psnCd) {
		try {
			if (Constants.MODE_ADD.equals(editForm.getMode())) {
				t003Dao.insertCustomer(editForm, psnCd);
			} else {
				t003Dao.updateCustomer(editForm, psnCd);
			}
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
