package org.osivia.portal.kernel.common.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.jboss.portal.common.net.URLTools;
import org.jboss.portal.core.controller.Controller;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.server.RequestControllerDispatcher;
import org.jboss.portal.server.Server;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.ServerRequest;
import org.jboss.portal.server.ServerResponse;
import org.jboss.portal.server.impl.ServerInvocationContextImpl;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.web.WebRequest;
import org.jboss.portal.web.endpoint.EndPointRequest;
import org.jboss.portal.web.endpoint.EndPointServlet;


public class CommandIntegration extends Controller {
	final HttpServletRequest request;

	/** Describes a default servlet mapping. */
	private static final int DEFAULT_SERVLET_MAPPING = 0;

	private Server server;

	public CommandIntegration(HttpServletRequest request) {
		super();
		this.request = request;
		
		
		server = EasyMock.createMock("server", Server.class);

	}

	protected ControllerResponse service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException,  ControllerException {

		String requestURI = "/portal/auth/pagemarker/8/MonEspace";
		String contextPath = "/portal";


		// Determine the host for this request
		String portalHost = req.getServerName();

		// Determine the request path
        String portalRequestPath = requestURI.substring(contextPath.length());
        String portalContextPath = requestURI.substring(0, contextPath.length());

		// Apply the url decoding
		portalRequestPath = URLTools.decodeXWWWFormURL(portalRequestPath);
		portalContextPath = URLTools.decodeXWWWFormURL(portalContextPath);

		//
		URLContext urlContext = URLContext.newInstance(req.isSecure(), req.getRemoteUser() != null);

		//
		WebRequest webReq = new EndPointRequest(req, portalRequestPath, portalContextPath,
				EndPointServlet.ROOT_PATH_MAPPING);

		// ***************
		// ***************
		// ***************
		// ***************

		//
		ServerInvocationContext invocationCtx = new ServerInvocationContextImpl(req, resp, webReq, portalHost,
				portalRequestPath, portalContextPath, urlContext);

		//
		ServerRequest request = new ServerRequest(invocationCtx);
		request.setServer(server);

		//
		ServerResponse response = new ServerResponse(request, invocationCtx);

		//
		ServerInvocation invocation = new ServerInvocation(invocationCtx);
		invocation.setRequest(request);
		invocation.setResponse(response);

		//

		invocation.setHandler(new RequestControllerDispatcher(this));

		// Create controller context
		ControllerContext controllerContext = new ControllerContext(invocation, this);

		// Invoke the chain that creates the initial command
		// ControllerCommand cmd = commandFactory.doMapping(controllerContext,
		// invocation, invocation.getServerContext().getPortalHost(),
		// invocation.getServerContext().getPortalContextPath(),
		// invocation.getServerContext().getPortalRequestPath());

		PortalObjectId id = new PortalObjectId("", PortalObjectPath.ROOT_PATH);
		ControllerCommand cmd = new RenderPageCommand(id);


		return controllerContext.execute(cmd);
	}

}
