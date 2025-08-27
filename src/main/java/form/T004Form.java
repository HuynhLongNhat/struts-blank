package form;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class T004Form extends ActionForm {
    /**
	 * 
	 */
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
}