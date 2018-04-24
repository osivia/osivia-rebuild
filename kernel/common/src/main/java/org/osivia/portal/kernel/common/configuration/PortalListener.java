package org.osivia.portal.kernel.common.configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.beans.metadata.plugins.AbstractBeanMetaData;
import org.jboss.beans.metadata.plugins.AbstractConstructorMetaData;
import org.jboss.beans.metadata.plugins.AbstractValueMetaData;
import org.jboss.kernel.spi.dependency.KernelController;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.jboss.kernel.spi.event.KernelEvent;
import org.jboss.kernel.spi.event.KernelEventListener;
import org.jboss.kernel.spi.registry.KernelRegistry;
import org.jboss.kernel.spi.registry.KernelRegistryEntry;
import org.jboss.portal.common.mc.bootstrap.ServletContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Portal listener.
 *
 * @author Cédric Krommenhoek
 * @see ServletContextListener
 * @see KernelEventListener
 */
@WebListener
public class PortalListener implements ServletContextListener, KernelEventListener {

    /** Handback. */
    private static final Object HANDBACK = "portal";


    /** Servlet context. */
    private ServletContext servletContext;
    /** Bootstrap. */
    private PortalBootstrap bootstrap;
    /** Servlet context controller context. */
    private KernelControllerContext servletContextControllerContext;
    /** Registered indicator. */
    private boolean registered;


    @Autowired
    private ApplicationContext applicationContext;


    /** Log. */
    private final Log log;


    /**
     * Constructor.
     */
    public PortalListener() {
        super();
        this.log = LogFactory.getLog(this.getClass());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        this.servletContext = event.getServletContext();
        this.bootstrap = new PortalBootstrap(this);
        this.bootstrap.run();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        KernelRegistry registry = this.bootstrap.getKernel().getRegistry();
        KernelController controller = this.bootstrap.getKernel().getController();

        if (this.servletContextControllerContext != null) {
            controller.uninstall("ServletContext");
        }

        if (this.registered) {
            this.registered = false;

            try {
                registry.unregisterListener(this, null, HANDBACK);
            } catch (Throwable e) {
                this.log.error("Cannot unregister kernel registry listener", e);
            }
        }

        this.servletContext = null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(KernelEvent event, Object handback) {
        Object context = event.getContext();

        if (context instanceof String) {
            String key = (String) context;

            // We add/remove every bean except the servlet context itself...
            if (!"ServletContext".equals(key)) {
                String type = event.getType();
                if ("KERNEL_REGISTRY_REGISTERED".equals(type)) {
                    KernelRegistryEntry entry = this.bootstrap.getKernel().getRegistry().getEntry(context);
                    Object target = entry.getTarget();

                    this.servletContext.setAttribute(key, target);
                } else if ("KERNEL_REGISTRY_UNREGISTERED".equals(type)) {
                    this.servletContext.removeAttribute(key);
                }
            }
        }
    }


    /**
     * Bootstrap the kernel.
     */
    public void bootstrap() {
        KernelRegistry registry = this.bootstrap.getKernel().getRegistry();
        KernelController controller = this.bootstrap.getKernel().getController();

        try {
            registry.registerListener(this, null, HANDBACK);

            this.registered = true;

            AbstractBeanMetaData beanMetaData = new AbstractBeanMetaData("ServletContext", ServletContext.class.getName());
            AbstractConstructorMetaData constructorMetaData = new AbstractConstructorMetaData();
            constructorMetaData.setFactory(new AbstractValueMetaData(new ServletContextFactory(this.servletContext)));
            constructorMetaData.setFactoryMethod("getInstance");
            beanMetaData.setConstructor(constructorMetaData);
            this.servletContextControllerContext = controller.install(beanMetaData);


        } catch (Throwable e) {
            this.log.error("Portal kernel bootstrap failed", e);
        }
    }

}
