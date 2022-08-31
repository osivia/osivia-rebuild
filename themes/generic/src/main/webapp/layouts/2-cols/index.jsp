<%@ page import="org.jboss.portal.server.PortalConstants" %>
<%@ page import="java.util.ResourceBundle"%>
<%@ taglib uri="portal-layout" prefix="p" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>

<% ResourceBundle rb = ResourceBundle.getBundle("Resource", request.getLocale()); %>
<!DOCTYPE html>
<html>
<head>

    <p:theme themeName="generic" resourceType="link"/>
	<p:headerContent />
    <p:theme themeName="generic" resourceType="script"/>

</head>

<!--<op:translate key="CUSTOMIZER_MSG_1" />-->

<body class="fullheight overflow-hidden d-flex flex-column">

	<div class="layout">
		<div class="row">
		   <div class="col">
		  		<p:region regionName='toolbar' regionID='toolbar'/>
		    </div>
		 </div>  
		<div class="row">
		   <div class="col">
		  		<%@include file="../includes/content-navbar.jsp" %>
		    </div>
		 </div>  		 		
		 <div class="row">
		   <div class="col-md-4">
		   		<p:region regionName='nav' regionID='nav'/>
		   </div>
		   <div class="col-md-8">
			
				 <div class="row">
				   <div class="col-md-6">
				   		<p:region regionName='col-1' regionID='col-1'/>
				   </div>
				   <div class="col-md-6">
				   		<p:region regionName='col-2' regionID='col-2'/>
				   </div>				   
				</div>	
			</div>    
		  </div>

	</div>
	           
	<%@include file="../includes/footer.jsp" %>


</body>
</html>
