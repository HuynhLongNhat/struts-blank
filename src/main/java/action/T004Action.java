package action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import common.Constants;
import dto.T001Dto;
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

    private ActionForward processImport(ActionMapping mapping, T004Form form, HttpServletRequest request)
            throws Exception {        
        HttpSession session = request.getSession(false);
        T001Dto loggedInUser = (T001Dto) session.getAttribute(Constants.SESSION_USER);
        Integer psnCd = (loggedInUser != null) ? loggedInUser.getPsnCd() : null;

        List<String> results = t004Service.importFile(form.getUploadFile(), psnCd);

        if (results != null && !results.isEmpty()) {
            // Nếu phần tử đầu tiên chứa "Customer data have been imported successfully." 
            // thì coi là thành công
            if (results.get(0).startsWith("Customer data have been imported successfully")) {
                String successMessage = String.join("\n", results);
                ActionMessages messages = new ActionMessages();
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("success.import.completed", successMessage));
                saveMessages(request, messages);
            } else {
                // Trường hợp lỗi
                String allErrors = String.join("\n", results);
                ActionMessages actionErrors = new ActionMessages();
                actionErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.import.message", allErrors));
                saveErrors(request, actionErrors);
            }
        }

        return mapping.findForward(Constants.T004_IMPORT);
    }


}