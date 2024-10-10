<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<portlet:defineObjects/>

<c:set var="namespace"><portlet:namespace/></c:set>

<portlet:actionURL name="home" var="homeUrl"/>
<portlet:actionURL name="switchMode" var="switchModeUrl" />
<portlet:actionURL name="submit" var="submitUrl"/>


<%--@elvariable id="status" type="org.osivia.portal.cms.portlets.edition.page.main.controller.EditionStatus"--%>
<%--@elvariable id="form" type="org.osivia.portal.cms.portlets.edition.page.main.controller.EditionForm"--%>

    <div class="d-flex justify-content-between me-3">
        <%--Home--%>
        <div class="d-flex text-truncate ">
            <div class="navbar navbar-expand">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a href="${homeUrl}" class="nav-link">
                            <i class="glyphicons glyphicons-basic-home"></i>
                         </a>
                    </li>
        
                    <c:forEach var="child" items="${status.breadcrumb}" varStatus="status">
                        <c:choose>
                            <c:when test="${not status.first and not status.last}">
                                 <li class="nav-item">
                                    <span class="text-secondary nav-link">/ ${child.name}</span>
                                </li>
                            </c:when>
                            <c:when test="${not status.first and status.last}">
                                 <li class="nav-item">
                                    <span class="text-secondary nav-link">/ ${child.name} [${child.id}]</span>
                                </li>
                            </c:when>                           
                        </c:choose>
                    </c:forEach>
                </ul>
  
                <%--Preview selector--%>
                <c:if test="${status.supportPreview && not status.liveSpace && status.modifiable}">
                    <div class="btn-group btn-group-sm">
                        <c:choose>
                            <c:when test="${status.preview and status.havingPublication}">
                                <a href="${switchModeUrl}" class="btn btn-success text-nowrap">
                                    <span>En ligne</span>
                                </a>
                                <a href="#" class="btn btn-warning text-nowrap">
                                    <span>&Eacute;dition</span>
                                </a>
                            </c:when>
        
                            <c:when test="${status.preview}">
                                <a href="#" class="btn btn-success text-nowrap">
                                    <span>En ligne</span>
                                </a>
                                <a href="#" class="btn btn-warning text-nowrap">
                                    <span>&Eacute;dition</span>
                                </a>
                            </c:when>
        
                            <c:otherwise>
                                <a href="#" class="btn btn-success text-nowrap">
                                    <span>En ligne</span>
                                </a>
                                <a href="${switchModeUrl}" class="btn btn-warning text-nowrap">
                                    <span>Edition</span>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
            
            </div>
        </div>

        
        <div class="d-flex border-start flex-shrink-0 me-5">
            <div class="navbar navbar-expand">        
                <%--Toolbar--%>
                ${status.toolbar}
            
            </div>
        </div>
        

    </div>
