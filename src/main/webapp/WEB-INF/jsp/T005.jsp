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

		<!-- CHỈ MỘT FORM DUY NHẤT -->
		<html:form action="/T005" method="post">
			<!-- Hidden field cho action -->
			<html:hidden property="action" value="" styleId="hiddenAction" />

			<div class="form-wrapper">
				<!-- Left Column -->
				<div class="form-column">
					<div class="left-column">
						<html:select name="T005Form" property="selectedLeftHeader"
							styleId="lstLeftHeader" styleClass="form-card"
							size="10">
							<html:optionsCollection name="T005Form" property="leftHeaders"
								label="label" value="value" />
						</html:select>


						<div class="button-container">
							<html:submit styleId="btnSave" styleClass="save-btn"
							onclick="setActionAndSubmit('save')" 
							>
								<bean:message key="T003.button.save" />
							</html:submit>
						</div>
					</div>
					<div class="arrow-buttons">
						<logic:equal name="T005Form" property="disabledRight" value="true">
							<html:button property="btnRight" styleId="btnRight"
								disabled="true" styleClass="btn-arrow">→</html:button>
						</logic:equal>
						<logic:notEqual name="T005Form" property="disabledRight"
							value="true">
							<html:button property="btnRight" styleId="btnRight"
								onclick="setActionAndSubmit('moveRight')" styleClass="btn-arrow">→</html:button>
						</logic:notEqual>

						<html:button property="btnLeft" styleId="btnLeft"
							onclick="setActionAndSubmit('moveLeft')" styleClass="btn-arrow">←</html:button>
					</div>
				</div>

				<!-- Right Column -->
				<div class="form-column">
					<div class="right-column">
						<html:select name="T005Form" property="selectedRightHeader"
							styleId="lstRightHeader" styleClass="form-card"
							size="10" style="width:200px;">
							<html:optionsCollection name="T005Form" property="rightHeaders"
								label="label" value="value" />
						</html:select>
						<div class="button-container">
							<html:button property="cancel" styleId="btnCancel"
								onclick="setActionAndSubmit('cancel')" 
								styleClass="cancel-btn">
								<bean:message key="T005.button.cancel" />
							</html:button>
						</div>
					</div>
					<div class="arrow-buttons">
						<html:button property="btnUp" styleId="btnUp"
							onclick="setActionAndSubmit('moveUp')" styleClass="btn-arrow">↑</html:button>
						<html:button property="btnDown" styleId="btnDown"
							onclick="setActionAndSubmit('moveDown')" styleClass="btn-arrow">↓</html:button>
					</div>
				</div>
			</div>
		</html:form>
	</div>

	<%@ include file="Footer.jsp"%>
	<script>
		function setActionAndSubmit(actionValue) {
			document.getElementById('hiddenAction').value = actionValue;
			document.forms[0].submit();
		}
	</script>
	<script
		src="<%=request.getContextPath()%>/WebContent/js/T005.js?ts=<%=System.currentTimeMillis()%>"></script>
</body>
</html>