<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<c:set var="namespace">
	<portlet:namespace />
</c:set>


<portlet:actionURL name="switchMode" var="switchModeURL" />
<portlet:actionURL name="home" var="homeURL" />
<portlet:actionURL name="publish" var="publishURL" />

<portlet:actionURL name="addPortlet" var="addPortletURL" />
<portlet:actionURL name="addPortletNav" var="addPortletNavURL" />
<portlet:actionURL name="addFolder" var="addFolderURL" />
<portlet:actionURL name="addDocument" var="addDocumentURL" />
<portlet:actionURL name="setACL" var="setACLURL" />
<portlet:actionURL name="reload" var="setReloadURL" />

<portlet:actionURL name="submit" var="submitUrl" />

<nav class="edition p-1">

	<form:form action="${submitUrl}" method="post" modelAttribute="form"
		class="row w-100">



		<div class="col-3  d-flex align-items-center">
       		<a class ="pr-2" href="${homeURL}">
				<i class="glyphicons glyphicons-basic-home"></i>
			</a>
			

			<c:if test="${status.supportPreview && not status.liveSpace && status.modifiable}">
				<c:if test="${status.preview}">
					<div class="btn-group">
						<c:if test="${status.havingPublication}">
							<a href="${switchModeURL}" class="btn btn-sm btn-light my-1"
								title="" data-toggle="tooltip" data-placement="bottom"
								data-original-title="En ligne"> <span>En ligne</span>
							</a>
						</c:if>
						<c:if test="${not status.havingPublication}">
							<span class="btn btn-sm btn-light text-muted my-1">En
								ligne</span>

						</c:if>
						<span class="btn btn-sm btn-warning my-1">Edition</span>
					</div>
				</c:if>

				<c:if test="${not status.preview}">
					<div class="btn-group">
						<span class="btn btn-sm btn-success   my-1">En ligne</span> <a
							href="${switchModeURL}" class="btn btn-sm btn-light my-1"
							title="" data-toggle="tooltip" data-placement="bottom"
							data-original-title="En ligne"> <span>Edition</span>
						</a>
					</div>
				</c:if>
			</c:if>
		</div>
		<div class="col-8  d-flex align-items-center">

			${status.toolbar}
			

<!--

			<c:if test="${not  status.supportPreview ||  status.preview}">
				<c:if test="${fn:containsIgnoreCase(status.subtypes, 'folder')}">
					<span> <a href="${addFolderURL}"
						class=" btn-sm btn-light m-1"> folder</a>
					</span>
				</c:if>
				<c:if test="${fn:containsIgnoreCase(status.subtypes, 'document')}">
					<span> <a href="${addDocumentURL}"
						class=" btn-sm btn-light m-1"> document</a>
					</span>
				</c:if>
			</c:if>


-->	

			

		</div>
		<div class="col-1  d-flex align-items-center flex-row-reverse p-0">
			<form:select path="locale" items="${status.locales}"
				data-change-submit="${namespace}-update-locale" />

			<input id="${namespace}-update-locale" type="submit"
				name="update-locale" class="d-none">
		</div>
		
		
	</form:form>

</nav>

