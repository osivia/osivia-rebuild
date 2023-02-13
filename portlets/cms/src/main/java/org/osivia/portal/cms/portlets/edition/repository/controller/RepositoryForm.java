package org.osivia.portal.cms.portlets.edition.repository.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RepositoryForm {
    List<RepositoryBean> repositories;
    /** upload multipart file. */
    private Map fileUpload;
    


    
    public List<RepositoryBean> getRepositories() {
        return repositories;
    }

    
    public void setRepositories(List<RepositoryBean> repositories) {
        this.repositories = repositories;
    }
    
    

    
    public Map getFileUpload() {
        return fileUpload;
    }


    
    public void setFileUpload(Map fileUpload) {
        this.fileUpload = fileUpload;
    }


    
     
}
