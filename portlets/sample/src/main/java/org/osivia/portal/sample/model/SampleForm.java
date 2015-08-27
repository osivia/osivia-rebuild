package org.osivia.portal.sample.model;

/**
 * Sample form.
 * 
 * @author Cédric Krommenhoek
 */
public class SampleForm {

    /** First name. */
    private String firstName;
    /** Last name. */
    private String lastName;


    /**
     * Constructor.
     */
    public SampleForm() {
        super();
    }


    /**
     * Getter for firstName.
     *
     * @return the firstName
     */
    public String getFirstName() {
        return this.firstName;
    }


    /**
     * Setter for firstName.
     *
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * Getter for lastName.
     *
     * @return the lastName
     */
    public String getLastName() {
        return this.lastName;
    }


    /**
     * Setter for lastName.
     *
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
