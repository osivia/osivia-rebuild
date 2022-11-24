package org.osivia.portal.core.content;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.VirtualNavigationUtils;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.NavigationItem;
import org.osivia.portal.api.cms.model.Templateable;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.SpaceCacheBean;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.locale.ILocaleService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.api.player.Player;
import org.osivia.portal.api.preview.IPreviewModeService;
import org.osivia.portal.api.theming.TemplateAdapter;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.container.persistent.DefaultCMSPageFactory;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.dynamic.StartDynamicWindowInNewPageCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.PortalObjectUtilsInternal;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;
import org.springframework.beans.factory.annotation.Autowired;

public class PublicationManager implements IPublicationManager {

	private CMSService cmsService;

	private IDynamicService dynamicService;

	@Autowired
	private IPreviewModeService previewModeService;

	/** The locale service. */
	@Autowired
	private ILocaleService localeService;

	private ICMSServiceLocator cmServiceLocator;

	private CMSService getCMSService() {
		if (cmsService == null) {
			cmsService = Locator.getService(CMSService.class);
		}

		return cmsService;
	}

	private IDynamicService getDynamicService() {
		if (dynamicService == null) {
			dynamicService = Locator.getService(IDynamicService.class);
		}
		return dynamicService;
	}

	private IPreviewModeService getPreviewModeService() {

		return previewModeService;
	}

	private ILocaleService getLocaleService() {

		return localeService;
	}

	public ICMSServiceLocator getCmsServiceLocator() {
		if (cmServiceLocator == null)
			cmServiceLocator = Locator.getService(ICMSServiceLocator.MBEAN_NAME, ICMSServiceLocator.class);
		return cmServiceLocator;
	}

	protected PortalObjectId getPageTemplate(CMSContext cmsContext, Document doc, NavigationItem navigation)
			throws ControllerException {

		Document space;
		try {
			space = getCMSService().getCMSSession(cmsContext).getDocument(navigation.getSpaceId());

			String spaceTemplateID = space.getId().getInternalID();

			spaceTemplateID += IPublicationManager.PAGEID_CTX;
			if (cmsContext.isPreview()) {
				spaceTemplateID += IPublicationManager.PAGEID_ITEM_SEPARATOR + IPublicationManager.PAGEID_PREVIEW
						+ IPublicationManager.PAGEID_VALUE_SEPARATOR + "true";
			}
			spaceTemplateID += IPublicationManager.PAGEID_ITEM_SEPARATOR + IPublicationManager.PAGEID_LOCALE
					+ IPublicationManager.PAGEID_VALUE_SEPARATOR + cmsContext.getlocale();

			String spacePath = space.getId().getRepositoryName() + ":" + "/" + spaceTemplateID + "/"
					+ DefaultCMSPageFactory.getRootPageName();

			String templateRelativePath = "";

			Document templateDoc = null;

			// Find first page
			while (!navigation.isRoot()) {
				Document navDoc = getCMSService().getCMSSession(cmsContext).getDocument(navigation.getDocumentId());
				if (navDoc instanceof Templateable) {
					templateDoc = navDoc;
					break;
				}
				navigation = navigation.getParent();
			}

			if (templateDoc != null) {

				NavigationItem nav = getCMSService().getCMSSession(cmsContext).getNavigationItem(templateDoc.getId());

				while (!nav.isRoot()) {
					templateRelativePath = "/" + nav.getDocumentId().getInternalID() + templateRelativePath;
					nav = nav.getParent();
				}

			}

			String templatePath = spacePath + templateRelativePath;

			PortalObjectId templateId = PortalObjectId.parse(templatePath, PortalObjectPath.CANONICAL_FORMAT);
			return templateId;
		} catch (CMSException e) {
			throw new ControllerException(e);
		}
	}

	@Override
	public PortalObjectId getPageId(PortalControllerContext portalCtx, UniversalID parentID, UniversalID docId,
			Map<String, String> pageProps, Map<String, String> pageParams, String restorableName)
			throws ControllerException {

		PortalObjectId pageId = null;

		try {
			CMSContext cmsContext = new CMSContext(portalCtx);

			Document doc;
			CMSItem notSupportedCMSItem;
			Object nxNativeItem;

			ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalCtx);

			CMSServiceCtx cmsReadItemContext = new CMSServiceCtx();
			cmsReadItemContext.setPortalControllerContext(portalCtx);

			if ("task".equals(docId.getRepositoryName())) {
				doc = null;
				notSupportedCMSItem = getCmsServiceLocator().getCMSService().getTask(cmsReadItemContext,
						UUID.fromString(docId.getInternalID()));
				nxNativeItem = notSupportedCMSItem.getNativeItem();
			} else {
				// check if user repository is compatible with context (locale, preview)

				NativeRepository userRepository = getCMSService().getUserRepository(cmsContext,
						docId.getRepositoryName());

				if (getPreviewModeService().isPreviewing(portalCtx, docId) && !userRepository.supportPreview())
					getPreviewModeService().changePreviewMode(portalCtx, docId);
				if (!userRepository.getLocales().contains(getLocaleService().getLocale(portalCtx)))
					getLocaleService().setLocale(portalCtx, null);

				cmsContext.setPreview(getPreviewModeService().isPreviewing(portalCtx, docId));
				cmsContext.setLocale(getLocaleService().getLocale(portalCtx));

				doc = getCMSService().getCMSSession(cmsContext).getDocument(docId);

				if ("nx".equals(docId.getRepositoryName())) {
					nxNativeItem = doc.getNativeItem();

				} else
					nxNativeItem = null;

				notSupportedCMSItem = null;
			}

			UniversalID virtualTaskId = null;
			String virtualTaskPath = null;

			if (nxNativeItem != null) {
				cmsReadItemContext.setDoc(nxNativeItem);
				virtualTaskPath = getCmsServiceLocator().getCMSService().getAdaptedNavigationPath(cmsReadItemContext);
				if (virtualTaskPath != null) {
					virtualTaskId = getCmsServiceLocator().getCMSService().getUniversalIDFromPath(cmsReadItemContext,
							virtualTaskPath);
					virtualTaskPath = VirtualNavigationUtils.adaptPath(virtualTaskPath, doc.getId().getInternalID());
				}
			}

			// Get space
			UniversalID spaceId;
			if (doc != null) {
				spaceId = doc.getSpaceId();
			} else {
				spaceId = null;
			}

			// Force load of dirty datas associated to current space
			if (spaceId != null) {
				CMSController ctrl = new CMSController(portalCtx);

				CMSSession session;
				try {
					session = Locator.getService(org.osivia.portal.api.cms.service.CMSService.class)
							.getCMSSession(ctrl.getCMSContext());
					SpaceCacheBean modifiedTs = session.getSpaceCacheInformations(spaceId);
					if (modifiedTs.getLastSpaceModification() != null)
						PageProperties.getProperties().setCheckingSpaceTS(modifiedTs.getLastSpaceModification());
				} catch (CMSException e) {
					throw new RuntimeException(e);
				}
			}

			boolean pageDisplay = false;

			// System.out.println("*** PUBMANAGER " + docId );

			NavigationItem navigation;
			String pagePath;

			boolean uncontextualize = false;
			

			if( doc != null)	{
				if( StringUtils.equals(System.getProperty("osivia.cms.repository."+doc.getId().getRepositoryName()+".mutualizationByDefault"), "false"))	{
					uncontextualize = true;
				}
			}
			

			if (uncontextualize == false) {

				if (spaceId != null) {

					try {
						navigation = getCMSService().getCMSSession(cmsContext).getNavigationItem(docId);

						// if( docId.getInternalID().contains("kFG8vy"))
						// System.out.println("*** PUBMANAGER NAV-> " + navigation.getDocumentId() );
					} catch (CMSException e) {
						navigation = null;
					}
				} else {
					navigation = null;
				}

				if (navigation != null) {

					final NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext
							.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

					PageNavigationalState previousPNS = null;

					Document space = getCMSService().getCMSSession(cmsContext).getDocument(navigation.getSpaceId());

					// Get previous pns in same space
					Page oldPage = PortalObjectUtilsInternal.getPage(controllerContext);
					if (oldPage != null) {
						String sSpaceId = oldPage.getProperty("osivia.spaceId");
						if (spaceId != null) {
							UniversalID oldSpaceId = new UniversalID(sSpaceId);
							if (space.getId().equals(oldSpaceId)) {
								previousPNS = nsContext.getPageNavigationalState(oldPage.getId().toString());
							}
						}
					}

					String templatePath = null;

					// Navigation associated to the type of item (eq: folder)
					if (navigation.getCustomizedTemplateId() != null) {

						UniversalID templateId = navigation.getCustomizedTemplateId();

						List<TemplateAdapter> templateAdapters = getCmsServiceLocator().getCMSService()
								.getTemplateAdapters(cmsReadItemContext);
						for (TemplateAdapter adapter : templateAdapters) {
							templateId = adapter.adapt(templateId);
						}

						Document template = getCMSService().getCMSSession(cmsContext).getDocument(templateId);

						NavigationItem navTemplate = getCMSService().getCMSSession(cmsContext)
								.getNavigationItem(template.getId());

						/* Adapt to template engine naming rules */
						String templatePagePath = "";

						while (!navTemplate.isRoot()) {
							templatePagePath = "/" + navTemplate.getDocumentId().getInternalID() + templatePagePath;
							navTemplate = navTemplate.getParent();
						}

						templatePath = navigation.getCustomizedTemplateId().getRepositoryName() + ":/"
								+ template.getSpaceId().getInternalID() + "/" + DefaultCMSPageFactory.getRootPageName()
								+ templatePagePath;

					}

					if (templatePath == null)
						templatePath = getPageTemplate(cmsContext, doc, navigation)
								.toString(PortalObjectPath.CANONICAL_FORMAT);

					Map<String, String> properties = new HashMap<String, String>();

					// Add custom page Props
					if (pageProps != null) {
						for (String name : pageProps.keySet()) {
							properties.put(name, pageProps.get(name));
						}
					}

					if ((pageProps != null) && (StringUtils.equals(pageProps.get("osivia.pageType"), "template"))) {
						if (parentID != null) {
							properties.put("osivia.navigationId", parentID.toString());
							properties.put("osivia.spaceId", parentID.toString());
							properties.put("osivia.contentId", parentID.toString());
							properties.put("osivia.content.preview", BooleanUtils
									.toStringTrueFalse(getPreviewModeService().isPreviewing(portalCtx, parentID)));
							properties.put("osivia.content.locale", getLocaleService().getLocale(portalCtx).toString());
							properties.put("osivia.templateId", docId.toString());

						} else {
							throw new RuntimeException("No Parent Id for template instanciation");
						}

					} else {
						properties.put("osivia.contentId", docId.toString());
						if (virtualTaskId != null) {
							properties.put("osivia.navigationId", virtualTaskId.toString());
							properties.put("osivia.spaceId", virtualTaskId.toString());
							properties.put("osivia.virtualTaskPath", virtualTaskPath);
						} else {

							properties.put("osivia.navigationId", navigation.getDocumentId().toString());
							properties.put("osivia.spaceId", navigation.getSpaceId().toString());
						}

						properties.put("osivia.content.preview", BooleanUtils.toStringTrueFalse(doc.isPreview()));
						properties.put("osivia.content.locale", doc.getLocale().toString());
					}

					Map<Locale, String> displayNames = new HashMap<Locale, String>();
					String displayName = space.getTitle();
					if (StringUtils.isNotEmpty(displayName)) {
						displayNames.put(Locale.FRENCH, displayName);
					}

					String pageDynamicID = "space_" + navigation.getSpaceId().getInternalID();
					pageDynamicID += IPublicationManager.PAGEID_CTX;
					if (cmsContext.isPreview()) {
						pageDynamicID += IPublicationManager.PAGEID_ITEM_SEPARATOR + IPublicationManager.PAGEID_PREVIEW
								+ IPublicationManager.PAGEID_VALUE_SEPARATOR + "true";
					}
					pageDynamicID += IPublicationManager.PAGEID_ITEM_SEPARATOR + IPublicationManager.PAGEID_LOCALE
							+ IPublicationManager.PAGEID_VALUE_SEPARATOR + cmsContext.getlocale();

					if (parentID == null) {
						if ("false".equals(space.getProperties().get("osivia.connect.templated")))
							parentID = getCMSService().getDefaultPortal(cmsContext);
						else {
							// Get Portal name from template
							// sites:/ID_SITE_A__ctx__preview_true__locale_fr/root
							int indexPortal = templatePath.indexOf(":/");
							int indexRoot = templatePath.indexOf("/root");
							if (indexRoot != -1) {
								parentID = new UniversalID(templatePath.substring(0, indexPortal),
										templatePath.substring(indexPortal + 2, indexRoot));

							} else {
								parentID = doc.getSpaceId();
							}
						}
					}

					pagePath = getDynamicService().startDynamicPage(portalCtx,
							parentID.getRepositoryName() + ":/" + parentID.getInternalID(), pageDynamicID, displayNames,
							templatePath, properties, pageParams, restorableName);

					if ("nx".equals(doc.getId().getRepositoryName())) {
						if (navigation.getDocumentId().equals(doc.getId()))
							pageDisplay = true;
					}

					Page page = (Page) controllerContext.getController().getPortalObjectContainer()
							.getObject(PortalObjectId.parse(pagePath, PortalObjectPath.CANONICAL_FORMAT));

					// Propagation des selecteurs si les paramètres ne sont pas explicites
					final Map<QName, String[]> pageState = new HashMap<QName, String[]>();

					if ((previousPNS != null) && ((pageParams == null) || (pageParams.size() == 0))) {
						if ("1".equals(page.getProperty("osivia.cms.propagateSelectors"))) {
							final String[] selectors = previousPNS
									.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "selectors"));

							if (selectors != null) {
								previousPNS = nsContext.getPageNavigationalState(page.getId().toString());
								pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "selectors"), selectors);
								nsContext.setPageNavigationalState(page.getId().toString(),
										new PageNavigationalState(pageState));
							}
						}
					}

					// Reinitialisation des renders parameters et de l'état
					final Iterator<PortalObject> i = page.getChildren(Page.WINDOW_MASK).iterator();
					while (i.hasNext()) {
						final Window window = (Window) i.next();

						final NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class,
								window.getId());

						final WindowNavigationalState windowNavState = WindowNavigationalState.create();

						// On la force en vue NORMAL
						final WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState,
								WindowState.NORMAL, windowNavState.getMode(), ParametersStateString.create());
						controllerContext.setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
					}

				} else {
					pagePath = null;
				}
			} else {
				// uncontextualization
				navigation = null;
				Page page = PortalObjectUtilsInternal.getPage(controllerContext);
				if (page != null) {
					pagePath = page.getId().getNamespace()+ ":"+page.getId().getPath().toString(PortalObjectPath.CANONICAL_FORMAT);
				} else {
					pagePath = null;
				}

			}

			if (((doc == null && notSupportedCMSItem != null) || (doc != null && !(doc instanceof Templateable)))
					&& pageDisplay == false) {

				Map<String, String> windowProperties = new HashMap<String, String>();
				Map<String, String> windowParams = new HashMap<String, String>();

				if (doc != null)
					windowProperties.put(Constants.WINDOW_PROP_URI, doc.getId().toString());
				else {
					if (notSupportedCMSItem != null) {
						windowProperties.put(Constants.WINDOW_PROP_NOT_SUPPORTED_PATH,
								notSupportedCMSItem.getCmsPath());
					}
				}

				String instance;

				CMSServiceCtx handlerCtx = new CMSServiceCtx();
				handlerCtx.setPortalControllerContext(portalCtx);
				handlerCtx.setDoc((nxNativeItem != null) ? nxNativeItem : doc);
				handlerCtx.setServletRequest(
						controllerContext.getServerInvocation().getServerContext().getClientRequest());

				Player contentProperties;
				if (nxNativeItem != null)
					contentProperties = getCmsServiceLocator().getCMSService().getItemHandler(handlerCtx);
				else
					contentProperties = getCmsServiceLocator().getCMSService(doc.getId().getRepositoryName())
							.getItemHandler(handlerCtx);

				instance = contentProperties.getPortletInstance();

				windowProperties.putAll(contentProperties.getWindowProperties());

				if( doc != null)
					windowProperties.put("osivia.title", doc.getTitle());
				
				windowProperties.put("osivia.hideTitle", "1");

				if (navigation != null)
					windowProperties.put("osivia.cms.contextualization", "1");

				if (pagePath != null) {
					getDynamicService().startDynamicWindow(portalCtx, pagePath, "content", "virtual", instance,
							windowProperties, windowParams);
				} else {
					// Empty page (ex: link to procedure from mail, uncontextualized content, ...)
					Map<Locale, String> displayNames = new HashMap<Locale, String>();
					String displayName = "content";
					if (StringUtils.isNotEmpty(displayName)) {
						displayNames.put(Locale.FRENCH, displayName);
					}

					UniversalID defaultPortalID = getCMSService().getDefaultPortal(cmsContext);
					
					StartDynamicWindowInNewPageCommand cmd = new StartDynamicWindowInNewPageCommand(
							defaultPortalID, null, "content", instance,
							windowProperties, new HashMap<>(), null);
					UpdatePageResponse resp = (UpdatePageResponse) controllerContext.execute(cmd);

					return resp.getPageId();
				}

			}

			// Initial window
			if (pageProps != null && pageProps.get("osivia.initialWindowInstance") != null) {

				String instanceName = pageProps.get("osivia.initialWindowInstance");
				String region = pageProps.get("osivia.initialWindowRegion");

				Map<String, String> windowProps = new HashMap<>();
				Map<String, List<String>> initProps = PageParametersEncoder
						.decodeProperties(pageProps.get("osivia.initialWindowProps"));
				if (initProps != null) {
					for (String prop : initProps.keySet()) {
						windowProps.put(prop, initProps.get(prop).get(0));
					}
				}

				Map<String, String> windowParams = new HashMap<>();
				Map<String, List<String>> initParams = PageParametersEncoder
						.decodeProperties(pageProps.get("osivia.initialWindowParams"));
				if (initParams != null) {
					for (String param : initParams.keySet()) {
						windowParams.put(param, initParams.get(param).get(0));
					}
				}

				getDynamicService().startDynamicWindow(portalCtx, pagePath, "content", region, instanceName,
						windowProps, windowParams);
			}

			pageId = PortalObjectId.parse(pagePath, PortalObjectPath.CANONICAL_FORMAT);

		} catch (Exception e) {
			throw new ControllerException(e);
		}

		return pageId;

	}

}
