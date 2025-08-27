package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.struts.upload.FormFile;

import dao.T004Dao;

public class T004Service {

    private T004Dao t004DAO = new T004Dao();

    public List<String> validateAndImportFile(FormFile uploadFile) throws Exception {
        List<String> errors = new ArrayList<>();
        
        // 1.1 Check if file exists
        if (uploadFile == null || uploadFile.getFileSize() == 0) {
            throw new Exception("File import is not existed !");
        }

        String fileName = uploadFile.getFileName();
        
        // 1.2 Check file extension
        if (!fileName.toLowerCase().endsWith(".csv")) {
            throw new Exception("File import is invalid !");
        }

        // 1.3 Check file content is empty
        if (uploadFile.getFileData() == null || uploadFile.getFileData().length == 0) {
            throw new Exception("File import is empty !");
        }

        // Read and process CSV file
        BufferedReader reader = new BufferedReader(new InputStreamReader(uploadFile.getInputStream()));
        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            
            // Skip empty lines
            if (line.trim().isEmpty()) {
                continue;
            }
            
            // Process CSV line (assuming comma-separated values)
            String[] values = line.split(",");
            
            // Validate minimum number of columns
            if (values.length < 6) {
                errors.add("Line " + lineNumber + ": Invalid format - not enough columns");
                continue;
            }

            String customerId = values[0].trim();
            String customerName = values[1].trim();
            String sex = values[2].trim();
            String birthday = values[3].trim();
            String email = values[4].trim();

            // Validate customer ID
            if (!customerId.isEmpty()) {
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
        }
        reader.close();

        // If there are validation errors, return them
        if (!errors.isEmpty()) {
            return errors;
        }

        // If no errors, proceed with import
        boolean importSuccess = t004DAO.importData(uploadFile);
        
        if (!importSuccess) {
            throw new Exception("Import failed due to system error.");
        }

        return null;
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr == null || !dateStr.matches("\\d{4}/\\d{2}/\\d{2}")) {
            return false;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.setLenient(false);
            Date date = sdf.parse(dateStr);
            return true;
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