<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />



<c:set var="tab" value="6" scope="request" />
<jsp:include page="nav.jsp" />

<portlet:resourceURL id="export" var="exportUrl"/>
<portlet:resourceURL id="notFound" var="notFoundUrl"/>
<portlet:resourceURL id="forbidden" var="forbiddenUrl"/>
<portlet:resourceURL id="cachedResource" var="cachedResourceUrl">
    <portlet:param name="_cacheScope" value="PAGE"/>
</portlet:resourceURL>

<div>
    <a href="${exportUrl}" class="btn btn-default no-ajax-link">Ressource </a>
</div>


<div>
    <a href="${notFoundUrl}" class="btn btn-default no-ajax-link">Not found </a>
</div>

<div>
    <img src="${forbiddenUrl}" class="btn btn-default no-ajax-link"/> Forbidden image
</div>

<div>
    <img height ="50px" src="${cachedResourceUrl}" class="btn btn-default no-ajax-link"/> Cached image
</div>
