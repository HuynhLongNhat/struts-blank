package action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import common.Constants;
import form.T004Form;
import service.T004Service;
import utils.Helper;

public class T004Action extends Action {

    private T004Service t004Service = new T004Service();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // Validate session
        if (!Helper.isLogin(request)) {
            return mapping.findForward(Constants.T001_LOGIN);
        }

        T004Form t004Form = (T004Form) form;
        String action = t004Form.getAction();

        if ("import".equals(action)) {
            return processImport(mapping, t004Form, request);
        }

        return mapping.findForward(Constants.T004_IMPORT);
    }

    private ActionForward processImport(ActionMapping mapping, T004Form form, HttpServletRequest request) throws Exception {
        try {
            List<String> errors = t004Service.validateAndImportFile(form.getUploadFile());

            if (errors != null && !errors.isEmpty()) {
                // Gộp list lỗi thành một chuỗi, mỗi lỗi xuống dòng
                String allErrors = String.join("\\n", errors);

                ActionMessages actionErrors = new ActionMessages();
                actionErrors.add(
                    ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("error.import.message", allErrors)
                );
                saveErrors(request, actionErrors);
            } else {
                ActionMessages messages = new ActionMessages();
                messages.add(
                    ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("success.import.completed")
                );
                saveMessages(request, messages);
            }
        } catch (Exception e) {
            ActionMessages actionErrors = new ActionMessages();
            actionErrors.add(
                ActionMessages.GLOBAL_MESSAGE,
                new ActionMessage("error.import.exception", e.getMessage())
            );
            saveErrors(request, actionErrors);
        }

        return mapping.findForward(Constants.T004_IMPORT);
    }

}