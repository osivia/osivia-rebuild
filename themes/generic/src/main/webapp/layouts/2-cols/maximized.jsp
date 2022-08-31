<%@ page import="org.jboss.portal.server.PortalConstants" %>
<%@page import="java.util.ResourceBundle"%>
<%@ taglib uri="portal-layout" prefix="p" %>
<% ResourceBundle rb = ResourceBundle.getBundle("Resource", request.getLocale()); %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

    <p:theme themeName="generic" resourceType="link"/>
	<p:headerContent />
    <p:theme themeName="generic" resourceType="script"/> 
</head>

<body class="fullheight overflow-hidden d-flex flex-column">

<p:region regionName='AJAXScripts' regionID='AJAXScripts'/>

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
				   <div class="col">
				   		<p:region regionName='maximized'/>
				   </div>
		   
				</div>	
			</div>    
		  </div>

	</div>
<%@include file="../includes/footer.jsp" %>


<p:region regionName='AJAXFooter' regionID='AJAXFooter'/>

</body>
</html>
