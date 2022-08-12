package org.osivia.portal.api.cms.service;

import java.io.InputStream;
import java.io.OutputStream;

public interface StreamableRepository {
    
    void restore();
    
    void saveTo(OutputStream out);
    
    void readFrom(InputStream in);

}
