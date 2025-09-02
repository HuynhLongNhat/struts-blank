package common;

public final class TableConstants {

    private TableConstants() {}

    // ---------------- MSTUSER ----------------
    public static final String TABLE_MSTUSER = "MSTUSER";

    // các cột được dùng trong code của bạn
    public static final String USER_USERID        = "USERID";      // trong SQL: USERID
    public static final String USER_USERNAME      = "USERNAME";    // USERNAME
    public static final String USER_PASSWORD      = "PASSWORD";    // PASSWORD
    public static final String USER_PSN_CD        = "PSN_CD";      // PSN_CD
    public static final String USER_DELETE_YMD    = "DELETE_YMD";  // DELETE_YMD
    public static final String USER_INSERT_YMD    = "INSERT_YMD";  // INSERT_YMD (nếu có)
    public static final String USER_INSERT_PSN_CD = "INSERT_PSN_CD";
    public static final String USER_UPDATE_YMD    = "UPDATE_YMD";
    public static final String USER_UPDATE_PSN_CD = "UPDATE_PSN_CD";

    // ---------------- MSTCUSTOMER ----------------
    public static final String TABLE_MSTCUSTOMER = "MSTCUSTOMER";

    public static final String CUST_CUSTOMER_ID      = "CUSTOMER_ID";
    public static final String CUST_CUSTOMER_NAME    = "CUSTOMER_NAME";
    public static final String CUST_SEX              = "SEX";
    public static final String CUST_BIRTHDAY         = "BIRTHDAY";
    public static final String CUST_EMAIL            = "EMAIL";
    public static final String CUST_ADDRESS          = "ADDRESS";
    public static final String CUST_DELETE_YMD       = "DELETE_YMD";
    public static final String CUST_INSERT_YMD       = "INSERT_YMD";
    public static final String CUST_INSERT_PSN_CD    = "INSERT_PSN_CD";
    public static final String CUST_UPDATE_YMD       = "UPDATE_YMD";
    public static final String CUST_UPDATE_PSN_CD    = "UPDATE_PSN_CD";

}
