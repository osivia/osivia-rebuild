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

<div>
	<c:if test="${status.supportPreview}">
		<c:if test="${status.preview}">
			<div class="btn-group">
				<c:if test="${status.havingPublication}">
					<a href="${switchModeURL}" class="btn btn-default" title=""
						data-toggle="tooltip" data-placement="bottom"
						data-original-title="En ligne"> <span>En ligne</span>
					</a> 
				</c:if>
				<c:if test="${not status.havingPublication}">
					<a href="#" class="btn btn-default disabled" title=""
						data-toggle="tooltip" data-placement="bottom"
						data-original-title="En ligne"> <span>En ligne</span>
					</a> 
				</c:if>				
				<span class="btn btn-warning active">Edition</span>
			</div>
		</c:if>

		<c:if test="${not status.preview}">
			<div class="btn-group">
				<span class="btn btn-success">En ligne</span> <a
					href="${switchModeURL}" class="btn btn-default" title=""
					data-toggle="tooltip" data-placement="bottom"
					data-original-title="En ligne"> <span>Edition</span>
				</a>
			</div>
		</c:if>
	</c:if>
	
	<c:if test="${status.supportPreview}">
		<c:if test="${status.preview}">
			<span> <a href="${publishURL}" class="btn btn-default"> publier</a>
			</span>
		</c:if>
	</c:if>		

	<c:if test="${status.pageEdition && ( not  status.supportPreview ||  status.preview ) }">
		<span> <a href="${addPageURL}" class="btn btn-default">+
				page</a>
		</span> <span> <a href="${addPortletURL}" class="btn btn-default">
				portlet</a>
		</span> <span> <a href="${addPortletNavURL}" class="btn btn-default">
				portlet(nav)</a>
		</span>
	</c:if>		

	<c:if test="${not  status.supportPreview ||  status.preview}">
		<c:if test="${fn:containsIgnoreCase(status.subtypes, 'folder')}">
			<span> <a href="${addFolderURL}" class="btn btn-default">
				folder</a>
			</span>	
		</c:if>	
		<c:if test="${fn:containsIgnoreCase(status.subtypes, 'document')}">
			<span> <a href="${addDocumentURL}" class="btn btn-default">
				document</a>
			</span>	
		</c:if>			
	</c:if>	
	
</div>

