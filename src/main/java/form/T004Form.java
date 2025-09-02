package form;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

import common.Constants;

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
        if (Constants.ACTION_IMPORT.equals(action)) {
            // 1. Check file existence
            if (uploadFile == null || uploadFile.getFileSize() == 0) {
                errors.add("uploadFile", new ActionMessage(Constants.ERROR_IMPORT_NOT_EXISTED));
                return errors;
            }
            String fileName = uploadFile.getFileName();
            // 2. Check extension .csv
            if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {             
                errors.add("uploadFile", new ActionMessage(Constants.ERROR_IMPORT_INVALID));
                return errors;
            }
            // 3. Check file content
            try {            	
                byte[] fileData = uploadFile.getFileData();
                if (fileData == null || fileData.length == 0) {               
                    errors.add(Constants.GLOBAL, new ActionMessage("error.import.empty"));
                    return errors;
                }
                String content = new String(fileData, StandardCharsets.UTF_8).trim();
                if (content.isEmpty()) {
                    errors.add(Constants.GLOBAL, new ActionMessage("error.import.empty"));
                    return errors;
                }
            } catch (IOException e) {
            	  throw new RuntimeException("Error while reading uploaded file", e);
            }
        }
        return errors;
    }


}
