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

/**
 * Form bean for the T004 (Import CSV) screen.
 *
 * <p>This class represents the form used when uploading a CSV file for import.
 * It provides validation logic to ensure that the uploaded file is present,
 * has the correct extension, and contains valid content.</p>
 *
 * <p>Extends {@link ActionForm} as per Struts framework conventions.</p>
 *
 * @author YourName
 * @version 1.0
 * @since 2025-09-07
 */
public class T004Form extends ActionForm {
    private static final long serialVersionUID = 1L;

    /** The action to be executed (e.g., "import"). */
    private String action;

    /** The uploaded CSV file (wrapped by Struts {@link FormFile}). */
    private FormFile uploadFile;

    /** @return the action parameter */
    public String getAction() {
        return action;
    }

    /** @param action the action parameter to set */
    public void setAction(String action) {
        this.action = action;
    }

    /** @return the uploaded CSV file */
    public FormFile getUploadFile() {
        return uploadFile;
    }

    /** @param uploadFile the uploaded CSV file to set */
    public void setUploadFile(FormFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    /**
     * Resets the form fields before each request.
     *
     * @param mapping the action mapping
     * @param request the HTTP request
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.action = null;
        this.uploadFile = null;
    }

    /**
     * Validates the form input based on the current action.
     *
     * <p>Specifically for the {@code import} action, this method checks:</p>
     * <ul>
     *   <li>File existence and non-empty size</li>
     *   <li>File extension must be <b>.csv</b></li>
     *   <li>File content must not be empty</li>
     * </ul>
     *
     * @param mapping the action mapping
     * @param request the HTTP request
     * @return {@link ActionErrors} containing validation errors, if any
     */
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

            // 2. Check file extension (.csv only)
            if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
                errors.add("uploadFile", new ActionMessage(Constants.ERROR_IMPORT_INVALID));
                return errors;
            }

            // 3. Check file content is not empty
            try {
                byte[] fileData = uploadFile.getFileData();
                if (fileData == null || fileData.length == 0) {
                    errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_IMPORT_EMPTY));
                    return errors;
                }

                String content = new String(fileData, StandardCharsets.UTF_8).trim();
                if (content.isEmpty()) {
                    errors.add(Constants.GLOBAL, new ActionMessage(Constants.ERROR_IMPORT_EMPTY));
                    return errors;
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while reading uploaded file", e);
            }
        }
        return errors;
    }
}
