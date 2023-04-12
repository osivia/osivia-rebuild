package org.osivia.portal.api.cms.service;

import java.io.InputStream;
import java.io.OutputStream;

public interface StreamableRepository {
    
    public void checkAndReload();
    
    void saveTo(OutputStream out);
    
    void readFrom(InputStream in);
    
    void merge(InputStream in, MergeParameters params, OutputStream out) throws MergeException;

}
