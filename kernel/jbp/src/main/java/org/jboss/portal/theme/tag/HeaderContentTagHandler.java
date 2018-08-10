
package org.jboss.portal.theme.tag;

import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.w3c.dom.Element;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JSP tag to write the header content set as portlet response property. <p>Portlets can set a response property to
 * signal to the portal that the provided value should be injected into the head of the response document. This tag is
 * the means to inject that content.</p>
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 12382 $, $LastChangedDate: 2008-12-15 08:50:19 -0500 (Mon, 15 Dec 2008) $
 * @see
 */
public class HeaderContentTagHandler extends SimpleTagSupport
{
   protected static final OutputFormat serializerOutputFormat = new OutputFormat() {
	   {
          setOmitXMLDeclaration(true);
       }
   };

   public void doTag() throws JspException, IOException
   {
      // Get page and region
      PageContext app = (PageContext)getJspContext();
      HttpServletRequest request = (HttpServletRequest)app.getRequest();

      //
      PageResult page = (PageResult)request.getAttribute(LayoutConstants.ATTR_PAGE);
      JspWriter out = this.getJspContext().getOut();
      if (page == null)
      {
         out.write("<p bgcolor='red'>No page to render!</p>");
         out.write("<p bgcolor='red'>The page to render (PageResult) must be set in the request attribute '" + LayoutConstants.ATTR_PAGE + "'</p>");
         out.flush();
         return;
      }

    
      out.write("TEST");
      
      out.flush();
   }
}
