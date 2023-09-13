<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal"
	prefix="op"%>
<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<c:set var="namespace">
	<portlet:namespace />
</c:set>


<portlet:renderURL var="selectUrl">
	<portlet:param name="step" value="select-elements" />
</portlet:renderURL>

<portlet:resourceURL id="export" var="exportUrl">
	<portlet:param name="repositoryName" value="${repository.name}" />
</portlet:resourceURL>

<div class="h5">
   <span><op:translate key="MODIFY_REPOSITORY_MERGE_DOWNLOAD_LABEL"/></span>
</div>

<div class="card my-3 p-2">
   <span>${helpMessage}</span>
</div>


<div  class="mb-3 d-flex justify-content-center">
	<a href="${exportUrl}" class="btn btn-link  no-ajax-link d-flex">
       <i class="glyphicons glyphicons-basic-square-upload"></i>
		<op:translate key="MODIFY_REPOSITORY_MERGE_DOWNLOAD_LINK" />
	</a>
</div>
	


<%--Buttons--%>
<div class="float-end">
	<a href="${selectUrl}" class="btn btn-primary "> 
	   <span><op:translate key="MODIFY_REPOSITORY_MERGE_RETURN_TO_SELECT" /></span>
	</a>
</div>