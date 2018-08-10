package org.jboss.metadata;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.management.MalformedObjectNameException;
import org.jboss.deployment.DeploymentException;
import org.jboss.logging.Logger;
import org.jboss.metadata.WebSecurityMetaData.WebResourceCollection;
import org.jboss.metadata.serviceref.ServiceRefDelegate;
import org.jboss.mx.loading.LoaderRepositoryFactory;
import org.jboss.mx.loading.LoaderRepositoryFactory.LoaderRepositoryConfig;
import org.jboss.mx.util.ObjectNameFactory;
import org.jboss.security.RunAsIdentity;
import org.jboss.security.SecurityRoleMetaData;
import org.jboss.wsf.spi.serviceref.ServiceRefMetaData;
import org.w3c.dom.Element;

public class WebMetaData extends MetaData {
	private static Logger log = Logger.getLogger(WebMetaData.class);
	private HashMap servletClassNames = new HashMap();
	private HashMap servletParams = new HashMap();
	private HashMap contextParams = new HashMap();
	private HashMap servletMappings = new HashMap();
	private HashMap resourceReferences = new HashMap();
	private HashMap resourceEnvReferences = new HashMap();
	private HashMap messageDestinationReferences = new HashMap();
	private HashMap messageDestinations = new HashMap();
	private ArrayList environmentEntries = new ArrayList();
	private ArrayList securityContraints = new ArrayList();
	private HashMap securityRoles = new HashMap();
	private HashMap ejbReferences = new HashMap();
	private HashMap ejbLocalReferences = new HashMap();
	private HashMap<String, ServiceRefMetaData> serviceReferences = new HashMap();
	private HashMap securityRoleReferences = new HashMap();
	private HashMap runAsNames = new HashMap();
	private HashMap runAsIdentity = new HashMap();
	private boolean distributable = false;
	private boolean java2ClassLoadingCompliance = false;
	private boolean useJBossWebLoader = false;
	private LoaderRepositoryConfig loaderConfig;
	private String contextRoot;
	private String jaccContextID;
	private ArrayList virtualHosts = new ArrayList();
	private String securityDomain;
	private boolean jaccRoleNameStar = false;
	private boolean flushOnSessionInvalidation;
	private HashMap wsdlPublishLocationMap = new HashMap();
	private boolean webServiceDeployment;
	private String configName;
	private String configFile;
	private ClassLoader encLoader;
	private ClassLoader cxtLoader;
	private ArrayList depends = new ArrayList();
	public static final int SESSION_INVALIDATE_ACCESS = 0;
	public static final int SESSION_INVALIDATE_SET_AND_GET = 1;
	public static final int SESSION_INVALIDATE_SET_AND_NON_PRIMITIVE_GET = 2;
	public static final int SESSION_INVALIDATE_SET = 3;
	private int invalidateSessionPolicy = 2;
	public static final int REPLICATION_TYPE_SYNC = 0;
	public static final int REPLICATION_TYPE_ASYNC = 1;

	private int replicationType = 0;
	public static final int REPLICATION_GRANULARITY_SESSION = 0;
	public static final int REPLICATION_GRANULARITY_ATTRIBUTE = 1;
	public static final int REPLICATION_GRANULARITY_FIELD = 2;
	private int replicationGranularity = 0;
	private boolean replicationFieldBatchMode = true;
	public static final int DEFAULT_MAX_UNREPLICATED_INTERVAL = 60;
	private Integer maxUnreplicatedInterval = null;
	private int sessionCookies = 0;
	public static final int SESSION_COOKIES_DEFAULT = 0;
	public static final int SESSION_COOKIES_ENABLED = 1;
	public static final int SESSION_COOKIES_DISABLED = 2;
	private URLClassLoader resourceCl;

	public void setResourceClassLoader(URLClassLoader resourceCl) {
		this.resourceCl = resourceCl;
	}

	public Iterator getEnvironmentEntries() {
		return this.environmentEntries.iterator();
	}

	public void setEnvironmentEntries(Collection environmentEntries) {
		this.environmentEntries.clear();
		this.environmentEntries.addAll(environmentEntries);
	}

	public Iterator getEjbReferences() {
		return this.ejbReferences.values().iterator();
	}

	public void setEjbReferences(Map ejbReferences) {
		this.ejbReferences.clear();
		this.ejbReferences.putAll(ejbReferences);
	}

	public Iterator getEjbLocalReferences() {
		return this.ejbLocalReferences.values().iterator();
	}

	public void setEjbLocalReferences(Map ejbReferences) {
		this.ejbLocalReferences.clear();
		this.ejbLocalReferences.putAll(ejbReferences);
	}

	public Iterator getResourceReferences() {
		return this.resourceReferences.values().iterator();
	}

	public void setResourceReferences(Map resourceReferences) {
		this.resourceReferences.clear();
		this.resourceReferences.putAll(resourceReferences);
	}

	public Iterator getResourceEnvReferences() {
		return this.resourceEnvReferences.values().iterator();
	}

	public void setResourceEnvReferences(Map resourceReferences) {
		this.resourceEnvReferences.clear();
		this.resourceEnvReferences.putAll(resourceReferences);
	}

	public Iterator getMessageDestinationReferences() {
		return this.messageDestinationReferences.values().iterator();
	}

	public void setMessageDestinationReferences(Map messageDestinationReferences) {
		this.messageDestinationReferences.clear();
		this.messageDestinationReferences.putAll(messageDestinationReferences);
	}

	public MessageDestinationMetaData getMessageDestination(String name) {
		return (MessageDestinationMetaData) this.messageDestinations.get(name);
	}

	public void setMessageDestination(Map messageDestinations) {
		this.messageDestinations.clear();
		this.messageDestinations.putAll(messageDestinations);
	}

	public Map<String, ServiceRefMetaData> getServiceReferences() {
		return this.serviceReferences;
	}

	public void setServiceReferences(Map<String, ServiceRefMetaData> serviceReferences) {
		this.serviceReferences.clear();
		this.serviceReferences.putAll(serviceReferences);
	}

	public String getContextRoot() {
		return this.contextRoot;
	}

	public void setContextRoot(String contextRoot) {
		this.contextRoot = contextRoot;
	}

	public String getConfigFile() {
		return this.configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getConfigName() {
		return this.configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getWsdlPublishLocationByName(String name) {
		return (String) this.wsdlPublishLocationMap.get(name);
	}

	public void setWsdlPublishLocationMap(Map wsdlPublishLocationMap) {
		this.wsdlPublishLocationMap.clear();
		this.wsdlPublishLocationMap.putAll(wsdlPublishLocationMap);
	}

	public boolean isWebServiceDeployment() {
		return this.webServiceDeployment;
	}

	public void setWebServiceDeployment(boolean webServiceDeployment) {
		this.webServiceDeployment = webServiceDeployment;
	}

	public String getJaccContextID() {
		return this.jaccContextID;
	}

	public void setJaccContextID(String jaccContextID) {
		this.jaccContextID = jaccContextID;
	}

	public String getSecurityDomain() {
		return this.securityDomain;
	}

	public void setSecurityDomain(String securityDomain) {
		this.securityDomain = securityDomain;
	}

	public boolean isJaccRoleNameStar() {
		return this.jaccRoleNameStar;
	}

	public void setJaccRoleNameStar(boolean jaccRoleNameStar) {
		this.jaccRoleNameStar = jaccRoleNameStar;
	}

	public boolean isFlushOnSessionInvalidation() {
		return this.flushOnSessionInvalidation;
	}

	public void setFlushOnSessionInvalidation(boolean flag) {
		this.flushOnSessionInvalidation = flag;
	}

	public Iterator getSecurityContraints() {
		return this.securityContraints.iterator();
	}

	public void setSecurityConstraints(Collection securityContraints) {
		this.securityContraints.clear();
		this.securityContraints.addAll(securityContraints);
	}

	public Map getSecurityRoleRefs() {
		return this.securityRoleReferences;
	}

	public List getSecurityRoleRefs(String servletName) {
		List roles = (List) this.securityRoleReferences.get(servletName);
		return roles;
	}

	public void setSecurityRoleReferences(Map securityRoleReferences) {
		this.securityRoleReferences.clear();
		this.securityRoleReferences.putAll(securityRoleReferences);
	}

	public Set getSecurityRoleNames() {
		return new HashSet(this.securityRoles.keySet());
	}

	public Map getSecurityRoles() {
		return new HashMap(this.securityRoles);
	}

	public void setSecurityRoles(Map securityRoles) {
		this.securityRoles.clear();
		this.securityRoles.putAll(securityRoles);
	}

	public Set getSecurityRoleNamesByPrincipal(String userName) {
		HashSet roleNames = new HashSet();
		Iterator it = this.securityRoles.values().iterator();

		while (it.hasNext()) {
			SecurityRoleMetaData srMetaData = (SecurityRoleMetaData) it.next();
			if (srMetaData.getPrincipals().contains(userName)) {
				roleNames.add(srMetaData.getRoleName());
			}
		}

		return roleNames;
	}

	public RunAsIdentity getRunAsIdentity(String servletName) {
		RunAsIdentity runAs = (RunAsIdentity) this.runAsIdentity.get(servletName);
		if (runAs == null) {
			HashMap var3 = this.runAsIdentity;
			synchronized (this.runAsIdentity) {
				String roleName = (String) this.runAsNames.get(servletName);
				if (roleName != null) {
					runAs = new RunAsIdentity(roleName, (String) null);
					this.runAsIdentity.put(servletName, runAs);
				}
			}
		}

		return runAs;
	}

	public Map getRunAsIdentity() {
		return this.runAsIdentity;
	}

	public void setRunAsIdentity(Map runAsIdentity) {
		this.runAsIdentity.clear();
		this.runAsIdentity.putAll(runAsIdentity);
	}

	public HashMap getContextParams() {
		return this.contextParams;
	}

	public void setContextParams(Map contextParams) {
		this.contextParams.clear();
		this.contextParams.putAll(contextParams);
	}

	public HashMap getServletMappings() {
		return this.servletMappings;
	}

	public void setServletMappings(Map servletMappings) {
		this.servletMappings.clear();
		this.servletMappings.putAll(servletMappings);
	}

	public Set getServletNames() {
		return new HashSet(this.servletMappings.keySet());
	}

	public Map getServletParams(String servletName) {
		Map params = (Map) this.servletParams.get(servletName);
		if (params == null) {
			params = new HashMap();
			this.servletParams.put(servletName, params);
		}

		return (Map) params;
	}

	public Map getServletClassMap() {
		return new HashMap(this.servletClassNames);
	}

	public void mergeSecurityRoles(Map applRoles) {
		Iterator it = applRoles.entrySet().iterator();

		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String roleName = (String) entry.getKey();
			SecurityRoleMetaData appRole = (SecurityRoleMetaData) entry.getValue();
			SecurityRoleMetaData srMetaData = (SecurityRoleMetaData) this.securityRoles.get(roleName);
			if (srMetaData != null) {
				Set principalNames = appRole.getPrincipals();
				srMetaData.addPrincipalNames(principalNames);
			} else {
				this.securityRoles.put(roleName, entry.getValue());
			}
		}

	}

	public Iterator getVirtualHosts() {
		return this.virtualHosts.iterator();
	}

	public void setVirtualHosts(Collection virtualHosts) {
		this.virtualHosts.clear();
		this.virtualHosts.addAll(virtualHosts);
	}

	public boolean getDistributable() {
		return this.distributable;
	}

	public void setDistributable(boolean distributable) {
		this.distributable = distributable;
	}

	public Collection getDepends() {
		return this.depends;
	}

	public void setDepends(Collection depends) {
		this.depends.clear();
		this.depends.addAll(depends);
	}

	public boolean getJava2ClassLoadingCompliance() {
		return this.java2ClassLoadingCompliance;
	}

	public void setJava2ClassLoadingCompliance(boolean flag) {
		this.java2ClassLoadingCompliance = flag;
	}

	public boolean isUseJBossWebLoader() {
		return this.useJBossWebLoader;
	}

	public void setUseJBossWebLoader(boolean flag) {
		this.useJBossWebLoader = flag;
	}

	public LoaderRepositoryConfig getLoaderConfig() {
		return this.loaderConfig;
	}

	public void setLoaderConfig(LoaderRepositoryConfig loaderConfig) {
		this.loaderConfig = loaderConfig;
	}

	public ClassLoader getENCLoader() {
		return this.encLoader;
	}

	public void setENCLoader(ClassLoader encLoader) {
		this.encLoader = encLoader;
	}

	public ClassLoader getContextLoader() {
		return this.cxtLoader;
	}

	public void setContextLoader(ClassLoader cxtLoader) {
		this.cxtLoader = cxtLoader;
	}

	public int getSessionCookies() {
		return this.sessionCookies;
	}

	public void setSessionCookies(int sessionCookies) {
		this.sessionCookies = sessionCookies;
	}

	public int getInvalidateSessionPolicy() {
		return this.invalidateSessionPolicy;
	}

	public void setInvalidateSessionPolicy(int invalidateSessionPolicy) {
		this.invalidateSessionPolicy = invalidateSessionPolicy;
	}

	public int getReplicationType() {
		return this.replicationType;
	}

	public int getReplicationGranularity() {
		return this.replicationGranularity;
	}

	public void setReplicationGranularity(int replicationGranularity) {
		this.replicationGranularity = replicationGranularity;
	}

	public boolean getReplicationFieldBatchMode() {
		return this.replicationFieldBatchMode;
	}

	public void setReplicationFieldBatchMode(boolean batchMode) {
		this.replicationFieldBatchMode = batchMode;
	}

	public Integer getMaxUnreplicatedInterval() {
		return this.maxUnreplicatedInterval;
	}

	public void setMaxUnreplicatedInterval(Integer maxUnreplicatedInterval) {
		this.maxUnreplicatedInterval = maxUnreplicatedInterval;
	}

	public void importXml(Element element) throws DeploymentException {
		String rootTag = element.getOwnerDocument().getDocumentElement().getTagName();
		if (rootTag.equals("web-app")) {
			this.importWebXml(element);
		} else if (rootTag.equals("jboss-web")) {
			this.importJBossWebXml(element);
		}

	}

	protected void importWebXml(Element webApp) throws DeploymentException {
		Iterator iterator = getChildrenByTagName(webApp, "servlet");

		Element serviceRef;
		String roleName;
		String urlPattern;
		Element userData;
		String name;
		String type;
		Iterator iter3;
		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			roleName = getElementContent(getUniqueChild(serviceRef, "servlet-name"));
			urlPattern = getElementContent(getOptionalChild(serviceRef, "servlet-class"));
			if (urlPattern != null) {
				this.servletClassNames.put(roleName, urlPattern);
			}

			for (Iterator initParams = getChildrenByTagName(serviceRef, "init-param"); initParams
					.hasNext(); ((Map) this.servletParams.get(roleName)).put(name, type)) {
				userData = (Element) initParams.next();
				name = getElementContent(getUniqueChild(userData, "param-name"));
				type = getElementContent(getUniqueChild(userData, "param-value"));
				if (null == this.servletParams.get(roleName)) {
					this.servletParams.put(roleName, new HashMap());
				}
			}

			iter3 = getChildrenByTagName(serviceRef, "security-role-ref");
			ArrayList roleNames = new ArrayList();

			Element runAs;
			while (iter3.hasNext()) {
				runAs = (Element) iter3.next();
				SecurityRoleRefMetaData roleRef = new SecurityRoleRefMetaData();
				roleRef.importEjbJarXml(runAs);
				roleNames.add(roleRef);
			}

			this.securityRoleReferences.put(roleName, roleNames);
			runAs = getOptionalChild(serviceRef, "run-as");
			if (runAs != null) {
				String runAsName = getElementContent(getOptionalChild(runAs, "role-name"));
				this.runAsNames.put(roleName, runAsName);
			}
		}

		iterator = getChildrenByTagName(webApp, "context-param");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			roleName = getElementContent(getUniqueChild(serviceRef, "param-name"));
			urlPattern = getElementContent(getUniqueChild(serviceRef, "param-value"));
			this.contextParams.put(roleName, urlPattern);
		}

		iterator = getChildrenByTagName(webApp, "servlet-mapping");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			roleName = getElementContent(getUniqueChild(serviceRef, "servlet-name"));
			urlPattern = getElementContent(getUniqueChild(serviceRef, "url-pattern"));
			this.servletMappings.put(roleName, urlPattern);
		}

		iterator = getChildrenByTagName(webApp, "resource-ref");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			ResourceRefMetaData resourceRefMetaData = new ResourceRefMetaData();
			resourceRefMetaData.importEjbJarXml(serviceRef);
			this.resourceReferences.put(resourceRefMetaData.getRefName(), resourceRefMetaData);
		}

		iterator = getChildrenByTagName(webApp, "resource-env-ref");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			ResourceEnvRefMetaData refMetaData = new ResourceEnvRefMetaData();
			refMetaData.importEjbJarXml(serviceRef);
			this.resourceEnvReferences.put(refMetaData.getRefName(), refMetaData);
		}

		iterator = getChildrenByTagName(webApp, "message-destination-ref");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			MessageDestinationRefMetaData messageDestinationRefMetaData = new MessageDestinationRefMetaData();
			messageDestinationRefMetaData.importEjbJarXml(serviceRef);
			this.messageDestinationReferences.put(messageDestinationRefMetaData.getRefName(),
					messageDestinationRefMetaData);
		}

		iterator = getChildrenByTagName(webApp, "message-destination");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();

			try {
				MessageDestinationMetaData messageDestinationMetaData = new MessageDestinationMetaData();
				messageDestinationMetaData.importEjbJarXml(serviceRef);
				this.messageDestinations.put(messageDestinationMetaData.getName(), messageDestinationMetaData);
			} catch (Throwable var14) {
				throw new DeploymentException("Error in web.xml for message destination: " + var14.getMessage());
			}
		}

		iterator = getChildrenByTagName(webApp, "env-entry");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			EnvEntryMetaData envEntryMetaData = new EnvEntryMetaData();
			envEntryMetaData.importEjbJarXml(serviceRef);
			this.environmentEntries.add(envEntryMetaData);
		}

		iterator = getChildrenByTagName(webApp, "security-constraint");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			WebSecurityMetaData wsmd = new WebSecurityMetaData();
			this.securityContraints.add(wsmd);
			Iterator iter2 = getChildrenByTagName(serviceRef, "web-resource-collection");

			Element authContraint;
			while (iter2.hasNext()) {
				authContraint = (Element) iter2.next();
				userData = getUniqueChild(authContraint, "web-resource-name");
				name = getElementContent(userData);
				WebResourceCollection wrc = wsmd.addWebResource(name);
				Iterator iter21 = getChildrenByTagName(authContraint, "url-pattern");

				while (iter21.hasNext()) {
					Element urlPattern = (Element) iter21.next();
					String pattern = getElementContent(urlPattern);
					wrc.addPattern(pattern);
				}

				Iterator iter22 = getChildrenByTagName(authContraint, "http-method");

				while (iter22.hasNext()) {
					Element httpMethod = (Element) iter22.next();
					String method = getElementContent(httpMethod);
					wrc.addHttpMethod(method);
				}
			}

			authContraint = getOptionalChild(serviceRef, "auth-constraint");
			Element transport;
			if (authContraint == null) {
				wsmd.setUnchecked(true);
			} else {
				iter3 = getChildrenByTagName(authContraint, "role-name");

				while (iter3.hasNext()) {
					transport = (Element) iter3.next();
					type = getElementContent(transport);
					wsmd.addRole(type);
				}

				if (wsmd.getRoles().size() == 0) {
					wsmd.setExcluded(true);
				}
			}

			userData = getOptionalChild(serviceRef, "user-data-constraint");
			if (userData != null) {
				transport = getUniqueChild(userData, "transport-guarantee");
				type = getElementContent(transport);
				wsmd.setTransportGuarantee(type);
			}
		}

		iterator = getChildrenByTagName(webApp, "security-role");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			roleName = getElementContent(getUniqueChild(serviceRef, "role-name"));
			this.securityRoles.put(roleName, new SecurityRoleMetaData(roleName));
		}

		iterator = getChildrenByTagName(webApp, "ejb-ref");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			EjbRefMetaData ejbRefMetaData = new EjbRefMetaData();
			ejbRefMetaData.importEjbJarXml(serviceRef);
			this.ejbReferences.put(ejbRefMetaData.getName(), ejbRefMetaData);
		}

		iterator = getChildrenByTagName(webApp, "ejb-local-ref");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			EjbLocalRefMetaData ejbRefMetaData = new EjbLocalRefMetaData();
			ejbRefMetaData.importEjbJarXml(serviceRef);
			this.ejbLocalReferences.put(ejbRefMetaData.getName(), ejbRefMetaData);
		}

		iterator = MetaData.getChildrenByTagName(webApp, "service-ref");

		while (iterator.hasNext()) {
			serviceRef = (Element) iterator.next();
			ServiceRefMetaData refMetaData = (new ServiceRefDelegate()).newServiceRefMetaData();
			refMetaData.importStandardXml(serviceRef);
			this.serviceReferences.put(refMetaData.getServiceRefName(), refMetaData);
		}

		iterator = getChildrenByTagName(webApp, "distributable");
		if (iterator.hasNext()) {
			this.distributable = true;
		}

	}

	protected void importJBossWebXml(Element jbossWeb) throws DeploymentException {
		Element contextRootElement = getOptionalChild(jbossWeb, "context-root");
		if (contextRootElement != null) {
			this.contextRoot = getElementContent(contextRootElement);
		}

		Element securityDomainElement = getOptionalChild(jbossWeb, "security-domain");
		if (securityDomainElement != null) {
			this.securityDomain = getElementContent(securityDomainElement);
			Boolean flag = Boolean.valueOf(securityDomainElement.getAttribute("flushOnSessionInvalidation"));
			this.flushOnSessionInvalidation = flag;
		}

		Element jaccStarRoleElement = getOptionalChild(jbossWeb, "jacc-star-role-allow");
		if (jaccStarRoleElement != null) {
			this.jaccRoleNameStar = "true".equalsIgnoreCase(getElementContent(jaccStarRoleElement));
		}

		Iterator iterator = getChildrenByTagName(jbossWeb, "virtual-host");

		Element sessionReplicationRootElement;
		String useCookiesElementContent;
		while (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();
			useCookiesElementContent = getElementContent(sessionReplicationRootElement);
			this.virtualHosts.add(useCookiesElementContent);
		}

		iterator = getChildrenByTagName(jbossWeb, "resource-ref");

		while (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();
			useCookiesElementContent = getElementContent(getUniqueChild(sessionReplicationRootElement, "res-ref-name"));
			ResourceRefMetaData refMetaData = (ResourceRefMetaData) this.resourceReferences
					.get(useCookiesElementContent);
			if (refMetaData == null) {
				throw new DeploymentException(
						"resource-ref " + useCookiesElementContent + " found in jboss-web.xml but not in web.xml");
			}

			refMetaData.importJbossXml(sessionReplicationRootElement);
		}

		iterator = getChildrenByTagName(jbossWeb, "resource-env-ref");

		while (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();
			useCookiesElementContent = getElementContent(
					getUniqueChild(sessionReplicationRootElement, "resource-env-ref-name"));
			ResourceEnvRefMetaData refMetaData = (ResourceEnvRefMetaData) this.resourceEnvReferences
					.get(useCookiesElementContent);
			if (refMetaData == null) {
				throw new DeploymentException(
						"resource-env-ref " + useCookiesElementContent + " found in jboss-web.xml but not in web.xml");
			}

			refMetaData.importJbossXml(sessionReplicationRootElement);
		}

		iterator = getChildrenByTagName(jbossWeb, "message-destination-ref");

		while (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();
			useCookiesElementContent = getElementContent(
					getUniqueChild(sessionReplicationRootElement, "message-destination-ref-name"));
			MessageDestinationRefMetaData messageDestinationRefMetaData = (MessageDestinationRefMetaData) this.messageDestinationReferences
					.get(useCookiesElementContent);
			if (messageDestinationRefMetaData == null) {
				throw new DeploymentException("message-destination-ref " + useCookiesElementContent
						+ " found in jboss-web.xml but not in web.xml");
			}

			messageDestinationRefMetaData.importJbossXml(sessionReplicationRootElement);
		}

		iterator = getChildrenByTagName(jbossWeb, "message-destination");

		while (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();

			try {
				useCookiesElementContent = getUniqueChildContent(sessionReplicationRootElement,
						"message-destination-name");
				MessageDestinationMetaData messageDestinationMetaData = (MessageDestinationMetaData) this.messageDestinations
						.get(useCookiesElementContent);
				if (messageDestinationMetaData == null) {
					throw new DeploymentException("message-destination " + useCookiesElementContent
							+ " found in jboss-web.xml but not in web.xml");
				}

				messageDestinationMetaData.importJbossXml(sessionReplicationRootElement);
			} catch (Throwable var16) {
				throw new DeploymentException("Error in web.xml for message destination: " + var16.getMessage());
			}
		}

		iterator = getChildrenByTagName(jbossWeb, "security-role");

		String repType;
		while (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();
			useCookiesElementContent = getElementContent(getUniqueChild(sessionReplicationRootElement, "role-name"));
			SecurityRoleMetaData securityRoleMetaData = (SecurityRoleMetaData) this.securityRoles
					.get(useCookiesElementContent);
			if (securityRoleMetaData == null) {
				throw new DeploymentException("Security role '" + useCookiesElementContent
						+ "' defined in jboss-web.xml" + " is not defined in web.xml");
			}

			Iterator itPrincipalNames = getChildrenByTagName(sessionReplicationRootElement, "principal-name");

			while (itPrincipalNames.hasNext()) {
				repType = getElementContent((Element) itPrincipalNames.next());
				securityRoleMetaData.addPrincipalName(repType);
			}
		}

		iterator = getChildrenByTagName(jbossWeb, "ejb-ref");

		while (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();
			useCookiesElementContent = getElementContent(getUniqueChild(sessionReplicationRootElement, "ejb-ref-name"));
			EjbRefMetaData ejbRefMetaData = (EjbRefMetaData) this.ejbReferences.get(useCookiesElementContent);
			if (ejbRefMetaData == null) {
				throw new DeploymentException(
						"ejb-ref " + useCookiesElementContent + " found in jboss-web.xml but not in web.xml");
			}

			ejbRefMetaData.importJbossXml(sessionReplicationRootElement);
		}

		iterator = getChildrenByTagName(jbossWeb, "ejb-local-ref");

		while (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();
			useCookiesElementContent = getElementContent(getUniqueChild(sessionReplicationRootElement, "ejb-ref-name"));
			EjbLocalRefMetaData ejbLocalRefMetaData = (EjbLocalRefMetaData) this.ejbLocalReferences
					.get(useCookiesElementContent);
			if (ejbLocalRefMetaData == null) {
				throw new DeploymentException(
						"ejb-local-ref " + useCookiesElementContent + " found in jboss-web.xml but not in web.xml");
			}

			ejbLocalRefMetaData.importJbossXml(sessionReplicationRootElement);
		}

		iterator = MetaData.getChildrenByTagName(jbossWeb, "service-ref");

		while (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();
			useCookiesElementContent = MetaData.getUniqueChildContent(sessionReplicationRootElement,
					"service-ref-name");
			ServiceRefMetaData refMetaData = (ServiceRefMetaData) this.serviceReferences.get(useCookiesElementContent);
			if (refMetaData == null) {
				throw new DeploymentException(
						"service-ref " + useCookiesElementContent + " found in jboss-web.xml but not in web.xml");
			}

			refMetaData.importJBossXml(sessionReplicationRootElement);
		}

		iterator = getChildrenByTagName(jbossWeb, "webservice-description");

		String repMethod;
		while (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();
			useCookiesElementContent = getElementContent(
					getUniqueChild(sessionReplicationRootElement, "webservice-description-name"));
			this.configName = MetaData.getOptionalChildContent(sessionReplicationRootElement, "config-name");
			this.configFile = MetaData.getOptionalChildContent(sessionReplicationRootElement, "config-file");
			repMethod = getOptionalChildContent(sessionReplicationRootElement, "wsdl-publish-location");
			this.wsdlPublishLocationMap.put(useCookiesElementContent, repMethod);
		}

		Iterator dependsElements = getChildrenByTagName(jbossWeb, "depends");

		Element classLoading;
		while (dependsElements.hasNext()) {
			classLoading = (Element) dependsElements.next();
			repMethod = getElementContent(classLoading);
			this.depends.add(ObjectNameFactory.create(repMethod));
		}

		iterator = getChildrenByTagName(jbossWeb, "use-session-cookies");
		if (iterator.hasNext()) {
			sessionReplicationRootElement = (Element) iterator.next();
			useCookiesElementContent = getElementContent(sessionReplicationRootElement);
			Boolean useCookies = Boolean.valueOf(useCookiesElementContent);
			if (useCookies) {
				this.sessionCookies = 1;
			} else {
				this.sessionCookies = 2;
			}
		}

		sessionReplicationRootElement = getOptionalChild(jbossWeb, "replication-config");
		String servletName;
		Element loader;
		Element servlet;
		if (sessionReplicationRootElement != null) {
			classLoading = getOptionalChild(sessionReplicationRootElement, "replication-trigger");
			if (classLoading != null) {
				repMethod = getElementContent(classLoading);
				if ("SET_AND_GET".equalsIgnoreCase(repMethod)) {
					this.invalidateSessionPolicy = 1;
				} else if ("SET_AND_NON_PRIMITIVE_GET".equalsIgnoreCase(repMethod)) {
					this.invalidateSessionPolicy = 2;
				} else {
					if (!"SET".equalsIgnoreCase(repMethod)) {
						throw new DeploymentException("replication-trigger value set to a non-valid value: '"
								+ repMethod
								+ "' (should be ['SET_AND_GET', 'SET_AND_NON_PRIMITIVE_GET', 'SET']) in jboss-web.xml");
					}

					this.invalidateSessionPolicy = 3;
				}
			}

			servlet = getOptionalChild(sessionReplicationRootElement, "replication-type");
			if (servlet != null) {
				servletName = getElementContent(servlet);
				if ("SYNC".equalsIgnoreCase(servletName)) {
					this.replicationType = 0;
				} else {
					if (!"ASYNC".equalsIgnoreCase(servletName)) {
						throw new DeploymentException("replication-type value set to a non-valid value: '" + servletName
								+ "' (should be ['SYNC', 'ASYNC']) in jboss-web.xml");
					}

					this.replicationType = 1;
				}
			}

			Element replicationGranularityElement = MetaData.getOptionalChild(sessionReplicationRootElement,
					"replication-granularity");
			if (replicationGranularityElement != null) {
				repType = MetaData.getElementContent(replicationGranularityElement);
				if ("SESSION".equalsIgnoreCase(repType)) {
					this.replicationGranularity = 0;
				} else if ("ATTRIBUTE".equalsIgnoreCase(repType)) {
					this.replicationGranularity = 1;
				} else {
					if (!"FIELD".equalsIgnoreCase(repType)) {
						throw new DeploymentException("replication-granularity value set to a non-valid value: '"
								+ repType + "' (should be ['SESSION', 'ATTRIBUTE', or 'FIELD'']) in jboss-web.xml");
					}

					this.replicationGranularity = 2;
				}
			}

			loader = MetaData.getOptionalChild(sessionReplicationRootElement, "replication-field-batch-mode");
			if (loader != null) {
				Boolean flag = Boolean.valueOf(MetaData.getElementContent(loader));
				this.replicationFieldBatchMode = flag;
			}

			Element maxUnreplicatedIntervalElement = MetaData.getOptionalChild(sessionReplicationRootElement,
					"max-unreplicated-interval");
			if (maxUnreplicatedIntervalElement != null) {
				String maxUnrep = MetaData.getElementContent(maxUnreplicatedIntervalElement);

				try {
					this.maxUnreplicatedInterval = Integer.valueOf(maxUnrep);
				} catch (NumberFormatException var15) {
					throw new DeploymentException("max-unreplicated-interval value set to a non-integer value: '"
							+ maxUnrep + " in jboss-web.xml");
				}
			}
		}

		classLoading = MetaData.getOptionalChild(jbossWeb, "class-loading");
		if (classLoading != null) {
			repMethod = classLoading.getAttribute("java2ClassLoadingCompliance");
			if (repMethod.length() == 0) {
				repMethod = "true";
			}

			boolean flag = Boolean.valueOf(repMethod);
			this.setJava2ClassLoadingCompliance(flag);
			loader = MetaData.getOptionalChild(classLoading, "loader-repository");
			if (loader != null) {
				this.useJBossWebLoader = true;

				try {
					this.loaderConfig = LoaderRepositoryFactory.parseRepositoryConfig(loader);
				} catch (MalformedObjectNameException var14) {
					throw new DeploymentException(var14);
				}
			}
		}

		iterator = getChildrenByTagName(jbossWeb, "servlet");

		while (iterator.hasNext()) {
			servlet = (Element) iterator.next();
			servletName = getElementContent(getUniqueChild(servlet, "servlet-name"));
			repType = getOptionalChildContent(servlet, "run-as-principal");
			String webXmlRunAs = (String) this.runAsNames.get(servletName);
			if (repType != null) {
				if (webXmlRunAs == null) {
					throw new DeploymentException("run-as-principal: " + repType
							+ " found in jboss-web.xml but there was no run-as in web.xml");
				}

				Set extraRoles = this.getSecurityRoleNamesByPrincipal(repType);
				RunAsIdentity runAs = new RunAsIdentity(webXmlRunAs, repType, extraRoles);
				this.runAsIdentity.put(servletName, runAs);
			} else if (webXmlRunAs != null) {
				RunAsIdentity runAs = new RunAsIdentity(webXmlRunAs, (String) null);
				this.runAsIdentity.put(servletName, runAs);
			}
		}

	}
}