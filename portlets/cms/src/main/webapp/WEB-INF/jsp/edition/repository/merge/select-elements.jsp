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

<portlet:actionURL name="select" var="submitUrl" />

<div class="h5">
   <span><op:translate key="MODIFY_REPOSITORY_MERGE_ELEMENT_SELECTIONS_LABEL"/></span>
</div>

<form:form action="${submitUrl}" modelAttribute="form" >
    
    
    <div class="row mt-5">
	    <div class="form-group col-md-6">
	     <div class="form-check">
	         <form:checkbox path="mergeParams.mergeProfiles" cssClass="form-check-input" />
	         <form:label path="mergeParams.mergeProfiles" cssClass="form-check-label" ><op:translate key="MODIFY_REPOSITORY_MERGE_PROFILES_LABEL" /></form:label>
	      </div>
	    </div>
	    
	    <div class="form-group col-md-6">
	     <div class="form-check">
	         <form:checkbox path="mergeParams.mergeStyles" cssClass="form-check-input" />
	         <form:label path="mergeParams.mergeStyles" cssClass="form-check-label" ><op:translate key="MODIFY_REPOSITORY_MERGE_STYLES_LABEL" /></form:label>
	      </div>
	    </div>
    </div>
    
    <div class="row hr my-2">
        <hr>
    </div>
    

    <c:forEach var="page" items="${pages}" varStatus="pageStatus">
         <c:if test="${pageStatus.count %2 == 1}">
                <div class="row mt-3">
         </c:if>

                     <div class="form-group col-md-6">
                         <div class="form-check text-truncate">
                             <form:checkbox path="mergedPages" value="${page.key}" cssClass="form-check-input" />
                             <div class="d-flex w-100">
                                <div class="col-6 text-truncate ">${page.value}</div>
                                <div class="col-6 text-truncate text-muted">[${page.key}]</div>
                             </div>
                          </div>
                      </div>
				      
         <c:if test="${ (pageStatus.count %2 == 0) || (pageStatus.count == fn:length(pages))}">

                </div>
         </c:if>
       
    </c:forEach>
    
    <%--Buttons--%>
    <div class="float-end">
        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_REPOSITORY_MERGE_ELEMENT_SELECTIONS_VALIDATE_LABEL" /></span>
        </button>
    </div> 


    
    
</form:form>


