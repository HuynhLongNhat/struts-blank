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
     * Searches for customers based on search conditions and applies pagination logic.
     * <p>
     * - Initializes the search condition object (SCO) if it does not exist.  
     * - Updates SCO with new search criteria when the action is "search".  
     * - Calculates the current page and offset for database query.  
     * - Executes the search through DAO and retrieves results with total count.  
     * - Calculates total pages and adjusts current page if out of range.  
     * - Sets pagination attributes back to the form for rendering.  
     * - Returns the updated SCO for future searches.  
     * </p>
     *
     * @param form the {@link T002Form} containing search criteria and pagination parameters
     * @param sco  the {@link T002SCO} (Search Condition Object) holding previous search state,
     *             may be {@code null} on first search
     * @return the updated {@link T002SCO} reflecting current search criteria and state
     * @throws SQLException if a database access error occurs
     */
    public T002SCO searchCustomers(T002Form form, T002SCO sco) throws SQLException {
        String action = form.getAction();

        // Initialize search condition object if not provided
        if (sco == null) {
            sco = new T002SCO();
        }

        // If the action is a new search, update SCO with criteria from the form
        if (Constants.ACTION_SEARCH.equals(action)) {
            updateScoFromForm(sco, form);
        }

        // Calculate current page number (defaults if not specified)
        int currentPage = calculateCurrentPage(form);

        // Calculate offset for SQL query based on page size
        int offset = calculateOffset(currentPage);

        // Perform customer search via DAO, retrieving results and total record count
        Map<String, Object> data = t002Dao.searchCustomers(sco, offset, Constants.PAGE_SIZE);

        // Extract list of customers from query result
        List<T002Dto> customers = extractCustomersFromData(data);

        // Extract total record count from query result
        int totalRecords = extractTotalCountFromData(data);

        // Calculate total number of pages based on record count and page size
        int totalPages = calculateTotalPages(totalRecords);

        // Adjust current page if it exceeds total pages (e.g., after deletion)
        currentPage = adjustCurrentPage(currentPage, totalPages);

        // Set pagination info and search results back into the form
        setPaginationAttributes(form, currentPage, totalPages, customers, sco);

        // Return updated SCO for storing in session
        return sco;
    }


    /**
     * Deletes multiple customers from the database based on their IDs.
     *
     * @param customerIds An array of customer IDs to be deleted.
     * @throws SQLException If a database access error occurs during deletion.
     */
    public void deleteCustomers(int[] customerIds) throws SQLException {
        List<Integer> ids = Arrays.stream(customerIds).boxed().collect(Collectors.toList());
        t002Dao.deleteCustomer(ids);
    }
    
    /**
     * Exports customer data to a CSV string based on search conditions.
     * <p>
     * If the provided {@link T002SCO} object is null, it will be created from
     * the form. Then all customers matching the search conditions are fetched
     * and written into a CSV format with a UTF-8 BOM to ensure compatibility
     * with applications like Excel.
     * </p>
     *
     * @param form The form containing search conditions.
     * @param sco  The search condition object (can be null; will be created if so).
     * @return A CSV-formatted string containing customer data.
     * @throws SQLException If a database access error occurs while fetching customers.
     */
    public String exportCustomersToCSV(T002Form form, T002SCO sco) throws SQLException {
        if (sco == null) {
            sco = createScoFromForm(form);
        }
        List<T002Dto> customers = fetchAllCustomers(sco);

        StringBuilder sb = new StringBuilder();
        sb.append('\ufeff'); // Add UTF-8 BOM for proper encoding in Excel
        sb.append(CSV_HEADER).append("\n");

        for (T002Dto customer : customers) {
            sb.append(buildCustomerRow(customer)).append("\n");
        }

        return sb.toString();
    }

    /**
     * Builds a single CSV row string from the given customer DTO.
     * <p>
     * Each field is extracted from the {@link T002Dto} object and formatted
     * according to CSV rules:
     * <ul>
     *   <li>Customer ID is converted to string.</li>
     *   <li>Text fields (name, sex, email, address) are escaped using
     *       {@link #escapeCsv(String)} to handle special characters.</li>
     *   <li>Birthday is kept as-is (assumed to be preformatted).</li>
     *   <li>Email and address are enclosed in proper CSV formatting.</li>
     *   <li>Customer ID is wrapped in quotes and birthday is prefixed with
     *       <code>=</code> to prevent Excel from misinterpreting the value.</li>
     * </ul>
     * </p>
     *
     * @param customer The customer DTO containing the data.
     * @return A CSV-formatted string representing one row of customer data.
     */
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

    /**
     * Updates the search condition object (SCO) with values
     * provided from the form input.
     * <p>
     * This ensures that the customer search will use the latest
     * criteria entered by the user.
     * </p>
     *
     * @param sco   The search condition object to be updated.
     * @param form  The form containing user input for search filters.
     */
    private void updateScoFromForm(T002SCO sco, T002Form form) {
        // Set customer name filter
        sco.setCustomerName(form.getCustomerName());

        // Set gender filter
        sco.setSex(form.getSex());

        // Set date of birth range filter (from - to)
        sco.setBirthdayFrom(form.getBirthdayFrom());
        sco.setBirthdayTo(form.getBirthdayTo());
    }

    /**
     * Determines the current page number for pagination.
     * <p>
     * If the form does not specify a valid page (<= 0), defaults to page 1.
     * </p>
     *
     * @param form The form containing the current page value.
     * @return The validated current page number (minimum 1).
     */
    private int calculateCurrentPage(T002Form form) {
        // Ensure currentPage is at least 1
        return form.getCurrentPage() > 0 ? form.getCurrentPage() : 1;
    }

    /**
     * Calculates the database offset based on the current page.
     * <p>
     * Offset is used in SQL queries to skip records for pagination.
     * </p>
     *
     * @param currentPage The current page number.
     * @return The record offset (zero-based index).
     */
    private int calculateOffset(int currentPage) {
        // Example: Page 1 -> offset 0, Page 2 -> offset PAGE_SIZE, etc.
        return (currentPage - 1) * Constants.PAGE_SIZE;
    }

    /**
     * Extracts a list of {@link T002Dto} customers from the data map.
     *
     * @param data The result map containing "customers" entry.
     * @return A list of customers, or an empty list if none are found or invalid.
     */
    private List<T002Dto> extractCustomersFromData(Map<String, Object> data) {
        Object customersObj = data.get("customers");
        
        // Ensure the object is a List and contains T002Dto instances
        if (customersObj instanceof List) {
            return ((List<?>) customersObj).stream()
                    .filter(T002Dto.class::isInstance) // Keep only T002Dto objects
                    .map(T002Dto.class::cast)          // Cast to T002Dto
                    .collect(Collectors.toList());
        }
        // Return empty list if data is missing or invalid
        return Collections.emptyList();
    }

    /**
     * Extracts the total record count from the data map.
     *
     * @param data The result map containing "totalCount" entry.
     * @return The total number of records, or 0 if not found.
     */
    private int extractTotalCountFromData(Map<String, Object> data) {
        Object totalCountObj = data.get("totalCount");
        // Ensure value is numeric before converting
        return (totalCountObj instanceof Number) ? ((Number) totalCountObj).intValue() : 0;
    }

    /**
     * Calculates the total number of pages based on record count.
     *
     * @param totalRecords The total number of records.
     * @return Total number of pages, at least 0.
     */
    private int calculateTotalPages(int totalRecords) {
        // Use ceiling division to ensure partial pages count as one
        return (int) Math.ceil((double) totalRecords / Constants.PAGE_SIZE);
    }

    /**
     * Adjusts the current page to ensure it does not exceed total pages.
     *
     * @param currentPage The current page requested.
     * @param totalPages  The total number of pages available.
     * @return A valid current page within range.
     */
    private int adjustCurrentPage(int currentPage, int totalPages) {
        // If current page is larger than total pages, clamp to last page
        if (currentPage > totalPages && totalPages > 0) {
            return totalPages;
        }
        return currentPage;
    }
    /**
     * Sets pagination-related attributes on the form, such as the list of customers
     * and navigation button states (first, previous, next, last).
     *
     * @param form        The form to populate with pagination data.
     * @param currentPage The current page number.
     * @param totalPages  The total number of pages.
     * @param customers   The list of customers to display.
     * @param sco         The search condition object used for filtering.
     */
    private void setPaginationAttributes(T002Form form, 
                                         int currentPage, int totalPages, 
                                         List<T002Dto> customers, T002SCO sco) {
        // Set the customers retrieved for the current page
        form.setCustomers(customers);

        // Disable "first" and "previous" buttons if on the first page
        form.setDisabledFirst(currentPage == 1);
        form.setDisabledPrevious(currentPage == 1);

        // Disable "next" and "last" buttons if on the last page or no data
        form.setDisabledNext(currentPage == totalPages || totalPages == 0);
        form.setDisabledLast(currentPage == totalPages || totalPages == 0);

        // Update form with detailed pagination data and search conditions
        updateFormWithPaginationData(form, currentPage, totalPages, sco);
    }

    /**
     * Updates the form with current pagination values and search condition fields.
     * <p>
     * Ensures the form retains both navigation info (page numbers) and
     * user-entered search filters across requests.
     * </p>
     *
     * @param form        The form to populate.
     * @param currentPage The current page number.
     * @param totalPages  The total number of pages.
     * @param sco         The search condition object containing filters.
     */
    private void updateFormWithPaginationData(T002Form form, int currentPage, int totalPages, T002SCO sco) {
        // Update current, previous, next, and total page numbers
        form.setCurrentPage(currentPage);
        form.setPrevPage((currentPage > 1) ? currentPage - 1 : 1);
        form.setNextPage((currentPage < totalPages) ? currentPage + 1 : totalPages);
        form.setTotalPages(totalPages);

        // Retain search filters so they persist in the form after navigation
        form.setCustomerName(sco.getCustomerName());
        form.setSex(sco.getSex());
        form.setBirthdayFrom(sco.getBirthdayFrom());
        form.setBirthdayTo(sco.getBirthdayTo());
    }


   
    /**
     * Creates a new search condition object (SCO) from the form input.
     *
     * @param form The form containing search filters.
     * @return A populated {@link T002SCO} object.
     */
    private T002SCO createScoFromForm(T002Form form) {
        T002SCO sco = new T002SCO();
        // Copy filter values from form into SCO
        sco.setCustomerName(form.getCustomerName());
        sco.setSex(form.getSex());
        sco.setBirthdayFrom(form.getBirthdayFrom());
        sco.setBirthdayTo(form.getBirthdayTo());
        return sco;
    }

    /**
     * Fetches all customers that match the given search conditions.
     * <p>
     * Uses a very large limit (Integer.MAX_VALUE) to ensure all records
     * are retrieved, typically for CSV export purposes.
     * </p>
     *
     * @param sco The search condition object.
     * @return A list of all matching customers.
     * @throws SQLException if a database access error occurs.
     */
    private List<T002Dto> fetchAllCustomers(T002SCO sco) throws SQLException {
        // Query DAO with offset 0 and maximum possible limit
        Map<String, Object> data = t002Dao.searchCustomers(sco, 0, Integer.MAX_VALUE);
        // Extract list of customers from result map
        return extractCustomersFromData(data);
    }

    /**
     * Generates a CSV file name using the current date.
     * <p>
     * Format: prefix + yyyyMMdd + .csv
     * Example: "Customer_20250909.csv"
     * </p>
     *
     * @return A file name string for the exported CSV.
     */
    public String generateFileName() {
        // Format current date for file name
        String dateString = new SimpleDateFormat(CSV_DATE_FORMAT).format(new Date());
        return CSV_FILE_PREFIX + dateString + CSV_FILE_EXTENSION;
    }

    /**
     * Escapes a value for safe inclusion in a CSV file.
     * <p>
     * - Wraps value in quotes.
     * - Escapes internal quotes by doubling them.
     * - Returns empty quotes if value is null or empty.
     * </p>
     *
     * @param value The raw string value to escape.
     * @return The escaped CSV value.
     */
    private String escapeCsv(String value) {
        if (value == null || value.isEmpty()) {
            return "\"\""; // Represent empty as ""
        }
        // Escape quotes by doubling them, then wrap in quotes
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

}