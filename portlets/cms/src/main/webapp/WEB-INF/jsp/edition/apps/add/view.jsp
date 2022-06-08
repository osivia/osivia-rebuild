<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<c:set var="namespace">
	<portlet:namespace />
</c:set>


<portlet:actionURL name="submit" var="submitUrl" />

<form:form action="${submitUrl}" method="post" modelAttribute="form"
	class="w-100">
	<ul class="p-0 mx-auto">
		<c:forEach var="app" items="${form.apps}">
			<portlet:actionURL name="addPortlet" var="addPortletURL">
				<portlet:param name="appId" value="${app.id}" />
			</portlet:actionURL>


			<li class="d-flex m-2">
				<div class="col-1 pl-0">
					<c:choose>
					    <c:when test="${not empty app.iconLocation}">
					        <img src="${app.iconLocation} "/> 
					    </c:when>
					    <c:otherwise>
					        <i class=\"glyphicons glyphicons-basic-square-empty\"></i>
					    </c:otherwise>
					</c:choose>
				</div>
				<div class="flex-grow-1 text-truncate"><span>${app.displayName}</span> </div>
				<div class="col-2"> <a href="${addPortletURL}"	class=" btn-sm btn-light m-1"> <op:translate key="ADD_PORTLET_LABEL" /></a></div>
			</li>
		</c:forEach>
	</ul>
</form:form>



