/*
 * Copyright (c) 2007, Your Corporation. All Rights Reserved.
 */

package org.jboss.portal.registration;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 8966 $
 * @since 2.6.3
 */
public interface RegistrationPolicyChangeListener
{
   void policyUpdatedTo(RegistrationPolicy policy);
}
