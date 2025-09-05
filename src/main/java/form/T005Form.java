package form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.LabelValueBean;  

import common.Constants;

/**
 * Form class for T005 screen.
 * Holds user information, header configuration, and selected actions.
 */
public class T005Form extends ActionForm {

    private static final long serialVersionUID = 1L;

    // User information fields
    private String userName;
    private String sex;
    private String email;
    private String address;
    private String customerId;
    private String customerName;
    private String birthday;

    // Control flag
    private boolean disabledRight;

    // Lists of available and selected headers
    private List<LabelValueBean> leftHeaders = new ArrayList<>();
    private List<LabelValueBean> rightHeaders = new ArrayList<>();

    // Currently selected headers
    private String selectedLeftHeader;
    private String selectedRightHeader;

    // Action type (move, save, cancel, etc.)
    private String action;

    // ================== Getters & Setters ==================

    /** @return user name */
    public String getUserName() {
        return userName;
    }

    /** @param userName set user name */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** @return sex */
    public String getSex() {
        return sex;
    }

    /** @param sex set sex */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /** @return email */
    public String getEmail() {
        return email;
    }

    /** @param email set email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return address */
    public String getAddress() {
        return address;
    }

    /** @param address set address */
    public void setAddress(String address) {
        this.address = address;
    }

    /** @return customer ID */
    public String getCustomerId() {
        return customerId;
    }

    /** @param customerId set customer ID */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /** @return customer name */
    public String getCustomerName() {
        return customerName;
    }

    /** @param customerName set customer name */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /** @return birthday */
    public String getBirthday() {
        return birthday;
    }

    /** @param birthday set birthday */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * @return the list of left headers
     */
    public List<LabelValueBean> getLeftHeaders() {
        if (leftHeaders == null) {
            leftHeaders = new ArrayList<>();
        }
        return leftHeaders;
    }

    /**
     * @param leftHeaders set the list of left headers
     */
    public void setLeftHeaders(List<LabelValueBean> leftHeaders) {
        this.leftHeaders = leftHeaders;
    }

    /**
     * @return the list of right headers
     */
    public List<LabelValueBean> getRightHeaders() {
        if (rightHeaders == null) {
            rightHeaders = new ArrayList<>();
        }
        return rightHeaders;
    }

    /**
     * @param rightHeaders set the list of right headers
     */
    public void setRightHeaders(List<LabelValueBean> rightHeaders) {
        this.rightHeaders = rightHeaders;
    }

    /** @return selected left header */
    public String getSelectedLeftHeader() {
        return selectedLeftHeader;
    }

    /** @param selectedLeftHeader set selected left header */
    public void setSelectedLeftHeader(String selectedLeftHeader) {
        this.selectedLeftHeader = selectedLeftHeader;
    }

    /** @return selected right header */
    public String getSelectedRightHeader() {
        return selectedRightHeader;
    }

    /** @param selectedRightHeader set selected right header */
    public void setSelectedRightHeader(String selectedRightHeader) {
        this.selectedRightHeader = selectedRightHeader;
    }

    /** @return current action */
    public String getAction() {
        return action;
    }

    /** @param action set current action */
    public void setAction(String action) {
        this.action = action;
    }

    /** @return whether right list is disabled */
    public boolean isDisabledRight() {
        return disabledRight;
    }

    /** @param disabledRight set disabled flag for right list */
    public void setDisabledRight(boolean disabledRight) {
        this.disabledRight = disabledRight;
    }

    // ================== Validation ==================

    /**
     * Validate form input based on the current action.
     *
     * @param mapping the ActionMapping
     * @param request the HttpServletRequest
     * @return ActionErrors containing validation results
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String action = getAction();

        if (action == null) {
            return errors; // Không có action thì không validate
        }

        switch (action) {
            case Constants.ACTION_MOVE_RIGHT:
                requireSelected(selectedLeftHeader, errors);
                break;

            case Constants.ACTION_MOVE_LEFT:
                if (requireSelected(selectedRightHeader, errors)) {
                    if ("CheckBox".equals(selectedRightHeader) || "Customer ID".equals(selectedRightHeader)) {
                        errors.add(Constants.GLOBAL,
                                new ActionMessage("error.headerItem.cannotRemove", selectedRightHeader));
                    }
                }
                break;

            case Constants.ACTION_MOVE_UP:
            case Constants.ACTION_MOVE_DOWN:
                requireSelected(selectedRightHeader, errors);
                break;

            default:
                // các action khác không cần validate
                break;
        }

        return errors;
    }

    /**
     * Validate bắt buộc chọn header.
     *
     * @param value  giá trị được chọn
     * @param errors ActionErrors
     * @return true nếu có giá trị, false nếu null
     */
    private boolean requireSelected(String value, ActionErrors errors) {
        if (value == null) {
            errors.add(Constants.GLOBAL, new ActionMessage("error.headerItem.required"));
            return false;
        }
        return true;
    }
}
