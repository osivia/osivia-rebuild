/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.server.deployment;

import org.apache.log4j.Logger;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.common.xml.NullEntityResolver;
import org.jboss.portal.common.xml.XMLTools;
import org.jboss.portal.web.WebApp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Encapsulate the infos needed by the portal deployment layer to create the application.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 10228 $
 */
public abstract class PortalWebApp implements WebApp
{

   /** The logger. */
   protected final Logger log = Logger.getLogger(getClass());

   /** . */
   private Document descriptor;

   /** . */
   private ServletContext servletContext;

   /** . */
   private ClassLoader loader;

   /** . */
   private URL url;

   /** . */
   private String id;

   /** . */
   private String contextPath;

   /** Constructor with the given web app. */
   protected PortalWebApp()
   {
   }

   /** Instrument the web application. */
   public abstract void instrument() throws Exception;

   /** Return the URL associated to this webapp. */
   public final URL getURL()
   {
      return url;
   }

   /** Return the application id. */
   public final String getId()
   {
      return id;
   }

   /** Return the classloader of the web app. */
   public final ClassLoader getClassLoader()
   {
      return loader;
   }

   /** Returns the context path of the web application. */
   public final String getContextPath()
   {
      return contextPath;
   }

   /** Returns the servlet context of the web application. */
   public final ServletContext getServletContext()
   {
      return servletContext;
   }

   public final Document getDescriptor()
   {
      return descriptor;
   }

   /**
    * Import a file in the war file. The file could not be created for some reasons which are : <ul> <li>The parent dir
    * exists and is a file</li> <li>The parent dir does not exist and its creation failed</li> <li>An underlying
    * exception occurs when writing bytes from the source <code>Inputstream</code> to the target
    * <code>OutputStream</code></li> </ul>
    *
    * @param parentDirRelativePath the parent relative path in the web app starting from the app root
    * @param name                  the name the created file should have
    * @param source                the data of the target file
    * @param overwrite             if false and the file already exists nothing is done
    * @return true if the file has been created
    * @throws IOException if the file cannot be created
    */
   public final boolean importFile(String parentDirRelativePath, String name, InputStream source, boolean overwrite) throws IOException
   {
      ServletContext ctx = getServletContext();
      String contextPath = getContextPath();

      // Get the parent dir
      String parentAbsolutePath = ctx.getRealPath(parentDirRelativePath);
      File parentDir = new File(parentAbsolutePath);

      // We ensure it exists
      if (parentDir.exists())
      {
         if (parentDir.isFile())
         {
            throw new IOException("Target parent dir " + parentDirRelativePath + " already exists in the web application  and is a file " + contextPath);
         }
      }
      else
      {
         if (!parentDir.mkdirs())
         {
            throw new IOException("Was not able to create the parent dir " + parentDirRelativePath + " in the web application " + contextPath);
         }
      }

      //
      boolean done = false;
      File targetFile = new File(parentDir, name);
      if (overwrite || !targetFile.exists())
      {
         OutputStream target = null;
         try
         {
            target = IOTools.safeBufferedWrapper(new FileOutputStream(new File(parentDir, name)));
            IOTools.copy(source, target);
            done = true;
            log.debug("Copied file" + name + " to location " + parentDirRelativePath);
         }
         finally
         {
            IOTools.safeClose(target);
         }
      }
      return done;
   }

   protected final void init(
      ServletContext servletContext,
      URL url,
      ClassLoader loader,
      String contextPath,
      EntityResolver jbossAppEntityResolver) throws CannotCreatePortletWebAppException
   {
      this.servletContext = servletContext;
      this.url = url;
      this.loader = loader;
      this.contextPath = contextPath;
      this.id = contextPath;

      //
      readJBossAppDescriptor(jbossAppEntityResolver);

      // Override the id if not null
      if (descriptor != null)
      {
         Element jbossAppElt = descriptor.getDocumentElement();
         Element appNameElt = XMLTools.getUniqueChild(jbossAppElt, "app-name", false);
         if (appNameElt != null)
         {
            id = XMLTools.asString(appNameElt);
            log.debug("Detected explicit app name = " + id + " for application under path " + getContextPath());
         }
      }
   }

   /**
    * Read jboss-app.xml deployment and fetch the overriden id if it exists.
    *
    * @return the app id or null if it does not exists
    */
   private void readJBossAppDescriptor(EntityResolver jbossAppEntityResolver)
   {
      // Look for jboss-app.xml override
      InputStream in = null;
      try
      {
         in = IOTools.safeBufferedWrapper(getServletContext().getResourceAsStream("/WEB-INF/jboss-app.xml"));
         if (in != null)
         {
            DocumentBuilder builder = XMLTools.getDocumentBuilderFactory().newDocumentBuilder();
            if (jbossAppEntityResolver == null)
            {
               log.debug("Coult not obtain entity resolver for jboss-app.xml");
               jbossAppEntityResolver = new NullEntityResolver();
            }
            else
            {
               log.debug("Obtained entity resolver " + jbossAppEntityResolver + " for jboss-app.xml");
            }
            builder.setEntityResolver(jbossAppEntityResolver);
            descriptor = builder.parse(in);
         }
      }
      catch (IOException e)
      {
         log.debug("Cannot read jboss-app.xml", e);
      }
      catch (ParserConfigurationException e)
      {
         log.debug("Cannot read jboss-app.xml", e);
      }
      catch (SAXException e)
      {
         log.debug("Cannot read jboss-app.xml", e);
      }
      finally
      {
         IOTools.safeClose(in);
      }
   }

   public String toString()
   {
      return "WebApp[" + getContextPath() + "]";
   }

}
