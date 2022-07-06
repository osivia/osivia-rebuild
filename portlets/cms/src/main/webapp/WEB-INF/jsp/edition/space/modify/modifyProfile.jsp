<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>



<div class="portal-properties-administration-profile-detail portal-properties-administration-detail-modal">

<portlet:defineObjects />
<portlet:actionURL name="save" var="url" copyCurrentRenderParameters="true" />

<form:form action="${url}" method="post" modelAttribute="profileForm">
    <%--Title--%>

    <spring:bind path="profile.name">
        <div class="form-group required">
            <form:label path="profile.name"><op:translate key="MODIFY_PROFILE_NAME_LABEL" /></form:label>
            <form:input path="profile.name" required="required" cssClass="form-control ${status.error ? 'is-invalid' : ''}" placeholder="${placeholder}" />
            <form:errors path="profile.name" cssClass="invalid-feedback" />
        </div>
    </spring:bind>
    
        <spring:bind path="profile.role">
        <div class="form-group required">
            <form:label path="profile.role"><op:translate key="MODIFY_PROFILE_ROLE_LABEL" /></form:label>
            <form:input path="profile.role"  cssClass="form-control ${status.error ? 'is-invalid' : ''}" placeholder="${placeholder}" />
            <form:errors path="profile.role" cssClass="invalid-feedback" />
        </div>
    </spring:bind>
    
    <spring:bind path="profile.url">
        <div class="form-group">
            <form:label path="profile.url"><op:translate key="MODIFY_PROFILE_URL_LABEL" /></form:label>
            <form:input path="profile.url" cssClass="form-control ${status.error ? 'is-invalid' : ''}" placeholder="${placeholder}" />
            <form:errors path="profile.url" cssClass="invalid-feedback" />
        </div>
    </spring:bind>
    
    <spring:bind path="profile.virtualUser">
        <div class="form-group">
            <form:label path="profile.virtualUser"><op:translate key="MODIFY_PROFILE_VIRTUALUSER_LABEL" /></form:label>
            <form:input path="profile.virtualUser" required="required" cssClass="form-control ${status.error ? 'is-invalid' : ''}" placeholder="${placeholder}" />
            <form:errors path="profile.virtualUser" cssClass="invalid-feedback" />
        </div>
    </spring:bind>

    <%--Buttons--%>
    <div class="text-right">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">
            <span><op:translate key="CANCEL" /></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_PROFILE_VALIDATE_DETAIL_LABEL" /></span>
        </button>
    </div>

	<c:if test="${not empty profileForm.callBackUrl}">
		<a class="callBack" href="${profileForm.callBackUrl}">callback</a>
    </c:if>

</form:form>

</div>