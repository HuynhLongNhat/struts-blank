<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="form.T002Form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><bean:message key="label.searchCustomer" /></title>
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

			<label> <bean:message key="label.sex" /> <span
				class="sex-select"> <html:select property="sex"
						styleId="cboSex">
						<html:option value="" />
						<html:option value="0">
							<bean:message key="label.male" />
						</html:option>
						<html:option value="1">
							<bean:message key="label.female" />
						</html:option>
					</html:select>
			</span>
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
        <c:choose>
            <c:when test="${disableFirst}">
                <html:button property="first" disabled="true">&lt;&lt;</html:button>
            </c:when>
            <c:otherwise>
                <html:form action="/T002" method="post" style="display:inline;">
                    <html:hidden property="currentPage" value="1" />
                    <html:submit>&lt;&lt;</html:submit>
                </html:form>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${disablePrevious}">
                <html:button property="prev" disabled="true">&lt;</html:button>
            </c:when>
            <c:otherwise>
                <html:form action="/T002" method="post" style="display:inline;">
                    <html:hidden property="currentPage" value="${T002Form.prevPage}" />
                    <html:submit>&lt;</html:submit>
                </html:form>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="next">
        <c:choose>
            <c:when test="${disableNext}">
                <html:button property="next" disabled="true">&gt;</html:button>
            </c:when>
            <c:otherwise>
                <html:form action="/T002" method="post" style="display:inline;">
                    <html:hidden property="currentPage" value="${T002Form.nextPage}" />
                    <html:submit>&gt;</html:submit>
                </html:form>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${disableLast}">
                <html:button property="last" disabled="true">&gt;&gt;</html:button>
            </c:when>
            <c:otherwise>
                <html:form action="/T002" method="get" style="display:inline;">
                    <html:hidden property="currentPage" value="${T002Form.totalPages}" />
                    <html:submit>&gt;&gt;</html:submit>
                </html:form>
            </c:otherwise>
        </c:choose>
    </div>
</div>


		<!-- Customer List -->
		<html:form action="/T002" method="post">
			<table class="customer-table">
				<thead>
					<tr>
						<th><label class="custom-checkbox"> <html:checkbox
									property="selectAll" styleId="chkAll" /> <span></span>
						</label></th>
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
								<td><label class="custom-checkbox"> <html:multibox
											property="customerIds"
											value='<bean:write name="customer" property="customerID"/>' />
										<span></span>
								</label></td>

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
						<html:form action="/T002" method="post">
							<html:hidden property="action" value="remove" />
							<html:submit property="btnDelete" styleId="btnDelete">
								<bean:message key="label.delete" />
							</html:submit>
						</html:form>
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
					<html:form action="/T002" method="post" style="display:inline;">
						<html:hidden property="action" value="export" />
						<html:submit property="btnExport" styleId="btnExport">
							<bean:message key="button.export" />
						</html:submit>
					</html:form>
					<!-- Import -->
					<html:button property="btnImport" styleId="btnImport"
						onclick="redirectToExportPage();">
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
	<script src="<%=request.getContextPath()%>/WebContent/js/T002.js?ts=<%=System.currentTimeMillis()%>"></script>
	<%@ include file="Footer.jsp"%>

	<!-- JS -->
	
	<script>
		function redirectToEditPage() {
			window.location.href = '/Struts-blank/T003.do';
		}

		function redirectToExportPage() {
			window.location.href = '/Struts-blank/T004.do';
		}
	</script>
</body>
</html>
