package org.osivia.portal.kernel.tomcat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.StandardContext;
import org.jboss.portal.web.RequestDispatchCallback;
import org.jboss.portal.web.command.CommandDispatcher;
import org.jboss.portal.web.impl.DefaultServletContainerFactory;
import org.jboss.portal.web.spi.ServletContainerContext;
import org.jboss.portal.web.spi.WebAppContext;

/**
 * Tomcat servlet container context.
 *
 * @author Cédric Krommenhoek
 * @see ServletContainerContext
 * @see ContainerListener
 * @see LifecycleListener
 */
public class TomcatServletContainerContext implements ServletContainerContext, ContainerListener, LifecycleListener {

    /** Engine. */
    private final Engine engine;
    /** Command dispatcher. */
    private final CommandDispatcher dispatcher;
    /** Monitored hosts. */
    private final Set<String> monitoredHosts;
    /** Monitored contexts. */
    private final Set<String> monitoredContexts;

    /** Registration. */
    private Registration registration;


    /**
     * Constructor.
     *
     * @param engine engine
     */
    public TomcatServletContainerContext(Engine engine) {
        super();
        this.engine = engine;

        this.dispatcher = new CommandDispatcher();
        this.monitoredHosts = new HashSet<String>();
        this.monitoredContexts = new HashSet<String>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object include(ServletContext targetServletContext, HttpServletRequest request, HttpServletResponse response, RequestDispatchCallback callback,
            Object handback) throws ServletException, IOException {
        return this.dispatcher.include(targetServletContext, request, response, callback, handback);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setCallback(Registration registration) {
        this.registration = registration;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void unsetCallback(Registration registration) {
        this.registration = null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        Object source = event.getSource();
        if (source instanceof Context) {
            Context context = (Context) source;

            if (Lifecycle.AFTER_START_EVENT.equals(event.getType())) {
                this.start(context);
            } else if (Lifecycle.BEFORE_STOP_EVENT.equals(event.getType())) {
                this.stop(context);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void containerEvent(ContainerEvent event) {
        if (event.getData() instanceof Host) {
            Host host = (Host) event.getData();

            if (Container.ADD_CHILD_EVENT.equals(event.getType())) {
                this.registerHost(host);
            } else if (Container.REMOVE_CHILD_EVENT.equals(event.getType())) {
                this.unregisterHost(host);
            }
        } else if (event.getData() instanceof StandardContext) {
            StandardContext context = (StandardContext) event.getData();

            if (Container.ADD_CHILD_EVENT.equals(event.getType())) {
                this.registerContext(context);
            } else if (Container.REMOVE_CHILD_EVENT.equals(event.getType())) {
                this.unregisterContext(context);
            }
        }
    }


    /**
     * Start servlet container context.
     */
    public void start() {
        DefaultServletContainerFactory.registerContext(this);

        Container[] childrenContainers = this.engine.findChildren();
        for (Container childContainer : childrenContainers) {
            if (childContainer instanceof Host) {
                Host host = (Host) childContainer;
                this.registerHost(host);
            }
        }

        this.engine.addContainerListener(this);
    }


    /**
     * Stop servlet container context.
     */
    public void stop() {
        this.engine.removeContainerListener(this);

        Container[] childrenContainers = this.engine.findChildren();
        for (Container childContainer : childrenContainers) {
            if (childContainer instanceof Host) {
                Host host = (Host) childContainer;
                this.unregisterHost(host);
            }
        }

        this.registration.cancel();
        this.registration = null;
    }


    /**
     * Register host.
     *
     * @param host host
     */
    private void registerHost(Host host) {
        if (!this.monitoredHosts.contains(host.getName())) {
            Container[] childrenContainers = host.findChildren();
            for (Container childContainer : childrenContainers) {
                if (childContainer instanceof StandardContext) {
                    StandardContext context = (StandardContext) childContainer;
                    this.registerContext(context);
                }
            }

            host.addContainerListener(this);

            this.monitoredHosts.add(host.getName());
        }
    }


    /**
     * Unregister host.
     *
     * @param host host
     */
    private void unregisterHost(Host host) {
        if (this.monitoredHosts.contains(host.getName())) {
            this.monitoredHosts.remove(host.getName());

            host.removeContainerListener(this);

            Container[] childrenContainers = host.findChildren();
            for (Container childContainer : childrenContainers) {
                if (childContainer instanceof StandardContext) {
                    StandardContext context = (StandardContext) childContainer;
                    this.unregisterContext(context);
                }
            }
        }
    }


    /**
     * Register context.
     *
     * @param context standard context
     */
    private void registerContext(StandardContext context) {
        if (!this.monitoredContexts.contains(context.getName())) {
            context.addLifecycleListener(this);

            if (LifecycleState.STARTED.equals(context.getState())) {
                this.start(context);
            }

            this.monitoredContexts.add(context.getName());
        }
    }


    /**
     * Unregister context.
     *
     * @param context standard context
     */
    private void unregisterContext(StandardContext context) {
        if (this.monitoredContexts.contains(context.getName())) {
            this.monitoredContexts.remove(context.getName());

            if (LifecycleState.STARTED.equals(context.getState())) {
                this.stop(context);
            }

            context.removeLifecycleListener(this);
        }
    }


    /**
     * Start context.
     *
     * @param context context
     */
    private void start(Context context) {
        if (this.registration != null) {
            WebAppContext webAppContext = new TomcatWebAppContext(context);
            this.registration.registerWebApp(webAppContext);
        }
    }


    /**
     * Stop context.
     *
     * @param context context
     */
    private void stop(Context context) {
        if (this.registration != null) {
            this.registration.unregisterWebApp(context.getPath());
        }
    }

}
