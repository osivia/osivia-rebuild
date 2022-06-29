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

<div  data-ajax-region-modified="portal-properties-administration-profile-region">
                       
   <%@ include file="profiles.jspf" %>	
	
</div>
