package org.jboss.security.auth.spi;

import java.lang.reflect.Constructor;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.jboss.logging.Logger;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

public abstract class AbstractServerLoginModule implements LoginModule {
	protected Subject subject;
	protected CallbackHandler callbackHandler;
	protected Map sharedState;
	protected Map options;
	protected Logger log;
	protected boolean useFirstPass;
	protected boolean loginOk;
	protected String principalClassName;
	protected Principal unauthenticatedIdentity;

	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;
		this.log = Logger.getLogger(this.getClass());
		if (this.log.isTraceEnabled()) {
			this.log.trace("initialize, instance=@" + System.identityHashCode(this));
		}

		this.log.trace("Security domain: " + (String) options.get("jboss.security.security_domain"));
		String passwordStacking = (String) options.get("password-stacking");
		if (passwordStacking != null && passwordStacking.equalsIgnoreCase("useFirstPass")) {
			this.useFirstPass = true;
		}

		this.principalClassName = (String) options.get("principalClass");
		String name = (String) options.get("unauthenticatedIdentity");
		if (name != null) {
			try {
				this.unauthenticatedIdentity = this.createIdentity(name);
				this.log.trace("Saw unauthenticatedIdentity=" + name);
			} catch (Exception var8) {
				this.log.warn("Failed to create custom unauthenticatedIdentity", var8);
			}
		}

	}

	public boolean login() throws LoginException {
		this.log.trace("login");
		this.loginOk = false;
		if (this.useFirstPass) {
			try {
				Object identity = this.sharedState.get("javax.security.auth.login.name");
				Object credential = this.sharedState.get("javax.security.auth.login.password");
				if (identity != null && credential != null) {
					this.loginOk = true;
					return true;
				}
			} catch (Exception var3) {
				this.log.error("login failed", var3);
			}
		}

		return false;
	}

	public boolean commit() throws LoginException {
		this.log.trace("commit, loginOk=" + this.loginOk);
		if (!this.loginOk) {
			return false;
		} else {
			Set principals = this.subject.getPrincipals();
			Principal identity = this.getIdentity();
			principals.add(identity);
			Group[] roleSets = this.getRoleSets();

			for (int g = 0; g < roleSets.length; ++g) {
				Group group = roleSets[g];
				String name = group.getName();
				Group subjectGroup = this.createGroup(name, principals);
				// FIXME OSIVIA/MIG nested group ?
//				if (subjectGroup instanceof NestableGroup) {
//					SimpleGroup tmp = new SimpleGroup("Roles");
//					((Group) subjectGroup).addMember(tmp);
//					subjectGroup = tmp;
//				}

				Enumeration members = group.members();

				while (members.hasMoreElements()) {
					Principal role = (Principal) members.nextElement();
					((Group) subjectGroup).addMember(role);
				}
			}

			return true;
		}
	}

	public boolean abort() throws LoginException {
		this.log.trace("abort");
		return true;
	}

	public boolean logout() throws LoginException {
		this.log.trace("logout");
		Principal identity = this.getIdentity();
		Set principals = this.subject.getPrincipals();
		principals.remove(identity);
		return true;
	}

	protected abstract Principal getIdentity();

	protected abstract Group[] getRoleSets() throws LoginException;

	protected boolean getUseFirstPass() {
		return this.useFirstPass;
	}

	protected Principal getUnauthenticatedIdentity() {
		return this.unauthenticatedIdentity;
	}

	protected Group createGroup(String name, Set principals) {
		Group roles = null;
		Iterator iter = principals.iterator();

		while (iter.hasNext()) {
			Object next = iter.next();
			if (next instanceof Group) {
				Group grp = (Group) next;
				if (grp.getName().equals(name)) {
					roles = grp;
					break;
				}
			}
		}

		if (roles == null) {
			roles = new SimpleGroup(name);
			principals.add(roles);
		}

		return (Group) roles;
	}

	protected Principal createIdentity(String username) throws Exception {
		Principal p = null;
		if (this.principalClassName == null) {
			p = new SimplePrincipal(username);
		} else {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class clazz = loader.loadClass(this.principalClassName);
			Class[] ctorSig = new Class[]{String.class};
			Constructor ctor = clazz.getConstructor(ctorSig);
			Object[] ctorArgs = new Object[]{username};
			p = (Principal) ctor.newInstance(ctorArgs);
		}

		return (Principal) p;
	}
}