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

<!-- CSS (vẫn giữ WebContent nhưng bỏ bớt rườm rà, thêm version tránh cache) -->
<html:base />
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/WebContent/css/T002.css?v=<%=System.currentTimeMillis()%>" />

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
			<html:link action="/logout">
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
			<html:hidden property="action" value="search" />

			<label> <bean:message key="label.customerName" /> <html:text
					property="customerName" styleId="txtCustomerName" />
			</label>

			<label> <bean:message key="label.sex" /> <html:select
					property="sex" styleId="cboSex">
					<html:option value="" />
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
			<logic:notEmpty name="customers">
				<div class="previous">
					<logic:equal name="disableFirst" value="true">
						<html:button property="first" disabled="true">&lt;&lt;</html:button>
					</logic:equal>
					<logic:notEqual name="disableFirst" value="true">
						<html:form action="/T002" method="post" style="display:inline;">
							<input type="hidden" name="currentPage" value="1" />
							<html:submit>&lt;&lt;</html:submit>
						</html:form>
					</logic:notEqual>

					<logic:equal name="disablePrevious" value="true">
						<html:button property="prev" disabled="true">&lt;</html:button>
					</logic:equal>
					<logic:notEqual name="disablePrevious" value="true">
						<html:form action="/T002" method="post" style="display:inline;">
							<input type="hidden" name="currentPage"
								value="<bean:write name='T002Form' property='prevPage'/>" />
							<html:submit>&lt;</html:submit>
						</html:form>
					</logic:notEqual>
				</div>

				<div class="next">
					<logic:equal name="disableNext" value="true">
						<html:button property="next" disabled="true">&gt;</html:button>
					</logic:equal>
					<logic:notEqual name="disableNext" value="true">
						<html:form action="/T002" method="post" style="display:inline;">
							<input type="hidden" name="currentPage"
								value="<bean:write name='T002Form' property='nextPage'/>" />
							<html:submit>&gt;</html:submit>
						</html:form>
					</logic:notEqual>

					<logic:equal name="disableLast" value="true">
						<html:button property="last" disabled="true">&gt;&gt;</html:button>
					</logic:equal>
					<logic:notEqual name="disableLast" value="true">
						<html:form action="/T002" method="post" style="display:inline;">
							<input type="hidden" name="currentPage"
								value="<bean:write name='T002Form' property='totalPages'/>" />
							<html:submit>&gt;&gt;</html:submit>
						</html:form>
					</logic:notEqual>
				</div>
			</logic:notEmpty>
		</div>


		<!-- Customer List -->
		<html:form action="/T002" method="post">
		<html:hidden property="action" styleId="actionHidden" value="" />
			<table class="customer-table">
				<thead>
					<tr>
						<th><html:checkbox property="selectAll" styleId="chkAll" /></th>
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
								<td><html:link page="/T003.do" paramId="customerId"
										paramName="customer" paramProperty="customerID">
										<bean:write name="customer" property="customerID" />
									</html:link></td>
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
				<div class="action-group">
					<!-- Add New -->
					<html:button property="btnAddNew" styleId="btnAddNew"
						onclick="redirectToEditPage()">
						<bean:message key="label.addNew" />
					</html:button>

					<!-- Delete -->
					<logic:notEmpty name="customers">
						<html:button property="btnDelete" styleId="btnDelete"
							onclick="document.getElementById('actionHidden').value='remove'; this.form.submit();">
							<bean:message key="label.delete" />
						</html:button>
					</logic:notEmpty>
					<logic:empty name="customers">
						<html:button property="btnDelete" styleId="btnDelete"
							disabled="true">
							<bean:message key="label.delete" />
						</html:button>
					</logic:empty>
				</div>

				<div class="action-group">
					<!-- Export -->
					<html:button property="btnExport" styleId="btnExport"
						onclick="document.getElementById('actionHidden').value='export'; this.form.submit();">
						<bean:message key="button.export" />
					</html:button>


					<!-- Import -->
					<html:button property="btnImport" styleId="btnImport"
					  onclick="redirectToExportPage();"
					>
						<bean:message key="button.import" />
					</html:button>
				</div>

				<div class="action-group">
					<!-- Setting Header -->
					<html:button property="btnSettingHeader" styleId="btnSettingHeader">
						<bean:message key="button.settingHeader" />
					</html:button>
				</div>
			</div>

		</html:form>
	</div>

	<%@ include file="Footer.jsp"%>

	<!-- JS -->
	<script
		src="<%=request.getContextPath()%>/WebContent/js/T002.js?v=<%=System.currentTimeMillis()%>"></script>
</body>
</html>
