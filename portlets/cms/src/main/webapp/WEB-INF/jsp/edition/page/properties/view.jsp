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

<form:form action="${submitUrl}" method="post" modelAttribute="form"
	class="w-100">
	
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



