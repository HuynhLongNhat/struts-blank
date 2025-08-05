package dto;

/**
 * T001Dto - representing a user.
 * Used for transferring user login credentials and related information across
 * application layers.
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-07-21
 */
public class T001Dto {

    /** Unique personal code of this user */
    private Integer psnCd;

    /** User ID used for login authentication */
    private String userId;

    /** Password associated with this user account */
    private String password;

    /** Display name of this user */
    private String userName;

    /**
     * Gets the personal code of the user.
     *
     * @return the personal code
     */
    public Integer getPsnCd() {
        return psnCd;
    }

    /**
     * Sets the personal code of the user.
     *
     * @param psnCd the personal code to set
     */
    public void setPsnCd(Integer psnCd) {
        this.psnCd = psnCd;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the display name of the user.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the display name of the user.
     *
     * @param userName the user name to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
