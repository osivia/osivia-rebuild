package org.osivia.portal.api.cms;



/**
 * The Class UpdateInformations.
 */
public class UpdateInformations {
    
    /** The document. */
    private final UniversalID documentID;
    
    /** The document. */
    private final UniversalID spaceID;
 
    /** The scope. */
    private final UpdateScope scope;
    
    /** the repository **/
    private final String repository;

    
    

    /** Is the update action asynchronous */
    private boolean async = false;
    

    /**
     * Instantiates a new update informations.
     */
    public UpdateInformations( UniversalID documentID, UniversalID spaceID) {
       this( documentID, spaceID, UpdateScope.SCOPE_SPACE, false);
       
    }
    
    public UpdateInformations( String repository) {
        this( null, null, UpdateScope.SCOPE_REPOSITORY, false, repository);
        
    }
    
    
    /**
     * Instantiates a new update informations.
     *
     * @param documentID the document ID
     * @param async the async
     */
    public UpdateInformations(UniversalID documentID, UniversalID spaceID, UpdateScope scope, boolean async) {
        this(documentID, spaceID, scope, async, spaceID.getRepositoryName());
    }
    
    
    public UpdateInformations(UniversalID documentID, UniversalID spaceID, UpdateScope scope, boolean async, String repository) {
        super();
        this.documentID = documentID;
        this.spaceID = spaceID;
        this.async = async;
        this.scope = scope;
        this.repository = repository;
    }

    /**
     * Checks if is async.
     *
     * @return true, if is async
     */
    public boolean isAsync() {
        return async;
    }

    
    /**
     * Gets the document ID.
     *
     * @return the document ID
     */
    public UniversalID getDocumentID() {
        return documentID;
    }
    
    
    /**
     * Gets the space ID.
     *
     * @return the space ID
     */
    public UniversalID getSpaceID() {
        return spaceID;
    }

      
    /**
     * Gets the scope.
     *
     * @return the scope
     */
    public UpdateScope getScope() {
        return scope;
    }

    /**
     * Gets the repository
     * 
     * @return
     */
    public String getRepository() {
        return repository;
    }


}
