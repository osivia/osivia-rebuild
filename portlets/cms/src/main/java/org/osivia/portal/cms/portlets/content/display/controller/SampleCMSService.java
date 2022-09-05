package org.osivia.portal.cms.portlets.content.display.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.portlet.PortletContext;
import javax.servlet.http.HttpSession;

import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.DocumentContext;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.editor.EditorModule;
import org.osivia.portal.api.menubar.MenubarModule;
import org.osivia.portal.api.panels.PanelPlayer;
import org.osivia.portal.api.player.Player;
import org.osivia.portal.api.statistics.SpaceStatistics;
import org.osivia.portal.api.taskbar.TaskbarItems;
import org.osivia.portal.api.taskbar.TaskbarTask;
import org.osivia.portal.api.theming.TabGroup;
import org.osivia.portal.api.theming.TemplateAdapter;
import org.osivia.portal.api.urls.Link;
import org.osivia.portal.core.cms.BinaryDelegation;
import org.osivia.portal.core.cms.BinaryDescription;
import org.osivia.portal.core.cms.CMSBinaryContent;
import org.osivia.portal.core.cms.CMSConfigurationItem;
import org.osivia.portal.core.cms.CMSEditableWindow;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSPage;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.DocumentMetadata;
import org.osivia.portal.core.cms.DocumentsMetadata;
import org.osivia.portal.core.cms.DomainContextualization;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.NavigationItem;
import org.osivia.portal.core.cms.RegionInheritance;
import org.osivia.portal.core.cms.Satellite;

public class SampleCMSService implements ICMSService{

    @Override
    public String getParentPath(String documentPath) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public CMSItem getContent(CMSServiceCtx ctx, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public CMSItem getByShareId(CMSServiceCtx ctx, String shareID, boolean enabledLinkOnly) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Map<String, String> getNxPathParameters(String cmsPath) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public boolean checkContentAnonymousAccess(CMSServiceCtx cmsCtx, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
    }

    @Override
    public CMSPublicationInfos getPublicationInfos(CMSServiceCtx ctx, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public CMSItem getSpaceConfig(CMSServiceCtx cmsCtx, String publishSpacePath) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public CMSItem getPortalNavigationItem(CMSServiceCtx ctx, String publishSpacePath, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<CMSItem> getPortalNavigationSubitems(CMSServiceCtx ctx, String publishSpacePath, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<CMSItem> getPortalSubitems(CMSServiceCtx cmsContext, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<CMSItem> getWorkspaces(CMSServiceCtx cmsContext, boolean userWorkspaces, boolean administrator) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public CMSItem getUserWorkspace(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Map<String, NavigationItem> getFullLoadedPortalNavigationItems(CMSServiceCtx cmsContext, String basePath) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
    }

    @Override
    public Player getItemHandler(CMSServiceCtx ctx) throws CMSException {
        Document doc = (Document) ctx.getDoc();
        Map<String, String> properties = new HashMap<>();
        String instance = null;
        if ("folder".equals(doc.getType())) {
            instance = "BrowserInstance";
            properties.put(Constants.WINDOW_PROP_CACHE_PARENT_URI, doc.getId().toString());
        } else
            instance = "ContentInstance";


        // Player
        Player player = new Player();
        player.setWindowProperties(properties);
        player.setPortletInstance(instance);

        return player;
    }

    @Override
    public CMSBinaryContent getBinaryContent(CMSServiceCtx cmsCtx, String type, String path, String parameter) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Map<String, String> parseCMSURL(CMSServiceCtx cmsCtx, String requestPath, Map<String, String> requestParameters) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public String adaptWebPathToCms(CMSServiceCtx cmsCtx, String requestPath) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<CMSPage> computeUserPreloadedPages(CMSServiceCtx cmsCtx) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<CMSEditableWindow> getEditableWindows(CMSServiceCtx cmsContext, String path, String publishSpacePath, String sitePath, String navigationScope, Boolean isSpaceSite) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Map<String, RegionInheritance> getCMSRegionsInheritance(CMSItem item) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public void saveCMSRegionInheritance(CMSServiceCtx cmsContext, String path, String regionName, RegionInheritance inheritance) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public Set<CMSConfigurationItem> getCMSRegionLayoutsConfigurationItems(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Map<String, CMSConfigurationItem> getCmsRegionsInheritedLayout(CMSServiceCtx cmsContext, String basePath, String path, Set<CMSConfigurationItem> configuredLayouts) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Map<String, CMSConfigurationItem> getCMSRegionsSelectedLayout(CMSItem item, Set<CMSConfigurationItem> regionLayouts) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public void saveCMSRegionSelectedLayout(CMSServiceCtx cmsContext, String path, String regionName, String regionLayoutName) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public String getEcmDomain(CMSServiceCtx cmsCtx) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public void deleteFragment(CMSServiceCtx cmsCtx, String pagePath, String refURI) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public void moveFragment(CMSServiceCtx cmsCtx, String pagePath, String fromRegion, Integer fromPos, String toRegion, Integer toPos, String refUri) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public boolean isCmsWebPage(CMSServiceCtx cmsCtx, String cmsPath) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
    }

    @Override
    public void publishDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public void unpublishDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public void askToPublishDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public void cancelPublishWorkflow(CMSServiceCtx cmsCtx, String pagePath) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public void validatePublicationOfDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public void rejectPublicationOfDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public void deleteDocument(CMSServiceCtx cmsCtx, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public void putDocumentInTrash(CMSServiceCtx cmsCtx, String docId) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public String refreshUserAvatar(CMSServiceCtx cmsCtx, String username) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Link getBinaryResourceURL(CMSServiceCtx cmsCtx, BinaryDescription binary) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public BinaryDelegation validateBinaryDelegation(CMSServiceCtx cmsCtx, String path) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public TaskbarItems getTaskbarItems(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<TaskbarTask> getTaskbarTasks(CMSServiceCtx cmsContext, String basePath, boolean navigation) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public PanelPlayer getNavigationPanelPlayer(String instance) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public String getMoveUrl(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public String getReorderUrl(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public String getAdaptedNavigationPath(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public DomainContextualization getDomainContextualization(CMSServiceCtx cmsContext, String domainPath) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public DocumentMetadata getDocumentMetadata(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public DocumentsMetadata getDocumentsMetadata(CMSServiceCtx cmsContext, String basePath, Long timestamp) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Map<String, TabGroup> getTabGroups(CMSServiceCtx cmsContext) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<MenubarModule> getMenubarModules(CMSServiceCtx cmsContext) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public DocumentContext getDocumentContext(CMSServiceCtx cmsContext, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public <D extends DocumentContext> D getDocumentContext(CMSServiceCtx cmsContext, String path, Class<D> expectedType) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<TemplateAdapter> getTemplateAdapters(CMSServiceCtx cmsContext) {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<EcmDocument> getTasks(CMSServiceCtx cmsContext, String user) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public EcmDocument getTask(CMSServiceCtx cmsContext, String user, String path, UUID uuid) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Map<String, String> updateTask(CMSServiceCtx cmsContext, UUID uuid, String actionId, Map<String, String> variables) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public CMSItem getTask(CMSServiceCtx cmsContext, UUID uuid) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public void reloadSession(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public List<EcmDocument> getUserSubscriptions(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Map<String, String> getTitleMetadataProperties(CMSServiceCtx cmsContext, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<CMSEditableWindow> getProcedureDashboards(CMSServiceCtx cmsContext, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<SpaceStatistics> getSpaceStatistics(CMSServiceCtx cmsContext, Set<String> paths) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public void incrementsStatistics(CMSServiceCtx cmsContext, HttpSession httpSession, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public void updateStatistics(CMSServiceCtx cmsContext, HttpSession httpSession, List<SpaceStatistics> spaceStatistics) throws CMSException {
        throw new RuntimeException("Not yet implemented !");
        
    }

    @Override
    public Set<Satellite> getSatellites() throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public CMSItem getSharingRoot(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public String resolveLinkSharing(CMSServiceCtx cmsContext, String linkId) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public List<EditorModule> getEditorModules(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public Map<String, String> getEditorWindowBaseProperties(CMSServiceCtx cmsContext) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public UniversalID getUniversalIDFromPath(CMSServiceCtx cmsContext, String path) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public String getPathFromUniversalID(CMSServiceCtx cmsContext, UniversalID id) throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }

    @Override
    public PortletContext getPortletContext() throws CMSException {
        throw new RuntimeException("Not yet implemented !");

    }
    


}
