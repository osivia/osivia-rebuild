<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />


<div class="cms-browser card">
	<div class="card-body">
		<c:choose>
			<c:when test="${not empty children}">

				<c:forEach var="child" items="${children}">

					<!-- Link -->
					<a href="/portal/content/${child.id}"> ${child.title} </a>
					</br>


				</c:forEach>

			</c:when>

		</c:choose>
	</div>
</div>
