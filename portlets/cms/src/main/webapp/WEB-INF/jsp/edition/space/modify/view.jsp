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

<div class="portal-properties-administration">
	

	<row>
		<div class="col-md-12"> 	
		     <div class="form-group">
		         <label for="${namespace}-display-name"><op:translate
		                 key="USER_SAVED_SEARCHES_ADMINISTRATION_RENAME_DISPLAY_NAME"/></label>
		         <input id="${namespace}-display-name" type="text" name="displayName" class="form-control">
		     </div>
		</div>		     
    </row> 
    <row>
	     <div class="col-md-6">                    
	   		<%@ include file="profiles.jspf" %>	
		</div>
		 <div class="col-md-6">
		</div>
	</row>
	
</div>
