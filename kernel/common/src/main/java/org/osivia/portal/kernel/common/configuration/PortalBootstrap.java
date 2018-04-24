package org.osivia.portal.kernel.common.configuration;

import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap;

/**
 * Portal bootstrap.
 * 
 * @author Cédric Krommenhoek
 * @see BasicBootstrap
 */
public class PortalBootstrap extends BasicBootstrap {

    /** Callback. */
    private final PortalListener callback;


    /**
     * Constructor.
     * 
     * @param callback callback
     */
    public PortalBootstrap(PortalListener callback) {
        super();
        this.callback = callback;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void bootstrap() throws Throwable {
        super.bootstrap();
        this.callback.bootstrap();
    }

}
