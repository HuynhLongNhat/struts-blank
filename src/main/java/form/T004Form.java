package form;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class T004Form extends ActionForm {
    private static final long serialVersionUID = 1L;
    private String action;
    private FormFile uploadFile;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public FormFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(FormFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.action = null;
        this.uploadFile = null;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String action = getAction();

        if ("import".equals(action)) {
            // 1. Check file exist
            if (uploadFile == null || uploadFile.getFileSize() == 0) {
                errors.add("uploadFile", new ActionMessage("error.import.notExisted"));
                return errors;
            }

            String fileName = uploadFile.getFileName();

            // 2. Check extension .csv
            if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
                errors.add("uploadFile", new ActionMessage("error.import.invalid"));
                return errors;
            }

            // 3. Check file size or content empty
            // 3. Check file content
            try {
                byte[] fileData = uploadFile.getFileData();
                // case file rỗng hoặc null
                if (fileData == null || fileData.length == 0) {
                    errors.add("uploadFile", new ActionMessage("error.import.empty"));
                    return errors;
                }
                String content = new String(fileData, "UTF-8").trim();
                if (content.isEmpty()) {
                    errors.add("uploadFile", new ActionMessage("error.import.empty"));
                    return errors;
                }

            } catch (IOException e) {
                e.printStackTrace();
                errors.add("uploadFile", new ActionMessage("error.import.empty"));
                return errors;
            }
        }

        return errors;
    }


}
