package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.struts.upload.FormFile;

import dao.T004Dao;
import dto.T002Dto;

public class T004Service {

    private T004Dao t004DAO = new T004Dao();

    public List<String> validateAndImportFile(FormFile uploadFile) throws Exception {
        List<String> errors = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(uploadFile.getInputStream(), "UTF-8"));
        String line;
        int lineNumber = 0;
        List<T002Dto> validCustomers = new ArrayList<>();

        try {
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Process CSV line
                String[] values = parseCSVLine(line);

                // Validate minimum number of columns
                if (values.length < 6) {
                    errors.add("Line " + lineNumber + ": Invalid format - not enough columns");
                    continue;
                }

                String customerIdStr = values[0].trim();
                String customerName = values[1].trim();
                String sex = values[2].trim();
                String birthday = values[3].trim();
                String email = values[4].trim();
                String address = values.length > 5 ? values[5].trim() : "";

                // Validate each field
                List<String> lineErrors = validateCustomerData(customerIdStr, customerName, sex, birthday, email,
                        address, lineNumber);
                errors.addAll(lineErrors);

                // If no errors for this line, add to valid customers list
                if (lineErrors.isEmpty()) {
                    T002Dto customer = new T002Dto();
                    
                    // Set customer ID (0 for new customers)
                    int customerId = 0;
                    if (!customerIdStr.isEmpty()) {
                        customerId = Integer.parseInt(customerIdStr);
                    }
                    
                    customer.setCustomerID(customerId);
                    customer.setCustomerName(customerName);
                    customer.setSex(sex);
                    customer.setBirthday(birthday);
                    customer.setEmail(email);
                    customer.setAddress(address);
                    validCustomers.add(customer);
                }
            }
        } finally {
            reader.close();
        }

        // If there are validation errors, return them
        if (!errors.isEmpty()) {
            return errors;
        }

        // If no errors, proceed with import
        boolean importSuccess = t004DAO.importCustomerData(validCustomers);

        if (!importSuccess) {
            throw new Exception("Import failed due to system error.");
        }

        return null;
    }

    private String[] parseCSVLine(String line) {
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

    private List<String> validateCustomerData(String customerIdStr, String customerName, String sex, String birthday,
            String email, String address, int lineNumber) throws SQLException {
        List<String> errors = new ArrayList<>();
        // Validate customer ID - chỉ validate nếu có giá trị
        if (!customerIdStr.isEmpty()) {
        	System.out.println("customer :"+ customerIdStr);
                int customerId = Integer.parseInt(customerIdStr);
                boolean customerExists = t004DAO.checkCustomerExists(customerId);
                if (!customerExists) {
                    errors.add("Line " + lineNumber + ": CUSTOMER_ID=" + customerId + " is not existed");
                }   
        }

        // Validate customer name
        if (customerName.isEmpty()) {
            errors.add("Line " + lineNumber + ": CUSTOMER_NAME is empty");
        } else if (customerName.length() > 50) {
            errors.add("Line " + lineNumber + ": Value of CUSTOMER_NAME is more than 50 characters");
        }

        // Validate sex
        if (!sex.equalsIgnoreCase("Male") && !sex.equalsIgnoreCase("Female")) {
            errors.add("Line " + lineNumber + ": SEX=" + sex + " is invalid");
        }

        // Validate birthday
        if (!isValidDate(birthday)) {
            errors.add("Line " + lineNumber + ": BIRTHDAY=" + birthday + " is invalid");
        }

        // Validate email
        if (!isValidEmail(email)) {
            errors.add("Line " + lineNumber + ": EMAIL=" + email + " is invalid");
        }

        return errors;
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return false;
        }
        
        // Check format YYYY/MM/DD
        if (!dateStr.matches("\\d{4}/\\d{2}/\\d{2}")) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.setLenient(false); // Strict validation
            Date date = sdf.parse(dateStr);
            
            // Additional check to ensure the parsed date matches the input
            String formatted = sdf.format(date);
            return formatted.equals(dateStr);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}