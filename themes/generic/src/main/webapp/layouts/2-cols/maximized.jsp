<%@ page import="org.jboss.portal.server.PortalConstants" %>
<%@page import="java.util.ResourceBundle"%>
<%@ taglib uri="/WEB-INF/theme/portal-layout.tld" prefix="p" %>
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
   <!-- inject the theme, default to the Renewal theme if nothing is selected for the portal or the page -->
   <p:theme themeName="renewal"/>
   <!-- insert header content that was possibly set by portlets on the page -->
   <p:headerContent/>
</head>

<body id="body">
<p:region regionName='AJAXScripts' regionID='AJAXScripts'/>

<table class="layout" width="100%">
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
      <p:region regionName='maximized' />
    </td>
  </tr>
</table>
           
<%@include file="../includes/modal.jsp" %>


<p:region regionName='AJAXFooter' regionID='AJAXFooter'/>

</body>
</html>
