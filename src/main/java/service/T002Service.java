package service;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import common.Constants;
import dao.T002Dao;
import dto.T002Dto;
import dto.T002SCO;
import form.T002Form;

/**
 * Service class for handling customer-related operations on the T002 screen.
 * Provides methods for searching customers with pagination, deleting customers,
 * and exporting customer data to CSV.
 */
public class T002Service {

    private static final T002Service INSTANCE = new T002Service();
    private final T002Dao t002Dao = T002Dao.getInstance();
    
    // CSV constants
    private static final String CSV_HEADER = "\"Customer Id\",\"Customer Name\",\"Sex\",\"Birthday\",\"Email\",\"Address\"";
    private static final String CSV_FILE_PREFIX = "Customer_";
    private static final String CSV_FILE_EXTENSION = ".csv";
    private static final String CSV_DATE_FORMAT = "yyyyMMdd";

    private T002Service() {}

    public static T002Service getInstance() {
        return INSTANCE;
    }

    /**
     * Searches for customers based on search conditions and handles pagination logic.
     */
    public T002SCO searchCustomers(T002Form form, T002SCO sco) throws SQLException {
        String action = form.getAction();
        if (sco == null) {
            sco = new T002SCO();
        }
        if (Constants.ACTION_SEARCH.equals(action)) {
            updateScoFromForm(sco, form);
        }

        int currentPage = calculateCurrentPage(form);
        int offset = calculateOffset(currentPage);

        Map<String, Object> data = t002Dao.searchCustomers(sco, offset, Constants.PAGE_SIZE);
        List<T002Dto> customers = extractCustomersFromData(data);
        int totalRecords = extractTotalCountFromData(data);
        int totalPages = calculateTotalPages(totalRecords);

        currentPage = adjustCurrentPage(currentPage, totalPages);
        setPaginationAttributes( form, currentPage, totalPages, customers, sco);
        return sco;
    }


    /**
     * Deletes customers by their IDs.
     */
    public void deleteCustomers(int[] customerIds) throws SQLException {
        List<Integer> ids = Arrays.stream(customerIds).boxed().collect(Collectors.toList());
        t002Dao.deleteCustomer(ids);
    }

    /**
     * Exports customers to CSV file based on search conditions.
     */
    public String exportCustomersToCSV(T002Form form, T002SCO sco) throws SQLException {
        if (sco == null) {
            sco = createScoFromForm(form);
        }
        List<T002Dto> customers = fetchAllCustomers(sco);

        StringBuilder sb = new StringBuilder();
        sb.append('\ufeff'); // BOM for UTF-8
        sb.append(CSV_HEADER).append("\n");

        for (T002Dto customer : customers) {
            sb.append(buildCustomerRow(customer)).append("\n");
        }

        return sb.toString();
    }
    private String buildCustomerRow(T002Dto customer) {
        String customerId = String.valueOf(customer.getCustomerID());
        String customerName = escapeCsv(customer.getCustomerName());
        String sex = escapeCsv(customer.getSex());
        String birthday = customer.getBirthday();
        String email = escapeCsv(customer.getEmail());
        String address = escapeCsv(customer.getAddress());
        return String.format("\"%s\",%s,%s,=\"%s\",%s,%s",
                customerId, customerName, sex, birthday, email, address);
    }
   


    private void updateScoFromForm(T002SCO sco, T002Form form) {
        sco.setCustomerName(form.getCustomerName());
        sco.setSex(form.getSex());
        sco.setBirthdayFrom(form.getBirthdayFrom());
        sco.setBirthdayTo(form.getBirthdayTo());
    }

    private int calculateCurrentPage(T002Form form) {
        return form.getCurrentPage() > 0 ? form.getCurrentPage() : 1;
    }

    private int calculateOffset(int currentPage) {
        return (currentPage - 1) * Constants.PAGE_SIZE;
    }

    private List<T002Dto> extractCustomersFromData(Map<String, Object> data) {
        Object customersObj = data.get("customers");
        
        if (customersObj instanceof List) {
            return ((List<?>) customersObj).stream()
                    .filter(T002Dto.class::isInstance)
                    .map(T002Dto.class::cast)
                    .collect(Collectors.toList());
        }        
        return Collections.emptyList();
    }

    private int extractTotalCountFromData(Map<String, Object> data) {
        Object totalCountObj = data.get("totalCount");
        return (totalCountObj instanceof Number) ? ((Number) totalCountObj).intValue() : 0;
    }

    private int calculateTotalPages(int totalRecords) {
        return (int) Math.ceil((double) totalRecords / Constants.PAGE_SIZE);
    }

    private int adjustCurrentPage(int currentPage, int totalPages) {
        if (currentPage > totalPages && totalPages > 0) {
            return totalPages;
        }
        return currentPage;
    }

    private void setPaginationAttributes( T002Form form, 
                                       int currentPage, int totalPages, 
                                       List<T002Dto> customers, T002SCO sco) {
    	form.setCustomers(customers);
    	form.setDisabledFirst(currentPage == 1);
    	form.setDisabledPrevious(currentPage == 1);
    	form.setDisabledNext(currentPage == totalPages || totalPages == 0);
    	form.setDisabledLast(currentPage == totalPages || totalPages == 0);
    	updateFormWithPaginationData(form, currentPage, totalPages, sco);

    }

    private void updateFormWithPaginationData(T002Form form, int currentPage, int totalPages, T002SCO sco) {
        form.setCurrentPage(currentPage);
        form.setPrevPage((currentPage > 1) ? currentPage - 1 : 1);
        form.setNextPage((currentPage < totalPages) ? currentPage + 1 : totalPages);
        form.setTotalPages(totalPages);
        form.setCustomerName(sco.getCustomerName());
        form.setSex(sco.getSex());
        form.setBirthdayFrom(sco.getBirthdayFrom());
        form.setBirthdayTo(sco.getBirthdayTo());
    }

   
    private T002SCO createScoFromForm(T002Form form) {
        T002SCO sco = new T002SCO();
        sco.setCustomerName(form.getCustomerName());
        sco.setSex(form.getSex());
        sco.setBirthdayFrom(form.getBirthdayFrom());
        sco.setBirthdayTo(form.getBirthdayTo());
        return sco;
    }

    private List<T002Dto> fetchAllCustomers(T002SCO sco) throws SQLException {
        Map<String, Object> data = t002Dao.searchCustomers(sco, 0, Integer.MAX_VALUE);
        return extractCustomersFromData(data);
    }

    public String generateFileName() {
        String dateString = new SimpleDateFormat(CSV_DATE_FORMAT).format(new Date());
        return CSV_FILE_PREFIX + dateString + CSV_FILE_EXTENSION;
    }

   

    private String escapeCsv(String value) {
        if (value == null || value.isEmpty()) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}