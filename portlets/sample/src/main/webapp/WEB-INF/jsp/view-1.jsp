<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>

<%@ page contentType="text/html" isELIgnored="false"%>



<portlet:defineObjects />

<portlet:actionURL name="add" var="addURL">
    <portlet:param name="count" value="${count + 1}"/>
</portlet:actionURL>

<portlet:actionURL name="startWindow" var="startWindowURL">
</portlet:actionURL>

<portlet:actionURL name="refresh" var="refreshSafranCommand">
</portlet:actionURL>


<portlet:actionURL name="goToPage" var="goToPageURL">
</portlet:actionURL>
<portlet:actionURL name="throwException" var="throwExceptionURL">
</portlet:actionURL>
<portlet:renderURL portletMode="view" windowState="maximized" var="maximizedURL">
</portlet:renderURL>
<portlet:renderURL portletMode="view" windowState="normal" var="normalURL">
</portlet:renderURL>



<c:set var="tab" value="1" scope="request" />
<jsp:include page="nav.jsp" />

<div>

<h1 class="h4">Onglet #1</h1>



<p>  foo : ${foo} </p>

<p>  user : ${user} </p>

<p> <op:translate key="SAMPLE_MSG_1" /> <op:translate key="CUSTOMIZER_MSG_1" /></p>

<div class="input-group">

    <input type="text" value="${count}" class="form-control" disabled="disabled">
    <span class="input-group-btn ">
       <a href="${addURL}" class="btn sample-test">Ajouter</a>
    </span>
</div>    
<div class="p-2">    
     <span >
       <a href="${startWindowURL}" class="btn btn-secondary">startWindow</a>
    </span>
      <span >
       <a href="${pageURL}" class="btn btn-secondary">goToPageA</a>
    </span>  
        <span >
       <a href="${throwExceptionURL}" class="btn btn-secondary">Exception</a>
    </span>  
     <span >
       <a href="${maximizedURL}" class="btn btn-secondary">maximized</a>
    </span>  
         <span >
       <a href="${normalURL}" class="btn btn-secondary">normal</a>
    </span>   
   <span >
       <a href="${docURL}" class="btn btn-secondary">goToDoc1</a>
    </span>  
    
    <span >
       <a href="${startWindowCommand}" class="btn btn-secondary">startWindowCommand</a>
    </span>
    
        <span >
       <a href="${refreshSafranCommand}" class="btn btn-secondary">refresh</a>
    </span>
    
     
    <a class="btn btn-link" href="javascript:" title="Popup" data-load-url="${openInPopupUrl}" data-title="Popup" data-target="#osivia-modal" data-reload-on-close="true">
      popup
    </a>
   
    
</div>

</div>


	
