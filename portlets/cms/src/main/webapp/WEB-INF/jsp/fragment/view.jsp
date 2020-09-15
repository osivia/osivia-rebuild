<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<img class="img-fluid" src="${contextPath}/img/logo.png"/>
