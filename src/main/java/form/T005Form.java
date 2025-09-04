package form;

import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.util.LabelValueBean;

public class T005Form extends ActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String sex;
	private String email;
	private String address;
	private String customerId;
	private String customerName;
	private String birthday;

	// Các nút
	private String btnRight;
	private String btnLeft;
	private String btnUp;
	private String btnDown;
	private String cancel;

	// Danh sách headers
    private List<LabelValueBean> leftHeaders;
    private List<LabelValueBean> rightHeaders;

	// Header đang chọn
	private String[] selectedLeftHeader;
	private String[] selectedRightHeader;
  
    private String action ;
	
	// Getters & Setters
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBtnRight() {
		return btnRight;
	}

	public void setBtnRight(String btnRight) {
		this.btnRight = btnRight;
	}

	public String getBtnLeft() {
		return btnLeft;
	}

	public void setBtnLeft(String btnLeft) {
		this.btnLeft = btnLeft;
	}

	public String getBtnUp() {
		return btnUp;
	}

	public void setBtnUp(String btnUp) {
		this.btnUp = btnUp;
	}

	public String getBtnDown() {
		return btnDown;
	}

	public void setBtnDown(String btnDown) {
		this.btnDown = btnDown;
	}

	public String getCancel() {
		return cancel;
	}

	public void setCancel(String cancel) {
		this.cancel = cancel;
	}

	public boolean isBtnRightDisabled() {
		return leftHeaders == null || leftHeaders.isEmpty();
	}

	public boolean isBtnLeftDisabled() {
		return rightHeaders == null || rightHeaders.isEmpty();
	}

	public boolean isBtnUpDisabled() {
		return rightHeaders == null || rightHeaders.size() <= 1
				|| (selectedRightHeader != null && rightHeaders.indexOf(selectedRightHeader) == 0);
	}

	public boolean isBtnDownDisabled() {
		return rightHeaders == null || rightHeaders.size() <= 1 || (selectedRightHeader != null
				&& rightHeaders.indexOf(selectedRightHeader) == rightHeaders.size() - 1);
	}

	
	public List<LabelValueBean> getLeftHeaders() {
        return leftHeaders;
    }

    public void setLeftHeaders(List<LabelValueBean> leftHeaders) {
        this.leftHeaders = leftHeaders;
    }

    public List<LabelValueBean> getRightHeaders() {
        return rightHeaders;
    }

    public void setRightHeaders(List<LabelValueBean> rightHeaders) {
        this.rightHeaders = rightHeaders;
    }

	/**
	 * @return the selectedLeftHeader
	 */
	public String[] getSelectedLeftHeader() {
		return selectedLeftHeader;
	}

	/**
	 * @param selectedLeftHeader the selectedLeftHeader to set
	 */
	public void setSelectedLeftHeader(String[] selectedLeftHeader) {
		this.selectedLeftHeader = selectedLeftHeader;
	}
	
	public String[] getSelectedRightHeader() {
	    return selectedRightHeader;
	}

	public void setSelectedRightHeader(String[] selectedRightHeader) {
	    this.selectedRightHeader = selectedRightHeader;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
}
