<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />



<c:set var="tab" value="6" scope="request" />
<jsp:include page="nav.jsp" />

<portlet:resourceURL id="export" var="exportUrl"/>


<div>
    <a href="${exportUrl}" class="btn btn-default no-ajax-link">Ressource </a>
</div>
