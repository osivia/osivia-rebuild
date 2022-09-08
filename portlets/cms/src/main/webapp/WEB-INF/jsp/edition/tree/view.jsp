<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%> <%@
taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> <%@ taglib
prefix="form" uri="http://www.springframework.org/tags/form"%> <%@
taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> <%@
taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"
%> <%@ taglib uri="http://www.toutatice.fr/jsp/taglib/toutatice"
prefix="ttc" %> <%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<c:set var="namespace">
	<portlet:namespace />
</c:set>

<portlet:resourceURL id="browse" var="browseUrl">
</portlet:resourceURL>


<div class="browse">
	<div class="fancytree fancytree-links"
		data-lazyloadingurl="${browseUrl}">
		<div class="d-flex align-items-center mb-2">
			<op:translate key="MODIFY_FILTER" />
			<input type="text" data-expand="true" class="ml-2 flex-grow-1">
		</div>
	</div>
</div>


