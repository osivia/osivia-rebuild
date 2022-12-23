<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal"
	prefix="op"%>
<%@ taglib uri="http://www.toutatice.fr/jsp/taglib/toutatice"
	prefix="ttc"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>


<portlet:defineObjects />

<c:set var="namespace">
	<portlet:namespace />
</c:set>

<portlet:resourceURL id="browse" var="browseUrl">
</portlet:resourceURL>


<portlet:actionURL name="move" var="url" />

<form:form action="${url}" method="post" modelAttribute="form">

	
	<div class="move selector">
		<spring:bind path="target">
			<div class="fancytree fancytree-selector"
				data-lazyloadingurl="${browseUrl}">
				<p><op:translate key="MODIFY_MOVE_SELECT_PAGE" /></p>
				<form:hidden path="target" cssClass="selector-value form-control ${status.error ? 'is-invalid' : ''}"/>
				<form:errors path="target" cssClass="invalid-feedback my-2" />
				<div class="d-flex align-items-center my-2">
					<op:translate key="MODIFY_FILTER" />
					<input type="text" data-expand="true" class="ml-2 flex-grow-1">
				</div>
			</div>
 		</spring:bind>			
	</div>


    <%--Buttons--%>
    <div class="float-end">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            <span><op:translate key="CANCEL" /></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="MODIFY_MOVE_ACTION" /></span>
        </button>
    </div>
</form:form>