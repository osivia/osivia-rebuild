<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:actionURL name="addPage" var="addPageURL"/>
<portlet:actionURL name="addPortlet" var="addPortletURL"/>

<p>
     <span >
       <a href="${addPageURL}" class="btn btn-default">Add page</a>
    </span>
    
         <span >
       <a href="${addPortletURL}" class="btn btn-default">Add portlet</a>
    </span>
</p>

