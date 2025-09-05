package common;

/**
 * Centralized class for storing application-wide constants.
 *
 * <p>This class defines all static constant values used throughout the 
 * application such as screen forwards, action parameters, modes, 
 * session keys, request parameters, and error message keys.</p>
 *
 * <p>Using constants improves maintainability, avoids hardcoding 
 * strings across different layers (Controller, Service, DAO, JSP), 
 * and ensures consistency.</p>
 *
 * @author YourName
 * @version 1.0
 * @since 2025-08-21
 */
public class Constants {

    /** Global scope identifier, often used in validation or error messages. */
    public static final String GLOBAL = "global";

    // ============================================================
    // Action forwards (screen navigation)
    // ============================================================

    /** Forward to login screen (T001). */
    public static final String T001_LOGIN = "T001";

    /** Forward to customer search screen (T002). */
    public static final String T002_SEARCH = "T002";

    /** Forward to customer edit screen (T003). */
    public static final String T003_EDIT = "T003";
    
    public static final String T004_IMPORT = "T004";
    
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
    
    public static final String ACTION_EXPORT = "export";
    
    public static final String ACTION_IMPORT = "import";

    public static final String ACTION_MOVE_LEFT = "moveLeft";
    public static final String ACTION_MOVE_RIGHT = "moveRight";
    public static final String ACTION_MOVE_UP = "moveUp";
    public static final String ACTION_MOVE_DOWN	 = "moveDown";

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

    // ============================================================
    // Request / Form parameters
    // ============================================================

    /** Request parameter key for the current action. */
    public static final String PARAM_ACTION = "action";

    /** Request parameter key for the current page in pagination. */
    public static final String PARAM_CURRENT_PAGE = "currentPage";

    /** Request parameter key for the list of customers. */
    public static final String PARAM_CUSTOMERS = "customers";
    
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

    /** Error key for invalid birthday. */
    public static final String ERROR_BIRTHDAY_INVALID = "error.birthday.invalid";

    /** Error key for invalid email format. */
    public static final String ERROR_EMAIL_INVALID = "error.email.invalid";
    
    public static final String ERROR_IMPORT_NOT_EXISTED = "error.import.notExisted";
    public static final String ERROR_IMPORT_INVALID = "error.import.invalid";
    public static final String ERROR_IMPORT_EMPTY = "error.import.empty";
     
    public static final String ERROR_IMPORT_CUSTOMER_ID_NOT_EXIST = "error.import.customerId.notExist";
    public static final String ERROR_IMPORT_CUSTOMER_ID_INVALID   = "error.import.customerId.invalid";

    public static final String ERROR_IMPORT_CUSTOMER_NAME_EMPTY   = "error.import.customerName.empty";
    public static final String ERROR_IMPORT_CUSTOMER_NAME_TOO_LONG = "error.import.customerName.tooLong";

    public static final String ERROR_IMPORT_SEX_INVALID           = "error.import.sex.invalid";

    public static final String ERROR_IMPORT_BIRTHDAY_INVALID      = "error.import.birthday.invalid";

    public static final String ERROR_IMPORT_EMAIL_INVALID         = "error.import.email.invalid";
    
    public static final String ERROR_IMPORT_ADDRESS_TOO_LONG        = "error.import.address.tooLong";
    
    public static final String SUCCESS_IMPORT_COMPLETED = "success.import.completed";
    public static final String SUCCESS_IMPORT_INSERTED = "success.import.inserted";
    public static final String SUCCESS_IMPORT_UPDATED = "success.import.updated";
    
	public static final int MAX_CUSTOMER_NAME_LENGTH = 50;
	public static final int MAX_ADDRESS_LENGTH = 256;
	public static final String MALE = "Male";
	public static final String FEMALE = "Female";
    
    public static final String ERROR_IMPORT_MESSAGE = "error.import.message";
    public static final String DATE_PATTERN = "yyyy/MM/dd";
    
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
  
    // CSV constants
    public static final String CSV_HEADER = "\"Customer Id\",\"Customer Name\",\"Sex\",\"Birthday\",\"Email\",\"Address\"";
    public static final String CSV_FILE_PREFIX = "Customer_";
    public static final String CSV_FILE_EXTENSION = ".csv";
    public static final String CSV_DATE_FORMAT = "yyyyMMdd";

}
