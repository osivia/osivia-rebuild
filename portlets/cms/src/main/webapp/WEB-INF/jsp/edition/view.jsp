<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:actionURL name="switchMode" var="switchModeURL" />
<portlet:actionURL name="publish" var="publishURL" />
<portlet:actionURL name="addPage" var="addPageURL" />
<portlet:actionURL name="addPortlet" var="addPortletURL" />
<portlet:actionURL name="addPortletNav" var="addPortletNavURL" />
<portlet:actionURL name="addFolder" var="addFolderURL" />
<portlet:actionURL name="addDocument" var="addDocumentURL" />



<nav class="navbar navbar-expand-lg navbar-light bg-secondary p-1">
	<c:if test="${status.supportPreview}">
		<c:if test="${status.preview}">
			<div class="btn-group">
				<c:if test="${status.havingPublication}">
					<a href="${switchModeURL}" class="btn btn-sm btn-light my-1" title=""
						data-toggle="tooltip" data-placement="bottom"
						data-original-title="En ligne"> <span>En ligne</span>
					</a> 
				</c:if>
				<c:if test="${not status.havingPublication}">
					 <span class="btn btn-sm btn-light text-muted my-1">En ligne</span>

				</c:if>				
				<span class="btn btn-sm btn-warning active my-1">Edition</span>
			</div>
		</c:if>

		<c:if test="${not status.preview}">
			<div class="btn-group">
				<span class="btn btn-sm btn-success   my-1">En ligne</span> <a
					href="${switchModeURL}" class="btn btn-sm btn-light my-1" title=""
					data-toggle="tooltip" data-placement="bottom"
					data-original-title="En ligne"> <span>Edition</span>
				</a>
			</div>
		</c:if>
	</c:if>
	
	<c:if test="${status.supportPreview}">
		<c:if test="${status.preview}">
			<span> <a href="${publishURL}" class=" btn-sm btn-light m-1"> publier</a>
			</span>
		</c:if>
	</c:if>		

	<c:if test="${status.pageEdition && ( not  status.supportPreview ||  status.preview ) }">
		<span> <a href="${addPageURL}" class=" btn-sm btn-light m-1">
				page</a>
		</span> <span> <a href="${addPortletURL}" class=" btn-sm btn-light m-1">
				portlet</a>
		</span> <span> <a href="${addPortletNavURL}" class=" btn-sm btn-light m-1">
				portlet(nav)</a>
		</span>
	</c:if>		

	<c:if test="${not  status.supportPreview ||  status.preview}">
		<c:if test="${fn:containsIgnoreCase(status.subtypes, 'folder')}">
			<span> <a href="${addFolderURL}" class=" btn-sm btn-light m-1">
				folder</a>
			</span>	
		</c:if>	
		<c:if test="${fn:containsIgnoreCase(status.subtypes, 'document')}">
			<span> <a href="${addDocumentURL}" class=" btn-sm btn-light m-1">
				document</a>
			</span>	
		</c:if>			
	</c:if>	
	
</nav>

