package org.osivia.portal.cms.portlets.edition.repository.admin.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.portlet.Refreshable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RepositoryForm {
    
    

    public RepositoryForm() {
        super();
    }



    private Map fileUpload;
    private String repositoryChecked = null;
    private File duplicateFile = null;
    private CheckedItems checkedItems;
      
    
    public CheckedItems getCheckedItems() {
        return checkedItems;
    }

    
    public void setCheckedItems(CheckedItems checkedItems) {
        this.checkedItems = checkedItems;
    }


    public String getRepositoryChecked() {
        return repositoryChecked;
    }

    
    public void setRepositoryChecked(String repositoryChecked) {
        this.repositoryChecked = repositoryChecked;
    }


    public Map getFileUpload() {
        return fileUpload;
    }


    
    public void setFileUpload(Map fileUpload) {
        this.fileUpload = fileUpload;
    }


    
    public File getDuplicateFile() {
        return duplicateFile;
    }


    
    public void setDuplicateFile(File duplicateFile) {
        this.duplicateFile = duplicateFile;
    }
    
     
}
