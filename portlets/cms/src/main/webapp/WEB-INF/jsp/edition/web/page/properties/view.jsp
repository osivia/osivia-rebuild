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

<portlet:resourceURL id="browse" var="browseUrl">
</portlet:resourceURL>

<portlet:actionURL name="submit" var="submitUrl" />

<form:form action="${submitUrl}" method="post" modelAttribute="form"
	class="w-100">
	
	
	<%--Id--%>
    <c:set var="placeholder"><op:translate key="MODIFY_PAGE_PROPERTIES_ID_PLACEHOLDER" /></c:set>
    <spring:bind path="id">
        <div class="form-group required">
            <form:label path="id"><op:translate key="MODIFY_PAGE_PROPERTIES_ID_LABEL" /></form:label>
            <form:input path="id" cssClass="form-control ${status.error ? 'is-invalid' : ''}" placeholder="${placeholder}" />
            <form:errors path="id" cssClass="invalid-feedback" />
        </div>
    </spring:bind>
	
	
	<div class="browse selector form-group">
		<spring:bind path="templateId">
		
		<label><op:translate key="MODIFY_PAGE_SELECT_TEMPLATE" /></label>		
						
		<div class="d-flex">
			<input type="text" class="form-control selector-label flex-grow-1 my-auto" readonly="readonly">
			<c:set var="selectLabel"><op:translate key="MODIFY_PAGE_PROPERTIES_SELECT_TEMPLATE" /></c:set>
			<c:set var="clearFilterLabel"><op:translate key="MODIFY_PAGE_PROPERTIES_CLEAR_TEMPLATE" /></c:set>
                  <span class="d-flex my-auto">
                      <button type="button" class="btn btn-secondary p-1 m-1" data-toggle="collapse" data-target="#${namespace}-template" title="selectLabel">
                          <i class="glyphicons glyphicons-basic-chevron-down"></i>
                          <span class="sr-only">${selectLabel}</span>
                      </button>
                      <button type="button" class="btn btn-secondary selector-eraser p-1 m-1" title="${clearFilterLabel}">
                          <i class="halflings halflings-erase"></i>
                          <span class="sr-only">${clearFilterLabel}</span>
                      </button>
                  </span>
		</div>
				
		<div class="collapse" id="${namespace}-template">
			<div class="card my-2">
		  		<div class="card-body">		
					<div class="fancytree fancytree-selector"
						data-lazyloadingurl="${browseUrl}">
				

						<form:hidden path="templateId" cssClass="selector-value form-control ${status.error ? 'is-invalid' : ''}"/>
						<form:errors path="templateId" cssClass="invalid-feedback my-2" />
						<div class="d-flex align-items-center my-2">
							<op:translate key="MODIFY_FILTER" />
							<input type="text" data-expand="true" class="ml-2 flex-grow-1">
						</div>
					</div>
				</div>				
			</div>
		</div>
			
			
 		</spring:bind>			
	</div>
	
	

   
     
    <%--Buttons--%>
    <div class="text-right">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">
            <span><op:translate key="CANCEL" /></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_PAGE_PROPERTIES_VALIDATE_ACTION" /></span>
        </button>
    </div>     
	
</form:form>



