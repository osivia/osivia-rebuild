package org.osivia.portal.kernel.portal;

import java.io.Serializable;
import java.util.LinkedList;

import javax.xml.namespace.QName;

/**
 * Event route.
 *
 * @author Cédric Krommenhoek
 */
public class EventRoute {

    /** Parent. */
    private final EventRoute parent;
    /** Name. */
    private final QName name;
    /** Payload. */
    private final Serializable payload;
    /** Source. */
    private final String source;
    /** Destination. */
    private final String destination;
    /** Children. */
    private final LinkedList<EventRoute> children;

    /** Event acknowledgement. */
    private EventAcknowledgement acknowledgement;


    /**
     * Constructor.
     *
     * @param parent parent
     * @param name name
     * @param payload payload
     * @param source source
     * @param destination destination
     */
    public EventRoute(EventRoute parent, QName name, Serializable payload, String source, String destination) {
        super();
        this.parent = parent;
        this.name = name;
        this.payload = payload;
        this.source = source;
        this.destination = destination;
        this.children = new LinkedList<EventRoute>();
    }


    /**
     * Getter for parent.
     *
     * @return the parent
     */
    public EventRoute getParent() {
        return this.parent;
    }

    /**
     * Getter for name.
     *
     * @return the name
     */
    public QName getName() {
        return this.name;
    }

    /**
     * Getter for payload.
     *
     * @return the payload
     */
    public Serializable getPayload() {
        return this.payload;
    }

    /**
     * Getter for source.
     *
     * @return the source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Getter for destination.
     *
     * @return the destination
     */
    public String getDestination() {
        return this.destination;
    }

    /**
     * Getter for children.
     *
     * @return the children
     */
    public LinkedList<EventRoute> getChildren() {
        return this.children;
    }

    /**
     * Getter for acknowledgement.
     *
     * @return the acknowledgement
     */
    public EventAcknowledgement getAcknowledgement() {
        return this.acknowledgement;
    }

    /**
     * Setter for acknowledgement.
     * 
     * @param acknowledgement the acknowledgement to set
     */
    public void setAcknowledgement(EventAcknowledgement acknowledgement) {
        this.acknowledgement = acknowledgement;
    }

}
