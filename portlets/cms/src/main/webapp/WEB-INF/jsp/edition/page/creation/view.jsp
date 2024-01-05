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

<div class="portal-properties-page-creation">

<form:form action="${submitUrl}" method="post" modelAttribute="form"
	class="w-100">
	
	
	<%--Id--%>
    <c:set var="placeholder"><op:translate key="MODIFY_PAGE_CREATION_ID_PLACEHOLDER" /></c:set>
    <spring:bind path="id">
        <div class="form-group required">
            <form:label path="id"><op:translate key="MODIFY_PAGE_CREATION_ID_LABEL" /></form:label>
            
            <form:input path="id" cssClass="form-control ${status.error ? 'is-invalid' : ''}" placeholder="${placeholder}" />
            <form:errors path="id" cssClass="invalid-feedback" />
        </div>
    </spring:bind>
    
     <%--Display name--%>
    <c:set var="placeholder"><op:translate key="MODIFY_PAGE_CREATION_DISPLAY_NAME_PLACEHOLDER" /></c:set>
    <spring:bind path="displayName">
        <div class="form-group required">
            <form:label path="displayName"><op:translate key="MODIFY_PAGE_CREATION_DISPLAY_NAME_LABEL" /></form:label>
            
            <form:input path="displayName" cssClass="form-control ${status.error ? 'is-invalid' : ''}" placeholder="${placeholder}" />
            <form:errors path="displayName" cssClass="invalid-feedback" />
        </div>
    </spring:bind>
	
     
     
    <portlet:resourceURL id="browse" var="modelUrl">
       <portlet:param name="treeName" value="model"/>
    </portlet:resourceURL>
    
	<portlet:resourceURL id="browse" var="targetUrl">
	   <portlet:param name="treeName" value="target"/>
	</portlet:resourceURL>
	
	
	<div class="row ">
	   <div class="col-md-6 browse double-list">
	       <div class="card h-100">
               <div class="h-100 flex-column d-flex card-body">
                    <div class="card-title">
                        <h6><op:translate key="MODIFY_PAGE_CREATION_SELECT_MODEL" /></h6>
                    </div>   	       
	                <div class="form-group m-0 flex-grow-1 d-flex flex-column">
			             <div class="selector flex-grow-1">

                            <div class="mb-3">
                                <label for="${namespace}-filter" class="form-label"><op:translate key="MODIFY_FILTER" /></label>
                                <input id="${namespace}-filter" type="text" data-expand="true" class="filter-fancytree-by-selector form-control">
                            </div>
                                        
                            <div class="overflow-auto">  
                                <div class="fancy-scrollable">
								    <div class="fancytree fancytree-selector" data-lazyloadingurl="${modelUrl}" data-disabled="${form.noModel ? 'true' : 'false'}">
								        <form:hidden path="model" cssClass="selector-value form-control ${status.error ? 'is-invalid' : ''}"/>

								    </div>
                                </div>
                            </div>   
		                </div>

		                <div class="flex-grow-1 d-flex align-items-end">
                            <div class="form-check">
                                  <form:checkbox id="${namespace}-no-model" path="noModel" cssClass="form-check-input" data-toggle="fancytree"/>
                                  <label for="${namespace}-no-model" class="form-check-label"><op:translate key="MODIFY_PAGE_CREATION_NO_MODEL"/></label>
                            </div>
                         </div>		
	                
	               </div>
		        </div>
            </div>
		</div>	
       <div class="col-md-6 browse double-list">
            <div class="card h-100">

                <div class="h-100 flex-column d-flex card-body">
	                <div class="card-title">
	                    <h6><op:translate key="MODIFY_PAGE_CREATION_SELECT_TARGET" /></h6>
	                </div>     
	                <div class="form-group m-0 flex-grow-1 d-flex flex-column">           
     		            <div class="selector flex-grow-1">
			                 <spring:bind path="target">

			                     <form:hidden path="target" cssClass="selector-value form-control ${status.error ? 'is-invalid' : ''}"/>
			                     <form:errors path="target" cssClass="invalid-feedback" /> 
	
			                 </spring:bind>  

                            <div class="mb-3">
                                <label for="${namespace}-filter" class="form-label"><op:translate key="MODIFY_FILTER" /></label>
                                <input id="${namespace}-filter" type="text" data-expand="true" class="form-control">
                            </div>
                                        			                 
			                <div class="overflow-auto">  
		                        <div class="fancy-scrollable">
					                <div class="fancytree fancytree-selector" data-lazyloadingurl="${targetUrl}">

					                </div>
				                </div>
				            </div>    
			             </div>
		            </div>
		        </div>
            </div>
        </div>		
    </div>
     
    
    <%--Buttons--%>
    <div class="mt-3 float-end">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            <span><op:translate key="CANCEL" /></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_PAGE_PROPERTIES_VALIDATE_ACTION" /></span>
        </button>
    </div>     
	
</form:form>

</div>



