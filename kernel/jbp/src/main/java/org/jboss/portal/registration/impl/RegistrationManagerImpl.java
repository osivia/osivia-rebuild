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

package org.jboss.portal.registration.impl;

import org.jboss.logging.Logger;
import org.jboss.portal.common.util.ParameterValidation;
import org.jboss.portal.jems.as.system.AbstractJBossService;
import org.jboss.portal.registration.Consumer;
import org.jboss.portal.registration.ConsumerGroup;
import org.jboss.portal.registration.InvalidConsumerDataException;
import org.jboss.portal.registration.NoSuchRegistrationException;
import org.jboss.portal.registration.PropertyDescription;
import org.jboss.portal.registration.Registration;
import org.jboss.portal.registration.RegistrationException;
import org.jboss.portal.registration.RegistrationManager;
import org.jboss.portal.registration.RegistrationPersistenceManager;
import org.jboss.portal.registration.RegistrationPolicy;
import org.jboss.portal.registration.RegistrationStatus;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 11604 $
 * @since 2.6
 */
public class RegistrationManagerImpl extends AbstractJBossService implements RegistrationManager
{
   private static final Logger log = Logger.getLogger(RegistrationManager.class);

   private RegistrationPolicy policy;
   private RegistrationPersistenceManager persistenceManager;

   public RegistrationManagerImpl()
   {
   }

   public RegistrationPolicy getPolicy()
   {
      return policy;
   }

   public void setPolicy(RegistrationPolicy policy)
   {
      this.policy = policy;
      policy.setManager(this);
   }

   public RegistrationPersistenceManager getPersistenceManager()
   {
      return persistenceManager;
   }

   public void setPersistenceManager(RegistrationPersistenceManager persistenceManager)
   {
      this.persistenceManager = persistenceManager;
   }

   public Registration addRegistrationTo(String consumerName, Map registrationProperties, boolean createConsumerIfNeeded)
           throws RegistrationException
   {
      // the policy determines the identity of the consumer based on the given information (note that this might be obsoleted by using WS-Security)
      String identity = policy.getConsumerIdFrom(consumerName, registrationProperties);

      // validate the registration information
      policy.validateRegistrationDataFor(registrationProperties, identity);

      Consumer consumer = getOrCreateConsumer(identity, createConsumerIfNeeded, consumerName);

      // create the actual registration
      Registration registration = persistenceManager.addRegistrationFor(identity, registrationProperties);

      // let the policy decide what the handle should be
      String handle = policy.createRegistrationHandleFor(registration.getId());
      registration.setRegistrationHandle(handle);

      return registration;
   }

   public Consumer createConsumer(String name) throws RegistrationException, InvalidConsumerDataException
   {
      // check with policy if we allow the consumer
      policy.validateConsumerName(name);

      String identity = policy.getConsumerIdFrom(name, Collections.EMPTY_MAP);

      Consumer consumer = persistenceManager.createConsumer(identity, name);

      // deal with group if needed
      // let the policy decide if there should be a group associated with the Consumer and if yes, with which id
      String groupName = policy.getAutomaticGroupNameFor(name);
      if (groupName != null)
      {
         addConsumerToGroupNamed(name, groupName, true, false);
      }

      return consumer;
   }

   public Consumer addConsumerToGroupNamed(String consumerName, String groupName, boolean createGroupIfNeeded, boolean createConsumerIfNeeded) throws RegistrationException
   {
      // check with the policy if we allow the group name in case we need to create it
      if (createGroupIfNeeded)
      {
         policy.validateConsumerGroupName(groupName);
      }

      // check with policy if we allow the consumer name in case we need to create it
      if (createConsumerIfNeeded)
      {
         policy.validateConsumerName(consumerName);
      }

      ConsumerGroup group = getConsumerGroup(groupName);
      boolean justCreatedGroup = false;
      if (group == null)
      {
         if (createGroupIfNeeded)
         {
            createConsumerGroup(groupName);
            justCreatedGroup = true;
         }
         else
         {
            throw new NoSuchRegistrationException("There is no existing ConsumerGroup named '" + groupName + "'.");
         }
      }

      String identity = policy.getConsumerIdFrom(consumerName, Collections.EMPTY_MAP);
      try
      {
         getOrCreateConsumer(identity, createConsumerIfNeeded, consumerName);
      }
      catch (NoSuchRegistrationException e)
      {
         if (justCreatedGroup)
         {
            removeConsumerGroup(groupName);
         }
      }

      return persistenceManager.addConsumerToGroupNamed(identity, groupName);
   }

   public ConsumerGroup createConsumerGroup(String groupName) throws RegistrationException
   {
      // check with the policy if we allow the group
      policy.validateConsumerGroupName(groupName);

      return persistenceManager.createConsumerGroup(groupName);
   }

   public void removeConsumer(String identity) throws RegistrationException, NoSuchRegistrationException
   {
      Consumer consumer = getOrCreateConsumer(identity, false, null);

      ConsumerGroup group = consumer.getGroup();
      if (group != null)
      {
         group.removeConsumer(consumer);
      }

      // cascade delete the registrations
      for (Iterator i = new ArrayList(consumer.getRegistrations()).iterator(); i.hasNext();)
      {
         removeRegistration((Registration)i.next());
      }

      // let the registry do the actual deletion
      persistenceManager.removeConsumer(identity);
   }

   public void removeConsumer(Consumer consumer) throws RegistrationException, NoSuchRegistrationException
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(consumer, "Consumer");

      removeConsumer(consumer.getId());
   }

   public Consumer getConsumerByIdentity(String identity) throws RegistrationException
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(identity, "identity", null);
      return persistenceManager.getConsumerById(identity);
   }

   public Consumer getConsumerFor(String registrationHandle) throws RegistrationException
   {
      return (Consumer)getConsumerOrRegistration(registrationHandle, true);
   }

   public Registration getRegistration(String registrationHandle) throws RegistrationException
   {
      return (Registration)getConsumerOrRegistration(registrationHandle, false);
   }

   public void removeRegistration(String registrationHandle) throws RegistrationException, NoSuchRegistrationException
   {
      Registration registration = getRegistration(registrationHandle);
      if (registration == null)
      {
         throw new NoSuchRegistrationException("There is no Registration with handle '" + registrationHandle + "'");
      }
      removeRegistration(registration);
   }

   public void removeRegistration(Registration registration) throws RegistrationException
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(registration, "Registration");

      registration.setStatus(RegistrationStatus.INVALID); // just in case...
//      registration.clearAssociatedState(); // todo: do we need to clear associated state?

      persistenceManager.removeRegistration(registration.getId());
   }

   public ConsumerGroup getConsumerGroup(String groupName) throws RegistrationException
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(groupName, "ConsumerGroup name", null);
      return persistenceManager.getConsumerGroup(groupName);
   }

   private Consumer getOrCreateConsumer(String identity, boolean createConsumerIfNeeded, String consumerName)
           throws RegistrationException
   {
      Consumer consumer = getConsumerByIdentity(identity);
      if (consumer == null)
      {
         if (createConsumerIfNeeded)
         {
            consumer = createConsumer(consumerName);
         }
         else
         {
            throw new NoSuchRegistrationException("There is no Consumer named '" + consumerName + "'.");
         }
      }
      return consumer;
   }

   private Object getConsumerOrRegistration(String registrationHandle, boolean getConsumer)
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(registrationHandle, "registration handle", null);

      Registration registration = persistenceManager.getRegistration(registrationHandle);
      if (registration == null)
      {
         return null;
      }
      else
      {
         return getConsumer ? registration.getConsumer() : registration;
      }
   }

   public Collection getConsumerGroups()
   {
      return persistenceManager.getConsumerGroups();
   }

   public void removeConsumerGroup(ConsumerGroup group) throws RegistrationException
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(group, "ConsumerGroup");

      for (Object consumer : group.getConsumers())
      {
         removeConsumer((Consumer)consumer);
      }

      persistenceManager.removeConsumerGroup(group.getName());
   }

   public void removeConsumerGroup(String name) throws RegistrationException
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(name, "ConsumerGroup name");
      removeConsumerGroup(getConsumerGroup(name));
   }

   public Collection getConsumers()
   {
      return persistenceManager.getConsumers();
   }

   public void clear() throws RegistrationException
   {
      Collection<Consumer> consumers = new ArrayList<Consumer>(getConsumers());
      for (Consumer consumer : consumers)
      {
         removeConsumer(consumer);
      }

      Collection<ConsumerGroup> groups = new ArrayList<ConsumerGroup>(getConsumerGroups());
      for (ConsumerGroup group : groups)
      {
         removeConsumerGroup(group);
      }
   }

   /**
    * We listen to registration property changes on the producer configuration so that we can invalidate the current
    * registrations. Consumers will need to call modifyRegistration since properties have changed... which requires
    * throwing an OperationFailedFault... not an InvalidRegistrationFault!
    *
    * @param registrationProperties
    */
   public void propertiesHaveChanged(Map<QName, ? extends PropertyDescription> registrationProperties)
   {
      log.debug("Registration properties have changed, existing registrations will be invalidated...");
      Collection registrations = persistenceManager.getRegistrations();
      for (Object registration : registrations)
      {
         Registration reg = (Registration)registration;

         // pending instead of invalid as technically, the registration is not yet invalid
         reg.setStatus(RegistrationStatus.PENDING);
//         reg.clearAssociatedState(); //todo: do we need to clear the associated state? If we do, should we wait until current operations are done?
      }

      // make policy aware of new registration properties
      policy.setExpectations(registrationProperties);
   }

   /**
    * We listen for RegistrationPolicy changes so that we can provide the proper behavior at all time if the policy has
    * been changed by users since this RegistrationManager was initialized...
    *
    * @param policy
    */
   public void policyUpdatedTo(RegistrationPolicy policy)
   {
      setPolicy(policy);
   }
}
