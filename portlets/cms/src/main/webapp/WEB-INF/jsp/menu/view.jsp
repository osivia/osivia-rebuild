<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />


<div class="nuxeo-publish-navigation">
    <c:choose>
        <c:when test="${not empty displayItem and not empty displayItem.children}">
            <nav class="menu-default">
                <!-- Title -->
                <h3 class="hidden"><op:translate key="MENU_TITLE_DEFAULT" /></h3>
        
             
                <!-- Menu -->
                <c:set var="parent" value="${displayItem}" scope="request" />
                <c:set var="level" value="1" scope="request" />

				<div class="list-group list-group-root card card-body bg-light p-0 m-2">                
               		<jsp:include page="display-items.jsp" />
              	</div>

            </nav>
        </c:when>
        
        <c:when test="${not empty displayItem}">
            <p class="text-muted text-center"><op:translate key="NO_ITEMS" /></p>
        </c:when>
    
        <c:otherwise>
            <p class="text-danger">
                <i class="halflings halflings-exclamation-sign"></i>
                <span><op:translate key="MESSAGE_PATH_UNDEFINED" /></span>
            </p>
        </c:otherwise>
    </c:choose>
</div>
