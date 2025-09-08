package service;

import java.util.*;
import form.ColumnHeader;
import form.T005Form;

/**
 * Service class that handles operations for the T005 screen.
 * <p>
 * This service manages two lists of column headers (left and right),
 * allowing users to move headers between lists and reorder them within the right list.
 * Column headers are loaded from an external properties file.
 * </p>
 */
public class T005Service {

    /** Singleton instance of {@code T005Service}. */
    private static final T005Service instance = new T005Service();

    /** 
     * Private constructor to enforce the singleton pattern.
     */
    private T005Service() {}

    /**
     * Returns the singleton instance of {@code T005Service}.
     *
     * @return singleton instance
     */
    public static T005Service getInstance() {
        return instance;
    }

    /**
     * Load default right column headers from the properties file.
     *
     * @return list of right column headers
     */
    public List<ColumnHeader> getDefaultRightHeaders() {
        return loadHeadersFromProperties("right");
    }

    /**
     * Load default left column headers from the properties file.
     *
     * @return list of left column headers
     */
    public List<ColumnHeader> getDefaultLeftHeaders() {
        return loadHeadersFromProperties("left");
    }

    /**
     * Move selected items from the left list to the right list.
     *
     * @param t005Form the form containing headers
     * @return true if items were moved, false otherwise
     */
    public boolean moveRight(T005Form t005Form) {
        boolean isMoved = moveItemsBetweenLists(
            t005Form.getLeftHeaders(), 
            t005Form.getRightHeaders(), 
            t005Form.getSelectedLeftHeader()
        );
        t005Form.setSelectedLeftHeader(null);
        t005Form.setSelectedRightHeader(null);
        return isMoved;
    }

    /**
     * Move selected items from the right list to the left list.
     *
     * @param t005Form the form containing headers
     * @return true if items were moved, false otherwise
     */
    public boolean moveLeft(T005Form t005Form) {
        boolean isMoved = moveItemsBetweenLists(
            t005Form.getRightHeaders(), 
            t005Form.getLeftHeaders(), 
            t005Form.getSelectedRightHeader()
        );
        t005Form.setSelectedLeftHeader(null);
        t005Form.setSelectedRightHeader(null);
        return isMoved;
    }

    /**
     * Move selected items up in the right list.
     *
     * @param t005Form the form containing headers
     * @return true if items were reordered, false otherwise
     */
    public boolean moveUp(T005Form t005Form) {
        return reorderItems(t005Form, -1);
    }

    /**
     * Move selected items down in the right list.
     *
     * @param t005Form the form containing headers
     * @return true if items were reordered, false otherwise
     */
    public boolean moveDown(T005Form t005Form) {
        return reorderItems(t005Form, +1);
    }

    /**
     * Helper method to move selected items from a source list to a target list.
     *
     * @param sourceList the list to move items from
     * @param targetList the list to move items to
     * @param selectedValues array of selected item values
     * @return true if items were moved, false otherwise
     */
    private boolean moveItemsBetweenLists(List<ColumnHeader> sourceList, List<ColumnHeader> targetList, String[] selectedValues) {
        if (selectedValues == null || selectedValues.length == 0) {
            return false;
        }

        Iterator<ColumnHeader> iterator = sourceList.iterator();
        List<String> selectedList = Arrays.asList(selectedValues);

        while (iterator.hasNext()) {
            ColumnHeader item = iterator.next();
            if (selectedList.contains(item.getValue())) {
                targetList.add(new ColumnHeader(item.getLabel(), item.getValue(), item.getCssClass()));
                iterator.remove();
            }
        }
        return true;
    }

    /**
     * Helper method to reorder selected items within the right list.
     *
     * @param t005Form   the form containing right headers
     * @param direction  -1 to move items up, +1 to move items down
     * @return true if items were reordered, false otherwise
     */
    private boolean reorderItems(T005Form t005Form, int direction) {
        List<ColumnHeader> rightHeaders = t005Form.getRightHeaders();
        String[] selectedValues = t005Form.getSelectedRightHeader();

        if (selectedValues == null || selectedValues.length == 0) {
            return false;
        }

        List<String> selectedList = Arrays.asList(selectedValues);

        if (direction < 0) { // move up
            for (int i = 1; i < rightHeaders.size(); i++) {
                ColumnHeader current = rightHeaders.get(i);
                ColumnHeader previous = rightHeaders.get(i - 1);

                if (selectedList.contains(current.getValue()) && !selectedList.contains(previous.getValue())) {
                    rightHeaders.set(i - 1, current);
                    rightHeaders.set(i, previous);
                }
            }
        } else { // move down
            for (int i = rightHeaders.size() - 2; i >= 0; i--) {
                ColumnHeader current = rightHeaders.get(i);
                ColumnHeader next = rightHeaders.get(i + 1);

                if (selectedList.contains(current.getValue()) && !selectedList.contains(next.getValue())) {
                    rightHeaders.set(i, next);
                    rightHeaders.set(i + 1, current);
                }
            }
        }

        t005Form.setSelectedRightHeader(selectedValues);
        return true;
    }

    /**
     * Load column headers from a properties file based on a given prefix.
     * <p>
     * Example keys in {@code columnHeaders.properties}:
     * <pre>
     * right.1.label=Customer ID
     * right.1.value=customerID
     * right.1.cssClass=header-customerId
     * </pre>
     *
     * @param prefix "left" or "right"
     * @return list of column headers
     */
    private List<ColumnHeader> loadHeadersFromProperties(String prefix) {
        List<ColumnHeader> headers = new ArrayList<>();
        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("columnHeaders.properties"));

            int index = 1;
            while (true) {
                String label = properties.getProperty(prefix + "." + index + ".label");
                String value = properties.getProperty(prefix + "." + index + ".value");
                String cssClass = properties.getProperty(prefix + "." + index + ".cssClass");

                if (label == null || value == null) {
                    break;
                }
                headers.add(new ColumnHeader(label, value, cssClass));
                index++;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return headers;
    }
}
