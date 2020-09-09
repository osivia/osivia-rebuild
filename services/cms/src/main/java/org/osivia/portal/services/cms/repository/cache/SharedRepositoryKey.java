package org.osivia.portal.services.cms.repository.cache;


/**
 * The Class SharedRepositoryKey.
 */
public class SharedRepositoryKey {
    
    /** The repository name. */
    private String repositoryName;
    
    /** The preview. */
    private boolean preview;

    
    /**
     * Gets the repository name.
     *
     * @return the repository name
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    
    /**
     * Checks if is preview.
     *
     * @return true, if is preview
     */
    public boolean isPreview() {
        return preview;
    }


    /**
     * Instantiates a new shared repository key.
     *
     * @param repositoryName the repository name
     * @param preview the preview
     */
    public SharedRepositoryKey(String repositoryName, boolean preview) {
        super();
        this.repositoryName = repositoryName;
        this.preview = preview;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (preview ? 1231 : 1237);
        result = prime * result + ((repositoryName == null) ? 0 : repositoryName.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SharedRepositoryKey other = (SharedRepositoryKey) obj;
        if (preview != other.preview)
            return false;
        if (repositoryName == null) {
            if (other.repositoryName != null)
                return false;
        } else if (!repositoryName.equals(other.repositoryName))
            return false;
        return true;
    }
    

}
