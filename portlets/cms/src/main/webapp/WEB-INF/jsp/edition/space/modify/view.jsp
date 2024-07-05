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

<div class="portal-properties-administration">

	<form:form action="${url}" method="post" modelAttribute="form">
	
		<portlet:renderURL var="tabProfilesUrl">
		    <portlet:param name="tab" value="profiles"/>
		</portlet:renderURL>
		
		<portlet:renderURL var="tabStylesUrl">
		    <portlet:param name="tab" value="styles"/>
		</portlet:renderURL>
		
			
	    <div data-ajax-region="portal-properties-administration-vars-region">
	 	    <input type="hidden" name="sortSrc"/>
			<input type="hidden" name="sortTarget"/>
			<input type="hidden" name="formAction"/>  
	    </div>
	   
	    <ul class="nav nav-tabs"> 
	        <li class="nav-item">
	                <a class="nav-link <c:if test="${empty tab or tab eq 'profiles'}">active</c:if>" href="${tabProfilesUrl}"><op:translate key="MODIFY_SPACE_PROFILES" /></a>
	        </li>
	
	        <li class="nav-item">
	                <a class="nav-link <c:if test="${tab eq 'styles'}">active</c:if>" href="${tabStylesUrl}"><op:translate key="MODIFY_SPACE_STYLES" /></a>
	        </li>
	    </ul> 
	    
	    
	    <c:if test="${empty tab or tab eq 'profiles'}">
	 	   		<%@ include file="profiles.jspf" %>	
	    </c:if>
	    <c:if test="${tab eq 'styles'}">
			 	<%@ include file="styles.jspf" %>	
		</c:if>
		 
		<input type="submit" class="d-none">
		 
		<div class="d-flex flex-row-reverse"> </div>
		
		
	</form:form>
	
	
     <%--Confirmation modal--%>
     <div id="${namespace}-confirmation-modal" class="confirmation-modal modal fade" tabindex="-1">
         <div class="modal-dialog">
             <div class="modal-content">
                 <div class="modal-header">
                     <h5 class="modal-title" data-confirm-title="<op:translate key="MODIFY_SPACE_CONFIRM_DELETE_TITLE"/>"  data-error-title="<op:translate key="MODIFY_SPACE_CONFIRM_DELETE_ERROR_TITLE"/>">
                         <span class="confirmationTitle"></span>
                     </h5>            
                 </div>
                 <div class="modal-body">
                     <form action="X"  method="post">
                         <input type="submit" class="d-none">
                         
                         <p class="confirmationMessage">
                             
                         </p>
     
                         
                         <div class="d-flex justify-content-center gap-2 ">
                             <button type="button" class="btn btn-primary confirmationButton" onclick="confirmModalSubmit(this);">
                                 <span><op:translate key="MODIFY_SPACE_CONFIRM_DELETE"/></span>
                             </button>
     
                             <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                 <span><op:translate key="CANCEL"/></span>
                             </button>
                         </div>
                         
                     </form>
                 </div>
             </div>
         </div>
     </div>
	    
</div>

