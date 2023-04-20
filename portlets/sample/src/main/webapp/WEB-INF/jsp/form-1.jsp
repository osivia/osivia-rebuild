<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:actionURL name="submit1" var="actionURL">
    <portlet:param name="tab" value="4"/>
</portlet:actionURL>


<c:set var="tab" value="4" scope="request" />
<jsp:include page="nav.jsp" />

<div>
<h1 class="h4">Formulaire <small>Step #1</small></h1>


<form action="${actionURL}" method="post">
    <button type="submit">Bouton</button>
    <input type="submit" value="Input">
</form>


<form:form modelAttribute="sampleForm" htmlEscape="false" action="${actionURL}" method="post" cssClass="form-horizontal" role="form">
    <div class="form-group">
        <form:label path="firstName" cssClass="control-label col-sm-3">First name</form:label>
        <div class="col-sm-9">
             <form:input path="firstName" cssClass="form-control" />
        </div>
    </div>
    
    <div class="form-group">
        <div class="col-sm-offset-3 col-sm-9">
            <form:button>Next</form:button>
            <button type="submit" class="btn btn-primary">Next</button>
            <button type="reset" class="btn btn-default">Reset</button>
        </div>
    </div>
</form:form>

<p>
    <a href="${actionURL}" class="btn btn-danger">Test</a>
</p>

</div>