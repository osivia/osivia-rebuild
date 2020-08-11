<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:actionURL name="submit" var="submitUrl"/>

<form:form action="${submitUrl}" method="post">
      <button type="submit" name="add-page" class="btn btn-primary">Add page</button>
</form:form>
