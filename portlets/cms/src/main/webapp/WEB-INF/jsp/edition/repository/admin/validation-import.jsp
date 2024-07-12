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

<portlet:actionURL name="confirm-import" var="confirmImportUrl" />
<portlet:actionURL name="cancel-import" var="cancelImportUrl" />

<div class="h5">
   <span><op:translate key="MODIFY_REPOSITORY_IMPORT_RESULT_LABEL"/></span>
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
                   <a href="javascript:" class="input-confirmation-modal btn btn-primary  no-ajax-link d-flex" data-bs-toggle="modal" data-bs-target="#${namespace}-confirmation-modal">
                        <op:translate key="MODIFY_REPOSITORY_IMPORT_FILE_LABEL" />
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
                <a href="${cancelImportUrl}" class="btn btn-primary  no-ajax-link d-flex" >
                     <op:translate key="MODIFY_REPOSITORY_IMPORT_RETURN" />
               </a>
             </div>
                    
        </c:otherwise>
    </c:choose>
    



    


<%--Confirmation modal--%>
<div id="${namespace}-confirmation-modal" class="modal fade" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <span><op:translate key="MODIFY_REPOSITORY_IMPORT_CONFIRM_TITLE"/></span>
                </h5>            
            </div>
            <div class="modal-body">
                <form action="${confirmImportUrl}"  method="post">
                    <input type="submit" name="save"  class="d-none">
                    
                    <p>
                        ${form.checkedItems.confirmationMessage}
                    </p>

                    <div class="d-flex justify-content-center gap-2">
                        <button type="button" class="btn btn-primary" onclick="importModalSubmit(this);">
                            <span><op:translate key="MODIFY_REPOSITORY_IMPORT_CONFIRM"/></span>
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
