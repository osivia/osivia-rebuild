package org.osivia.portal.kernel.portal;

import javax.xml.namespace.QName;

/**
 * Page parameter def.
 *
 * @author Cédric Krommenhoek
 */
public class PageParameterDef {

    /** Name. */
    private final QName name;
    /** Value. */
    private final String value;
    /** Frozen indicator. */
    private final boolean frozen;


    /**
     * Constructor.
     *
     * @param name name
     * @param value value
     * @param frozen frozen indicator
     */
    public PageParameterDef(QName name, String value, boolean frozen) {
        super();
        this.name = name;
        this.value = value;
        this.frozen = frozen;
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
     * Getter for value.
     * 
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Getter for frozen.
     * 
     * @return the frozen
     */
    public boolean isFrozen() {
        return this.frozen;
    }

}
