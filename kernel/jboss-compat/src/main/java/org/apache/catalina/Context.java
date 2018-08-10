package org.apache.catalina;

import javax.servlet.ServletContext;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.deploy.ErrorPage;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.util.CharsetMapper;
import org.apache.tomcat.util.http.mapper.Mapper;

public abstract interface Context extends Container
{
  public static final String RELOAD_EVENT = "reload";

  public abstract Object[] getApplicationEventListeners();

  public abstract void setApplicationEventListeners(Object[] paramArrayOfObject);

  public abstract Object[] getApplicationLifecycleListeners();

  public abstract void setApplicationLifecycleListeners(Object[] paramArrayOfObject);

  public abstract boolean getAvailable();

  public abstract void setAvailable(boolean paramBoolean);

  public abstract CharsetMapper getCharsetMapper();

  public abstract void setCharsetMapper(CharsetMapper paramCharsetMapper);

  public abstract String getConfigFile();

  public abstract void setConfigFile(String paramString);

  public abstract boolean getConfigured();

  public abstract void setConfigured(boolean paramBoolean);

  public abstract boolean getCookies();

  public abstract void setCookies(boolean paramBoolean);

  public abstract boolean getCrossContext();

  public abstract String getAltDDName();

  public abstract void setAltDDName(String paramString);

  public abstract void setCrossContext(boolean paramBoolean);

  public abstract String getDisplayName();

  public abstract void setDisplayName(String paramString);

  public abstract boolean getDistributable();

  public abstract void setDistributable(boolean paramBoolean);

  public abstract String getDocBase();

  public abstract void setDocBase(String paramString);

  public abstract String getEncodedPath();

  public abstract boolean getIgnoreAnnotations();

  public abstract void setIgnoreAnnotations(boolean paramBoolean);

  public abstract LoginConfig getLoginConfig();

  public abstract void setLoginConfig(LoginConfig paramLoginConfig);

  public abstract Mapper getMapper();

  public abstract NamingResources getNamingResources();

  public abstract void setNamingResources(NamingResources paramNamingResources);

  public abstract String getPath();

  public abstract void setPath(String paramString);

  public abstract String getPublicId();

  public abstract void setPublicId(String paramString);

  public abstract boolean getReloadable();

  public abstract void setReloadable(boolean paramBoolean);

  public abstract boolean getOverride();

  public abstract void setOverride(boolean paramBoolean);

  public abstract boolean getPrivileged();

  public abstract void setPrivileged(boolean paramBoolean);

  public abstract ServletContext getServletContext();

  public abstract int getSessionTimeout();

  public abstract void setSessionTimeout(int paramInt);

  public abstract boolean getSwallowOutput();

  public abstract void setSwallowOutput(boolean paramBoolean);

  public abstract String getWrapperClass();

  public abstract void setWrapperClass(String paramString);

  public abstract void addApplicationListener(String paramString);

  public abstract void addApplicationParameter(ApplicationParameter paramApplicationParameter);

  public abstract void addConstraint(SecurityConstraint paramSecurityConstraint);

  public abstract void addErrorPage(ErrorPage paramErrorPage);

  public abstract void addFilterDef(FilterDef paramFilterDef);

  public abstract void addFilterMap(FilterMap paramFilterMap);

  public abstract void addInstanceListener(String paramString);

  public abstract void addJspMapping(String paramString);

  public abstract void addLocaleEncodingMappingParameter(String paramString1, String paramString2);

  public abstract void addMimeMapping(String paramString1, String paramString2);

  public abstract void addParameter(String paramString1, String paramString2);

  public abstract void addRoleMapping(String paramString1, String paramString2);

  public abstract void addSecurityRole(String paramString);

  public abstract void addServletMapping(String paramString1, String paramString2);

  public abstract void addTaglib(String paramString1, String paramString2);

  public abstract void addWatchedResource(String paramString);

  public abstract void addWelcomeFile(String paramString);

  public abstract void addWrapperLifecycle(String paramString);

  public abstract void addWrapperListener(String paramString);

  public abstract Wrapper createWrapper();

  public abstract String[] findApplicationListeners();

  public abstract ApplicationParameter[] findApplicationParameters();

  public abstract SecurityConstraint[] findConstraints();

  public abstract ErrorPage findErrorPage(int paramInt);

  public abstract ErrorPage findErrorPage(String paramString);

  public abstract ErrorPage[] findErrorPages();

  public abstract FilterDef findFilterDef(String paramString);

  public abstract FilterDef[] findFilterDefs();

  public abstract FilterMap[] findFilterMaps();

  public abstract String[] findInstanceListeners();

  public abstract String findMimeMapping(String paramString);

  public abstract String[] findMimeMappings();

  public abstract String findParameter(String paramString);

  public abstract String[] findParameters();

  public abstract String findRoleMapping(String paramString);

  public abstract boolean findSecurityRole(String paramString);

  public abstract String[] findSecurityRoles();

  public abstract String findServletMapping(String paramString);

  public abstract String[] findServletMappings();

  public abstract String findStatusPage(int paramInt);

  public abstract int[] findStatusPages();

  public abstract String findTaglib(String paramString);

  public abstract String[] findTaglibs();

  public abstract String[] findWatchedResources();

  public abstract boolean findWelcomeFile(String paramString);

  public abstract String[] findWelcomeFiles();

  public abstract String[] findWrapperLifecycles();

  public abstract String[] findWrapperListeners();

  public abstract void reload();

  public abstract void removeApplicationListener(String paramString);

  public abstract void removeApplicationParameter(String paramString);

  public abstract void removeConstraint(SecurityConstraint paramSecurityConstraint);

  public abstract void removeErrorPage(ErrorPage paramErrorPage);

  public abstract void removeFilterDef(FilterDef paramFilterDef);

  public abstract void removeFilterMap(FilterMap paramFilterMap);

  public abstract void removeInstanceListener(String paramString);

  public abstract void removeMimeMapping(String paramString);

  public abstract void removeParameter(String paramString);

  public abstract void removeRoleMapping(String paramString);

  public abstract void removeSecurityRole(String paramString);

  public abstract void removeServletMapping(String paramString);

  public abstract void removeTaglib(String paramString);

  public abstract void removeWatchedResource(String paramString);

  public abstract void removeWelcomeFile(String paramString);

  public abstract void removeWrapperLifecycle(String paramString);

  public abstract void removeWrapperListener(String paramString);

  public abstract boolean getXmlNamespaceAware();

  public abstract boolean getXmlValidation();

  public abstract void setXmlValidation(boolean paramBoolean);

  public abstract void setXmlNamespaceAware(boolean paramBoolean);

  public abstract void setTldValidation(boolean paramBoolean);

  public abstract boolean getTldValidation();

  public abstract boolean getTldNamespaceAware();

  public abstract void setTldNamespaceAware(boolean paramBoolean);
}

/* Location:           /home/jeanseb/tmp/jbossweb/jbossweb.jar
 * Qualified Name:     org.apache.catalina.Context
 * JD-Core Version:    0.6.0
 */