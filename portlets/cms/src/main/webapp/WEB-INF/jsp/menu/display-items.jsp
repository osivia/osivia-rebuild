<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<c:set var="childLevel" value="${level + 1}" />




        <c:forEach var="child" items="${parent.children}">
            <!-- Selected item ? -->
            <c:remove var="selected" />
            <c:if test="${child.selected}">
                <c:set var="selected" value="selected" />
            </c:if>
            
            <!-- Current item ? -->
            <c:remove var="current" />
            <c:if test="${child.current or child.lastSelected}">
                <c:set var="current" value="active" />
            </c:if>
    
   
                   <!-- Link -->
                <a href="${child.url}" class="list-group-item ${selected} ${current} pl-${level+1}">            
                    ${child.title}
                </a>     
            

                    
                <c:if test="${not empty child.children}">
                    <c:set var="parent" value="${child}" scope="request" />
                    <c:set var="level" value="${childLevel}" scope="request" />

                    	<jsp:include page="display-items.jsp" />

                </c:if>

        </c:forEach>


