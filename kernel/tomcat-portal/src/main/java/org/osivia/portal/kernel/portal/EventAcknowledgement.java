package org.osivia.portal.kernel.portal;

import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;

/**
 * Event acknowledgement.
 *
 * @author Cédric Krommenhoek
 */
public class EventAcknowledgement {

    /**
     * Constructor.
     */
    public EventAcknowledgement() {
        super();
    }


    /**
     * Consumed event acknowledgement.
     *
     * @author Cédric Krommenhoek
     * @see EventAcknowledgement
     */
    public static class Consumed extends EventAcknowledgement {

        /** Portlet invovation response. */
        private final PortletInvocationResponse response;


        /**
         * Constructor.
         *
         * @param response portlet invocation response
         */
        public Consumed(PortletInvocationResponse response) {
            super();
            this.response = response;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Consumed " + this.response.toString();
        }


        /**
         * Getter for response.
         *
         * @return the response
         */
        public PortletInvocationResponse getResponse() {
            return this.response;
        }

    }


    /**
     * Failed event acknowledgement.
     *
     * @author Cédric Krommenhoek
     * @see EventAcknowledgement
     */
    public static class Failed extends EventAcknowledgement {

        /** Throwable. */
        private final Throwable throwable;


        /**
         * Constructor.
         *
         * @param throwable throwable
         */
        public Failed(Throwable throwable) {
            super();
            this.throwable = throwable;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Failed";
        }


        /**
         * Getter for throwable.
         *
         * @return the throwable
         */
        public Throwable getThrowable() {
            return this.throwable;
        }

    }


    /**
     * Discarded event acknowledgement.
     *
     * @author Cédric Krommenhoek
     * @see EventAcknowledgement
     */
    public static class Discarded extends EventAcknowledgement {

        /** Cause. */
        private final int cause;


        /**
         * Constructor.
         *
         * @param cause cause
         */
        public Discarded(int cause) {
            super();
            this.cause = cause;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Discarded";
        }


        /**
         * Getter for cause.
         * 
         * @return the cause
         */
        public int getCause() {
            return this.cause;
        }

    }

}
