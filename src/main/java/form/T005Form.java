package form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import common.Constants;

/**
 * Form class for T005 screen.
 * <p>
 * Manages available and selected column headers along with user actions such as
 * moving headers left, right, up, or down.
 * </p>
 */
public class T005Form extends ActionForm {

	private static final long serialVersionUID = 1L;

	// ================== Fields ==================

	/** Flag to indicate whether the right header list is disabled */
	private boolean disabledRight;

	/** List of available headers on the left side */
	private List<ColumnHeader> leftHeaders = new ArrayList<>();

	/** List of selected headers on the right side */
	private List<ColumnHeader> rightHeaders = new ArrayList<>();

	/** Currently selected headers from the left side */
	private String[] selectedLeftHeader;

	/** Currently selected headers from the right side */
	private String[] selectedRightHeader;

	/** Current action type (move, save, cancel, etc.) */
	private String action;

	// ================== Getters & Setters ==================

	/**
	 * Get the list of available headers on the left side.
	 *
	 * @return list of {@link ColumnHeader} on the left
	 */
	public List<ColumnHeader> getLeftHeaders() {
		return leftHeaders;
	}

	/**
	 * Set the list of available headers on the left side.
	 *
	 * @param leftHeaders list of {@link ColumnHeader} to assign
	 */
	public void setLeftHeaders(List<ColumnHeader> leftHeaders) {
		this.leftHeaders = leftHeaders;
	}

	/**
	 * Get the list of selected headers on the right side.
	 *
	 * @return list of {@link ColumnHeader} on the right
	 */
	public List<ColumnHeader> getRightHeaders() {
		return rightHeaders;
	}

	/**
	 * Set the list of selected headers on the right side.
	 *
	 * @param rightHeaders list of {@link ColumnHeader} to assign
	 */
	public void setRightHeaders(List<ColumnHeader> rightHeaders) {
		this.rightHeaders = rightHeaders;
	}

	/**
	 * Get the array of currently selected headers on the left.
	 *
	 * @return array of selected left header values
	 */
	public String[] getSelectedLeftHeader() {
		return selectedLeftHeader;
	}

	/**
	 * Set the array of selected headers on the left.
	 *
	 * @param selectedLeftHeader array of left header values to assign
	 */
	public void setSelectedLeftHeader(String[] selectedLeftHeader) {
		this.selectedLeftHeader = selectedLeftHeader;
	}

	/**
	 * Get the array of currently selected headers on the right.
	 *
	 * @return array of selected right header values
	 */
	public String[] getSelectedRightHeader() {
		return selectedRightHeader;
	}

	/**
	 * Set the array of selected headers on the right.
	 *
	 * @param selectedRightHeader array of right header values to assign
	 */
	public void setSelectedRightHeader(String[] selectedRightHeader) {
		this.selectedRightHeader = selectedRightHeader;
	}

	/**
	 * Get the current action.
	 *
	 * @return the current action (move, save, cancel, etc.)
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Set the current action.
	 *
	 * @param action the action to assign
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Check if the right list is disabled.
	 *
	 * @return true if disabled, false otherwise
	 */
	public boolean isDisabledRight() {
		return disabledRight;
	}

	/**
	 * Set the disabled flag for the right list.
	 *
	 * @param disabledRight true to disable, false to enable
	 */
	public void setDisabledRight(boolean disabledRight) {
		this.disabledRight = disabledRight;
	}

	// ================== Copy / Deep Copy ==================

	/**
	 * Copy data from another form into the current form.
	 * <p>
	 * Performs a deep copy of header lists and arrays to avoid reference sharing.
	 * </p>
	 *
	 * @param source the source {@link T005Form} to copy data from
	 */
	public void copyFrom(T005Form source) {
		if (source.getLeftHeaders() != null) {
			this.leftHeaders = cloneHeaders(source.getLeftHeaders());
		}
		if (source.getRightHeaders() != null) {
			this.rightHeaders = cloneHeaders(source.getRightHeaders());
		}

	}

	/**
	 * Create a new form instance and copy all data from the current form.
	 *
	 * @return a new {@link T005Form} containing copied data
	 */
	public T005Form deepCopy() {
		T005Form copy = new T005Form();
		copy.copyFrom(this);
		return copy;
	}

	/**
	 * Create a deep copy of a list of column headers.
	 *
	 * @param headers the original list of headers
	 * @return a new list containing cloned {@link ColumnHeader} objects
	 */
	private List<ColumnHeader> cloneHeaders(List<ColumnHeader> headers) {
		List<ColumnHeader> copy = new ArrayList<>();
		for (ColumnHeader item : headers) {
			copy.add(new ColumnHeader(item.getLabel(), item.getValue(), item.getCssClass()));
		}
		return copy;
	}
	
	@Override
	public void reset (ActionMapping mapping, HttpServletRequest request) {
      this.action = null;
	}
	// ================== Validation ==================

	/**
	 * Validate form data based on the current action.
	 * <ul>
	 * <li><b>Move Right</b>: requires at least one left header to be selected.</li>
	 * <li><b>Move Left</b>: requires at least one right header to be selected, and
	 * prevents removal of "checkbox" or "customerID".</li>
	 * <li><b>Move Up / Move Down</b>: requires at least one right header to be
	 * selected.</li>
	 * </ul>
	 *
	 * @param mapping the current {@link ActionMapping}
	 * @param request the current {@link HttpServletRequest}
	 * @return {@link ActionErrors} containing validation results
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
	    ActionErrors errors = new ActionErrors();
	    String action = getAction();

	    // If no action is specified → skip validation
	    if (action == null) {
	        return errors; 
	    }

	    switch (action) {
	        case Constants.ACTION_MOVE_RIGHT:
	            // Validate when moving items from left to right:
	            // Must select at least one item on the left list
	            requireSelected(selectedLeftHeader, errors);
	            break;

	        case Constants.ACTION_MOVE_LEFT:
	            // Validate when moving items from right to left:
	            // Must select at least one item on the right list
	            if (requireSelected(selectedRightHeader, errors)) {
	                // Additionally: check that certain items cannot be removed
	                for (String header : selectedRightHeader) {
	                    if (Constants.HEADER_CHECKBOX.equalsIgnoreCase(header)
	                        || Constants.HEADER_CUSTOMER_ID.equalsIgnoreCase(header)) {

	                        // Use label for error message based on header type
	                        String label = Constants.HEADER_CHECKBOX.equalsIgnoreCase(header) 
	                                ? Constants.LABEL_CHECKBOX
	                                : Constants.LABEL_CUSTOMER_ID;

	                        // Add validation error: these headers cannot be removed
	                        errors.add(Constants.GLOBAL, 
	                            new ActionMessage(Constants.ERROR_CANNOT_REMOVE, label));
	                    }
	                }
	            }
	            break;

	        case Constants.ACTION_MOVE_UP:
	        case Constants.ACTION_MOVE_DOWN:
	            // Validate when reordering items (up or down):
	            // Must select at least one item on the right list
	            requireSelected(selectedRightHeader, errors);
	            break;

	        default:
	            // For other actions → no validation rules
	            break;
	    }

	    return errors;
	}


	/**
	 * Utility method to check that at least one header has been selected.
	 *
	 * @param values array of selected values
	 * @param errors the {@link ActionErrors} object to collect validation errors
	 * @return true if values are not null and not empty, false otherwise
	 */
	private boolean requireSelected(String[] values, ActionErrors errors) {
		if (values == null || values.length == 0) {
			errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_HEADER_REQUIRED));
			return false;
		}
		return true;
	}
}
