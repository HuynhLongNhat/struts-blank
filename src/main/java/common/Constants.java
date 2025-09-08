package common;

/**
 * Centralized class for storing application-wide constants.
 *
 * <p>This class defines all static constant values used throughout the 
 * application such as screen forwards, action parameters, modes, 
 * session keys, request parameters, error message keys, and CSV settings.</p>
 *
 * <p>Using constants improves maintainability, avoids hardcoding 
 * strings across different layers (Controller, Service, DAO, JSP), 
 * and ensures consistency.</p>
 *
 * @author YourName
 * @version 1.1
 * @since 2025-08-21
 */
public class Constants {

    // ============================================================
    // Global identifiers
    // ============================================================

    /** Global scope identifier, often used in validation or error messages. */
    public static final String GLOBAL = "global";


    // ============================================================
    // Screen forwards (navigation)
    // ============================================================

    /** Forward to login screen (T001). */
    public static final String T001_LOGIN = "T001";

    /** Forward to customer search screen (T002). */
    public static final String T002_SEARCH = "T002";

    /** Forward to customer edit screen (T003). */
    public static final String T003_EDIT = "T003";

    /** Forward to import screen (T004). */
    public static final String T004_IMPORT = "T004";

    /** Forward to settings screen (T005). */
    public static final String T005_SETTING = "T005";


    // ============================================================
    // Action parameters (HTTP request actions)
    // ============================================================

    /** Action parameter for login. */
    public static final String ACTION_LOGIN = "login";

    /** Action parameter for removing/deleting a record. */
    public static final String ACTION_REMOVE = "remove";

    /** Action parameter for searching records. */
    public static final String ACTION_SEARCH = "search";

    /** Action parameter for logging out. */
    public static final String ACTION_LOGOUT = "logout";

    /** Action parameter for saving a record (insert or update). */
    public static final String ACTION_SAVE = "save";

    /** Action parameter for exporting data. */
    public static final String ACTION_EXPORT = "export";

    /** Action parameter for importing data. */
    public static final String ACTION_IMPORT = "import";

    /** Action parameter for moving selected item(s) left. */
    public static final String ACTION_MOVE_LEFT = "moveLeft";

    /** Action parameter for moving selected item(s) right. */
    public static final String ACTION_MOVE_RIGHT = "moveRight";

    /** Action parameter for moving selected item(s) up. */
    public static final String ACTION_MOVE_UP = "moveUp";

    /** Action parameter for moving selected item(s) down. */
    public static final String ACTION_MOVE_DOWN = "moveDown";

    /** Action parameter for canceling the operation. */
    public static final String ACTION_CANCEL = "cancel";


    // ============================================================
    // Mode identifiers (form operation mode)
    // ============================================================

    /** Indicates that the form is in edit mode. */
    public static final String MODE_EDIT = "EDIT";

    /** Indicates that the form is in add mode. */
    public static final String MODE_ADD = "ADD";


    // ============================================================
    // Pagination settings
    // ============================================================

    /** Number of records displayed per page for pagination. */
    public static final int PAGE_SIZE = 15;


    // ============================================================
    // Session attributes
    // ============================================================

    /** Session attribute key for storing the logged-in user. */
    public static final String SESSION_USER = "user";

    /** Session attribute key for storing T002SCO (search conditions). */
    public static final String SESSION_T002_SCO = "T002SCO";

    /** Session attribute key for column header configuration. */
    public static final String SESSION_COLUMN_HEADER = "columnHeader";

    /** Session attribute key for temporary column header backup. */
    public static final String SESSION_COLUMN_HEADER_TEMPORARY = "columnHeaderTemporary";


    // ============================================================
    // Request / Form parameters
    // ============================================================

    /** Request parameter key for the current action. */
    public static final String PARAM_ACTION = "action";

    /** Request parameter key for the current page in pagination. */
    public static final String PARAM_CURRENT_PAGE = "currentPage";

    /** Request parameter key for the list of customers. */
    public static final String PARAM_CUSTOMERS = "customers";

    /** Request parameter key for total record count. */
    public static final String PARAM_TOTAL_COUNT = "totalCount";


    // ============================================================
    // Error message keys (for validation and i18n resource bundles)
    // ============================================================

    /** Error key for failed login attempt. */
    public static final String ERROR_MSG_LOGIN_FAILED = "error.login.failed";

    /** Error key for missing customer ID. */
    public static final String ERROR_CUSTOMER_ID_REQUIRED = "error.customerId.required";

    /** Error key for missing user ID. */
    public static final String ERROR_USER_ID_REQUIRED = "error.userId.required";

    /** Error key for missing password. */
    public static final String ERROR_PASSWORD_REQUIRED = "error.password.required";

    /** Error key for invalid birthday range. */
    public static final String ERROR_BIRTHDAY_RANGE = "error.birthday.range";

    /** Error key for invalid "birthday from" format. */
    public static final String ERROR_BIRTHDAY_FROM_FORMAT = "error.birthdayFrom.format";

    /** Error key for invalid "birthday to" format. */
    public static final String ERROR_BIRTHDAY_TO_FORMAT = "error.birthdayTo.format";

    /** Error key for invalid birthday format. */
    public static final String ERROR_BIRTHDAY_INVALID = "error.birthday.invalid";

    /** Error key for invalid email format. */
    public static final String ERROR_EMAIL_INVALID = "error.email.invalid";

    /** Error key when trying to remove non-removable column headers. */
    public static final String ERROR_CANNOT_REMOVE = "error.headerItem.cannotRemove";

    /** Error key when no column header is selected. */
    public static final String ERROR_HEADER_REQUIRED = "error.headerItem.required";


    // ============================================================
    // Import error messages
    // ============================================================

    /** Error key when the import file does not exist. */
    public static final String ERROR_IMPORT_NOT_EXISTED = "error.import.notExisted";

    /** Error key when the import file is invalid. */
    public static final String ERROR_IMPORT_INVALID = "error.import.invalid";

    /** Error key when the import file is empty. */
    public static final String ERROR_IMPORT_EMPTY = "error.import.empty";

    /** Error key when a customer ID in the import file does not exist in DB. */
    public static final String ERROR_IMPORT_CUSTOMER_ID_NOT_EXIST = "error.import.customerId.notExist";

    /** Error key when a customer ID in the import file is invalid. */
    public static final String ERROR_IMPORT_CUSTOMER_ID_INVALID = "error.import.customerId.invalid";

    /** Error key when customer name is missing in the import file. */
    public static final String ERROR_IMPORT_CUSTOMER_NAME_EMPTY = "error.import.customerName.empty";

    /** Error key when customer name exceeds the allowed length. */
    public static final String ERROR_IMPORT_CUSTOMER_NAME_TOO_LONG = "error.import.customerName.tooLong";

    /** Error key when gender (sex) value is invalid in the import file. */
    public static final String ERROR_IMPORT_SEX_INVALID = "error.import.sex.invalid";

    /** Error key when birthday is invalid in the import file. */
    public static final String ERROR_IMPORT_BIRTHDAY_INVALID = "error.import.birthday.invalid";

    /** Error key when email format is invalid in the import file. */
    public static final String ERROR_IMPORT_EMAIL_INVALID = "error.import.email.invalid";

    /** Error key when address exceeds the allowed length. */
    public static final String ERROR_IMPORT_ADDRESS_TOO_LONG = "error.import.address.tooLong";


    /** General import error message key. */
    public static final String ERROR_IMPORT_MESSAGE = "error.import.message";


    // ============================================================
    // Success messages
    // ============================================================

    /** Success key when import process completed. */
    public static final String SUCCESS_IMPORT_COMPLETED = "success.import.completed";

    /** Success key when new data is inserted during import. */
    public static final String SUCCESS_IMPORT_INSERTED = "success.import.inserted";

    /** Success key when existing data is updated during import. */
    public static final String SUCCESS_IMPORT_UPDATED = "success.import.updated";


    // ============================================================
    // Validation limits
    // ============================================================

    /** Maximum length allowed for customer name. */
    public static final int MAX_CUSTOMER_NAME_LENGTH = 50;

    /** Maximum length allowed for address. */
    public static final int MAX_ADDRESS_LENGTH = 256;


    // ============================================================
    // Common values
    // ============================================================

    /** Value representing Male gender. */
    public static final String MALE = "Male";

    /** Value representing Female gender. */
    public static final String FEMALE = "Female";

    /** Default date pattern used in the application. */
    public static final String DATE_PATTERN = "yyyy/MM/dd";

    /** Regular expression pattern for validating email. */
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";


    // ============================================================
    // CSV export/import settings
    // ============================================================

    /** CSV header row. */
    public static final String CSV_HEADER = "\"Customer Id\",\"Customer Name\",\"Sex\",\"Birthday\",\"Email\",\"Address\"";

    /** Prefix for exported CSV filenames. */
    public static final String CSV_FILE_PREFIX = "Customer_";

    /** File extension for CSV files. */
    public static final String CSV_FILE_EXTENSION = ".csv";

    /** Date format used in CSV filenames. */
    public static final String CSV_DATE_FORMAT = "yyyyMMdd";


    // ============================================================
    // Column header definitions
    // ============================================================

    /** Internal column header name for checkbox. */
    public static final String HEADER_CHECKBOX = "checkbox";

    /** Internal column header name for customer ID. */
    public static final String HEADER_CUSTOMER_ID = "customerID";

    /** Display label for checkbox column. */
    public static final String LABEL_CHECKBOX = "CheckBox";

    /** Display label for customer ID column. */
    public static final String LABEL_CUSTOMER_ID = "Customer ID";

}
