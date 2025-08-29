package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts.upload.FormFile;

import dao.T004Dao;
import dto.T002Dto;
import utils.Helper;

public class T004Service {

	private static final int MAX_CUSTOMER_NAME_LENGTH = 50;
	private static final int MAX_ADDRESS_LENGTH = 256;
	private static final String MALE = "Male";
	private static final String FEMALE = "Female";

	private final T004Dao t004Dao;

	// Constructor injection for better testability
	public T004Service() {
		this.t004Dao = new T004Dao();
	}

	public T004Service(T004Dao t004Dao) {
		this.t004Dao = t004Dao;
	}

	public List<String> importFile(FormFile uploadFile , Integer psnCd) throws Exception {
		List<String> errors = new ArrayList<>();
		List<T002Dto> validCustomers = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(uploadFile.getInputStream(), "UTF-8"))) {

			processCsvFile(reader, errors, validCustomers);
		}
		return handleImportResults(errors, validCustomers, psnCd);
	}

	private void processCsvFile(BufferedReader reader, List<String> errors, List<T002Dto> validCustomers)
			throws Exception {
		String line;
		int lineNumber = 0;

		while ((line = reader.readLine()) != null) {
			lineNumber++;

			if (shouldSkipLine(lineNumber, line)) {
				continue;
			}
			processCsvLine(line, lineNumber, errors, validCustomers);
		}
	}

	private boolean shouldSkipLine(int lineNumber, String line) {
		return lineNumber == 1 || line.trim().isEmpty();
	}

	private void processCsvLine(String line, int lineNumber, List<String> errors, List<T002Dto> validCustomers)
			throws SQLException {
		String[] values = parseCsvLine(line);

		CustomerData customerData = extractCustomerData(values);
		List<String> lineErrors = validateCustomerData(customerData, lineNumber);

		if (lineErrors.isEmpty()) {
			validCustomers.add(createCustomerDto(customerData));
		} else {
			errors.addAll(lineErrors);
		}
	}

	private CustomerData extractCustomerData(String[] values) {
		return new CustomerData(values[0].trim(), values[1].trim(),
				                values[2].trim(), values[3].trim().replaceFirst("^=", ""),
				                values[4].trim(), values[5].trim());
	}

	private T002Dto createCustomerDto(CustomerData data) {
		T002Dto customer = new T002Dto();

		int customerId = 0;
		if (!data.customerIdStr.isEmpty()) {
			customerId = Integer.parseInt(data.customerIdStr);
		}

		customer.setCustomerID(customerId);
		customer.setCustomerName(data.customerName);
		customer.setSex(data.sex);
		customer.setBirthday(data.birthday);
		customer.setEmail(data.email);
		customer.setAddress(data.address);

		return customer;
	}

	private List<String> handleImportResults(List<String> errors, List<T002Dto> validCustomers, Integer psnCd) throws Exception {
	    List<String> results = new ArrayList<>();

	    if (!errors.isEmpty()) {
	        return errors; // Trả về lỗi nếu có
	    }

	    if (validCustomers.isEmpty()) {
	        throw new Exception("No valid customer data to import");
	    }

	    // DAO trả về danh sách dòng insert/update
	    Map<String, List<Integer>> result = t004Dao.importCustomerData(validCustomers, psnCd);

	    if (result == null || result.isEmpty()) {
	        throw new Exception("Import failed due to system error.");
	    }

	    List<Integer> insertedLines = result.getOrDefault("inserted", new ArrayList<>());
	    List<Integer> updatedLines = result.getOrDefault("updated", new ArrayList<>());

	    results.add("Customer data have been imported successfully.");
	    results.add("Inserted line(s): " + (insertedLines.isEmpty() ? "None" : joinIntegers(insertedLines)));
	    results.add("Updated line(s): " + (updatedLines.isEmpty() ? "None" : joinIntegers(updatedLines)));

	    return results;
	}

	private String joinIntegers(List<Integer> list) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < list.size(); i++) {
	        sb.append(list.get(i));
	        if (i < list.size() - 1) {
	            sb.append(", ");
	        }
	    }
	    return sb.toString();
	}

	private String[] parseCsvLine(String line) {
		List<String> result = new ArrayList<>();
		boolean inQuotes = false;
		StringBuilder currentField = new StringBuilder();

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (c == '"') {
				inQuotes = !inQuotes;
			} else if (c == ',' && !inQuotes) {
				result.add(currentField.toString());
				currentField = new StringBuilder();
			} else {
				currentField.append(c);
			}
		}
		result.add(currentField.toString());

		return result.toArray(new String[0]);
	}

	private List<String> validateCustomerData(CustomerData data, int lineNumber) throws SQLException {
		List<String> errors = new ArrayList<>();

		validateCustomerId(data.customerIdStr, lineNumber, errors);
		validateCustomerName(data.customerName, lineNumber, errors);
		validateSex(data.sex, lineNumber, errors);
		validateBirthday(data.birthday, lineNumber, errors);
		validateEmail(data.email, lineNumber, errors);
		validateAddress(data.address, lineNumber, errors);

		return errors;
	}

	private void validateCustomerId(String customerIdStr, int lineNumber, List<String> errors) throws SQLException {
		if (!customerIdStr.isEmpty()) {
			try {
				int customerId = Integer.parseInt(customerIdStr);
				boolean customerExists = t004Dao.checkCustomerExists(customerId);
				if (!customerExists) {
					errors.add("Line " + lineNumber + ": CUSTOMER_ID=" + customerId + " is not existed");
				}
			} catch (NumberFormatException e) {
				errors.add("Line " + lineNumber + ": CUSTOMER_ID=" + customerIdStr + " is not a valid number");
			}
		}
	}

	private void validateCustomerName(String customerName, int lineNumber, List<String> errors) {
		if (customerName.isEmpty()) {
			errors.add("Line " + lineNumber + ": CUSTOMER_NAME is empty");
		} else if (customerName.length() > MAX_CUSTOMER_NAME_LENGTH) {
			errors.add("Line " + lineNumber + ": Value of CUSTOMER_NAME is more than " + MAX_CUSTOMER_NAME_LENGTH
					+ " characters");
		}
	}

	private void validateSex(String sex, int lineNumber, List<String> errors) {
		if (!sex.equalsIgnoreCase(MALE) && !sex.equalsIgnoreCase(FEMALE)) {
			errors.add("Line " + lineNumber + ": SEX=" + sex + " is invalid");
		}
	}

	private void validateBirthday(String birthday, int lineNumber, List<String> errors) {
		if (!Helper.isValidDate(birthday)) {
			errors.add("Line " + lineNumber + ": BIRTHDAY=" + birthday + " is invalid");
		}
	}

	private void validateEmail(String email, int lineNumber, List<String> errors) {
		if (!Helper.isValidEmail(email)) {
			errors.add("Line " + lineNumber + ": EMAIL=" + email + " is invalid");
		}
	}
	private void validateAddress(String address, int lineNumber, List<String> errors) {
		if (address.length() > MAX_ADDRESS_LENGTH) {
			errors.add("Line " + lineNumber + ": Value of ADDRESS is more than " + MAX_ADDRESS_LENGTH + " characters");
		}
	}

	// Helper record to hold customer data
	private static class CustomerData {
		final String customerIdStr;
		final String customerName;
		final String sex;
		final String birthday;
		final String email;
		final String address;

		CustomerData(String customerIdStr, String customerName, String sex, String birthday, String email,
				String address) {
			this.customerIdStr = customerIdStr;
			this.customerName = customerName;
			this.sex = sex;
			this.birthday = birthday;
			this.email = email;
			this.address = address;
		}
	}
}