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
        <span><bean:message key="T003.breadcrumb.login" /></span> &gt;
        <span><bean:message key="T003.breadcrumb.searchCustomer" /></span> &gt;
        <span><bean:message key="T005.breadcrumb.settting" /></span>
    </div>

    <div class="welcome">
        <span>
            <bean:message key="T003.label.welcome" /> 
           <label
				id="lblUserName">${user.userName}</label>
        </span>
        <html:link action="/logout">
            <bean:message key="T003.label.logout" />
        </html:link>
    </div>

    <div class="blue-bar"></div>

    <html:form action="/T005" styleClass="form-wrapper">
        <!-- Left Column -->
        <div class="form-column">
            <div class="left-column">
                <div class="form-card">
                    <div class="form-group">
                        <label id="sex">
                            <bean:message key="label.sex" />
                        </label>
                     
                    </div>

                    <div class="form-group">
                        <label id="email">
                            <bean:message key="T003.label.email" />
                        </label>
                       
                    </div>

                    <div class="form-group address-group">
                        <label id="address">
                            <bean:message key="T003.label.address" />
                        </label>
                      
                    </div>
                </div>

                <div class="button-container">
                    <html:submit styleClass="save-btn">
                        <bean:message key="T003.button.save" />
                    </html:submit>
                </div>
            </div>

            <div class="arrow-buttons">
                <html:button property="btnRight">→</html:button>
                <html:button property="btnLeft">←</html:button>
            </div>
        </div>

        <!-- Right Column -->
        <div class="form-column">
            <div class="right-column">
                <div class="form-card">
                    <div class="form-group">
                        <label id="customerId">
                            <bean:message key="label.customerId" />
                        </label>                      
                    </div>
                    <div class="form-group">
                        <label id="customerName">
                            <bean:message key="label.customerName" />
                        </label>
                     
                    </div>

                    <div class="form-group">
                        <label id="birthday">
                            <bean:message key="label.birthday" />
                        </label>
                      
                    </div>
                </div>

                <div class="button-container">
                    <html:button property="cancel"
                                 onclick="window.location.href='<%=request.getContextPath()%>/cancel'"
                                 styleClass="cancel-btn">
                        <bean:message key="T005.button.cancel" />
                    </html:button>
                </div>
            </div>

            <div class="arrow-buttons">
                <html:button property="btnUp">↑</html:button>
                <html:button property="btnDown">↓</html:button>
            </div>
        </div>
    </html:form>
</div>

<%@ include file="Footer.jsp"%>
<script src="<%=request.getContextPath()%>/WebContent/js/T005.js?ts=<%=System.currentTimeMillis()%>"></script>
</body>
</html>
