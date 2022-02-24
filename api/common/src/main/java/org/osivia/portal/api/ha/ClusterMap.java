package org.osivia.portal.api.ha;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;



/**
 * The object which is shared
 */

public class ClusterMap implements  Serializable {


    /** The reception ts. created on local objet replication */
    private transient long  receptionTs= System.currentTimeMillis();

    private Map<String, String> object;

    private static final long serialVersionUID = 7279382806295593212L;

    public ClusterMap() {
        super();
    }


    public ClusterMap( Map<String, String> object) {
        super();
        this.object = object;
    }



    public Map<String, String> getObject() {
        return object;
    }


    public long getReceptionTs() {
        return receptionTs;
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            receptionTs = System.currentTimeMillis();
        }
    
    
}
