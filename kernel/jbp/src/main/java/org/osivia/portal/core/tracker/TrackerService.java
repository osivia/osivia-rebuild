/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
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
 *
 */
package org.osivia.portal.core.tracker;

import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.portal.common.http.HttpRequest;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.system.ServiceMBeanSupport;
import org.osivia.portal.core.tracker.ITracker;


/**
 * 
 * Permet de connaitrea tout moment la commande en cours
 *  (les commandes sont stockées dans une pile)
 * 
 * 
 * @author jsteux
 *
 */
@SuppressWarnings("unchecked")

public class TrackerService extends ServiceMBeanSupport implements ITracker, Serializable {
	
	private Log log = LogFactory.getLog(TrackerService.class);
	
	private static ThreadLocal<TrackerBean> trackerBean = new ThreadLocal<TrackerBean>();

	
	public TrackerBean getTrackerBean(){
		TrackerBean bean = trackerBean.get();
		if( bean == null)	{
			bean = new TrackerBean( );
			trackerBean.set(bean);
		}
			
		return bean;
	}
	
	
	public Object getInternalBean(){
		TrackerBean bean = trackerBean.get();
		if( bean == null)	{
			bean = new TrackerBean( );
			trackerBean.set(bean);
		}
			
		return bean;
	}

	
	public void createThreadContext( Object main){
			TrackerBean mainBean = (TrackerBean) main;
			TrackerBean newBean = new TrackerBean( );
			newBean.setStack((Stack) mainBean.getStack().clone());
			newBean.setRequest(mainBean.getRequest());
			newBean.setSession(mainBean.getSession());
			newBean.setParent(mainBean);
			trackerBean.set(newBean);
		}
	
	public Object getParentBean()	{
		return getTrackerBean().getParent();
	}


	public Stack getStack(){
		
		Stack stack =  getTrackerBean().getStack();
		return stack;
	}
	
	

	public Object getCurrentState() {
		try	{ return getStack().peek();
		}
		catch(EmptyStackException e)	{
			return null;
		}
	}

	public void popState() {
		getStack().pop();
	}

	public void pushState(Object state) {
		getStack().push( state);
	}
	

	public void startService() throws Exception {
		log.info("start service TrackerService");
		RequestContextUtil.currentTracker = this;
	}

	public void stopService() throws Exception {
		log.info("stop service TrackerService");
		RequestContextUtil.currentTracker = null;
	}

	public HttpSession getHttpSession() {
		
		return getTrackerBean().getSession();
	}

	public void setHttpSession(HttpSession session) {
		getTrackerBean().setSession(session);
	}
	
	public HttpServletRequest getHttpRequest() {
		
		return getTrackerBean().getRequest();
	}

	public void setHttpRequest(HttpServletRequest request) {
		getTrackerBean().setRequest(request);
	}


	public void init() {
		trackerBean.set(null);
		
	}
	
	
}
