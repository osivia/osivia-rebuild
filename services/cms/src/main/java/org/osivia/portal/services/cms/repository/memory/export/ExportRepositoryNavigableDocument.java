package org.osivia.portal.services.cms.repository.memory.export;


public class ExportRepositoryNavigableDocument {
    
    public String getParentId() {
        return parentId;
    }
    
    public ExportRepositoryDocument getDoc() {
        return doc;
    }
    public ExportRepositoryNavigableDocument(String parentId, ExportRepositoryDocument doc) {
        super();
        this.parentId = parentId;
        this.doc = doc;
    }
    private final String parentId;
    private final ExportRepositoryDocument doc;
}
