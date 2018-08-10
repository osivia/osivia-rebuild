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

package org.jboss.portal.registration.policies;

import org.jboss.portal.common.util.ParameterValidation;
import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.portal.registration.Consumer;
import org.jboss.portal.registration.DuplicateRegistrationException;
import org.jboss.portal.registration.InvalidConsumerDataException;
import org.jboss.portal.registration.PropertyDescription;
import org.jboss.portal.registration.Registration;
import org.jboss.portal.registration.RegistrationException;
import org.jboss.portal.registration.RegistrationManager;
import org.jboss.portal.registration.RegistrationPolicy;
import org.jboss.portal.registration.RegistrationStatus;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides a default implementation of RegistrationPolicy which should be enough for most user needs provided the
 * appropriate {@link RegistrationPropertyValidator} has been configured to validate registration properties.
 *
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 11406 $
 * @since 2.6
 */
public class DefaultRegistrationPolicy extends AbstractJBossService implements RegistrationPolicy
{
   private RegistrationManager manager;
   private RegistrationPropertyValidator validator;
   private Map<QName, ? extends PropertyDescription> expectations;


   public RegistrationManager getManager()
   {
      return manager;
   }

   public void setManager(RegistrationManager manager)
   {
      this.manager = manager;
   }

   public void setExpectations(Map<QName, ? extends PropertyDescription> registrationPropertyDescriptions)
   {
      this.expectations = registrationPropertyDescriptions;
   }

   /**
    * Only accepts the registration if no registration with identical values exists for the Consumer identified by the
    * specified identify and delegates the validation of properties to the configured RegistrationPropertyValidator.
    *
    * @throws DuplicateRegistrationException if a Consumer with the same identity has already registered with the same
    *                                        registration properties.
    */
   public void validateRegistrationDataFor(Map<QName, ? extends PropertyDescription> registrationProperties, String consumerIdentity)
           throws IllegalArgumentException, RegistrationException
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(registrationProperties, "Registration properties");
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(consumerIdentity, "Consumer identity", null);

      StringBuilder message = new StringBuilder();

      // check that values are consistent with expectations
      if (expectations != null)
      {
         Set<QName> expectedNames = expectations.keySet();
         boolean consistentWithExpectations = true;

         // check for extra properties
         Set<QName> unexpected = new HashSet<QName>(registrationProperties.keySet());
         unexpected.removeAll(expectedNames);
         if (!unexpected.isEmpty())
         {
            consistentWithExpectations = false;
            message.append("Consumer '").append(consumerIdentity)
                    .append("' provided values for unexpected registration properties:\n");
            for (QName name : unexpected)
            {
               message.append("\t- ").append(name).append("\n");
            }
         }

         for (Map.Entry<QName, ? extends PropertyDescription> entry : expectations.entrySet())
         {
            QName name = entry.getKey();
            Object value = registrationProperties.get(name);
            try
            {
               validator.validateValueFor(name, value);
            }
            catch (IllegalArgumentException e)
            {
               message.append("Missing value for expected '").append(name.getLocalPart()).append("' property.\n");
               consistentWithExpectations = false;
            }
         }

         if (!consistentWithExpectations)
         {
            log.debug(message);
            throw new InvalidConsumerDataException(message.toString());
         }
      }

      // check that this is not a duplicate registration if the status is not pending
      Consumer consumer = manager.getConsumerByIdentity(consumerIdentity);
      if (consumer != null && !RegistrationStatus.PENDING.equals(consumer.getStatus()))
      {
         // allow the new registration only if the registration properties are different that existing registrations
         // for this consumer...
         for (Object o : consumer.getRegistrations())
         {
            Registration registration = (Registration)o;
            if (registration.hasEqualProperties(registrationProperties))
            {
               throw new DuplicateRegistrationException("Consumer named '" + consumer.getName()
                       + "' has already been registered with the same set of registration properties. Registration rejected!");
            }
         }
      }
   }

   /** Simply returns the given registration id. */
   public String createRegistrationHandleFor(String registrationId)
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(registrationId, "Registration id", null);
      return registrationId;
   }

   /** Doesn't support automatic ConsumerGroups so always return <code>null</code>. */
   public String getAutomaticGroupNameFor(String consumerName)
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(consumerName, "Consumer name", null);
      return null;
   }

   /** Simply returns the given consumer name, trusted (!) to be unique. */
   public String getConsumerIdFrom(String consumerName, Map registrationProperties) throws IllegalArgumentException, InvalidConsumerDataException
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(consumerName, "Consumer name", null);
      return consumerName;
   }

   /** Rejects registration if a Consumer with the specified name already exists. */
   public void validateConsumerName(String consumerName) throws IllegalArgumentException, RegistrationException
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(consumerName, "Consumer name", null);

      Consumer consumer = manager.getConsumerByIdentity(getConsumerIdFrom(consumerName, Collections.EMPTY_MAP));
      if (consumer != null)
      {
         throw new DuplicateRegistrationException("A Consumer named '" + consumerName + "' has already been registered.");
      }
   }

   /** Rejects name if a ConsumerGroup with the specified name already exists. */
   public void validateConsumerGroupName(String groupName) throws IllegalArgumentException, RegistrationException
   {
      // this is already the behavior in the RegistrationPersistenceManager so no need to do anything
   }

   /**
    * Instructs this policy to use the specified RegistrationPropertyValidator. There shouldn't be any need to call this
    * method manually, as the validator is configured via the WSRP Producer xml configuration file.
    *
    * @param validator
    */
   public void setValidator(RegistrationPropertyValidator validator)
   {
      this.validator = validator;
   }

   /**
    * Retrieves the currently configured RegistrationPropertyValidator.
    *
    * @return
    */
   public RegistrationPropertyValidator getValidator()
   {
      return validator;
   }
}
