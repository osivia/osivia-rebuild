package org.osivia.portal.kernel.tomcat;

import java.util.HashSet;
import java.util.Set;

import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;

/**
 * Tomcat servlet container context.
 *
 * @author Cédric Krommenhoek
 * @see ContainerListener
 * @see LifecycleListener
 */
public class TomcatServletContainerContext implements ContainerListener, LifecycleListener {

    /** Engine. */
    private final Engine engine;
    /** Monitored hosts. */
    private final Set<String> monitoredHosts;
    /** Monitored contexts. */
    private final Set<String> monitoredContexts;


    /**
     * Constructor.
     *
     * @param engine engine
     */
    public TomcatServletContainerContext(Engine engine) {
        super();
        this.engine = engine;
        this.monitoredHosts = new HashSet<String>();
        this.monitoredContexts = new HashSet<String>();
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

            this.start(context);

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

            this.stop(context);

            context.removeLifecycleListener(this);
        }
    }


    /**
     * Start context.
     * 
     * @param context context
     */
    private void start(Context context) {
        // TODO
    }


    /**
     * Stop context.
     * 
     * @param context context
     */
    private void stop(Context context) {
        // TODO
    }


}
