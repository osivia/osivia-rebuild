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


<portlet:actionURL name="modifyPortal" var="url" />

<form:form action="${url}" method="post" modelAttribute="form">


	<div  data-ajax-region-modified="portal-properties-administration-vars-region">
	    <input type="hidden" name="sortSrc"/>
		<input type="hidden" name="sortTarget"/>
		<input type="hidden" name="formAction"/>
	</div>		

	<div  data-ajax-region-modified="portal-properties-administration-profile-region">
	   <%@ include file="profiles.jspf" %>	
	</div>
	


</form:form>
