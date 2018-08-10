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
package org.jboss.portal.portlet.deployment.jboss.metadata;

import org.jboss.portal.common.transaction.Transactions;

/**
 * Specific metadata specified to jboss portlet container.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7226 $
 */
public class JBossPortletMetaData implements Cloneable
{

   /** . */
   private String name;

   /** . */
   private Boolean remotable;

   /** . */
   private SecurityConstraintMetaData securityConstraint;

   /** . */
   private Integer cacheRefType;

   /** . */
   private Transactions.Type txType;

   /** . */
   private Boolean distributed;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public Boolean getRemotable()
   {
      return remotable;
   }

   public void setRemotable(Boolean remotable)
   {
      this.remotable = remotable;
   }

   public Boolean getDistributed()
   {
      return distributed;
   }

   public void setDistributed(Boolean distributed)
   {
      this.distributed = distributed;
   }

   public SecurityConstraintMetaData getSecurityConstraint()
   {
      return securityConstraint;
   }

   public void setSecurityConstraint(SecurityConstraintMetaData securityConstraint)
   {
      this.securityConstraint = securityConstraint;
   }

   public Integer getCacheRefType()
   {
      return cacheRefType;
   }

   public void setCacheRefType(Integer cacheRefType)
   {
      this.cacheRefType = cacheRefType;
   }

   public Transactions.Type getTxType()
   {
      return txType;
   }

   public void setTxType(Transactions.Type txType)
   {
      this.txType = txType;
   }

   /**
    * Merge the current meta data with portlet application.
    */
   public void merge(JBossApplicationMetaData application)
   {
      if (remotable == null)
      {
         remotable = application.getRemotable();
      }
   }

   /**
    * Merge the current meta data with a specified one.
    */
   public void merge(JBossPortletMetaData portlet)
   {
      if (remotable == null)
      {
         remotable = portlet.getRemotable();
      }
      if (txType == null)
      {
         txType = portlet.getTxType();
      }
      if (cacheRefType == null)
      {
         cacheRefType = portlet.getCacheRefType();
      }
      if (securityConstraint == null)
      {
         securityConstraint = portlet.getSecurityConstraint();
      }
      if (distributed == null)
      {
         distributed = portlet.getDistributed();
      }
   }

   public Object clone()
   {
      try
      {
         return super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         throw new Error(e);
      }
   }
}
