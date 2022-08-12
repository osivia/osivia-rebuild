/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.ecm.automation.client.model;

import java.io.Serializable;

import org.osivia.portal.api.cms.EcmDocument;

/**
 * A immutable document. You cannot modify documents. Documents are as they are
 * returned by the server. To modify documents use operations.
 * <p>
 * You need to create your own wrapper if you need to access the document
 * properties in a multi-level way. This is a flat representation of the
 * document.
 * <p>
 * Possible property value types:
 * <ul>
 * <li>String
 * <li>Number
 * <li>Date
 * <ul>
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class NxDocumentMock implements EcmDocument, Serializable {

    /** The title. */
    private String type;
    
    
    public void setType(String type) {
        this.type = type;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getPath() {
        return path;
    }

    
    public void setPath(String path) {
        this.path = path;
    }

   
    public String getName() {
        return name;
    }

    /** The name. */
    private String name;

    
    /** The path. */
    private String path;
    
    
    /** The title. */
    private String title;
 
    
    public String getTitle() {
        return title;
    }


    
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Instantiates a new document impl.
     *
     * @param id the id
     * @param properties the properties
     */
    public NxDocumentMock( ) {
        super();

    }


    @Override
    public String getType() {
        if( type != null)
            return type;
        else
            return "document";
    }



     
   

  
}
