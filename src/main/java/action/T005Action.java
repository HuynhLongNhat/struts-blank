package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import common.Constants;
import form.T005Form;
import service.T005Service;
import utils.Helper;

public class T005Action extends Action {

	private T005Service t005Service = new T005Service();

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// Validate session
		if (!Helper.isLogin(request)) {
			return mapping.findForward(Constants.T001_LOGIN);
		}
		T005Form t005Form = (T005Form) form;

		// Xử lý arrow buttons
		if (t005Form.getBtnRight() != null) {
			t005Service.moveRight(t005Form);
		} else if (t005Form.getBtnLeft() != null) {
			t005Service.moveLeft(t005Form);
		} else if (t005Form.getBtnUp() != null) {
			t005Service.moveUp(t005Form);
		} else if (t005Form.getBtnDown() != null) {
			t005Service.moveDown(t005Form);
		}

		// Xử lý lưu dữ liệu
		if (request.getParameter("saveBtn") != null) {
			t005Service.saveData(t005Form);
		}

		// Load dữ liệu ban đầu
		t005Service.initData(t005Form);
		return mapping.findForward(Constants.T005_SETTING);

	}
}
