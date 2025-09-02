package service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
    public void searchCustomers(T002Form form, HttpServletRequest request, HttpSession session) throws SQLException {
        T002SCO sco = getOrInitializeSco(form, request, session);
        int currentPage = calculateCurrentPage(form);
        int offset = calculateOffset(currentPage);

        Map<String, Object> data = t002Dao.searchCustomers(sco, offset, Constants.PAGE_SIZE);
        List<T002Dto> customers = extractCustomersFromData(data);
        int totalRecords = extractTotalCountFromData(data);
        int totalPages = calculateTotalPages(totalRecords);

        currentPage = adjustCurrentPage(currentPage, totalPages);
        setPaginationAttributes(request, form, currentPage, totalPages, customers, sco);
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
    public void exportCustomersToCSV(T002Form form, HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        T002SCO sco = getExportSco(request, form);
        List<T002Dto> customers = fetchAllCustomers(sco);

        prepareCsvResponse(response);
        writeCsvContent(response, customers);
    }

    private T002SCO getOrInitializeSco(T002Form form, HttpServletRequest request, HttpSession session) {
        T002SCO sco = (T002SCO) session.getAttribute(Constants.SESSION_T002_SCO);
        
        if (sco == null) {
            sco = new T002SCO();
            session.setAttribute(Constants.SESSION_T002_SCO, sco);
        }

        if (isSearchAction(request)) {
            updateScoFromForm(sco, form);
            session.setAttribute(Constants.SESSION_T002_SCO, sco);
        }
        
        return sco;
    }

    private boolean isSearchAction(HttpServletRequest request) {
        String action = request.getParameter(Constants.PARAM_ACTION);
        return Constants.ACTION_SEARCH.equals(action);
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

    private void setPaginationAttributes(HttpServletRequest request, T002Form form, 
                                       int currentPage, int totalPages, 
                                       List<T002Dto> customers, T002SCO sco) {
        
        request.setAttribute(Constants.PARAM_CUSTOMERS, customers);
        request.setAttribute(Constants.PARAM_DISABLE_FIRST, currentPage == 1);
        request.setAttribute(Constants.PARAM_DISABLE_PREV, currentPage == 1);
        request.setAttribute(Constants.PARAM_DISABLE_NEXT, currentPage == totalPages || totalPages == 0);
        request.setAttribute(Constants.PARAM_DISABLE_LAST, currentPage == totalPages || totalPages == 0);

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

    private T002SCO getExportSco(HttpServletRequest request, T002Form form) {
        HttpSession session = request.getSession();
        T002SCO sco = (T002SCO) session.getAttribute(Constants.SESSION_T002_SCO);
        
        if (sco == null) {
            sco = createScoFromForm(form);
        }
        
        return sco;
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

    private void prepareCsvResponse(HttpServletResponse response) {
        String fileName = generateFileName();
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setCharacterEncoding("UTF-8");
    }

    private String generateFileName() {
        String dateString = new SimpleDateFormat(CSV_DATE_FORMAT).format(new Date());
        return CSV_FILE_PREFIX + dateString + CSV_FILE_EXTENSION;
    }

    private void writeCsvContent(HttpServletResponse response, List<T002Dto> customers) throws IOException {
        try (PrintWriter writer = createUtf8Writer(response)) {
            writer.write('\ufeff'); // BOM for UTF-8
            writer.println(CSV_HEADER);
            
            for (T002Dto customer : customers) {
                writeCustomerRow(writer, customer);
            }
        }
    }

    private PrintWriter createUtf8Writer(HttpServletResponse response) throws IOException {
        return new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
    }

    private void writeCustomerRow(PrintWriter writer, T002Dto customer) {
        String customerId = String.valueOf(customer.getCustomerID());
        String customerName = escapeCsv(customer.getCustomerName());
        String sex = escapeCsv(customer.getSex());
        String birthday = customer.getBirthday();
        String email = escapeCsv(customer.getEmail());
        String address = escapeCsv(customer.getAddress());
        
        writer.printf("\"%s\",%s,%s,=\"%s\",%s,%s%n",
                customerId, customerName, sex, birthday, email, address);
    }

    private String escapeCsv(String value) {
        if (value == null || value.isEmpty()) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}