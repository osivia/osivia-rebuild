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

<form:form action="${url}" method="post" modelAttribute="form">



<div class="portal-properties-administration">


	<div class="d-flex flex-row-reverse my-2">
	
	 	<button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_SPACE_APPLY_LABEL" /></span>
        </button>
	
	</div>
	
   <div data-ajax-region="portal-properties-administration-vars-region">
 	    <input type="hidden" name="sortSrc"/>
		<input type="hidden" name="sortTarget"/>
		<input type="hidden" name="formAction"/>  
   </div>
    
    <div class="row">
	     <div class="col-md-6">                    
	   		<%@ include file="profiles.jspf" %>	
		</div>
		 <div class="col-md-6">
		 	<%@ include file="styles.jspf" %>	
		</div>
	</div>
	
</div>
    <div class="d-flex flex-row-reverse my-2">
    
        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_SPACE_APPLY_LABEL" /></span>
        </button>
    
    </div>
		<input type="submit" class="d-none">	
</form:form>
