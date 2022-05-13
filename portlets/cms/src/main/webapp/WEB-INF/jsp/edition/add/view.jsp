<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<c:set var="namespace">
	<portlet:namespace />
</c:set>

XXXXXXXXXXXXXXXXXXXXXxx


<portlet:actionURL name="addPortlet" var="addPortletURL" />
<portlet:actionURL name="submit" var="submitUrl" />

	<form:form action="${submitUrl}" method="post" modelAttribute="form"
		class="row w-100">

	
				<span> <a href="${addPortletURL}"
					class=" btn-sm btn-light m-1"> portlet</a>
				</span>
				
	</form:form>

</nav>

