package action;

import java.util.function.Consumer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import common.Constants;
import form.T005Form;
import service.T005Service;
import utils.Helper;

/**
 * Action class for managing column header settings (T005 screen).
 * <p>
 * This class handles user actions such as moving headers (left, right, up, down),
 * saving, canceling, and initializing the header setting form.
 */
public class T005Action extends Action {

    /** Service for column header operations. */
    private static final T005Service columnHeaderService = T005Service.getInstance();

    /**
     * Executes the requested action based on user input.
     *
     * @param mapping     Struts action mapping
     * @param actionForm  form bean holding request data
     * @param request     HTTP request
     * @param response    HTTP response
     * @return            ActionForward to the next page
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm actionForm,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Redirect to login if user is not authenticated
        if (!Helper.isLogin(request)) {
            return mapping.findForward(Constants.T001_LOGIN);
        }

        T005Form settingForm = (T005Form) actionForm;
        String userAction = settingForm.getAction();

        if (userAction == null) {
            return initializeSettingForm(mapping, settingForm, request);
        }

        switch (userAction) {
            case Constants.ACTION_MOVE_RIGHT:
                return processMoveAction(mapping, settingForm, request, columnHeaderService::moveRight);
            case Constants.ACTION_MOVE_LEFT:
                return processMoveAction(mapping, settingForm, request, columnHeaderService::moveLeft);
            case Constants.ACTION_MOVE_UP:
                return processMoveAction(mapping, settingForm, request, columnHeaderService::moveUp);
            case Constants.ACTION_MOVE_DOWN:
                return processMoveAction(mapping, settingForm, request, columnHeaderService::moveDown);
            case Constants.ACTION_SAVE:
                return saveChanges(mapping, request);
            case Constants.ACTION_CANCEL:
                return cancelChanges(mapping, request);
            default:
                return initializeSettingForm(mapping, settingForm, request);
        }
    }

    /**
     * Initializes the form with values from session or sets defaults if not found.
     *
     * @param mapping      Struts action mapping
     * @param currentForm  form bean to populate
     * @param request      HTTP request
     * @return             ActionForward to T005 setting page
     */
    private ActionForward initializeSettingForm(ActionMapping mapping, T005Form currentForm,
                                                HttpServletRequest request) throws Exception {

        HttpSession session = request.getSession();

        T005Form temporaryForm = (T005Form) session.getAttribute(Constants.SESSION_COLUMN_HEADER_TEMPORARY);
        if (temporaryForm != null) {
            currentForm.copyFrom(temporaryForm);
        } else {
            T005Form savedForm = (T005Form) session.getAttribute(Constants.SESSION_COLUMN_HEADER);
            if (savedForm != null) {
                currentForm.copyFrom(savedForm);
            } else {
                setupDefaultHeaders(currentForm);
                session.setAttribute(Constants.SESSION_COLUMN_HEADER, currentForm.deepCopy());
            }
            session.setAttribute(Constants.SESSION_COLUMN_HEADER_TEMPORARY, currentForm.deepCopy());
        }

        return mapping.findForward(Constants.T005_SETTING);
    }

    /**
     * Sets up default left and right headers when no session data exists.
     *
     * @param form target form bean to update
     */
    private void setupDefaultHeaders(T005Form form) {
        form.setLeftHeaders(columnHeaderService.getDefaultLeftHeaders());
        form.setRightHeaders(columnHeaderService.getDefaultRightHeaders());
        form.setDisabledRight(form.getLeftHeaders().isEmpty());
    }

    /**
     * Handles header move actions (right, left, up, down).
     *
     * @param mapping     Struts action mapping
     * @param currentForm current form bean
     * @param request     HTTP request
     * @param moveAction  specific move operation (Consumer)
     * @return            ActionForward to T005 setting page
     */
    private ActionForward processMoveAction(ActionMapping mapping, T005Form currentForm,
                                            HttpServletRequest request, Consumer<T005Form> moveAction) throws Exception {

        HttpSession session = request.getSession();

        // Always work on a temporary form stored in session
        T005Form temporaryForm = getOrCreateTemporaryForm(session, currentForm);
        currentForm.copyFrom(temporaryForm);

        // Apply move logic
        moveAction.accept(currentForm);
        currentForm.setDisabledRight(currentForm.getLeftHeaders().isEmpty());

        // Save updated temp form in session
        session.setAttribute(Constants.SESSION_COLUMN_HEADER_TEMPORARY, currentForm.deepCopy());

        return initializeSettingForm(mapping, currentForm, request);
    }

    /**
     * Retrieves or creates a temporary form from session.
     *
     * @param session     HTTP session
     * @param currentForm current form to use if no session data found
     * @return            temporary form
     */
    private T005Form getOrCreateTemporaryForm(HttpSession session, T005Form currentForm) {
        T005Form temporaryForm = (T005Form) session.getAttribute(Constants.SESSION_COLUMN_HEADER_TEMPORARY);

        if (temporaryForm == null) {
            T005Form savedForm = (T005Form) session.getAttribute(Constants.SESSION_COLUMN_HEADER);
            if (savedForm != null) {
                temporaryForm = savedForm.deepCopy();
            } else {
                setupDefaultHeaders(currentForm);
                temporaryForm = currentForm.deepCopy();
            }
            session.setAttribute(Constants.SESSION_COLUMN_HEADER_TEMPORARY, temporaryForm);
        }
        return temporaryForm;
    }

    /**
     * Saves changes by committing temporary session data to the main session.
     *
     * @param mapping Struts action mapping
     * @param request HTTP request
     * @return        ActionForward to T002 search page
     */
    private ActionForward saveChanges(ActionMapping mapping, HttpServletRequest request) {
        HttpSession session = request.getSession();
        T005Form temporaryForm = (T005Form) session.getAttribute(Constants.SESSION_COLUMN_HEADER_TEMPORARY);

        if (temporaryForm != null) {
            session.setAttribute(Constants.SESSION_COLUMN_HEADER, temporaryForm.deepCopy());
        }
        session.removeAttribute(Constants.SESSION_COLUMN_HEADER_TEMPORARY);

        return mapping.findForward(Constants.T002_SEARCH);
    }

    /**
     * Cancels changes by discarding temporary session data.
     *
     * @param mapping Struts action mapping
     * @param request HTTP request
     * @return        ActionForward to T002 search page
     */
    private ActionForward cancelChanges(ActionMapping mapping, HttpServletRequest request) {
        request.getSession().removeAttribute(Constants.SESSION_COLUMN_HEADER_TEMPORARY);
        return mapping.findForward(Constants.T002_SEARCH);
    }
}
