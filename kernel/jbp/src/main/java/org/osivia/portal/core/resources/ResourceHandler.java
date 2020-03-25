package org.osivia.portal.core.resources;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handle the parsing of portlet resources
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.jboss.portal.core.controller.ajax.AjaxResponseHandler;
import org.jboss.portal.theme.impl.render.dynamic.response.NonParsableResource;
import org.jboss.portal.theme.impl.render.dynamic.response.PageResource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Parse jboss-portlet.xml resources and convert them into pageResource
 * 
 * @author Jean-Sébastien
 */
public class ResourceHandler extends DefaultHandler {

    private static Map<String, PageResource> cache = new Hashtable<String, PageResource>();
    
    private static final Logger logger = Logger.getLogger(ResourceHandler.class);    

    private static SAXParser saxParser = null;


    // current resource
    private PageResource resource = null;


    // getter method for employee list
    public PageResource getResource() {
        return resource;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        resource = new PageResource();
        resource.setTag(qName.toUpperCase());
        resource.setRel(attributes.getValue("rel"));
        resource.setType(attributes.getValue("type"));
        resource.setHref(attributes.getValue("href"));
        resource.setMedia(attributes.getValue("media"));
        resource.setRel(attributes.getValue("rel"));
        resource.setSrc(attributes.getValue("src"));
    }

    


    /**
     * Compute resource.
     *
     * @param definition the definition
     * @return the resource
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException the SAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static synchronized PageResource computeResource(String definition) throws Exception  {
        PageResource resource;
        try {
            if (saxParser == null) {
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                saxParser = saxParserFactory.newSAXParser();
            }
            ResourceHandler handler = new ResourceHandler();
            InputStream targetStream = new ByteArrayInputStream(definition.getBytes());
            saxParser.parse(targetStream, handler);
            resource = handler.getResource();
            cache.put(definition, resource);
        } catch (Exception e) {
            logger.error("error during parsing of "+ definition, e);
            
            resource = new NonParsableResource();
            cache.put(definition, resource);
            throw e;
        }
        return resource;
    }

    public static PageResource getResource(String definition) throws Exception {
        PageResource resource = cache.get(definition);
        if (resource == null) {
            resource = computeResource(definition);
        }
        return resource;
    }


}