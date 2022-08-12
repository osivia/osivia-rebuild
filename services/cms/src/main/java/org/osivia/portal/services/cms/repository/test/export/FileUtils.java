package org.osivia.portal.services.cms.repository.test.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Page;
import org.osivia.portal.api.cms.model.Space;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryFolder;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositoryPage;
import org.osivia.portal.api.cms.repository.model.shared.MemoryRepositorySpace;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.services.cms.repository.test.FileRepository;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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

    private ExportRepositoryDocument saveDocument(MemoryRepositoryDocument srcDoc, Map<String, RepositoryDocument> documents) {

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
            targetDoc.children.add(saveDocument(child, documents));
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
                    export.documents.add((saveDocument((MemoryRepositoryDocument) doc, documents)));
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


}
