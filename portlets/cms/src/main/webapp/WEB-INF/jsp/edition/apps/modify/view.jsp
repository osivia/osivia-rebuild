<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<portlet:defineObjects/>

<c:set var="namespace"><portlet:namespace/></c:set>


<portlet:actionURL name="modifyPortlet" var="url"/>

<%--@elvariable id="form" type="org.osivia.portal.cms.portlets.edition.page.apps.modify.controller.ModifyForm"--%>
<%--@elvariable id="stylesList" type="java.util.List"--%>
<%--@elvariable id="layoutGroups" type="java.util.List"--%>
<%--@elvariable id="layoutGroup" type="org.osivia.portal.api.ui.layout.LayoutGroup"--%>
<form:form action="${url}" method="post" modelAttribute="form">
    <%--Title--%>
    <div class="mb-3">
        <c:set var="placeholder"><op:translate key="MODIFY_PORTLET_TITLE"/></c:set>
        <form:label path="title" cssClass="form-label"><op:translate key="MODIFY_PORTLET_TITLE"/></form:label>
        <form:input path="title" cssClass="form-control" placeholder="${placeholder}" htmlEscape="true"/>
    </div>

    <%--Display options--%>
    <div class="mb-3">
        <div class="form-check">
            <form:checkbox id="${namespace}-display-title" path="displayTitle" data-toggle="fancytree" cssClass="form-check-input"/>
            <label for="${namespace}-display-title" class="form-check-label"><op:translate key="MODIFY_PORTLET_DISPLAY_TITLE"/></label>
        </div>
        <div class="form-check">
            <form:checkbox id="${namespace}-display-panel" path="displayPanel" cssClass="form-check-input"/>
            <label for="${namespace}-display-panel" class="form-check-label"><op:translate key="MODIFY_PORTLET_DISPLAY_PANEL"/></label>
        </div>
        <div class="form-check">
            <form:checkbox id="${namespace}-hide-if-empty" path="hideIfEmpty" cssClass="form-check-input"/>
            <label for="${namespace}-hide-if-empty" class="form-check-label"><op:translate key="MODIFY_PORTLET_DISPLAY_HIDE_IF_EMPTY"/></label>
        </div>
    </div>

    <%--Styles--%>
    <div class="mb-3">
        <form:label path="styles" cssClass="form-label"><op:translate key="MODIFY_PORTLET_STYLES"/></form:label>
        <c:choose>
            <c:when test="${empty stylesList}">
                <div class="form-text">
                    <span><op:translate key="MODIFY_PORTLET_NO_STYLE_AVAILABLE"/></span>
                </div>
            </c:when>
            <c:otherwise>
                <div>
                    <c:forEach var="style" items="${stylesList}" varStatus="status">
                        <div class="form-check form-check-inline">
                            <form:checkbox id="${namespace}-style-${status.index}" path="styles" value="${style}" cssClass="form-check-input"/>
                            <label for="${namespace}-style-${status.index}" class="form-check-label">${style}</label>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <%--Styles not implemented --%>
    <c:if test="${not empty form.unimplementedStyles}">
	    <div class="mb-3">
	        <form:label path="unimplementedStyles" cssClass="form-label"><op:translate key="MODIFY_PORTLET_UNIMPLEMENTED_STYLES"/></form:label>
	           <div>
	               <c:forEach var="style" items="${form.unimplementedStyles}" varStatus="status">
	                   <div class="form-check form-check-inline">
	                       <form:checkbox id="${namespace}-style-${status.index}" path="unimplementedStyles" value="${style}" cssClass="form-check-input"/>
	                       <label for="${namespace}-style-${status.index}" class="form-check-label">${style}</label>
	                   </div>
	               </c:forEach>
	           </div>
	    </div>    
    </c:if>
    
    <%--Linked layout item--%>
    <c:if test="${not empty layoutGroups}">
        <div class="mb-3">
            <form:label path="linkedLayoutItemId" cssClass="form-label"><op:translate key="MODIFY_PORTLET_LINKED_LAYOUT_ITEM"/></form:label>
            <form:select path="linkedLayoutItemId" cssClass="form-select" disabled="${not form.supportTabSelection}">
                <form:option value=""><op:translate key="MODIFY_PORTLET_LINKED_LAYOUT_ITEM_OPTION_EMPTY"/></form:option>
                    <c:forEach var="layoutGroup" items="${layoutGroups}">
                        <optgroup label="${layoutGroup.label}">
                            <c:forEach var="layoutItem" items="${layoutGroup.items}">
                                <form:option value="${layoutItem.id}">${layoutItem.label}</form:option>
                            </c:forEach>
                        </optgroup>
                    </c:forEach>
            </form:select>
            <div class="form-text"><op:translate key="MODIFY_PORTLET_LINKED_LAYOUT_ITEM_HELP"/></div>
        </div>
    </c:if>

    <div class="mb-3">
        <form:label path="profiles" cssClass="form-label"><op:translate key="MODIFY_PORTLET_CONDITION_DISPLAY_LABEL" /></form:label>
        <div class="form-group row">
            <c:forEach var="profile" items="${profilesList}">
                <div class="col-md-6">
	                <div class="form-check">
	                    <form:checkbox path="profiles" value="${profile.name}" cssClass="form-check-input"/>
	                    <form:label path="profiles" cssClass="form-check-label" >${profile.name}</form:label>
	                </div>
               </div>
            </c:forEach>
        </div>
    </div>        

    <%--Buttons--%>
    <div class="text-end">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            <span><op:translate key="CANCEL"/></span>
        </button>

        <button type="submit" name="save" class="btn btn-primary">
            <span><op:translate key="MODIFY_PORTLET_LABEL"/></span>
        </button>
    </div>
</form:form>
