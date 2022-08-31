<%@ page import="org.jboss.portal.server.PortalConstants" %>
<%@page import="java.util.ResourceBundle"%>
<%@ taglib uri="portal-layout" prefix="p" %>
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
   <!-- to correct the unsightly Flash of Unstyled Content. -->
   <script type="text/javascript"></script>

   <p:headerContent/>
   <p:theme themeName="renewal"/>   
</head>

<body>
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
Z	</div>
<%@include file="../includes/footer.jsp" %>