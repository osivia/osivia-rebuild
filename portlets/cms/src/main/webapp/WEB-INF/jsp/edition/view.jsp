<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:actionURL name="switchMode" var="switchModeURL" />
<portlet:actionURL name="addPage" var="addPageURL" />
<portlet:actionURL name="addPortlet" var="addPortletURL" />
<portlet:actionURL name="addPortletNav" var="addPortletNavURL" />

<div>
	<c:if test="${status.supportPreview}">
		<c:if test="${status.preview}">
			<div class="btn-group">
				<a href="${switchModeURL}" class="btn btn-default" title=""
					data-toggle="tooltip" data-placement="bottom"
					data-original-title="En ligne"> <span>En ligne</span>
				</a> <span class="btn btn-warning active">Edition</span>
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

	<span> <a href="${addPageURL}" class="btn btn-default">+
			page</a>
	</span> <span> <a href="${addPortletURL}" class="btn btn-default">+
			portlet</a>
	</span> <span> <a href="${addPortletNavURL}" class="btn btn-default">+
			portlet(nav)</a>
	</span>
</div>

