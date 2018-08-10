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
package org.jboss.portal.server.deployment.jboss;

import org.jboss.deployment.DeploymentException;
import org.jboss.logging.Logger;
import org.jboss.portal.common.net.URLNavigator;
import org.jboss.portal.common.net.URLVisitor;
import org.jboss.portal.common.util.SetMap;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11553 $
 */
public class PortalDeploymentInfoContext
{

   /** . */
   private static final Logger log = Logger.getLogger(PortalDeploymentInfoContext.class);

   /** . */
   private final PortalDeploymentInfo pdi;

   /** The various deployments that this deployment info generated. */
   private final SetMap deployments;

   public PortalDeploymentInfoContext(PortalDeploymentInfo pdi)
   {
      this.pdi = pdi;
      this.deployments = new SetMap();
   }

   public PortalDeploymentInfo getInfo()
   {
      return pdi;
   }

   public void add(DeploymentFactoryContext factoryCtx, boolean proceedLifcycle) throws DeploymentException
   {
      try
      {
         FactoryVisitor visitor = new FactoryVisitor(factoryCtx);
         URLNavigator.visit(pdi.url, visitor);

         // Record the different deployment contexts created by this factory
         for (Iterator i = visitor.ctxs.iterator(); i.hasNext();)
         {
            DeploymentContext ctx = (DeploymentContext)i.next();
            deployments.put(ctx.getURL(), ctx);

            //
            if (proceedLifcycle)
            {
               try
               {
                  ctx.create();
                  ctx.start();
               }
               catch (DeploymentException e)
               {
                  log.error("Failed to deploy url " + pdi.url);
               }
            }
         }
      }
      catch (IOException e)
      {
         throw new DeploymentException(e);
      }
   }

   public void remove(DeploymentFactoryContext factoryContext, boolean proceedLifecycle)
   {
      for (Iterator i = new HashSet(deployments.keySet()).iterator(); i.hasNext();)
      {
         URL childURL = (URL)i.next();
         for (Iterator j = deployments.iterator(childURL); j.hasNext();)
         {
            DeploymentContext deploymentContext = (DeploymentContext)j.next();

            // Take care of it only if the factory deployed it
            if (deploymentContext.getFactoryContext().getName().equals(factoryContext.getName()))
            {
               j.remove();

               //
               if (proceedLifecycle)
               {
                  try
                  {
                     deploymentContext.stop();
                     deploymentContext.destroy();
                  }
                  catch (DeploymentException e)
                  {
                     e.printStackTrace();
                  }
               }
            }
         }
      }
   }

   private class FactoryVisitor implements URLVisitor
   {

      /** . */
      private final Set ctxs;

      /** . */
      private final LinkedList acceptStack;

      /** . */
      private final DeploymentFactoryContext factoryCtx;

      public FactoryVisitor(DeploymentFactoryContext factoryCtx)
      {
         this.factoryCtx = factoryCtx;
         this.acceptStack = new LinkedList();
         this.ctxs = new HashSet();
      }

      public void startDir(URL url, String name)
      {
         log.debug("Trying factory " + factoryCtx.getName() + " to accept dir " + url);
         boolean accepted = factoryCtx.getFilter().acceptDir(url);
         if (accepted)
         {
            log.debug("Factory " + factoryCtx.getName() + " accepted dir " + url);
         }
         else
         {
            log.debug("Factory " + factoryCtx.getName() + " did not accept dir " + url);
         }
         acceptStack.addLast(Boolean.valueOf(accepted));
      }

      public void endDir(URL url, String name)
      {
         acceptStack.removeLast();
      }

      public void file(URL url, String name)
      {
         Boolean accepted = Boolean.TRUE;
         if (acceptStack.size() > 0)
         {
            accepted = (Boolean)acceptStack.getLast();
         }
         if (accepted.booleanValue())
         {
            log.debug("Trying factory " + factoryCtx.getName() + " to accept file " + url);
            if (factoryCtx.getFilter().acceptFile(url))
            {
               log.debug("Factory " + factoryCtx.getName() + " accepted file " + url);
               DeploymentContext ctx = new DeploymentContext(factoryCtx, pdi.pwa, url, pdi.getServer());
               ctxs.add(ctx);
            }
         }
      }
   }

   public void create() throws DeploymentException
   {
      // @syllant-patch@ : patch to sort -object.xml files
      Set<URL> sortedDeploymentKeys = new TreeSet<URL>(new URLComparator());
      sortedDeploymentKeys.addAll(deployments.keySet());
      for (Iterator<URL> i = sortedDeploymentKeys.iterator(); i.hasNext();)
      {
         URL url = (URL)i.next();
         
         for (Iterator<DeploymentContext> j = deployments.iterator(url); j.hasNext();)
         {
            DeploymentContext ctx = (DeploymentContext)j.next();
            ctx.create();
         }
      }
   }

   public void start() throws DeploymentException
   {
      // @syllant-patch@ : patch to sort -object.xml files
      Set<URL> sortedDeploymentKeys = new TreeSet<URL>(new URLComparator());
      sortedDeploymentKeys.addAll(deployments.keySet());
      for (Iterator<URL> i = sortedDeploymentKeys.iterator(); i.hasNext();)
      {
         URL url = (URL)i.next();
         for (Iterator<DeploymentContext> j = deployments.iterator(url); j.hasNext();)
         {
            DeploymentContext ctx = (DeploymentContext)j.next();
            ctx.start();
         }
      }
   }

   public void stop() throws DeploymentException
   {
      // @syllant-patch@ : patch to sort -object.xml files
      Set<URL> sortedDeploymentKeys = new TreeSet<URL>(new OppositeURLComparator());
      sortedDeploymentKeys.addAll(deployments.keySet());
      for (Iterator<URL> i = sortedDeploymentKeys.iterator(); i.hasNext();)
      {
         URL url = (URL)i.next();
         for (Iterator<DeploymentContext> j = deployments.iterator(url); j.hasNext();)
         {
            DeploymentContext ctx = (DeploymentContext)j.next();
            ctx.stop();
         }
      }
   }

   public void destroy() throws DeploymentException
   {
      // @syllant-patch@ : patch to sort -object.xml files
      Set<URL> sortedDeploymentKeys = new TreeSet<URL>(new OppositeURLComparator());
      sortedDeploymentKeys.addAll(deployments.keySet());
      for (Iterator<URL> i = sortedDeploymentKeys.iterator(); i.hasNext();)
      {
         URL url = (URL)i.next();
         for (Iterator<DeploymentContext> j = deployments.iterator(url); j.hasNext();)
         {
            DeploymentContext ctx = (DeploymentContext)j.next();
            ctx.destroy();
         }
      }
   }
   
   // @syllant-patch@ : patch to sort -object.xml files
   private class URLComparator implements Comparator<URL>
   {
      public int compare(URL o1, URL o2)
      {
         return o1.getPath().compareTo(o2.getPath());
      }
   }

   private class OppositeURLComparator implements Comparator<URL>
   {
      public int compare(URL o1, URL o2)
      {
         return - o1.getPath().compareTo(o2.getPath());
      }
   }
}
