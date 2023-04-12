<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<portlet:defineObjects/>

<c:set var="namespace"><portlet:namespace/></c:set>


<portlet:actionURL name="modifyPortlet" var="url"/>

<%--@elvariable id="form" type="org.osivia.portal.cms.portlets.edition.page.apps.modify.controller.ModifyForm"--%>
<%--@elvariable id="stylesList" type="java.util.List"--%>
<form:form action="${url}" method="post" modelAttribute="form">
    <div class="mb-3">
        <c:set var="placeholder"><op:translate key="MODIFY_PORTLET_TITLE"/></c:set>
        <form:label path="title" cssClass="form-label"><op:translate key="MODIFY_PORTLET_TITLE"/></form:label>
        <form:input path="title" cssClass="form-control" placeholder="${placeholder}" htmlEscape="true"/>
    </div>

    <div class="mb-3">
        <div class="form-check">
            <form:checkbox id="${namespace}-display-title" path="displayTitle" cssClass="form-check-input"/>
            <label for="${namespace}-display-title" class="form-check-label"><op:translate key="MODIFY_PORTLET_DISPLAY_TITLE"/></label>
        </div>

        <div class="form-check">
            <form:checkbox id="${namespace}-display-panel" path="displayPanel" cssClass="form-check-input"/>
            <label for="${namespace}-display-panel" class="form-check-label"><op:translate key="MODIFY_PORTLET_DISPLAY_PANEL"/></label>
        </div>
        <div class="form-check">
            <form:checkbox id="${namespace}-hide-if-empty" path="hideIfEmpty" cssClass="form-check-input"/>
            <label for="${namespace}-hide-if-empty" class="form-check-label"><op:translate key="MODIFY_PORTLET_DISPLAY_HIDE_IF_EMPTY"/></label>
        </div>
    </div>

    <div class="mb-3">
        <form:label path="styles" cssClass="form-label"><op:translate key="MODIFY_PORTLET_STYLES"/></form:label>
        <c:forEach var="style" items="${stylesList}" varStatus="status">
            <div class="form-check form-check-inline">
                <form:checkbox id="${namespace}-style-${status.index}" path="styles" value="${style}" cssClass="form-check-input"/>
                <label for="${namespace}-style-${status.index}" class="form-check-label">${style}</label>
            </div>
        </c:forEach>
        <c:if test="${empty stylesList}">
            <div class="form-text">
                <span><op:translate key="MODIFY_PORTLET_NO_STYLE_AVAILABLE"/></span>
            </div>
        </c:if>
    </div>

    <%--Buttons--%>
    <div class="text-end">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            <span><op:translate key="CANCEL"/></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_PORTLET_LABEL"/></span>
        </button>
    </div>
</form:form>
