/*
 * (C) Copyright 2016 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.osivia.portal.core.directory.v2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.osivia.portal.api.directory.v2.IDirDelegate;
import org.osivia.portal.api.directory.v2.IDirService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.transaction.ITransactionService;
import org.springframework.stereotype.Service;

/**
 * Handler of calls to the directory service. Should check that a delegate has
 * been deployed and can answer the call
 * 
 * @author Loïc Billon
 * @since 4.4
 */

public class DirHandler implements InvocationHandler {

	private static final Log LOGGER = LogFactory.getLog(DirHandler.class);

	/** The delegate (the deployed directory service) */
	private IDirDelegate delegate;
	
	/** The transaction service. */
	ITransactionService transactionService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		Class<IDirService> declaringClass = (Class<IDirService>) method
				.getDeclaringClass();

		if (delegate != null) {
			
			ClassLoader currentCl = Thread.currentThread().getContextClassLoader();
			
			try {
				// Call service with the delegate classloader (avoid ClassNotFoundException)
				Thread.currentThread().setContextClassLoader(delegate.getClassLoader());
				
				// Create LDAP transaction
                ITransactionService transactionService = getTransactionService();
                 if (transactionService.isStarted() && (transactionService.getResource("LDAP") == null)) {
                    transactionService.register("LDAP", delegate.getTransactionResource());
                }
				
				
				
				
				IDirService directoryService = delegate
						.getDirectoryService(declaringClass);
	
				if (directoryService != null) {
					return method.invoke(directoryService, args);
				} else {
					throw new InvocationException("No directory service registered with signature "
							+ method.toString());
			}
			}finally{
				Thread.currentThread().setContextClassLoader(currentCl);
			}

		} else {
			LOGGER.error("No directory service deployed !");

			return null;
		}

	}

    /**
     * Gets the transaction service.
     *
     * @return the transaction service
     */
    private ITransactionService getTransactionService() {
        if( transactionService == null)
             transactionService = Locator.findMBean(ITransactionService.class, ITransactionService.MBEAN_NAME);
        return transactionService;
    }

	/**
	 * @return the delegate
	 */
	public IDirDelegate getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate
	 *            the delegate to set
	 */
	public void setDelegate(IDirDelegate delegate) {
		this.delegate = delegate;
	}

}
