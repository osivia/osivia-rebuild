
package org.jboss.portal.theme.tag;

import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.theming.IPageHeaderResourceService;
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
   
   /** Page header resource service. */
   private final IPageHeaderResourceService pageHeaderResourceService;

   
   /**
    * Constructor.
    */
   public HeaderContentTagHandler() {
       super();

       // Page header resource service
       this.pageHeaderResourceService = Locator.findMBean(IPageHeaderResourceService.class, IPageHeaderResourceService.MBEAN_NAME);
   }
   
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
//         this.writeResource(out, "<p bgcolor='red'>No page to render!</p>");
//         this.writeResource(out, "<p bgcolor='red'>The page to render (PageResult) must be set in the request attribute '" + LayoutConstants.ATTR_PAGE + "'</p>");
         out.flush();
         return;
      }

      // Jquery
      this.writeResource(out,  "<script src='/portal-assets/components/jquery/jquery-1.12.4.min.js'></script>");
      this.writeResource(out,  "<script src='/portal-assets/js/jquery-integration.js'></script>");    
      
      // jQuery UI
      this.writeResource(out,  "<script type='text/javascript' src='/portal-assets/components/jquery-ui/jquery-ui-1.11.3.min.js'></script>");
      this.writeResource(out, "<link rel='stylesheet' href='/portal-assets/components/jquery-ui/jquery-ui-1.11.3.min.css'>");
  
      
      this.writeResource(out,  "<script src='/portal-assets/js/ajax-redirection.js'></script>");  
      
      this.writeResource(out,  "<script src='/portal-assets/js/auto-submit.js'></script>");  
      this.writeResource(out,  "<script src='/portal-assets/js/modal.js'></script>");        
      
      // Bootstrap
      this.writeResource(out,  "<script src='/portal-assets/components/bootstrap/js/bootstrap.bundle.min.js'></script>");

      // Fancytree
      this.writeResource(out,  "<script src='/portal-assets/components/fancytree/jquery.fancytree-all-2.8.0.min.js'></script>");

      
      // Select2
      this.writeResource(out,  "<link rel='stylesheet' href='/portal-assets/components/select2/css/select2.min.css'>");
      this.writeResource(out,  "<script src='/portal-assets/components/select2/js/select2.full.min.js'></script>");

      
      this.writeResource(out,  "<script src='/portal-assets/js/select2-integration.js'></script>");  

      // Inline-edition
      this.writeResource(out,  "<script src='/portal-assets/js/inline-edition.js'></script>");  
   
      // Table
      this.writeResource(out,  "<script src='/portal-assets/js/table.js'></script>");  
      
      // Filler
      this.writeResource(out,  "<script src='/portal-assets/js/portlet-filler.js'></script>");      
      
      // Resizable
      this.writeResource(out,  "<script src='/portal-assets/js/resizable.js'></script>");      
      
      // Filler
      this.writeResource(out,  "<script src='/portal-assets/js/fancytree-integration.js'></script>");      
      
      //Location
      this.writeResource(out,  "<script src='/portal-assets/js/location.js'></script>");  
      
      // Logout
      this.writeResource(out,  "<script src='/portal-assets/js/logout.js'></script>"); 
      
      // Clipboard
      this.writeResource(out,  "<script src='/portal-assets/components/clipboard/clipboard.min.js'></script>");
      this.writeResource(out,  "<script src='/portal-assets/js/clipboard-integration.js'></script>");      
      
      this.writeResource(out, "<!-- portlet resources -->");
      
      out.flush();
   }
   
   
   /**
    * Write resource element.
    *
    * @param out JSP writer
    * @param resource resource element
    * @throws IOException
    */
   private void writeResource(JspWriter out, String resource) throws IOException {
       out.write(this.pageHeaderResourceService.adaptResourceElement(resource));
   }

}
