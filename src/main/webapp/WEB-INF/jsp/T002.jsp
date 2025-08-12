<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ page import="form.T002Form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><bean:message key="label.searchCustomer" /></title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/WebContent/css/T002.css">
</head>
<body>

	<%@ include file="Header.jsp"%>

	<div class="container">
		<!-- Navigation -->
		<div class="nav-group">
			<span><bean:message key="label.login" /></span> <span>&gt;</span> <span><bean:message
					key="label.searchCustomer" /></span>
		</div>

		<!-- Welcome -->
		<div class="welcome">
			<span> <bean:message key="label.welcome" /> <logic:present
					name="user">
					<bean:write name="user" property="userName" />
				</logic:present>


			</span>
			<html:link action="/T002Logout">
				<bean:message key="label.logout" />
			</html:link>
		</div>

		<div class="blue-bar"></div>

		<!-- Error Messages -->
		<div id="errorMessages" style="display: none">
			<html:errors />
		</div>

		<!-- Search Form -->
		<html:form action="/T002" styleClass="search-form" method="post">
			<html:hidden property="actionType" value="search" />
			<label> <bean:message key="label.customerName" /> <html:text
					property="customerName" styleId="txtCustomerName" />
			</label>

			<label> <bean:message key="label.sex" /> <html:select
					property="sex" styleId="cboSex">
					<html:option value=""></html:option>
					<html:option value="0">
						<bean:message key="label.male" />
					</html:option>
					<html:option value="1">
						<bean:message key="label.female" />
					</html:option>
				</html:select>
			</label>

			<label> <bean:message key="label.birthday" /> <html:text
					property="birthdayFrom" styleId="txtBirthdayFrom" /> - <html:text
					property="birthdayTo" styleId="txtBirthdayTo" />
			</label>

			<html:submit styleId="btnSearch">
				<bean:message key="label.search" />
			</html:submit>
		</html:form>

		<!-- Pagination -->
		<div class="btn-pagination">
			<div class="previous">
				<logic:equal name="disableFirst" value="true">
					<button id="btnFirst" disabled>&lt;&lt;</button>
				</logic:equal>
				<logic:notEqual name="disableFirst" value="true">
					<html:form action="/T002" method="post" style="display:inline;">
						<input type="hidden" name="currentPage" value="1" />
						<html:submit styleId="btnFirst">&lt;&lt;</html:submit>
					</html:form>
				</logic:notEqual>

				<logic:equal name="disablePrevious" value="true">
					<button id="btnPrevious" disabled>&lt;</button>
				</logic:equal>
				<logic:notEqual name="disablePrevious" value="true">
					<html:form action="/T002" method="post" style="display:inline;">
						<input type="hidden" name="currentPage"
							value="<bean:write name='T002Form' property='prevPage'/>" />
						<html:submit styleId="btnPrevious">&lt;</html:submit>
					</html:form>
				</logic:notEqual>

				<span><bean:message key="label.previous" /></span>
			</div>

			<div class="next">
				<span><bean:message key="label.next" /></span>

				<logic:equal name="disableNext" value="true">
					<button id="btnNext" disabled>&gt;</button>
				</logic:equal>
				<logic:notEqual name="disableNext" value="true">
					<html:form action="/T002" method="post" style="display:inline;">
						<input type="hidden" name="currentPage"
							value="<bean:write name='T002Form' property='nextPage'/>" />
						<html:submit styleId="btnNext">&gt;</html:submit>
					</html:form>
				</logic:notEqual>

				<logic:equal name="disableLast" value="true">
					<button id="btnLast" disabled>&gt;&gt;</button>
				</logic:equal>
				<logic:notEqual name="disableLast" value="true">
					<html:form action="/T002" method="post" style="display:inline;">
						<input type="hidden" name="currentPage"
							value="<bean:write name='T002Form' property='totalPages'/>" />
						<html:submit styleId="btnLast">&gt;&gt;</html:submit>
					</html:form>
				</logic:notEqual>
			</div>
		</div>

		<!-- Customer List -->
		<html:form action="/T002Delete" method="post">
			<html:hidden property="actionType" value="delete" />
			<table class="customer-table">
				<thead>
					<tr>
						<th><input type="checkbox" id="chkAll"></th>
						<th><bean:message key="label.customerId" /></th>
						<th><bean:message key="label.customerName" /></th>
						<th><bean:message key="label.sex" /></th>
						<th><bean:message key="label.birthday" /></th>
						<th><bean:message key="label.address" /></th>
					</tr>
				</thead>
				<tbody>
					<logic:notEmpty name="customers">
						<logic:iterate id="customer" name="customers" type="dto.T002Dto">
							<tr>
								<td><html:multibox property="customerIds">
										<bean:write name="customer" property="customerID" />
									</html:multibox></td>
								<td><a
									href="T003.do?customerId=<bean:write name='customer' property='customerID'/>">
										<bean:write name="customer" property="customerID" />
								</a></td>
								<td><bean:write name="customer" property="customerName" /></td>
								<td><bean:write name="customer" property="sex" /></td>
								<td><bean:write name="customer" property="birthday" /></td>
								<td><bean:write name="customer" property="address" /></td>
							</tr>
						</logic:iterate>
					</logic:notEmpty>
				</tbody>
			</table>

			<div class="actions">
				<button type="button" class="btn-add" id="btnAddnew"
					onclick="redirectToEditPage()">
					<bean:message key="label.addNew" />
				</button>
				<logic:notEmpty name="customers">
					<html:submit styleClass="btn-delete"
						styleId="btnDelete">
						<bean:message key="label.delete" />
					</html:submit>
				</logic:notEmpty>
				<logic:empty name="customers">
					<button type="button" class="btn-delete" id="btnDelete" disabled>
						<bean:message key="label.delete" />
					</button>
				</logic:empty>
			</div>
		</html:form>
	</div>

	<%@ include file="Footer.jsp"%>

	<script
		src="<%=request.getContextPath()%>/WebContent/js/script.js?<%=System.currentTimeMillis()%>"></script>
	<script>
	document.addEventListener("DOMContentLoaded", function () {
		var errorElement = document.getElementById("errorMessages");
		var errors = errorElement ? errorElement.textContent.trim() : "";
		if (errors) {
			alert(errors);
		}
	});

	function toggleAll(source) {
		const checkboxes = document.querySelectorAll('input[name="customerIds"]');
		checkboxes.forEach(chk => chk.checked = source.checked);
	}

	function redirectToEditPage() {
		window.location.href = 'T003.do';
	}

	document.addEventListener("DOMContentLoaded", function () {
		const checkAll = document.getElementById('chkAll');
		if (checkAll) {
			checkAll.addEventListener('change', function () {
				toggleAll(this);
			});
		}
	});
</script>
</body>
</html>
