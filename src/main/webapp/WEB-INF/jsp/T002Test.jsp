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
    <html:base />
    <link rel="stylesheet" type="text/css"
          href="<%=request.getContextPath()%>/WebContent/css/T002.css?v=<%=System.currentTimeMillis()%>" />
</head>
<body>

    <%@ include file="Header.jsp"%>

    <%
        T002Form searchForm = (T002Form) request.getAttribute("T002Form");
    %>

    <div class="container">

        <!-- Navigation -->
        <div class="nav-group">
            <span><bean:message key="label.login" /></span>
            <span>&gt;</span>
            <span><bean:message key="label.searchCustomer" /></span>
        </div>

        <!-- Welcome -->
        <div class="welcome">
            <span>
                <bean:message key="label.welcome" />
                <logic:present name="user">
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

            <label>
                <bean:message key="label.customerName" />
                <html:text property="customerName" styleId="txtCustomerName" />
            </label>

            <label>
                <bean:message key="label.sex" />
                <span class="sex-select">
                    <html:select property="sex" styleId="cboSex">
                        <html:option value="" />
                        <html:option value="0"><bean:message key="label.male" /></html:option>
                        <html:option value="1"><bean:message key="label.female" /></html:option>
                    </html:select>
                </span>
            </label>

            <label>
                <bean:message key="label.birthday" />
                <html:text property="birthdayFrom" styleId="txtBirthdayFrom" /> -
                <html:text property="birthdayTo" styleId="txtBirthdayTo" />
            </label>

            <html:submit styleId="btnSearch">
                <bean:message key="label.search" />
            </html:submit>
        </html:form>

        <!-- Customer List -->
        <html:form action="/T002" method="post">
            <table class="customer-table">
                <thead>
                    <tr>
                        <th>
                            <label class="custom-checkbox">
                                <html:checkbox property="selectAll" styleId="chkAll" />
                                <span></span>
                            </label>
                        </th>
                        <!-- Render header động -->
                        <logic:notEmpty name="T002Form" property="columnHeaders">
                            <logic:iterate id="header" name="T002Form" property="columnHeaders" 
                                           type="org.apache.struts.util.LabelValueBean">
                                <th><bean:write name="header" property="label" /></th>
                            </logic:iterate>
                        </logic:notEmpty>
                    </tr>
                </thead>
                <tbody>
                    <logic:notEmpty name="T002Form" property="customers">
                        <logic:iterate id="customer" name="T002Form" property="customers" type="dto.T002Dto">
                            <tr>
                                <td>
                                    <label class="custom-checkbox">
                                        <html:multibox property="customerIds">
                                            <bean:write name="customer" property="customerID" />
                                        </html:multibox>
                                        <span></span>
                                    </label>
                                </td>
                                <!-- Render giá trị động theo header -->
                                <logic:iterate id="header" name="T002Form" property="columnHeaders" 
                                               type="org.apache.struts.util.LabelValueBean">
                                    <td>
                                        <bean:write name="customer" property="<%= header.getValue() %>" />
                                    </td>
                                </logic:iterate>
                            </tr>
                        </logic:iterate>
                    </logic:notEmpty>
                </tbody>
            </table>
        </html:form>

        <!-- Actions -->
        <div class="actions">
            <div class="action-group">
                <html:button property="btnAddNew" styleId="btnAddNew" onclick="redirectToEditPage()">
                    <bean:message key="label.addNew" />
                </html:button>

                <html:form action="/T002" method="post">
                    <html:hidden property="action" value="remove" />
                    <html:submit property="btnDelete" styleId="btnDelete">
                        <bean:message key="label.delete" />
                    </html:submit>
                </html:form>
            </div>

            <div class="action-group">
                <html:form action="/T002" method="post" style="display:inline;">
                    <html:hidden property="action" value="export" />
                    <html:submit property="btnExport" styleId="btnExport">
                        <bean:message key="button.export" />
                    </html:submit>
                </html:form>

                <html:button property="btnImport" styleId="btnImport"
                             onclick="redirectToExportPage();">
                    <bean:message key="button.import" />
                </html:button>
            </div>

            <div class="action-group">
                <html:button property="btnSettingHeader" styleId="btnSettingHeader"
                             onclick="window.location.href='/Struts-blank/T005.do';">
                    <bean:message key="button.settingHeader" />
                </html:button>
            </div>
        </div>
    </div>

    <!-- JS -->
    <script src="<%=request.getContextPath()%>/WebContent/js/T002.js?ts=<%=System.currentTimeMillis()%>"></script>
    <%@ include file="Footer.jsp"%>

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
