<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>


<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<c:set var="namespace"><portlet:namespace /></c:set>

<portlet:resourceURL id="browse" var="browseUrl"/>


<div class="browse">
	<div class="fancytree fancytree-links" data-lazyloadingurl="${browseUrl}">
		<div class="mb-3">
			<label for="${namespace}-filter" class="form-label"><op:translate key="MODIFY_FILTER" /></label>
			<input id="${namespace}-filter" type="text" data-expand="true" class="form-control">
		</div>
	</div>
</div>


