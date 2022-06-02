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


<portlet:actionURL name="submit" var="submitUrl" />

<form:form action="${submitUrl}" method="post">

<portlet:actionURL name="deleteContent" var="deletePortletURL"/>


<div>
     <p class="mb-3"><op:translate key="CONFIRM_DELETE" /></p>
     
     <div class="text-right">
		 <button type="button" class="btn btn-secondary" data-dismiss="modal">
            <span><op:translate key="CANCEL" /></span>
     	 </button>     
          <a href="${deletePortletURL}" class="btn btn-primary ml-2">
               <span> <op:translate key="CONFIRM" /></span>
          </a>
	</div>          
</div>



</form:form>



    
        

        