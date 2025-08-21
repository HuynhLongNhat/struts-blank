<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<!DOCTYPE html>
<html>
<head>
<title><bean:message key="T003.title" /></title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/WebContent/css/T003.css?<%=System.currentTimeMillis()%>">
</head>
<body>
	<%@ include file="Header.jsp"%>

	<div class="container">
		<div class="nav-group">
			<span><bean:message key="T003.breadcrumb.login" /></span> <span>&gt;</span>
			<span><bean:message key="T003.breadcrumb.searchCustomer" /></span> <span>&gt;</span>
			<span> <logic:equal name="T003Form" property="mode"
					value="EDIT">
					<bean:message key="T003.breadcrumb.editCustomer" />
				</logic:equal> <logic:notEqual name="T003Form" property="mode" value="EDIT">
					<bean:message key="T003.breadcrumb.addCustomer" />
				</logic:notEqual>
			</span>
		</div>

		<div class="welcome">
			<span> <bean:message key="T003.label.welcome" /> <label
				id="lblUserName">${user.userName}</label>
			</span>
			<html:link action="/logout">
				<bean:message key="T003.label.logout" />
			</html:link>
		</div>

		<div class="blue-bar">
			<label id="llblScreenName"> <logic:equal name="T003Form"
					property="mode" value="EDIT">
					<bean:message key="T003.label.edit" />
				</logic:equal> <logic:notEqual name="T003Form" property="mode" value="EDIT">
					<bean:message key="T003.label.add" />
				</logic:notEqual>
			</label>
		</div>

		<div class="form-container">
			<html:form action="/T003" method="post" styleId="customerForm">
			  <html:hidden property="action" value="save"/>
			<html:hidden property="mode" />
				<div class="error-message">
					<html:errors />
				</div>

				<div class="form-group">
					<label><bean:message key="T003.label.customerId" /></label>

					<!-- Hiển thị label customerId khi ở mode EDIT -->
					<logic:equal name="T003Form" property="mode" value="EDIT">
						<label id="lblCustomerID"> <bean:write name="T003Form"
								property="customerId" />
						</label>
					</logic:equal>

					<!-- Chỉ hiển thị input hidden khi mode=EDIT và customerId khác rỗng/null -->
					<logic:equal name="T003Form" property="mode" value="EDIT">
						<logic:notEmpty name="T003Form" property="customerId">
							<html:hidden property="customerId" styleId="txtCustomerId" />
						</logic:notEmpty>
					</logic:equal>
				</div>



				<div class="form-group">
					<label><bean:message key="T003.label.customerName" /></label>
					<html:text property="customerName" styleId="txtCustomerName" />
				</div>

				<div class="form-group">
					<label><bean:message key="T003.label.sex" /></label>
					<html:select property="sex" styleId="cboSex">
						<html:option value=""></html:option>
						<html:option value="0">
							<bean:message key="T003.label.male" />
						</html:option>
						<html:option value="1">
							<bean:message key="T003.label.female" />
						</html:option>
					</html:select>
				</div>

				<div class="form-group">
					<label><bean:message key="T003.label.birthday" /></label>
					<html:text property="birthday" styleId="txtBirthday" />
				</div>

				<div class="form-group">
					<label><bean:message key="T003.label.email" /></label>
					<html:text property="email" styleId="txtEmail" />
				</div>

				<div class="form-group">
					<label><bean:message key="T003.label.address" /></label>
					<html:textarea property="address" styleId="txtAddress" rows="3" />
				</div>

				<div class="button-group">
					<html:submit styleId="btnSave">
						<bean:message key="T003.button.save" />
					</html:submit>
					<html:reset styleId="btnClear" onclick="clearFormEdit(); return false;">
						<bean:message key="T003.button.clear" />
					</html:reset>
				</div>
			</html:form>
		</div>
	</div>

	<%@ include file="Footer.jsp"%>
	<script src="<%=request.getContextPath()%>/WebContent/js/T003.js?ts=<%=System.currentTimeMillis()%>"></script>
</body>
</html>
