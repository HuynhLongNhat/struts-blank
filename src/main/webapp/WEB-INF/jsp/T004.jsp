<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<!DOCTYPE html>
<html>
<head>
<title><bean:message key="T003.title" /></title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/WebContent/css/T004.css?<%=System.currentTimeMillis()%>">
<style>
body {
	font-family: Arial, sans-serif;
	background-color: #80ffff;
	padding-top: 40px;
}

.blue-bar {
	background: blue;
	height: 20px;
}

.welcome {
	display: flex;
	margin-top: 30px;
	margin-bottom: 20px;
	justify-content: space-between;
}

.welcome a {
	color: #000;
	margin-left: 15px;
}

.container {
	margin: 0 20px;
}

.form-container {
    width: 500px;
    margin: 40px auto;
    padding: 25px;
    border: 1px solid #ddd;
    border-radius: 12px;
    background-color: #fafafa;
    box-shadow: 0 4px 10px rgba(0,0,0,0.1);
    font-family: Arial, sans-serif;
}

.form-row {
    display: flex;
    align-items: center;
    margin-bottom: 25px;
    justify-content: space-between;
}

.file-input-container {
    display: flex;
    align-items: center;
    width: 100%;
}

.custom-file-upload {
    padding: 8px 15px;
    background-color: #007bff;
    color: white;
    border-radius: 6px;
    cursor: pointer;
    margin-right: 10px;
    border: 1px solid #007bff;
    font-size: 14px;
}

.custom-file-upload:hover {
    background-color: #0056b3;
    border-color: #0056b3;
}

#uploadFile {
    display: none;
}

.file-name {
    flex: 1;
    padding: 6px;
    border: 1px solid #ccc;
    border-radius: 6px;
    background-color: white;
    min-height: 20px;
    font-size: 14px;
    color: #555;
}

.file-hint {
    margin-top: 5px;
    color: #888;
    font-size: 13px;
    font-style: italic;
    width: 100%;
    text-align: left;
}

.form-actions {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 20px;
}

#btnImport,
#btnCancel {
    padding: 8px 20px;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-size: 14px;
    transition: background 0.2s ease-in-out;
}

#btnImport {
    background-color: #007bff;
    color: white;
}

#btnImport:hover {
    background-color: #0056b3;
}

#btnCancel {
    background-color: #e0e0e0;
    color: #333;
}

#btnCancel:hover {
    background-color: #bdbdbd;
}
</style>
</head>
<body>
	<%@ include file="Header.jsp"%>

	<div class="container">
		<div class="nav-group">
			<span><bean:message key="T003.breadcrumb.login" /></span> <span>&gt;</span>
			<span><bean:message key="T004.breadcrumb.importCustomer" /></span> <span>&gt;</span>
			<span> 
				<bean:message key="T004.breadcrumb.importCustomer" />
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

		<div class="blue-bar"></div>
        <div id="errorMessages" style="display: none">
			<html:errors />
		</div>
        
		<div class="form-container">
			<html:form action="/T004" method="post" enctype="multipart/form-data" styleId="customerForm">
				<html:hidden property="action" value="import"/>
				<div class="form-row">
					<div class="file-input-container">
						
						<html:file property="uploadFile" styleId="uploadFile" onchange="updateFileName(this)"/>
						<div id="fileName" class="file-name"></div>
						<label for="uploadFile" class="custom-file-upload">
							Browse...
						</label>
					</div>
				</div>
				

				<div class="form-actions">
					<html:submit property="btnImport" styleId="btnImport">
						Import
					</html:submit>

					<html:button styleId="btnCancel" property="btnCancel"
						onclick="window.location.href='/Struts-blank/T002.do';">Cancel</html:button>

				</div>
			</html:form>
		</div>
	</div>

	<%@ include file="Footer.jsp"%>
	<script>
		function updateFileName(input) {
			var fileName = input.files[0] ? input.files[0].name : "No file chosen";
			document.getElementById("fileName").textContent = fileName;
		}
	</script>
	<script src="<%=request.getContextPath()%>/WebContent/js/T004.js?ts=<%=System.currentTimeMillis()%>"></script>
</body>
</html>