<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />
<portlet:actionURL name="save" var="url" />

<form:form action="${url}" method="post" modelAttribute="form">
    <%--Title--%>
    <c:set var="placeholder"><op:translate key="TITLE_PLACEHOLDER" /></c:set>
    <spring:bind path="title">
        <div class="form-group required">
            <form:label path="title"><op:translate key="TITLE_LABEL" /></form:label>
            <form:input path="title" cssClass="form-control ${status.error ? 'is-invalid' : ''}" placeholder="${placeholder}" />
            <form:errors path="title" cssClass="invalid-feedback" />
        </div>
    </spring:bind>

    <%--Buttons--%>
    <div class="text-right">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">
            <span><op:translate key="CANCEL" /></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="RENAME_ACTION" /></span>
        </button>
    </div>
</form:form>

