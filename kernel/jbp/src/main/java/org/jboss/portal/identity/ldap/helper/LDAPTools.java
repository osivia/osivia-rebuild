/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.portal.identity.ldap.helper;

/**
 * Helper class for ldap operation
 *
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public class LDAPTools
{

   /**
    * Process dn and retrieves a part from it:
    * uid=xxx,dc=example,dc=org - retrieves xxx
    *
    * @param dn
    * @return
    */
   public static String stripDnToName(String dn)
   {
      if (dn == null || dn.length() == 0)
      {
         throw new IllegalArgumentException("Cannot process empty dn");
      }
      String name = null;

      String[] parts = dn.split(",");

      parts = parts[0].split("=");
      if (parts.length != 2)
      {
         throw new IllegalArgumentException("Wrong dn format: " + dn);
      }

      return parts[1];
   }

   /**
    * Simple encoding to make a name rfc 2253 compiliant. For now it just escapes the special chars listed in rfc
    * with backslash. 
    * @param name
    * @return
    */
   public static String encodeRfc2253Name(String name)
   {
      //TODO: fully cover 2253 encoding

      //TODO: backslash
      //name = name.replaceAll("\\","");
      name = name.replaceAll(",","\\\\\\,");
      name = name.replaceAll("\\+","\\\\\\+");
      name = name.replaceAll("\"","\\\\\"");
      name = name.replaceAll("<","\\\\\\<");
      name = name.replaceAll(">","\\\\\\>");
      name = name.replaceAll(";","\\\\\\;");
      return name;
   }
}
