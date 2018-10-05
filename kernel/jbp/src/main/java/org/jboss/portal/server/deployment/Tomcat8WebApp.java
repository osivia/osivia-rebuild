package org.jboss.portal.server.deployment;

import java.net.URL;

import javax.management.ObjectName;
import javax.servlet.ServletContext;

import org.jboss.portal.server.deployment.CannotCreatePortletWebAppException;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.jboss.portal.web.WebApp;
import org.jboss.portal.web.WebAppEvent;

public class Tomcat8WebApp extends PortalWebApp {

	private  Object standardContext;
	private  WebApp webApp;

	public Tomcat8WebApp(WebAppEvent event) throws CannotCreatePortletWebAppException {

		try {
						//
			this.webApp = event.getWebApp();
//			this.standardContext = server.getAttribute(name, "managedResource");
			this.standardContext = webApp.getServletContext();

			String contextPath = webApp.getContextPath();
			ServletContext servletContext = webApp.getServletContext();
			ClassLoader loader = webApp.getClassLoader();
			URL url = webApp.getServletContext().getResource("/");

			//
			//init(servletContext, url, loader, contextPath, jbossAppEntityResolver);
			init(servletContext, url, loader, contextPath, null);
		} catch (Exception e) {
			CannotCreatePortletWebAppException ex;
			if (e instanceof CannotCreatePortletWebAppException) {
				ex = (CannotCreatePortletWebAppException) e;
			} else {
				ex = new CannotCreatePortletWebAppException(e);
			}
			throw ex;
		}
	}

	@Override
	public void instrument() throws Exception {

	}

}
