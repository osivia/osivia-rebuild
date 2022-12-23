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
	
	<div class="form-group mt-2">
        <form:label path="profiles"><op:translate key="MODIFY_PAGE_ACLS_PROFILES_LABEL" /></form:label>
		<div class="form-group">
			<c:forEach var="profile" items="${profilesList}">
				<div class="form-check">
			    	<form:checkbox path="profiles" value="${profile.name}" cssClass="form-check-input"/>
			    	<form:label path="profiles" cssClass="form-check-label" >${profile.name}</form:label>
			   </div>
			</c:forEach>
		</div>
	</div>	

    <%--Buttons--%>
    <div class="float-end">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            <span><op:translate key="CANCEL" /></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_PAGE_ACLS_VALIDATE_ACTION" /></span>
        </button>
    </div>     
	
</form:form>



