package org.osivia.portal.api.cms.repository;

import java.util.Map;

import org.osivia.portal.api.cms.repository.cache.SharedRepository;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;

public interface RepositoryFactory {
    public Map<SharedRepositoryKey, SharedRepository> getSharedRepositories();
}
