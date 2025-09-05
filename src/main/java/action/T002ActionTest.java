package action;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import common.Constants;
import dto.T002SCO;
import form.T002Form;
import service.T002Service;
import utils.Helper;

/**
 * Action class responsible for handling customer search, listing, deletion, 
 * and export operations on the T002 screen.
 */
public class T002ActionTest extends Action {

    /** Service layer instance for customer operations */
    private final T002Service t002Service = T002Service.getInstance();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Validate session
        if (!Helper.isLogin(request)) {
            return mapping.findForward(Constants.T001_LOGIN);
        }

        String action = request.getParameter(Constants.PARAM_ACTION);
        if (action == null) {
            return findCustomer(mapping, form, request, response);
        }

        switch (action) {
            case Constants.ACTION_REMOVE:
                return deleteCustomer(mapping, form, request, response);
            case Constants.ACTION_SEARCH:
                return findCustomer(mapping, form, request, response);
            case Constants.ACTION_EXPORT:
                return exportCSV(mapping, form, request, response);
            default:
                return findCustomer(mapping, form, request, response);
        }
    }

    /** Delete customers */
    public ActionForward deleteCustomer(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        T002Form t002Form = (T002Form) form;
        t002Service.deleteCustomers(t002Form.getCustomerIds());
        return findCustomer(mapping, form, request, response);
    }

    /** Search customers + apply saved headers */
    private ActionForward findCustomer(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        T002Form t002Form = (T002Form) form;

        T002SCO sco = (T002SCO) session.getAttribute(Constants.SESSION_T002_SCO);
        sco = t002Service.searchCustomers(t002Form, sco);
        session.setAttribute(Constants.SESSION_T002_SCO, sco);

        // Lấy cấu hình header đã save ở T005
        @SuppressWarnings("unchecked")
        List<LabelValueBean> columnHeader =
                (List<LabelValueBean>) session.getAttribute("columnHeader");

        if (columnHeader != null) {
            t002Form.setColumnHeaders(columnHeader);
        }

        return mapping.findForward(Constants.T002_SEARCH);
    }

    /** Export customers */
    public ActionForward exportCSV(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        T002Form t002Form = (T002Form) form;
        HttpSession session = request.getSession();
        T002SCO sco = (T002SCO) session.getAttribute(Constants.SESSION_T002_SCO);

        String csvData = t002Service.exportCustomersToCSV(t002Form, sco);
        String fileName = t002Service.generateFileName();

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write(csvData);
        }
        return null;
    }
}
