
package org.osivia.portal.core.cms.sessions;

import org.apache.log4j.Logger;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.core.ajax.AjaxResponseHandler;
import org.osivia.portal.core.imports.IPortalImportManager;
import org.osivia.portal.core.sessions.CMSSessionRecycle;
import org.osivia.portal.core.sessions.ICMSSessionStorage;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.TransactionManager;

/**
 * This interceptor start and terminate cms sessions.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8786 $
 */
public class CMSSessionInterceptor extends ServerInterceptor implements ICMSSessionStorage,IPortalImportManager {

    /** The current sessions. */
    ThreadLocal<Map<String, CMSSessionRecycle>> currentSessions = new ThreadLocal<Map<String, CMSSessionRecycle>>();

    /** The dirty proxies. */
    Queue<CMSSessionRecycle> fifo = new ConcurrentLinkedQueue<CMSSessionRecycle>();

    
    private static final Logger log = Logger.getLogger(CMSSessionInterceptor.class);
    
    private boolean pauseRequests = false;

    protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {
        try {
            
            // Pause requests during imports
            while( pauseRequests)   {
                try {
                    //log.info("stopping request");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
             
            
            invocation.invokeNext();
        } finally {
            Map<String, CMSSessionRecycle> sessions = currentSessions.get();
            if (sessions != null) {
                // Recycle proxy
                for (CMSSessionRecycle session : sessions.values()) {
                    
                    fifo.add(session);
                    
                    if( session == null)
                        log.error("insert null session");
                        
                }

                // dereference sessions
                currentSessions.set(null);
            }
        }
    }

    @Override
    public void storeSession(CMSSessionRecycle cmsSession) {
        Map<String, CMSSessionRecycle> sessions = currentSessions.get();
        if (sessions == null) {
            sessions = new Hashtable<>();
            currentSessions.set(sessions);
        }
        sessions.put(getKey(cmsSession.getCmsContext()), cmsSession);
    }

    @Override
    public CMSSession getSession(CMSContext cmsContext) {
        Map<String, CMSSessionRecycle> sessions = currentSessions.get();
        CMSSession session = null;
        if (sessions != null) {
            session = (CMSSession) sessions.get(getKey(cmsContext));
            if( session != null)    {
                // resynchronize request (dirty ThreadLocal)
                /*
                if( cmsContext.getPortalControllerContext() != null)
                    log.debug("resynchronize request "+ cmsContext.getPortalControllerContext().getHttpServletRequest());
                else
                    log.debug("resynchronize portalControllerContext = null ");
                
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                
                for(int i=8; i< Math.min(stack.length, 15); i++) {
                    log.debug("-"+stack[i].getClassName()+"."+stack[i].getMethodName());
                }
                */
                
                ((CMSSessionRecycle) session).setCMSContext(cmsContext);
            }
        }

        if (session == null) {
            if (!fifo.isEmpty()) {
                // Get recycled proxy

                CMSSessionRecycle sessionrecyle = null;
                
                try	{
                	sessionrecyle = fifo.remove();
                } catch( NoSuchElementException e)	{
                	sessionrecyle = null;
                }
                
                if(sessionrecyle != null)	{
	                sessionrecyle.setCMSContext(cmsContext);
	                
	                /*
	                
	                if( cmsContext.getPortalControllerContext() != null)
	                    log.debug("recycle request "+ cmsContext.getPortalControllerContext().getHttpServletRequest());
	                else
	                    log.debug("recycle portalControllerContext = null ");
	                
	                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
	                
	                for(int i=8; i< Math.min(stack.length, 15); i++) {
	                    log.debug("-"+stack[i].getClassName()+"."+stack[i].getMethodName());
	                }
	                */
	                
	                
	                
	                session = (CMSSession) sessionrecyle;
	                
	                storeSession( sessionrecyle);
                }
           }
        }

        return session;

    }

    protected String getKey(CMSContext cmsContext) {
        String locale = "";
        if(  cmsContext.getlocale() !=null)
            locale = cmsContext.getlocale().toString();
        

        
        return locale + "." + cmsContext.isPreview() + "." + cmsContext.isSuperUserMode() + "." + (cmsContext.getPortalControllerContext().getHttpServletRequest() != null);
        
        
    }
    
    @Override
    public void stopRequests() {
        pauseRequests = true;
        
        // wait for pending requests to stop
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }       
        
    }

    @Override
    public void restartRequests() {
        pauseRequests = false;
        
    }
}
