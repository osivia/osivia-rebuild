<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<portlet:defineObjects/>

<c:set var="namespace"><portlet:namespace/></c:set>

<portlet:actionURL name="home" var="homeUrl"/>
<portlet:actionURL name="switchMode" var="switchModeUrl" />
<portlet:actionURL name="submit" var="submitUrl"/>


<%--@elvariable id="status" type="org.osivia.portal.cms.portlets.edition.page.main.controller.EditionStatus"--%>
<%--@elvariable id="form" type="org.osivia.portal.cms.portlets.edition.page.main.controller.EditionForm"--%>
<nav class="navbar navbar-expand">
	<div class="container-fluid">
        <%--Home--%>
		<ul class="navbar-nav">
            <li class="nav-item">
                <a href="${homeUrl}" class="nav-link">
                    <i class="glyphicons glyphicons-basic-home"></i>
                    <span>Accueil</span>
                </a>
            </li>
        </ul>

        <%--Preview selector--%>
        <c:if test="${status.supportPreview && not status.liveSpace && status.modifiable}">
            <div class="btn-group btn-group-sm">
                <c:choose>
                    <c:when test="${status.preview and status.havingPublication}">
                        <a href="${switchModeUrl}" class="btn btn-success text-nowrap">
                            <span>En ligne</span>
                        </a>
                        <a href="#" class="btn btn-warning text-nowrap">
                            <span>&Eacute;dition</span>
                        </a>
                    </c:when>

                    <c:when test="${status.preview}">
                        <a href="#" class="btn btn-success text-nowrap">
                            <span>En ligne</span>
                        </a>
                        <a href="#" class="btn btn-warning text-nowrap">
                            <span>&Eacute;dition</span>
                        </a>
                    </c:when>

                    <c:otherwise>
                        <a href="#" class="btn btn-success text-nowrap">
                            <span>En ligne</span>
                        </a>
                        <a href="${switchModeUrl}" class="btn btn-warning text-nowrap">
                            <span>Edition</span>
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <%--Toolbar--%>
        ${status.toolbar}

        <%--Locale selector--%>
        <form:form action="${submitUrl}" method="post" modelAttribute="form" cssClass="d-flex">
            <input id="${namespace}-update-locale" type="submit" name="update-locale" class="d-none">
            <form:select path="locale" items="${status.locales}" cssClass="form-select" data-change-submit="${namespace}-update-locale"/>
        </form:form>
	</div>
</nav>
