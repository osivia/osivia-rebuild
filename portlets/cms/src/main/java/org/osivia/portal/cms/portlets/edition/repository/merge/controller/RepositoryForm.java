package org.osivia.portal.cms.portlets.edition.repository.merge.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osivia.portal.api.cms.service.MergeParameters;
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

    private MergeParameters mergeParams = new MergeParameters();
    private Set<String> mergedPages = new HashSet<String>();
    
    
    
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

    
    public MergeParameters getMergeParams() {
        return mergeParams;
    }

    
    public MultipartFile getFileUpload() {
        return fileUpload;
    }

    
    public void setFileUpload(MultipartFile fileUpload) {
        this.fileUpload = fileUpload;
    }
    
    
    
    
     
}
