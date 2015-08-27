<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<portlet:renderURL var="tab1URL">
    <portlet:param name="tab" value="1"/>
</portlet:renderURL>

<portlet:renderURL var="tab2URL">
    <portlet:param name="tab" value="2"/>
</portlet:renderURL>

<portlet:renderURL var="tab3URL">
    <portlet:param name="tab" value="3"/>
</portlet:renderURL>

<portlet:renderURL var="tab4URL">
    <portlet:param name="tab" value="4"/>
    <portlet:param name="step" value="1"/>
</portlet:renderURL>


<ul class="nav nav-tabs">
    <li
        <c:if test="${empty tab or tab eq 1}">class="active"</c:if>
    >
        <a href="${tab1URL}">Onglet #1</a>
    </li>
    
    <li
        <c:if test="${tab eq 2}">class="active"</c:if>
    >
        <a href="${tab2URL}">Onglet #2</a>
    </li>
    
    <li
        <c:if test="${tab eq 3}">class="active"</c:if>
    >
        <a href="${tab3URL}">Onglet #3</a>
    </li>
    
    <li
        <c:if test="${tab eq 4}">class="active"</c:if>
    >
        <a href="${tab4URL}">Formulaire</a>
    </li>
</ul>
