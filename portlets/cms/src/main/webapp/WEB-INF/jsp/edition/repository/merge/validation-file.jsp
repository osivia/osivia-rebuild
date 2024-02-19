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

<portlet:actionURL name="confirm-validation" var="confirmValidationUrl" />
<portlet:actionURL name="cancel-validation" var="cancelValidationtUrl" />

<div class="h5">
   <span><op:translate key="MODIFY_REPOSITORY_MERGE_FILE_VALIDATION_LABEL"/></span>
</div>


    <c:choose>
        <c:when test="${form.checkedItems.ok}">
            <div class="m-3 d-flex justify-content-center">
                  <span class="text-success fw-bold m-3">
                    <op:translate key="MODIFY_REPOSITORY_VALIDATION_RESULT_NO_ERROR"/>
                  </span>
            </div>        
            
            
            <%@ include file="../include/validation-report.jspf" %>
            
            <div  class="m-3 d-flex justify-content-center">
                       
               <a href="${confirmValidationUrl}" class="btn btn-primary  no-ajax-link d-flex" >
                     <op:translate key="MODIFY_REPOSITORY_MERGE_NEXT_STEP" />
               </a>
            </div>
        </c:when>
        
        <c:otherwise>
            <div class="m-3 d-flex justify-content-center">
	              <div>
	                 <div> 
		                 <span class="text-danger fw-bold m-3">
		                       <op:translate key="MODIFY_REPOSITORY_VALIDATION_RESULT_ERRORS"/>
		                 </span>
	                 </div>
                 </div>
             </div> 
            
            <%@ include file="../include/validation-report.jspf" %>
                 
             <div  class="m-3 d-flex justify-content-center">
                <a href="${cancelValidationtUrl}" class="btn btn-primary  no-ajax-link d-flex" >
                     <op:translate key="MODIFY_REPOSITORY_MERGE_RETURN" />
               </a>
             </div>
                    
        </c:otherwise>
    </c:choose>
    

    

