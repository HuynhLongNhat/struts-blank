<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Search Customer</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/WebContent/css/T002.css">
</head>
<body>
	<%@ include file="Header.jsp"%>
	<div class="container">
		<div class="nav-group">
			<span>Login</span> <span>&gt;</span> <span>Search Customer</span>
		</div>
		<div class="welcome">
			<span>Welcome <label>${user.userName}</label></span> <a
				href="${pageContext.request.contextPath}/logout">Logout</a>
		</div>

		<div class="blue-bar"></div>

	</div>

	<%@ include file="Footer.jsp"%>

	<script
		src="${pageContext.request.contextPath}/WebContent/js/script.js?<%=System.currentTimeMillis()%>"></script>
	<script>
    document.addEventListener("DOMContentLoaded", function () {
        const checkAll = document.getElementById('chkAll');
        const checkDetails = document.querySelectorAll('.chkDetail');

        if (checkAll) {
            checkAll.addEventListener('change', function () {
                checkDetails.forEach(chk => chk.checked = checkAll.checked);
            });
        }
    });

    function redirectToEditPage() {
        window.location.href = 'T003';
    }
</script>
</body>
</html>
