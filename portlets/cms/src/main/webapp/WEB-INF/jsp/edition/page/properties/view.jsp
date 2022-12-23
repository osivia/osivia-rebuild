<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> 
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
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
	
	
	<%--Id--%>
    <c:set var="placeholder"><op:translate key="MODIFY_PAGE_PROPERTIES_ID_PLACEHOLDER" /></c:set>
    <spring:bind path="id">
        <div class="form-group required">
            <form:label path="id"><op:translate key="MODIFY_PAGE_PROPERTIES_ID_LABEL" /></form:label>
            
            <form:input path="id" cssClass="form-control ${status.error ? 'is-invalid' : ''}" readonly="${not form.modifiableId}" placeholder="${placeholder}" />
            <form:errors path="id" cssClass="invalid-feedback" />
        </div>
    </spring:bind>
	
     <div class="form-group">
         <form:label path="layoutId"><op:translate key="MODIFY_PAGE_PROPERTIES_LAYOUT_LABEL" /></form:label>
		 <form:select path="layoutId" items="${form.layouts}" cssClass="form-control" />
		 <form:errors path="layoutId" cssClass="invalid-feedback" />
     </div>	
     
     <div class="form-group">
         <form:label path="themeId"><op:translate key="MODIFY_PAGE_PROPERTIES_THEME_LABEL" /></form:label>
		 <form:select path="themeId" items="${form.themes}" cssClass="form-control"/>
		 <form:errors path="themeId" cssClass="invalid-feedback" />
     </div>	     
     
     
     <div class="form-group form-check">
         <form:checkbox path="selectorsPropagation" cssClass="form-check-input" />
         <form:label path="selectorsPropagation" cssClass="form-check-label" ><op:translate key="MODIFY_PAGE_PROPERTIES_SELECTORS_PROPAGATION_LABEL" /></form:label>
      </div> 

	  <c:if test="${not empty form.categories}">     
	     <div class="form-group">
	         <form:label path="category"><op:translate key="MODIFY_PAGE_PROPERTIES_CATEGORY_LABEL" /></form:label>
			 <form:select path="category" items="${form.categories}" cssClass="form-control"/>
			 <form:errors path="category" cssClass="invalid-feedback" />
	     </div>	 
	  </c:if>     
    <%--Buttons--%>
    <div class="float-end">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            <span><op:translate key="CANCEL" /></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_PAGE_PROPERTIES_VALIDATE_ACTION" /></span>
        </button>
    </div>     
	
</form:form>



