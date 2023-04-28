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

<div class="card my-3 p-2">
   <span>${helpMessage}</span>
</div>

<portlet:actionURL name="submit" var="submitUrl" />

<form:form action="${submitUrl}" method="post" modelAttribute="form" enctype="multipart/form-data">
	

     <div  class="mb-3 d-flex justify-content-center">
         <form:input type="file" path="fileUpload" cssClass="file-merge"
                     data-merge="${namespace}-file" />
         <a href="#" class="btn btn-link  no-ajax-link d-flex">
             <i class="glyphicons glyphicons-basic-square-download"></i>
             <op:translate key="MODIFY_REPOSITORY_MERGE_CHOOSE_FILE"/>
         </a>		
     </div>
                         	
 	
	 <input id="${namespace}-file" type="submit" name="upload-file" class="d-none">
	
</form:form>



