/*
 * Copyright (c) 2007, Your Corporation. All Rights Reserved.
 */

package org.jboss.portal.registration;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 9177 $
 * @since 2.6.3
 */
public interface PropertyDescription
{
   QName getName();

   QName getType();
}
