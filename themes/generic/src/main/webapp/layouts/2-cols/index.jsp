<%@ page import="org.jboss.portal.server.PortalConstants" %>
<%@page import="java.util.ResourceBundle"%>
<%@ taglib uri="/WEB-INF/theme/portal-layout.tld" prefix="p" %>
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
   <!-- to correct the unsightly Flash of Unstyled Content. -->
   <script type="text/javascript"></script>

   
	<p:headerContent />
	
    <p:theme themeName="renewal"/>

<p:region regionName='AJAXScripts' regionID='AJAXScripts'/>
</head>

<!--<op:translate key="CUSTOMIZER_MSG_1" />-->

<body id="body">


<div class="layout">
	<table  width="100%">
	   <tr>
		<td width="100%" colspan="2" valign="top">    
	  		<p:region regionName='top' regionID='top'/>
	    </td>
	  </tr>  		
	  <tr>
	   <td width="20%" valign="top">
	   		<p:region regionName='logo' regionID='logo'/>
	   		<p:region regionName='nav' regionID='nav'/>
	    </td>   
	    <td width="80%" valign="top">
	      <!-- insert the content of the 'center' region of the page, and assign the css selector id 'regionB' -->
	      <%@include file="../includes/content-navbar.jsp" %>
	      
	      <p:region regionName='col-1' regionID='col-1'/>
	      <p:region regionName='col-2' regionID='col-2'/>
	    </td>
	  </tr>
	</table>
</div>
           
<%@include file="../includes/footer.jsp" %>

<p:region regionName='AJAXFooter' regionID='AJAXFooter'/>

</body>
</html>
