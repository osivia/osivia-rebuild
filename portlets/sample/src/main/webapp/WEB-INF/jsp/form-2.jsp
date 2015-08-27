<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:renderURL var="backURL">
    <portlet:param name="tab" value="4"/>
    <portlet:param name="step" value="1"/>
</portlet:renderURL>

<portlet:actionURL name="submit2" var="actionURL">
    <portlet:param name="tab" value="4"/>
</portlet:actionURL>


<c:set var="tab" value="4" scope="request" />
<jsp:include page="nav.jsp" />


<h1 class="h4">Formulaire <small>Step #2</small></h1>

<form:form modelAttribute="sampleForm" action="${actionURL}" htmlEscape="false" method="post" cssClass="form-horizontal">
    <div class="form-group">
        <label class="control-label col-sm-3">First name</label>
        <div class="col-sm-9">
             <p class="form-control-static">${sampleForm.firstName}</p>
        </div>
    </div>
    
    <div class="form-group">
        <form:label path="lastName" cssClass="control-label col-sm-3">Last name</form:label>
        <div class="col-sm-9">
             <form:input path="lastName" cssClass="form-control" />
        </div>
    </div>
    
    <div class="form-group">
        <div class="col-sm-offset-3 col-sm-9">
            <a href="${backURL}" class="btn btn-default">Back</a>
            <button type="submit" class="btn btn-primary">Submit</button>
            <button type="reset" class="btn btn-default">Reset</button>
        </div>
    </div>
</form:form>
