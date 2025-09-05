package service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.struts.util.LabelValueBean;
import form.T005Form;

/**
 * Service class that handles operations for the T005 screen.
 * This class provides methods to move items between two lists
 * (left and right) and to reorder items in the right list.
 */
public class T005Service {

    /**
     * Move selected items from the left list to the right list.
     *
     * @param form the T005Form containing left/right lists and selected item
     * @return true if the operation succeeded
     */
    public boolean moveRight(T005Form form) {
        List<LabelValueBean> left = form.getLeftHeaders();
        List<LabelValueBean> right = form.getRightHeaders();
        String selected = form.getSelectedLeftHeader();

        // Iterate over left list and move selected item(s) to right list
        Iterator<LabelValueBean> it = left.iterator();
        while (it.hasNext()) {
            LabelValueBean item = it.next();
            if (Arrays.asList(selected).contains(item.getValue())) {
                right.add(item);  // add item to right list
                it.remove();      // remove item from left list
            }
        }
        return true;
    }

    /**
     * Move selected items from the right list back to the left list.
     *
     * @param form the T005Form containing left/right lists and selected item
     * @return true if the operation succeeded
     */
    public boolean moveLeft(T005Form form) {
        List<LabelValueBean> left = form.getLeftHeaders();
        List<LabelValueBean> right = form.getRightHeaders();
        String selected = form.getSelectedRightHeader();

        // Iterate over right list and move selected item(s) to left list
        Iterator<LabelValueBean> it = right.iterator();
        while (it.hasNext()) {
            LabelValueBean item = it.next();
            if (Arrays.asList(selected).contains(item.getValue())) {
                left.add(item);   // add item back to left list
                it.remove();      // remove from right list
            }
        }
        return true;
    }

    /**
     * Move the selected item one position up in the right list.
     *
     * @param form the T005Form containing the right list and selected item
     * @return true if the swap succeeded, false if the item is at the top or not found
     */
    public boolean moveUp(T005Form form) {
        List<LabelValueBean> right = form.getRightHeaders();
        String selected = form.getSelectedRightHeader();

        // Start from index 1 because the first element cannot move up
        for (int i = 1; i < right.size(); i++) {
            LabelValueBean current = right.get(i);
            if (selected.equals(current.getValue())) {
                // Swap current item with the one above it
                LabelValueBean prev = right.get(i - 1);
                right.set(i - 1, current);
                right.set(i, prev);

                // Keep the selected state on the moved item
                form.setSelectedRightHeader(current.getValue());
                return true;
            }
        }
        return false;
    }

    /**
     * Move the selected item one position down in the right list.
     *
     * @param form the T005Form containing the right list and selected item
     * @return true if the swap succeeded, false if the item is at the bottom or not found
     */
    public boolean moveDown(T005Form form) {
        List<LabelValueBean> right = form.getRightHeaders();
        String selected = form.getSelectedRightHeader();

        // Iterate until the second-to-last element
        for (int i = 0; i < right.size() - 1; i++) {
            LabelValueBean current = right.get(i);
            if (selected.equals(current.getValue())) {
                // Swap current item with the one below it
                LabelValueBean next = right.get(i + 1);
                right.set(i + 1, current);
                right.set(i, next);

                // Keep the selected state on the moved item
                form.setSelectedRightHeader(current.getValue());
                return true;
            }
        }
        return false;
    }
}
