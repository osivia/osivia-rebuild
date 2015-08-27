package org.osivia.portal.kernel.tomcat;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.jboss.portal.web.command.CommandServlet;
import org.jboss.portal.web.spi.WebAppContext;

/**
 * Tomcat web app context.
 *
 * @author Cédric Krommenhoek
 * @see WebAppContext
 */
public class TomcatWebAppContext implements WebAppContext {

    /** Context. */
    private final Context context;
    /** Servlet context. */
    private final ServletContext servletContext;
    /** Class loader. */
    private final ClassLoader classLoader;
    /** Context path. */
    private final String contextPath;

    /** Command servlet. */
    private Wrapper commandServlet;


    /**
     * Constructor.
     *
     * @param context context
     */
    public TomcatWebAppContext(Context context) {
        super();
        this.context = context;

        this.servletContext = context.getServletContext();
        this.classLoader = context.getLoader().getClassLoader();
        this.contextPath = context.getPath();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws Exception {
        try {
            this.commandServlet = this.context.createWrapper();
            this.commandServlet.setName("JBossServlet");
            this.commandServlet.setLoadOnStartup(0);
            this.commandServlet.setServletClass(CommandServlet.class.getName());
            this.context.addChild(this.commandServlet);
            this.context.addServletMapping("/jbossportlet", "JBossServlet");
        } catch (Exception e) {
            this.cleanup();
            throw e;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        this.cleanup();
    }


    /**
     * Cleanup.
     */
    private void cleanup() {
        if (this.commandServlet != null) {
            try {
                this.context.removeServletMapping("jbossportlet");
                this.context.removeChild(this.commandServlet);
            } catch (Exception e) {
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getContextPath() {
        return this.contextPath;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean importFile(String parentDirRelativePath, String name, InputStream source, boolean overwrite) throws IOException {
        return false;
    }

}
