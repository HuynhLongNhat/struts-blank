package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import common.Constants;
import dao.T004Dao;
import dto.T002Dto;
import utils.Helper;

/**
 * Service class for handling customer import functionality (T004).
 * Responsibilities:
 *  - Read CSV file
 *  - Validate data line by line
 *  - Insert or update valid customers in database
 *  - Return success or error messages
 */
public class T004Service {
	
	
	/** Singleton instance of {@code T001Service} */
	private static final T004Service instance = new T004Service();

	/** DAO instance for accessing {@code MSTUSER} table */
	private final T004Dao t004Dao = T004Dao.getInstance();

	/** Private constructor to enforce singleton pattern */
	private T004Service() {
	}

	/**
	 * Returns the singleton instance of {@code T001Service}.
	 *
	 * @return singleton instance
	 */
	public static T004Service getInstance() {
		return instance;
	}

    /**
     * Imports customer data from uploaded CSV file.
     * Steps:
     * 1. Read CSV file with UTF-8 encoding
     * 2. Process each line
     * 3. Validate and collect valid customers
     * 4. Save to database
     * 5. Populate success or error messages
     *
     * @param uploadFile      Uploaded CSV file
     * @param psnCd           Logged-in user's person code
     * @param successMessages ActionMessages to store success messages
     * @return ActionMessages containing errors if any
     * @throws Exception if reading file or database operations fail
     */
    public ActionMessages importFile(FormFile uploadFile, Integer psnCd, ActionMessages successMessages) throws Exception {
        ActionMessages errors = new ActionMessages(); // Store validation errors
        List<T002Dto> validCustomers = new ArrayList<>(); // Store valid customer DTOs

        // Read CSV file with UTF-8 encoding
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(uploadFile.getInputStream(), "UTF-8"))) {
            processCsvFile(reader, errors, validCustomers);
        }

        if (!errors.isEmpty()) {
            return errors; // Return errors if any
        }

        // Insert/update valid data and populate success messages
        handleImportResults(successMessages, validCustomers, psnCd);
        return errors; // Empty if successful
    }

    /**
     * Processes CSV file line by line.
     * Skips the header line and empty lines automatically.
     * Each valid line is parsed, validated, and added to validCustomers.
     *
     * @param reader         BufferedReader for CSV file
     * @param errors         ActionMessages to store validation errors
     * @param validCustomers List to store valid customer DTOs
     * @throws Exception if reading file or processing fails
     */
    private void processCsvFile(BufferedReader reader, ActionMessages errors, List<T002Dto> validCustomers)
            throws Exception {
        String line;
        int lineNumber = 0;

        // Read each line from CSV
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            // Skip the first line (header) or empty lines
            if (lineNumber == 1 || line.trim().isEmpty()) {
                continue; // Move to next line
            }
            // Process the current CSV line: parse, validate, and add to validCustomers
            processCsvLine(line, lineNumber, errors, validCustomers);
        }
    }

    /**
     * Processes a single CSV line: parses, validates, and adds valid DTOs.
     *
     * @param line           CSV line content
     * @param lineNumber     Current line number
     * @param errors         ActionMessages to collect errors
     * @param validCustomers List to collect valid customer DTOs
     * @throws SQLException if database validation fails
     */
    private void processCsvLine(String line, int lineNumber, ActionMessages errors, List<T002Dto> validCustomers)
            throws SQLException {
        String[] values = parseCsvLine(line);

        // Trim and extract each field
        String customerIdStr = values[0].trim();
        String customerName = values[1].trim();
        String sex = values[2].trim();
        String birthday = values[3].trim().replaceFirst("^=", ""); // Remove = at start if Excel format
        String email = values[4].trim();
        String address = values[5].trim();

        // Validate current line
        ActionMessages lineErrors = validateCustomerData(customerIdStr, customerName, sex, birthday, email, address,
                lineNumber);

        if (lineErrors.isEmpty()) {
            // Create DTO for valid customer
            T002Dto dto = new T002Dto();
            dto.setCustomerID(customerIdStr.isEmpty() ? 0 : Integer.parseInt(customerIdStr));
            dto.setCustomerName(customerName);
            dto.setSex(sex);
            dto.setBirthday(birthday);
            dto.setEmail(email);
            dto.setAddress(address);

            validCustomers.add(dto); // Add to valid list
        } else {
            errors.add(lineErrors); // Add errors
        }
    }

    /**
     * Handles database insert/update and populates success messages.
     *
     * @param messages       ActionMessages to store success messages
     * @param validCustomers List of valid customer DTOs
     * @param psnCd          Logged-in user's person code
     * @throws Exception if database operations fail
     */
    private void handleImportResults(ActionMessages messages, List<T002Dto> validCustomers, Integer psnCd)
            throws Exception {
        Map<String, List<Integer>> result = t004Dao.importCustomerData(validCustomers, psnCd);

        List<Integer> insertedLines = result.getOrDefault("inserted", new ArrayList<>());
        List<Integer> updatedLines = result.getOrDefault("updated", new ArrayList<>());

        // Add success messages
        messages.add(ActionMessages.GLOBAL_MESSAGE,
                new ActionMessage(Constants.SUCCESS_IMPORT_COMPLETED));
        messages.add(ActionMessages.GLOBAL_MESSAGE,
                new ActionMessage(Constants.SUCCESS_IMPORT_INSERTED,
                        insertedLines.isEmpty() ? "None" : joinIntegers(insertedLines)));
        messages.add(ActionMessages.GLOBAL_MESSAGE,
                new ActionMessage(Constants.SUCCESS_IMPORT_UPDATED,
                        updatedLines.isEmpty() ? "None" : joinIntegers(updatedLines)));
    }

    /**
     * Joins a list of integers into a comma-separated string.
     *
     * @param list List of integers
     * @return Comma-separated string
     */
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

    /**
     * Parses a CSV line into array of fields, handling quoted values.
     *
     * @param line CSV line
     * @return Array of fields
     */
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes; // Toggle quote state
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        result.add(currentField.toString()); // Add last field
        return result.toArray(new String[0]);
    }

    /**
     * Validates all fields for a customer line.
     *
     * @param customerIdStr Customer ID
     * @param customerName  Customer name
     * @param sex           Sex
     * @param birthday      Birthday
     * @param email         Email
     * @param address       Address
     * @param lineNumber    Line number for error messages
     * @return ActionMessages containing validation errors
     * @throws SQLException if database validation fails
     */
    private ActionMessages validateCustomerData(String customerIdStr, String customerName, String sex, String birthday,
            String email, String address, int lineNumber) throws SQLException {
        ActionMessages errors = new ActionMessages();
        validateCustomerId(customerIdStr, lineNumber, errors);
        validateCustomerName(customerName, lineNumber, errors);
        validateSex(sex, lineNumber, errors);
        validateBirthday(birthday, lineNumber, errors);
        validateEmail(email, lineNumber, errors);
        validateAddress(address, lineNumber, errors);
        return errors;
    }


    /**
     * Validates the Customer ID.
     * Checks if the ID exists in the database if provided.
     *
     * @param customerIdStr Customer ID as string
     * @param lineNumber    Current line number for error messages
     * @param errors        ActionMessages to collect validation errors
     * @throws SQLException If database check fails
     */
    private void validateCustomerId(String customerIdStr, int lineNumber, ActionMessages errors) throws SQLException {
        // Only validate if ID is not empty
        if (!customerIdStr.isEmpty()) {
            int customerId = Integer.parseInt(customerIdStr); // Convert to integer
            boolean customerExists = t004Dao.checkCustomerExists(customerId); // Check existence in DB
            if (!customerExists) {
                // Add error if customer does not exist
                errors.add(Constants.GLOBAL,
                        new ActionMessage(Constants.ERROR_IMPORT_CUSTOMER_ID_NOT_EXIST, lineNumber, customerIdStr));
            }
        }
    }

    /**
     * Validates the Customer Name.
     * Must not be empty and not exceed MAX length.
     *
     * @param customerName Customer name
     * @param lineNumber   Current line number for error messages
     * @param errors       ActionMessages to collect validation errors
     */
    private void validateCustomerName(String customerName, int lineNumber, ActionMessages errors) {
        if (customerName.isEmpty()) {
            // Error if name is empty
            errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_IMPORT_CUSTOMER_NAME_EMPTY, lineNumber));
        } else if (customerName.length() > Constants.MAX_CUSTOMER_NAME_LENGTH) {
            // Error if name is too long
            errors.add(Constants.GLOBAL,
                    new ActionMessage(Constants.ERROR_IMPORT_CUSTOMER_NAME_TOO_LONG, lineNumber,
                            Constants.MAX_CUSTOMER_NAME_LENGTH));
        }
    }

    /**
     * Validates the Sex field.
     * Must be either MALE or FEMALE.
     *
     * @param sex        Sex value
     * @param lineNumber Current line number for error messages
     * @param errors     ActionMessages to collect validation errors
     */
    private void validateSex(String sex, int lineNumber, ActionMessages errors) {
        if (!sex.equalsIgnoreCase(Constants.MALE) && !sex.equalsIgnoreCase(Constants.FEMALE)) {
            // Add error if value is invalid
            errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_IMPORT_SEX_INVALID, lineNumber, sex));
        }
    }

    /**
     * Validates the Birthday field.
     * Must be a valid date in the expected format.
     *
     * @param birthday   Birthday string
     * @param lineNumber Current line number for error messages
     * @param errors     ActionMessages to collect validation errors
     */
    private void validateBirthday(String birthday, int lineNumber, ActionMessages errors) {
        if (!Helper.isValidDate(birthday)) {
            // Add error if date is invalid
            errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_IMPORT_BIRTHDAY_INVALID, lineNumber, birthday));
        }
    }

    /**
     * Validates the Email field.
     * Must be a valid email format.
     *
     * @param email      Email string
     * @param lineNumber Current line number for error messages
     * @param errors     ActionMessages to collect validation errors
     */
    private void validateEmail(String email, int lineNumber, ActionMessages errors) {
        if (!Helper.isValidEmail(email)) {
            // Add error if email format is invalid
            errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_IMPORT_EMAIL_INVALID, lineNumber, email));
        }
    }

    /**
     * Validates the Address field.
     * Must not exceed MAX length.
     *
     * @param address    Address string
     * @param lineNumber Current line number for error messages
     * @param errors     ActionMessages to collect validation errors
     */
    private void validateAddress(String address, int lineNumber, ActionMessages errors) {
        if (address.length() > Constants.MAX_ADDRESS_LENGTH) {
            // Add error if address too long
            errors.add(Constants.GLOBAL,
                    new ActionMessage(Constants.ERROR_IMPORT_ADDRESS_TOO_LONG, lineNumber,
                            Constants.MAX_ADDRESS_LENGTH));
        }
    }

}
