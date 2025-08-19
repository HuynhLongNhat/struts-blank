package action;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.MappingDispatchAction;

import common.Constants;
import dto.T001Dto;
import dto.T002Dto;
import form.T003Form;
import service.T003Service;
import utils.Helper;

/**
 * Struts Action for T003 screen. Handles loading customer form (edit/add) and
 * saving customer data.
 */
public class T003Action extends MappingDispatchAction {

	private final T003Service t003Service = T003Service.getInstance();
  
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String action = request.getParameter("action");
		if (Constants.ACTION_SAVE.equals(action)) {
			return save(mapping, form, request, response);
		}
		return load(mapping, form, request, response);
	}
	/**
	 * Loads the customer form.
	 */
	public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
	    
	    if (!Helper.isLogin(request)) {
	        return new ActionForward("T001.do", true);
	    }

	    T003Form t003Form = (T003Form) form;
	    String customerIdParam = request.getParameter("customerId");

	    if (customerIdParam != null && !customerIdParam.trim().isEmpty()) {
	        try {
	            Integer customerId = Integer.parseInt(customerIdParam.trim());
	            T002Dto customer = t003Service.getCustomerById(customerId);

	            if (customer != null) {
	                t003Form.setCustomerId(customer.getCustomerID());
	                t003Form.setCustomerName(customer.getCustomerName());
	                t003Form.setSex(customer.getSex());
	                t003Form.setBirthday(customer.getBirthday());
	                t003Form.setEmail(customer.getEmail());
	                t003Form.setAddress(customer.getAddress());
	                t003Form.setMode("EDIT");
	            } else {
	                t003Form.setMode("ADD");
	            }
	        } catch (NumberFormatException e) {
	            // Nếu parameter không phải số
	            t003Form.setMode("ADD");
	        }
	    } else {
	        // Nếu không có customerId
	        t003Form.setMode("ADD");
	    }

	    return mapping.findForward("T003");
	}

	/**
	 * Saves the customer (insert or update).
	 */
	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	                           HttpServletResponse response) throws Exception {
	    if (!Helper.isLogin(request)) {
	        return new ActionForward("T001.do", true);
	    }

	    T003Form t003Form = (T003Form) form;
	    T002Dto customer = new T002Dto();
	    customer.setCustomerID(t003Form.getCustomerId());
	    customer.setCustomerName(t003Form.getCustomerName());
	    customer.setSex(t003Form.getSex());
	    customer.setBirthday(t003Form.getBirthday());
	    customer.setEmail(t003Form.getEmail());
	    customer.setAddress(t003Form.getAddress());

	    String mode = t003Form.getMode();

	    HttpSession session = request.getSession(false);
	    T001Dto loggedInUser = (T001Dto) session.getAttribute("user");
	    Integer psnCd = (loggedInUser != null) ? loggedInUser.getPsnCd() : null;

	    try {
	        if ("ADD".equals(mode)) {
	            t003Service.insertCustomer(customer, psnCd);
	        } else {
	            t003Service.updateCustomer(customer, psnCd);
	        }
	        // Nếu không exception → coi như thành công → redirect
	        return new ActionForward("T002.do", true);
	    } catch (SQLException e) {
	        throw new ServletException("Database error occurred while saving customer.", e);
	    } catch (Exception e) {
	        return mapping.findForward("T003");
	      
	    }
	}
}
