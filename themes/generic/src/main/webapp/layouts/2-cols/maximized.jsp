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
    <td valign="top">
      <!-- insert the content of the 'center' region of the page, and assign the css selector id 'regionB' -->
      <p:region regionName='maximized' />
    </td>
  </tr>
</table>
           

<!-- TODO: Fix the auto jump in this tag -->
<div id="footer-container" class="portal-copyright">
<a class="portal-copyright" href="http://www.jboss.com/products/jbossportal">JBoss Portal</a><br/>
</div>

<p:region regionName='AJAXFooter' regionID='AJAXFooter'/>

</body>
</html>
