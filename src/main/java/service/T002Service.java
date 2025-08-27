package service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.Constants;
import dao.T002Dao;
import dto.T002Dto;
import dto.T002SCO;
import form.T002Form;

/**
 * Service class for handling customer-related operations on the T002 screen.
 * <p>
 * This class provides methods for searching customers with pagination
 * and deleting customers by their IDs. It acts as an intermediary
 * between the Action layer and the DAO layer.
 * </p>
 */
public class T002Service {

    /** Singleton instance of {@code T002Service} */
    private static final T002Service INSTANCE = new T002Service();

    /** Data Access Object for T002 (Customer) operations */
    private final T002Dao t002Dao = T002Dao.getInstance();

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private T002Service() {}

    /**
     * Provides the singleton instance of this service.
     *
     * @return the single {@code T002Service} instance
     */
    public static T002Service getInstance() {
        return INSTANCE;
    }

    /**
     * Searches for customers based on search conditions (SCO) and handles pagination logic.
     * <p>
     * The method updates the search condition object ({@link T002SCO}) stored in session,
     * fetches results from the DAO, calculates pagination data, and sets
     * both results and pagination attributes to the request for rendering in the view.
     * </p>
     *
     * @param form    The {@link T002Form} containing input search criteria and pagination info.
     * @param request The {@link HttpServletRequest} used to retrieve parameters and set attributes.
     * @param session The {@link HttpSession} used to store and retrieve the search condition object.
     * @throws SQLException if any database error occurs during the search.
     */
    public void searchCustomers(T002Form form, HttpServletRequest request, HttpSession session) throws SQLException {
        // Retrieve or initialize SCO (Search Condition Object)
        T002SCO sco = (T002SCO) session.getAttribute(Constants.SESSION_T002_SCO);
        if (sco == null) {
            sco = new T002SCO();
            session.setAttribute(Constants.SESSION_T002_SCO, sco);
        }
        // Update search conditions if action is "search"
        String action = request.getParameter(Constants.PARAM_ACTION);
        if (Constants.ACTION_SEARCH.equals(action)) {
            sco.setCustomerName(form.getCustomerName());
            sco.setSex(form.getSex());
            sco.setBirthdayFrom(form.getBirthdayFrom());
            sco.setBirthdayTo(form.getBirthdayTo());
            session.setAttribute(Constants.SESSION_T002_SCO, sco);
        }
        // Handle pagination
        int currentPage = form.getCurrentPage() > 0 ? form.getCurrentPage() : 1;

        int offset = (currentPage - 1) * Constants.PAGE_SIZE;

        // Fetch results from DAO
        Map<String, Object> data = t002Dao.searchCustomers(sco, offset, Constants.PAGE_SIZE);
        Object customersObj = data.get("customers");
       
        List<T002Dto> customers;
        
        if (customersObj instanceof List) {
            customers = ((List<?>) customersObj).stream()
                    .filter(T002Dto.class::isInstance)
                    .map(T002Dto.class::cast)
                    .collect(Collectors.toList());
        } else {
            customers = Collections.emptyList();
        }
        Object totalCountObj = data.get("totalCount");
        int totalRecords = (totalCountObj instanceof Number) ? ((Number) totalCountObj).intValue() : 0;

        int totalPages = (int) Math.ceil((double) totalRecords / Constants.PAGE_SIZE);      

        // Adjust current page if out of range
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        // Set attributes for view rendering
        request.setAttribute(Constants.PRAM_CUSTOMERS, customers);
        request.setAttribute(Constants.PRAM_DISABLE_FIRST, currentPage == 1);
        request.setAttribute(Constants.PRAM_DISABLE_PREV, currentPage == 1);
        request.setAttribute(Constants.PRAM_DISABLE_NEXT, currentPage == totalPages || totalPages == 0);
        request.setAttribute(Constants.PRAM_DISABLE_LAST, currentPage == totalPages || totalPages == 0);

        // Update form values with current search & pagination data
        form.setCurrentPage(currentPage);
        form.setPrevPage((currentPage > 1) ? currentPage - 1 : 1);
        form.setNextPage((currentPage < totalPages) ? currentPage + 1 : totalPages);
        form.setTotalPages(totalPages);
        form.setCustomerName(sco.getCustomerName());
        form.setSex(sco.getSex());
        form.setBirthdayFrom(sco.getBirthdayFrom());
        form.setBirthdayTo(sco.getBirthdayTo());
    }

    /**
     * Deletes customers by their IDs.
     * <p>
     * This method receives an array of customer IDs, converts it to a list,
     * and delegates the deletion to the DAO layer.
     * </p>
     *
     * @param customerIds Array of customer IDs to be deleted.
     * @throws SQLException if a database access error occurs.
     */
    public void deleteCustomers(int[] customerIds) throws SQLException {
        List<Integer> ids = Arrays.stream(customerIds).boxed().collect(Collectors.toList());
        t002Dao.deleteCustomer(ids);
    }
    
    /**
     * Exports customer data to CSV file based on search conditions.
     *
     * @param form     The {@link T002Form} containing input search criteria.
     * @param request  The {@link HttpServletRequest} used to retrieve parameters.
     * @param response The {@link HttpServletResponse} used to write CSV output.
     * @throws SQLException if any database error occurs during the search.
     * @throws IOException  if writing to response output stream fails.
     */
    public void exportCustomersToCSV(T002Form form, HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        HttpSession session = request.getSession();
        
        // Retrieve SCO from session or create new one based on form data
        T002SCO sco = (T002SCO) session.getAttribute(Constants.SESSION_T002_SCO);
        if (sco == null) {
            sco = new T002SCO();
            sco.setCustomerName(form.getCustomerName());
            sco.setSex(form.getSex());
            sco.setBirthdayFrom(form.getBirthdayFrom());
            sco.setBirthdayTo(form.getBirthdayTo());
        }
        
        // Get all records without pagination
        Map<String, Object> data = t002Dao.searchCustomers(sco, 0, Integer.MAX_VALUE);
        Object customersObj = data.get("customers");
        
        List<T002Dto> customers;
        if (customersObj instanceof List) {
            customers = ((List<?>) customersObj).stream()
                    .filter(T002Dto.class::isInstance)
                    .map(T002Dto.class::cast)
                    .collect(Collectors.toList());
        } else {
            customers = Collections.emptyList();
        }
        
        // Set response headers for CSV download
        String fileName = "Customer_" + new SimpleDateFormat("yyyyMMdd").format(new Date(0)) + ".csv";
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setCharacterEncoding("UTF-8");
        
        // Write CSV content
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
            // Write header
            writer.println("\"Customer Id\",\"Customer Name\",\"Sex\",\"Birthday\",\"Email\",\"Address\"");
            
            // Write data rows
            for (T002Dto customer : customers) {
                String customerId = String.valueOf(customer.getCustomerID());
                String customerName = escapeCsvField(customer.getCustomerName());
                String sex = customer.getSex(); // Đã được format trong DAO
                String birthday = customer.getBirthday() != null ? customer.getBirthday() : ""; // Giữ nguyên định dạng từ DB
                String email = escapeCsvField(customer.getEmail());
                String address = escapeCsvField(customer.getAddress());
                
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        customerId, customerName, sex, birthday, email, address));
            }
        }
    }
    
    /**
     * Escapes special characters in CSV fields.
     *
     * @param field the field value to escape
     * @return the escaped field value
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // Escape quotes by doubling them
        return field.replace("\"", "\"\"");
    }
    
}
