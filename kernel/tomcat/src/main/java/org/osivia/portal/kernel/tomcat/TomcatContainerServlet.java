package org.osivia.portal.kernel.tomcat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.catalina.Container;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Engine;
import org.apache.catalina.Wrapper;

/**
 * Tomcat container servlet.
 *
 * @author Cédric Krommenhoek
 * @see HttpServlet
 * @see ContainerServlet
 */
public class TomcatContainerServlet extends HttpServlet implements ContainerServlet {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;


    /** Wrapper. */
    private Wrapper wrapper;
    /** Tomcat servlet container context. */
    private TomcatServletContainerContext containerContext;
    /** Started indicator. */
    private boolean started;


    /**
     * Constructor.
     */
    public TomcatContainerServlet() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Wrapper getWrapper() {
        return this.wrapper;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;

        if (wrapper != null) {
            this.attemptStart();
        } else {
            this.attemptStop();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        this.started = true;
        this.attemptStart();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        this.started = false;
        this.attemptStop();
    }


    private void attemptStart() {
        if (this.started && this.wrapper != null) {
            this.start();
        }
    }

    private void attemptStop() {
        if (!this.started || this.wrapper == null) {
            this.stop();
        }
    }

    private void start() {
        Container container = this.wrapper;
        while (container.getParent() != null) {
            container = container.getParent();
            if (container instanceof Engine) {
                Engine engine = (Engine) container;
                this.containerContext = new TomcatServletContainerContext(engine);
                this.containerContext.start();
                break;
            }
        }
    }

    private void stop() {
        if (this.containerContext != null) {
            this.containerContext.stop();
            this.containerContext = null;
        }
    }

}
