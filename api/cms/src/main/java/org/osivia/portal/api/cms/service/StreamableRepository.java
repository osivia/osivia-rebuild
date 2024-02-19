package org.osivia.portal.api.cms.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface StreamableRepository {
    
    public void checkAndReload();
    
    void saveTo(OutputStream out);
    
    void readFrom(InputStream in);
    
    void merge(InputStream in, MergeParameters params, OutputStream out) throws MergeException;
    
    public String getVersion();
    
    public StreamableCheckResults checkInputFile( InputStream in);

}
