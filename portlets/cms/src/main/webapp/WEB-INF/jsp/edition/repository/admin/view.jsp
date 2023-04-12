<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<c:set var="namespace">
	<portlet:namespace />
</c:set>



<portlet:actionURL name="submit" var="submitUrl" />

<form:form action="${submitUrl}" method="post" modelAttribute="form" enctype="multipart/form-data"
	class="w-100">
	
	<ul class="list-unstyled">
		<c:forEach var="repository" items="${form.repositories}" varStatus="status">
			<li>
				<div class="card mb-2">
	                <div class="card-body p-2">
	                    <div class="d-flex align-items-center">
	
	                        <div class="flex-grow-1 my-1 mx-2">
	                            <h5 class="card-title mb-1"><c:out value="${repository.name}" /></h5>
		                            <div class="row">

 
		                                <div class="col-sm-2 d-flex">
		                                	<c:if test="${repository.streamable}">
		                            
				                                <portlet:resourceURL id="export" var="exportUrl">
		        									<portlet:param name="repositoryName" value="${repository.name}"/>
		   										</portlet:resourceURL>	
		   											                                	
				                                <a href="${exportUrl}" class="btn btn-link  btn-sm mb-1 ml-1  no-ajax-link text-truncate d-flex">
				                                  
				                                                    <i class="glyphicons glyphicons-basic-square-upload"></i>
				                                                    <strong class="d-inline"><op:translate key="MODIFY_REPOSITORY_EXPORT_LABEL"/></strong>
				                                </a>
			                                </c:if>
		                                </div>
		                                
		                                <div class="col-sm-2 d-flex">
		                                	<c:if test="${repository.streamable}">
									           <div>
									               <form:input type="file" path="fileUpload['${repository.name}']" cssClass="file-import"
									                           data-import-submit-id="${namespace}-file" data-import-repository-name-id="${namespace}-repositoryName" data-repository-name="${repository.name}" value="${browse}" />
					                                <a href="#" class="btn btn-link  btn-sm mb-1 ml-1  no-ajax-link text-truncate d-flex">
					                                  
					                                                    <i class="glyphicons glyphicons-basic-square-download"></i>
					                                                    <strong class="d-inline"><op:translate key="MODIFY_REPOSITORY_IMPORT_LABEL"/></strong>
					                                </a>		

									           </div>
		                                	</c:if>
		                                </div>
		                                
		                                <div class="col-sm-2 d-flex">
                                            <c:if test="${repository.streamable}">
                                               <div>
                                                    <a href="${repository.mergeUrl}" class="btn btn-link  btn-sm mb-1 ml-1   text-truncate d-flex">
                                                      
                                                                        <i class="glyphicons glyphicons-basic-axes-three-dimensional"></i>
                                                                        <strong class="d-inline"><op:translate key="MODIFY_REPOSITORY_MERGE_LABEL"/></strong>
                                                    </a>        
                                               </div>
                                            </c:if>
                                        </div>
		                                
		                            </div>
	                        </div>
	                    </div>
	                </div>
	            </div>	
			</li>
		</c:forEach>
	</ul>
	
	<input id="${namespace}-file" type="submit" name="upload-file" class="d-none">
	<input id="${namespace}-repositoryName" type="hidden" name="repositoryName" >
	
</form:form>



