<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>



<div class="portal-properties-administration-style-detail portal-properties-administration-detail-modal">

<portlet:defineObjects />
<portlet:actionURL name="save" var="url" copyCurrentRenderParameters="true" />

<form:form action="${url}" method="post" modelAttribute="styleForm">
    <%--Title--%>

    <spring:bind path="name">
        <div class="form-group required">
            <form:label path="name"><op:translate key="MODIFY_STYLE_NAME_LABEL" /></form:label>
            <form:input path="name" required="required" cssClass="form-control ${status.error ? 'is-invalid' : ''}" placeholder="${placeholder}" />
            <form:errors path="name" cssClass="invalid-feedback" />
        </div>
    </spring:bind>
    
    

    <%--Buttons--%>
    <div class="text-right">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">
            <span><op:translate key="CANCEL" /></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_STYLE_VALIDATE_DETAIL_LABEL" /></span>
        </button>
    </div>

	<c:if test="${not empty styleForm.callBackUrl}">
		<a class="callBack" href="${styleForm.callBackUrl}">callback</a>
    </c:if>

</form:form>

</div>