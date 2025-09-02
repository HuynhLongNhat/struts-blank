package service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dao.T005Dao;
import form.T005Form;

public class T005Service {
    private T005Dao dao = new T005Dao();

    public void moveRight(T005Form form) {
        String selected = form.getSelectedLeftHeader();
        if (selected != null && !selected.isEmpty() && form.getLeftHeaders() != null) {
            form.getLeftHeaders().remove(selected);
            if (form.getRightHeaders() == null) form.setRightHeaders(new ArrayList<>());
            form.getRightHeaders().add(selected); 
            form.setSelectedLeftHeader(null); 
        }
    }

    public void moveLeft(T005Form form) {
        String selected = form.getSelectedRightHeader();
        if (selected != null && !selected.isEmpty() && form.getRightHeaders() != null) {
            form.getRightHeaders().remove(selected);
            if (form.getLeftHeaders() == null) form.setLeftHeaders(new ArrayList<>());
            form.getLeftHeaders().add(selected);
            form.setSelectedRightHeader(null); 
        }
    }

    public void moveUp(T005Form form) {
        String selected = form.getSelectedRightHeader();
        List<String> right = form.getRightHeaders();
        if (selected != null && right != null) {
            int idx = right.indexOf(selected);
            if (idx > 0) {
                Collections.swap(right, idx, idx - 1);
            }
        }
    }

    public void moveDown(T005Form form) {
        String selected = form.getSelectedRightHeader();
        List<String> right = form.getRightHeaders();
        if (selected != null && right != null) {
            int idx = right.indexOf(selected);
            if (idx < right.size() - 1) {
                Collections.swap(right, idx, idx + 1);
            }
        }
    }

    public void initData(T005Form form) {
        form.setUserName("User Name Demo"); // hoặc lấy từ session

        // Ví dụ init left/right headers
        form.setLeftHeaders(new ArrayList<>(Arrays.asList("Header1", "Header2", "Header3")));
        form.setRightHeaders(new ArrayList<>(Arrays.asList("HeaderA", "HeaderB")));
    }


    public void saveData(T005Form form) {
        // Lưu dữ liệu từ form vào DB
        dao.save(form);
    }
}
