package org.osivia.portal.services.cms.repository.memory.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryFolder;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.MergeException;
import org.osivia.portal.api.cms.service.MergeParameters;
import org.osivia.portal.services.cms.repository.memory.FileRepository;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class FileUtils {

    FileRepository repository;


    protected static final Log log = LogFactory.getLog(FileRepository.class);


    public FileUtils(FileRepository repository) {
        super();
        this.repository = repository;
    }


    public void importFile(File importFile) {

        FileInputStream importStream;
        try {
            importStream = new FileInputStream(importFile);
        } catch (FileNotFoundException e1) {
            throw new RuntimeException(e1);
        }
        try {
            ObjectMapper om = buildJSonMapper();
            ExportRepositoryBean importDatas = om.readValue(importStream, ExportRepositoryBean.class);

            for (ExportRepositoryDocument doc : importDatas.documents) {
                importDocument(doc, null, null);
            }

            log.info("importFile");

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                importStream.close();
            } catch (IOException e) {
                log.error(e);
            }
            importStream = null;
        }
    }

    public String getCheckSum(File importFile) {
        try {
            return DigestUtils.md5Hex(new FileInputStream(importFile));
        } catch (Exception e) {
            return null;
        }
    }


    private ObjectMapper buildJSonMapper() {
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        om.disable(MapperFeature.AUTO_DETECT_GETTERS);
        om.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
        om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        om.setSerializationInclusion(Include.NON_NULL);
        return om;
    }


    private String importDocument(ExportRepositoryDocument doc, String spaceId, String parentId) throws CMSException {


        List<String> children = new ArrayList<>();
        MemoryRepositoryDocument docToCreate;

        if (StringUtils.equals("space", doc.type))
            spaceId = doc.id;

        if (CollectionUtils.isNotEmpty(doc.children)) {
            for (ExportRepositoryDocument child : doc.children) {
                children.add(importDocument(child, spaceId, doc.id));
            }
        }

        if (StringUtils.equals("page", doc.type)) {
            MemoryRepositoryPage page = new MemoryRepositoryPage(repository, doc.id, doc.id, doc.templateId, parentId, spaceId, children, doc.properties, doc.moduleRefs);

            page.setInheritedRegions(doc.inheritedRegions != null ? doc.inheritedRegions : new ArrayList<>());

            docToCreate = page;
        } else if (StringUtils.equals("space", doc.type)) {
            MemoryRepositorySpace space = new MemoryRepositorySpace(repository, doc.id, doc.id, doc.templateId, children, doc.properties, doc.moduleRefs);

            space.setProfiles(doc.profiles);
            space.setStyles(doc.styles);

            docToCreate = space;
        } else if (StringUtils.equals("folder", doc.type)) {
            MemoryRepositoryFolder folder = new MemoryRepositoryFolder(repository, doc.id, doc.id, parentId, spaceId, children, doc.properties);

            docToCreate = folder;
        } else {
            MemoryRepositoryDocument document = new MemoryRepositoryDocument(repository, doc.type, doc.id, doc.id, parentId, spaceId, children, doc.properties);

            docToCreate = document;
        }


        docToCreate.setACL(doc.acls != null ? doc.acls : new ArrayList<>());

        repository.addDocument(doc.id, docToCreate);

        return docToCreate.getInternalID();

    }

    private ExportRepositoryDocument saveDocument(MemoryRepositoryDocument srcDoc, Map<String, RepositoryDocument> documents, boolean insertChildren) {

        ExportRepositoryDocument targetDoc = new ExportRepositoryDocument();

        targetDoc.id = srcDoc.getInternalID();
        targetDoc.type = srcDoc.getType();

        targetDoc.properties = srcDoc.getProperties();
        targetDoc.acls = CollectionUtils.isNotEmpty(srcDoc.getACL()) ? srcDoc.getACL() : null;

        if (srcDoc instanceof Page) {
            targetDoc.moduleRefs = ((Page) srcDoc).getModuleRefs();
            targetDoc.templateId = ((Page) srcDoc).getTemplateId();
            targetDoc.inheritedRegions = CollectionUtils.isNotEmpty(((Page) srcDoc).getInheritedRegions()) ? ((Page) srcDoc).getInheritedRegions() : null;
        }

        if (srcDoc instanceof Space) {

            Space space = (Space) srcDoc;
            targetDoc.profiles = space.getProfiles();
            targetDoc.moduleRefs = space.getModuleRefs();
            targetDoc.styles = ((Space) srcDoc).getStyles();
        }


        for (String childId : srcDoc.getChildrenId()) {
            MemoryRepositoryDocument child = (MemoryRepositoryDocument) documents.get(childId);
            if (targetDoc.children == null)
                targetDoc.children = new ArrayList<>();
            
            if( insertChildren) {
               ExportRepositoryDocument documentToInsert = saveDocument(child, documents, true);
               targetDoc.children.add(documentToInsert);
            }
        }


        return targetDoc;
    }


    public void saveTo(OutputStream out) {
        ExportRepositoryBean export = new ExportRepositoryBean();

        Map<String, RepositoryDocument> documents = repository.getDocuments();

        for (String key : documents.keySet()) {
            // TODO expose only doc by ID (not path)
            if (!key.startsWith(("/"))) {
                RepositoryDocument doc = documents.get(key);
                if (doc.getParentInternalId() == null)
                    export.documents.add((saveDocument((MemoryRepositoryDocument) doc, documents, true)));
            }
        }

        ObjectMapper om = buildJSonMapper();


        ObjectWriter ow = om.writer();
        try {
            ow.writeValue(out, export);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    
    
    private ExportRepositoryNavigableDocument searchForId( ExportRepositoryDocument doc, ExportRepositoryDocument parent,  String id)    {
        if( doc.id.equals(id))   {
            if( parent == null)
                return new ExportRepositoryNavigableDocument(null, doc);
            else
                return new ExportRepositoryNavigableDocument(parent.id, doc);
        }
        else    {
            if( doc.children != null) {
                for( ExportRepositoryDocument child : doc.children)   {
                    ExportRepositoryNavigableDocument found = searchForId( child, doc, id);
                    if( found != null)
                        return found;
                }
            }
        }
        
       // Not found
       return null;
    }
    
    
    
    
    private void sortByOrder( NavigationItem navItem, MergeParameters params, LinkedHashSet<String> modifiedPages) throws CMSException   {
        if( params.getPagesId().contains(navItem.getDocumentId().getInternalID()))
            modifiedPages.add(navItem.getDocumentId().getInternalID());
        for (NavigationItem child: navItem.getChildren())   {
            sortByOrder( child,params, modifiedPages);
        }
    }
    
    /**
     * 
     * Merge the current repository and the input file
     * Hyerachy and order are set on the base of current repository (and also not preserved from the input file)
     * @param in
     * @param params
     * @param out
     * @throws MergeException
     */
    public void merge(InputStream in, MergeParameters params, OutputStream out) throws MergeException {
        ObjectMapper om = buildJSonMapper();
        try {
            ExportRepositoryBean mergeInDatas = om.readValue(in, ExportRepositoryBean.class);
            ExportRepositoryBean mergeOutDatas = mergeInDatas;

            ExportRepositoryDocument outRoot = mergeOutDatas.documents.get(0);


            // Sort modified page by order

            LinkedHashSet<String> modifiedPages = new LinkedHashSet<>();
            NavigationItem root = repository.getNavigationItem("DEFAULT");
            sortByOrder(root, params, modifiedPages);


            Map<String, RepositoryDocument> documents = repository.getDocuments();




            // ADD NEW_PAGE page child of DEFAULT_BUREAU-GENERIQUE-2020


            for (String internalId : modifiedPages) {


                // Remove old page if exists
                RepositoryDocument doc = documents.get(internalId);
                int oldIndex = -1;

                ExportRepositoryNavigableDocument existingDocument = searchForId(outRoot, null, internalId);
                if (existingDocument != null && existingDocument.getParentId() != null) {
                    ExportRepositoryNavigableDocument oldParent = searchForId(outRoot, null, existingDocument.getParentId());
                    int i = 0;
                    for (ExportRepositoryDocument child : oldParent.getDoc().children) {
                        if (child.id.equals(internalId))
                            oldIndex = i;
                        else
                            i++;
                    }
                    if (oldIndex != -1)
                        oldParent.getDoc().children.remove(oldIndex);
                }


                // Load new parent in destination file
                ExportRepositoryNavigableDocument newParent = searchForId(outRoot, null, doc.getParentInternalId());

                if (newParent == null && !doc.getInternalID().equals("DEFAULT")) {
                    throw new MergeException("Le parent ["+doc.getParentInternalId()+"]"+  " pour la page " + doc.getTitle()+ " ["+internalId+"] est manquant");
                }



                Document documentToAdd = repository.getDocument(internalId);
                
                int newOrder = -1;


                // Determine new order according to order in current repository
                
                if (newParent != null) {
                    NavigationItem documentToAddParent = repository.getNavigationItem(((MemoryRepositoryDocument) documentToAdd).getParentInternalId());


                    List<NavigationItem> documentToAddParentChildren = new ArrayList<>(documentToAddParent.getChildren());
                    Collections.reverse(documentToAddParentChildren);
                    boolean foundChild = false;
                    for (NavigationItem documentToAddParentChild : documentToAddParentChildren) {
                        // Search for source child
                        if (documentToAddParentChild.getDocumentId().getInternalID().equals(internalId))
                            foundChild = true;
                        else {

                            // continue if
                            // - we don't have reached the current element
                            // - we have reached the current element but no preceding doc is found
                            if (!foundChild || newOrder == -1) {
                                int i = 0;
                                // Search for destination child
                                for (ExportRepositoryDocument newChild : newParent.getDoc().children) {
                                    if (newOrder == -1 && newChild.id.equals(documentToAddParentChild.getDocumentId().getInternalID())) {
                                        if (!foundChild)
                                            // the new item is positionned before the detination doc
                                            newOrder = i;
                                        else
                                            // the new item is positionned after the detination doc
                                            newOrder = i + 1;
                                    }
                                    i++;
                                }
                            }

                        }
                    }
                }

                // Insert new Document
                ExportRepositoryDocument documentToSave = saveDocument((MemoryRepositoryDocument) documentToAdd, documents, false);
                if( newParent != null)  {
                    if (newOrder != -1)
                        newParent.getDoc().children.add(newOrder, documentToSave);
                    else
                        newParent.getDoc().children.add(documentToSave);
                }   else    {
                    mergeOutDatas.documents.remove(0);
                    outRoot = documentToSave;
                    mergeOutDatas.documents.add(0, outRoot);
                }


                ExportRepositoryNavigableDocument newDocument = searchForId(outRoot, null, internalId);

                // re-insert old children
                if (existingDocument != null) {
                    newDocument.getDoc().children = existingDocument.getDoc().children;
                }

            }
            

            if (params.isMergeProfiles())
                outRoot.profiles = ((Space) documents.get("DEFAULT")).getProfiles();

            if (params.isMergeStyles())
                outRoot.styles = ((Space) documents.get("DEFAULT")).getStyles();


            ObjectWriter ow = om.writer();
            ow.writeValue(out, mergeOutDatas);
        } catch (Exception e) {
            throw new MergeException(e.getMessage());
        }


    }
}
