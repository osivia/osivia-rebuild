package org.osivia.portal.cms.portlets.edition.repository.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class RepositoryIndex
{
    List<RepositoryBean> repositories;
    /** upload multipart file. */
    private Map fileUpload;
    


    public List<RepositoryBean> getRepositories() {
        return repositories;
    }

    
    public void setRepositories(List<RepositoryBean> repositories) {
        this.repositories = repositories;
    }
}
