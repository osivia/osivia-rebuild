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
package org.jboss.portal.server.servlet;

/**
 * The result of a request to a mapper.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class PathMappingResult
{

   private final Object target;
   private final String matchedPath;
   private final String remainingPath;

   public PathMappingResult(Object target, String matchedPath, String remainingPath)
   {
      this.target = target;
      this.matchedPath = matchedPath;
      this.remainingPath = remainingPath;
   }

   public Object getTarget()
   {
      return target;
   }

   public String getMatchedPath()
   {
      return matchedPath;
   }

   public String getRemainingPath()
   {
      return remainingPath;
   }

   public int hashCode()
   {
      int hashCode = (target != null ? target.hashCode() : 0) +
         (matchedPath != null ? matchedPath.hashCode() : 0) +
         (remainingPath != null ? remainingPath.hashCode() : 0);
      return hashCode;
   }

   public boolean equals(Object obj)
   {
      if (obj == this)
      {
         return true;
      }
      if (obj instanceof PathMappingResult)
      {
         PathMappingResult other = (PathMappingResult)obj;
         return (target == null ? (other.target == null) : target.equals(other.target)) &&
            (matchedPath == null ? (other.matchedPath == null) : matchedPath.equals(other.matchedPath)) &&
            (remainingPath == null ? (other.remainingPath == null) : remainingPath.equals(other.remainingPath));
      }
      return false;
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer("MappingResult[");
      buffer.append(target == null ? "null" : '\"' + target.toString() + '\"');
      buffer.append(',');
      buffer.append(matchedPath == null ? "null" : '\"' + matchedPath + '\"');
      buffer.append(',');
      buffer.append(remainingPath == null ? "null" : '\"' + remainingPath + '\"');
      buffer.append("]");
      return buffer.toString();
   }
}
