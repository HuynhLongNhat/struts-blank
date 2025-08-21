<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<!DOCTYPE html>
<html>
<head>
<title><bean:message key="label.login" /></title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/WebContent/css/T001.css?ts=<%=System.currentTimeMillis()%>">
</head>
<body>

	<%@ include file="Header.jsp"%>

	<nav class="nav-login">
		<bean:message key="label.login" />
	</nav>

	<div class="login-container">
		<h2 class="title">
			<bean:message key="title.login" />
		</h2>
		<label class="error-message" id="lblErrorMessage">
			<html:errors  />
		</label>
		<!-- Form Struts -->
		<html:form action="/T001" method="post">
		  <html:hidden property="action" value="login"/>
			<div class="input-group">
				<label for="txtuserID"> <bean:message key="label.userId" />
				</label>
				<html:text property="userId" styleId="txtuserID" />
			</div>

			<div class="input-group">
				<label for="txtpassword"> <bean:message key="label.password" />
				</label>
				<html:password property="password" styleId="txtpassword" />
			</div>

			<div class="button-group">
				<html:submit property="btnLogin" styleId="btnLogin">
					<bean:message key="button.login" />
				</html:submit>
				<html:reset styleId="btnClear" onclick="clearFormLogin(); return false;">
					<bean:message key="button.clear" />
				</html:reset>
			</div>

		</html:form>
	</div>
	<%@ include file="Footer.jsp"%>
	<script src="<%=request.getContextPath()%>/WebContent/js/T001.js?ts=<%=System.currentTimeMillis()%>"></script>
	<script>
	function clearFormFields() {
	    document.getElementById('txtuserID').value = '';
	    document.getElementById('txtpassword').value = '';
	    document.getElementById('lblErrorMessage').textContent = '';
	}
</script>
</body>
</html>