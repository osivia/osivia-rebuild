package org.osivia.portal.cms.portlets.edition.repository.merge.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osivia.portal.api.cms.service.MergeParameters;
import org.osivia.portal.cms.portlets.edition.repository.admin.controller.CheckedItems;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RepositoryForm {

     
    private MultipartFile fileUpload;
    private File fileToMerge;
    private File fileDownload;
    
    private Set<String> mergedPages = new HashSet<String>();
    private Set<String> mergedProfiles = new HashSet<String>();
    
    
    
    public Set<String> getMergedProfiles() {
        return mergedProfiles;
    }


    
    public void setMergedProfiles(Set<String> mergedProfiles) {
        this.mergedProfiles = mergedProfiles;
    }



    private CheckedItems checkedItems;
    
    
    public CheckedItems getCheckedItems() {
        return checkedItems;
    }

  
    public void setCheckedItems(CheckedItems checkedItems) {
        this.checkedItems = checkedItems;
    }



    public Set<String> getMergedPages() {
        return mergedPages;
    }


    
    public void setMergedPages(Set<String> mergedPages) {
        this.mergedPages = mergedPages;
    }


    public File getFileToMerge() {
        return fileToMerge;
    }

    
    public void setFileToMerge(File fileToMerge) {
        this.fileToMerge = fileToMerge;
    }


          
    
    public File getFileDownload() {
        return fileDownload;
    }
    
    public void setFileDownload(File fileDownload) {
        this.fileDownload = fileDownload;
    }

    


    
    public MultipartFile getFileUpload() {
        return fileUpload;
    }

    
    public void setFileUpload(MultipartFile fileUpload) {
        this.fileUpload = fileUpload;
    }
    
    
    
    
     
}
