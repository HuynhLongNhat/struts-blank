package form;

/**
 * Represents a column header displayed in a table.
 * <p>
 * This class is used to define metadata for each table column,
 * including the display label, the property mapping in the DTO,
 * and optional CSS class for styling.
 * </p>
 *
 * Example:
 * <pre>
 * ColumnHeader header = new ColumnHeader("Customer Name", "customerName", "col-name");
 * </pre>
 *
 * @author  
 * @version 1.0
 * @since 2025-09-07
 */
public class ColumnHeader {

    /** The label displayed on the UI (e.g., "Customer Name"). */
    private String label;

    /** The value used to map with a DTO property (e.g., "customerName"). */
    private String value;

    /** CSS class applied to the table cell/column (e.g., "col-name", "col-id"). */
    private String cssClass;

    /**
     * Default constructor.
     * <p>
     * Initializes an empty column header.
     * </p>
     */
    public ColumnHeader() {
    }

    /**
     * Constructor with label and value.
     *
     * @param label the display label for the column
     * @param value the value/property name used for DTO mapping
     */
    public ColumnHeader(String label, String value) {
        this.label = label;
        this.value = value;
    }

    /**
     * Full constructor with label, value, and CSS class.
     *
     * @param label    the display label for the column
     * @param value    the value/property name used for DTO mapping
     * @param cssClass the CSS class applied for styling
     */
    public ColumnHeader(String label, String value, String cssClass) {
        this.label = label;
        this.value = value;
        this.cssClass = cssClass;
    }

    // =========================
    // Getters and Setters
    // =========================

    /**
     * Gets the display label of the column.
     *
     * @return the column label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the display label of the column.
     *
     * @param label the column label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the value used for DTO property mapping.
     *
     * @return the property mapping value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value used for DTO property mapping.
     *
     * @param value the property mapping value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the CSS class applied to the column.
     *
     * @return the CSS class
     */
    public String getCssClass() {
        return cssClass;
    }

    /**
     * Sets the CSS class applied to the column.
     *
     * @param cssClass the CSS class to set
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
}
