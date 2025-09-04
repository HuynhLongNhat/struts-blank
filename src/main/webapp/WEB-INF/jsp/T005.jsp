<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<!DOCTYPE html>
<html>
<head>
<title><bean:message key="T005.title" /></title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/WebContent/css/T005.css?<%=System.currentTimeMillis()%>">
</head>
<body>
	<%@ include file="Header.jsp"%>

	<div class="container">
		<div class="nav-group">
			<span><bean:message key="T003.breadcrumb.login" /></span> &gt; <span><bean:message
					key="T003.breadcrumb.searchCustomer" /></span> &gt; <span><bean:message
					key="T005.breadcrumb.settting" /></span>
		</div>

		<div class="welcome">
			<span> <bean:message key="T003.label.welcome" /> <label
				id="lblUserName">${user.userName}</label>
			</span>
			<html:link action="/logout" styleId="lblLogout">
				<bean:message key="T003.label.logout" />
			</html:link>
		</div>

		<div class="blue-bar"></div>
		<div id="errorMessages" style="display: none">
			<html:errors />
		</div>
		<html:form action="/T005" method="" styleClass="form-wrapper">
			<!-- Left Column -->
			<div class="form-column">
				<div class="left-column">
					<html:select name="T005Form" property="selectedLeftHeader" styleId="lstLeftHeader"
						styleClass="form-card" multiple="true" size="10"
						style="width:200px;">
						<html:optionsCollection name="T005Form" property="leftHeaders"
							label="label" value="value" />
					</html:select>
					<div class="button-container">
						<html:submit styleId="btnSave" styleClass="save-btn">
							<bean:message key="T003.button.save" />
						</html:submit>
					</div>
				</div>
				<div class="arrow-buttons">
					<html:form action="/T005" method="post" style="display:inline;">
						<html:hidden property="action" value="moveRight" />
						<html:submit styleId="btnRight">→</html:submit>
					</html:form>

					<!-- Move Left -->
					<html:form action="/T005" method="post" style="display:inline;">
						<html:hidden property="action" value="moveLeft" />
						<html:submit styleId="btnLeft">←</html:submit>
					</html:form>
				</div>
			</div>
			<!-- Right Column -->
			<div class="form-column">
				<div class="right-column">
					<html:select name="T005Form"  property="selectedRightHeader"
						styleId="lstRightHeader" styleClass="form-card" multiple="true"
						size="10" style="width:200px;">
						<html:optionsCollection name="T005Form"   property="rightHeaders"
							label="label" value="value" />
					</html:select>
					<div class="button-container">
						<html:button property="cancel" styleId="btnCancel"
							onclick="window.location.href='<%=request.getContextPath()%>/cancel'"
							styleClass="cancel-btn">
							<bean:message key="T005.button.cancel" />
						</html:button>
					</div>
				</div>
				<div class="arrow-buttons">
					<html:form action="/T005" method="post" style="display:inline;">
						<html:hidden property="action" value="moveUp" />
						<html:submit styleId="btnUp">↑</html:submit>
					</html:form>

					<!-- Move Down -->
					<html:form action="/T005" method="post" style="display:inline;">
						<html:hidden property="action" value="moveDown" />
						<html:submit styleId="btnDown">↓</html:submit>
					</html:form>
				</div>
			</div>
		</html:form>
	</div>

	<%@ include file="Footer.jsp"%>
	<script
		src="<%=request.getContextPath()%>/WebContent/js/T005.js?ts=<%=System.currentTimeMillis()%>"></script>
</body>
</html>
