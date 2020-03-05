<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:actionURL name="add" var="addURL">
    <portlet:param name="count" value="${count + 1}"/>
</portlet:actionURL>

<portlet:actionURL name="startWindow" var="startWindowURL">

</portlet:actionURL>


<c:set var="tab" value="1" scope="request" />
<jsp:include page="nav.jsp" />


<h1 class="h4">Onglet #1</h1>

<p>  foo : ${foo} </p>

<div class="input-group">

    <input type="text" value="${count}" class="form-control" disabled="disabled">
    <span class="input-group-btn">
       <a href="${addURL}" class="btn btn-default">Ajouter</a>
    </span>
</div>    
<div class="input-group">    
     <span >
       <a href="${startWindowURL}" class="btn btn-default">startWindow</a>
    </span>
   
    
</div>
