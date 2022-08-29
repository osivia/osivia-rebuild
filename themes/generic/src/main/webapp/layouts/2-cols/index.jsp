<%@ page import="org.jboss.portal.server.PortalConstants" %>
<%@page import="java.util.ResourceBundle"%>
<%@ taglib uri="portal-layout" prefix="p" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>

<% ResourceBundle rb = ResourceBundle.getBundle("Resource", request.getLocale()); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><p:title default="<%= PortalConstants.VERSION.toString() %>"/></title>
    <meta http-equiv="Content-Type" content="text/html;"/>
	<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />   


    <p:theme themeName="renewal" resourceType="link"/>
	<p:headerContent />
    <p:theme themeName="renewal" resourceType="script"/>

    <p:region regionName='AJAXScripts' regionID='AJAXScripts'/>
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


<p:region regionName='AJAXFooter' regionID='AJAXFooter'/>

</body>
</html>
