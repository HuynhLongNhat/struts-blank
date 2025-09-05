package action;

import java.util.ArrayList;
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
import form.T005Form;
import service.T005Service;
import utils.Helper;

/**
 * Action class for managing column header settings (T005 screen).
 */
public class T005Action extends Action {

    private final T005Service t005Service = new T005Service();

    /**
     * Executes the requested action (move, save, cancel, init).
     *
     * @param mapping  ActionMapping for this request
     * @param form     ActionForm associated with this request
     * @param request  HttpServletRequest object
     * @param response HttpServletResponse object
     * @return ActionForward the next view to display
     * @throws Exception if processing fails
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!Helper.isLogin(request)) {
            return mapping.findForward(Constants.T001_LOGIN);
        }

        T005Form t005Form = (T005Form) form;
        initFormIfNull(t005Form);

        String action = t005Form.getAction();
        if (action != null) {
            switch (action) {
                case Constants.ACTION_MOVE_RIGHT:
                    return moveRight(mapping, t005Form, request);
                case Constants.ACTION_MOVE_LEFT:
                    return moveLeft(mapping, t005Form, request);
                case Constants.ACTION_MOVE_UP:
                    return moveUp(mapping, t005Form, request);
                case Constants.ACTION_MOVE_DOWN:
                    return moveDown(mapping, t005Form, request);
                case "save":
                    return save(mapping, t005Form, request);
                case "cancel":
                    return cancel(mapping, t005Form, request);
                default:
                    return initSettingForm(mapping, t005Form, request);
            }
        }

        return initSettingForm(mapping, t005Form, request);
    }

    /**
     * Initializes empty lists if null to avoid NullPointerException.
     *
     * @param form T005Form object
     */
    private void initFormIfNull(T005Form form) {
        if (form.getLeftHeaders() == null) {
            form.setLeftHeaders(new ArrayList<>());
        }
        if (form.getRightHeaders() == null) {
            form.setRightHeaders(new ArrayList<>());
        }
    }

    /**
     * Initializes setting form with session data or default values.
     *
     * @param mapping   ActionMapping
     * @param t005Form  T005Form object
     * @param request   HttpServletRequest object
     * @return ActionForward T005_SETTING forward
     * @throws Exception if initialization fails
     */
    public ActionForward initSettingForm(ActionMapping mapping, T005Form t005Form, HttpServletRequest request)
            throws Exception {
        HttpSession session = request.getSession();

        T005Form sessionForm = (T005Form) session.getAttribute("columnHeader");
        System.out.println("sessionForm" + sessionForm);
        if (sessionForm != null) {
            copyForm(sessionForm, t005Form);
        } else {
            List<LabelValueBean> leftHeaders = new ArrayList<>();
            leftHeaders.add(new LabelValueBean("Email", "email"));

            List<LabelValueBean> rightHeaders = new ArrayList<>();
            rightHeaders.add(new LabelValueBean("Customer ID", "Customer ID"));
            rightHeaders.add(new LabelValueBean("Customer Name", "Customer Name"));
            rightHeaders.add(new LabelValueBean("Sex", "Sex"));
            rightHeaders.add(new LabelValueBean("Birthday", "Birthday"));
            rightHeaders.add(new LabelValueBean("Address", "Address"));
            rightHeaders.add(new LabelValueBean("CheckBox", "CheckBox"));

            t005Form.setLeftHeaders(leftHeaders);
            t005Form.setRightHeaders(rightHeaders);
            t005Form.setDisabledRight(leftHeaders.isEmpty());
        }

        session.setAttribute("columnHeader", t005Form);
        return mapping.findForward(Constants.T005_SETTING);
    }

    /**
     * Copies values from source form to target form.
     *
     * @param source source T005Form
     * @param target target T005Form
     */
    private void copyForm(T005Form source, T005Form target) {
        target.setLeftHeaders(new ArrayList<>(source.getLeftHeaders()));
        target.setRightHeaders(new ArrayList<>(source.getRightHeaders()));
        target.setDisabledRight(source.isDisabledRight());
    }

    /**
     * Moves a column from left to right list.
     *
     * @param mapping ActionMapping
     * @param form    T005Form
     * @param request HttpServletRequest
     * @return ActionForward T005_SETTING forward
     * @throws Exception if processing fails
     */
    private ActionForward moveRight(ActionMapping mapping, T005Form form, HttpServletRequest request) throws Exception {
        return processMove(mapping, form, request, () -> t005Service.moveRight(form));
    }

    /**
     * Moves a column from right to left list.
     *
     * @param mapping ActionMapping
     * @param form    T005Form
     * @param request HttpServletRequest
     * @return ActionForward T005_SETTING forward
     * @throws Exception if processing fails
     */
    private ActionForward moveLeft(ActionMapping mapping, T005Form form, HttpServletRequest request) throws Exception {
        return processMove(mapping, form, request, () -> t005Service.moveLeft(form));
    }

    /**
     * Moves a column up in the right list.
     *
     * @param mapping ActionMapping
     * @param form    T005Form
     * @param request HttpServletRequest
     * @return ActionForward T005_SETTING forward
     * @throws Exception if processing fails
     */
    private ActionForward moveUp(ActionMapping mapping, T005Form form, HttpServletRequest request) throws Exception {
        return processMove(mapping, form, request, () -> t005Service.moveUp(form));
    }

    /**
     * Moves a column down in the right list.
     *
     * @param mapping ActionMapping
     * @param form    T005Form
     * @param request HttpServletRequest
     * @return ActionForward T005_SETTING forward
     * @throws Exception if processing fails
     */
    private ActionForward moveDown(ActionMapping mapping, T005Form form, HttpServletRequest request) throws Exception {
        return processMove(mapping, form, request, () -> t005Service.moveDown(form));
    }

    /**
     * Executes a move action and updates session.
     *
     * @param mapping   ActionMapping
     * @param form      T005Form
     * @param request   HttpServletRequest
     * @param moveAction move logic to execute
     * @return ActionForward T005_SETTING forward
     */
    private ActionForward processMove(ActionMapping mapping, T005Form form,
                                      HttpServletRequest request, Runnable moveAction) {
        HttpSession session = request.getSession();

        T005Form sessionForm = (T005Form) session.getAttribute("columnHeader");
        if (sessionForm != null) {
            copyForm(sessionForm, form);
        }

        moveAction.run();
        form.setDisabledRight(form.getLeftHeaders().isEmpty());

        session.setAttribute("columnHeader", form);
        return mapping.findForward(Constants.T005_SETTING);
    }

    /**
     * Saves current column settings to session and goes back to search screen.
     *
     * @param mapping ActionMapping
     * @param form    T005Form
     * @param request HttpServletRequest
     * @return ActionForward T002_SEARCH forward
     */
    private ActionForward save(ActionMapping mapping, T005Form form, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("columnHeader", form);
        return mapping.findForward(Constants.T002_SEARCH);
    }

    /**
     * Cancels changes and restores previous settings from session.
     *
     * @param mapping ActionMapping
     * @param form    T005Form
     * @param request HttpServletRequest
     * @return ActionForward T002_SEARCH forward
     */
    private ActionForward cancel(ActionMapping mapping, T005Form form, HttpServletRequest request) {
        HttpSession session = request.getSession();
        T005Form sessionForm = (T005Form) session.getAttribute("columnHeader");

        if (sessionForm != null) {
            copyForm(sessionForm, form);
        }

        return mapping.findForward(Constants.T002_SEARCH);
    }
}
