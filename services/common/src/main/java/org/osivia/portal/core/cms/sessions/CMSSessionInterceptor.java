
package org.osivia.portal.core.cms.sessions;

import org.apache.log4j.Logger;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.core.ajax.AjaxResponseHandler;
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

import javax.transaction.TransactionManager;

/**
 * This interceptor start and terminate cms sessions.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8786 $
 */
public class CMSSessionInterceptor extends ServerInterceptor implements ICMSSessionStorage {

    /** The current sessions. */
    ThreadLocal<Map<String, CMSSessionRecycle>> currentSessions = new ThreadLocal<Map<String, CMSSessionRecycle>>();

    /** The dirty proxies. */
    Queue<CMSSessionRecycle> fifo = new ConcurrentLinkedQueue<CMSSessionRecycle>();

    
    private static final Logger log = Logger.getLogger(CMSSessionInterceptor.class);

    protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {
        try {
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
                ((CMSSessionRecycle) session).setCMSContext(cmsContext);
            }
        }

        if (session == null) {
            if (!fifo.isEmpty()) {
                // Get recycled proxy

                CMSSessionRecycle sessionrecyle = null;

                sessionrecyle = fifo.remove();

                
                
                sessionrecyle.setCMSContext(cmsContext);
                session = (CMSSession) sessionrecyle;
                
                storeSession( sessionrecyle);
           }
        }

        return session;

    }

    protected String getKey(CMSContext cmsContext) {
        String locale = "";
        if(  cmsContext.getlocale() !=null)
            locale = cmsContext.getlocale().toString();
        return locale + "." + cmsContext.isPreview() + "." + cmsContext.isSuperUserMode();
    }
}
