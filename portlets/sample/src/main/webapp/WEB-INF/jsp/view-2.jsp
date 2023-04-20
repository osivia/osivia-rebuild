<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:actionURL name="foo" var="fooUrl" copyCurrentRenderParameters="true" />


<c:set var="tab" value="2" scope="request" />
<jsp:include page="nav.jsp" />

<div>
	<h1 class="h4">Onglet #2</h1>
	
	
	<div>
	    <a href="${fooUrl}" class="btn btn-default">Foo</a>
	</div>
</div>
