package action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.LabelValueBean;

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
		    String action = t005Form.getAction();
		    System.out.println("action" + action);
		if ("moveRight".equals(action)) {
	        moveRight(t005Form,request);
	    } 

		return initSettingForm(mapping, form, request, response);

	}

	public ActionForward initSettingForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		T005Form t005Form = (T005Form) form;
		List<LabelValueBean> leftHeaders = new ArrayList<>();
		leftHeaders.add(new LabelValueBean("Email", "email"));
		
		List<LabelValueBean> rightHeaders = new ArrayList<>();
		rightHeaders.add(new LabelValueBean("Customer ID", "customerId"));
		rightHeaders.add(new LabelValueBean("Customer Name", "customerName"));
		rightHeaders.add(new LabelValueBean("Sex", "sex"));
		rightHeaders.add(new LabelValueBean("Birthday", "birthday"));
		rightHeaders.add(new LabelValueBean("Address", "address"));
		rightHeaders.add(new LabelValueBean("Checkbox", "checkbox"));

		t005Form.setLeftHeaders(leftHeaders);
		t005Form.setRightHeaders(rightHeaders);
       
		return mapping.findForward(Constants.T005_SETTING);
	}
	private boolean moveRight(T005Form form, HttpServletRequest request) {
	    List<LabelValueBean> left = form.getLeftHeaders();
	    List<LabelValueBean> right = form.getRightHeaders();

	    if (form.getSelectedLeftHeader() == null || form.getSelectedLeftHeader().length == 0) {
	        // Trường hợp chưa chọn -> add message vào request
	        ActionMessages errors = new ActionMessages();
	        errors.add(Constants.GLOBAL, new ActionMessage("error.headerItem.required"));
	        saveErrors(request, errors); 
	        return false;
	    }

	    // Duyệt qua các item được chọn bên trái, chuyển sang phải
	    List<String> selected = Arrays.asList(form.getSelectedLeftHeader());
	    Iterator<LabelValueBean> it = left.iterator();
	    while (it.hasNext()) {
	        LabelValueBean item = it.next();
	        if (selected.contains(item.getValue())) {
	            right.add(item);
	            it.remove();
	        }
	    }
	    return true;
	}

}
