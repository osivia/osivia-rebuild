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


<portlet:actionURL name="modifyPortlet" var="url" />

<form:form action="${url}" method="post" modelAttribute="form">

    <c:set var="placeholder"><op:translate key="MODIFY_PORTLET_TITLE" /></c:set>

     <div class="form-group">
         <form:label path="title"><op:translate key="MODIFY_PORTLET_TITLE" /></form:label>
         <form:input path="title" cssClass="form-control ${status.error ? 'is-invalid' : ''}" placeholder="${placeholder}" htmlEscape="true" />
         <form:errors path="title" cssClass="invalid-feedback" />
     </div>
     <div class="form-check">
         <form:checkbox path="displayTitle" cssClass="form-check-input" />
         <form:label path="displayTitle" cssClass="form-check-label" ><op:translate key="MODIFY_PORTLET_DISPLAY_TITLE" /></form:label>
      </div>        
 
     <div class="form-check">
         <form:checkbox path="displayPanel" cssClass="form-check-input" />
         <form:label path="displayPanel" cssClass="form-check-label" ><op:translate key="MODIFY_PORTLET_DISPLAY_PANEL" /></form:label>
      </div> 
     <div class="form-check">
         <form:checkbox path="hideIfEmpty" cssClass="form-check-input" />
         <form:label path="hideIfEmpty" cssClass="form-check-label" ><op:translate key="MODIFY_PORTLET_DISPLAY_HIDE_IF_EMPTY" /></form:label>
      </div>      
      
	<div class="form-group mt-2">
        <form:label path="styles"><op:translate key="MODIFY_PORTLET_STYLES" /></form:label>
		<div class="form-group">
			<c:forEach var="style" items="${stylesList}">
				<div class="form-check form-check-inline">
			    	<form:checkbox path="styles" value="${style}" cssClass="form-check-input"/>
			    	<form:label path="styles" cssClass="form-check-label" >${style}</form:label>
			   </div>
			</c:forEach>
		</div>
	</div>									
              
    <%--Buttons--%>
    <div class="text-right">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">
            <span><op:translate key="CANCEL" /></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_PORTLET_LABEL" /></span>
        </button>
    </div>    
</form:form>



