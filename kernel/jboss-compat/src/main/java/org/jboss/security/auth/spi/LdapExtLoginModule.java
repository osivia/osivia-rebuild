package org.jboss.security.auth.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import javax.management.ObjectName;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.security.auth.login.LoginException;
import org.jboss.security.SimpleGroup;

public class LdapExtLoginModule extends UsernamePasswordLoginModule {
	private static final String ROLES_CTX_DN_OPT = "rolesCtxDN";
	private static final String ROLE_ATTRIBUTE_ID_OPT = "roleAttributeID";
	private static final String ROLE_ATTRIBUTE_IS_DN_OPT = "roleAttributeIsDN";
	private static final String ROLE_NAME_ATTRIBUTE_ID_OPT = "roleNameAttributeID";
	private static final String PARSE_ROLE_NAME_FROM_DN_OPT = "parseRoleNameFromDN";
	private static final String BIND_DN = "bindDN";
	private static final String BIND_CREDENTIAL = "bindCredential";
	private static final String BASE_CTX_DN = "baseCtxDN";
	private static final String BASE_FILTER_OPT = "baseFilter";
	private static final String ROLE_FILTER_OPT = "roleFilter";
	private static final String ROLE_RECURSION = "roleRecursion";
	private static final String DEFAULT_ROLE = "defaultRole";
	private static final String SEARCH_TIME_LIMIT_OPT = "searchTimeLimit";
	private static final String SEARCH_SCOPE_OPT = "searchScope";
	private static final String SECURITY_DOMAIN_OPT = "jaasSecurityDomain";
	protected String bindDN;
	protected String bindCredential;
	protected String baseDN;
	protected String baseFilter;
	protected String rolesCtxDN;
	protected String roleFilter;
	protected String roleAttributeID;
	protected String roleNameAttributeID;
	protected boolean roleAttributeIsDN;
	protected boolean parseRoleNameFromDN;
	protected int recursion = 0;
	protected int searchTimeLimit = 10000;
	protected int searchScope = 2;
	protected boolean trace;
	private transient SimpleGroup userRoles = new SimpleGroup("Roles");

	protected String getUsersPassword() throws LoginException {
		return "";
	}

	protected Group[] getRoleSets() throws LoginException {
		Group[] roleSets = new Group[]{this.userRoles};
		return roleSets;
	}

	protected boolean validatePassword(String inputPassword, String expectedPassword) {
		boolean isValid = false;
		if (inputPassword != null) {
			if (inputPassword.length() == 0) {
				boolean allowEmptyPasswords = true;
				String flag = (String) this.options.get("allowEmptyPasswords");
				if (flag != null) {
					allowEmptyPasswords = Boolean.valueOf(flag);
				}

				if (!allowEmptyPasswords) {
					this.log.trace("Rejecting empty password due to allowEmptyPasswords");
					return false;
				}
			}

			try {
				String username = this.getUsername();
				this.createLdapInitContext(username, inputPassword);
				this.defaultRole();
				isValid = true;
			} catch (Throwable var6) {
				super.setValidateError(var6);
			}
		}

		return isValid;
	}

	private void defaultRole() {
		try {
			String defaultRole = (String) this.options.get("defaultRole");
			if (defaultRole == null || defaultRole.equals("")) {
				return;
			}

			Principal p = super.createIdentity(defaultRole);
			this.log.trace("Assign user to role " + defaultRole);
			this.userRoles.addMember(p);
		} catch (Exception var3) {
			super.log.debug("could not add default role to user", var3);
		}

	}

	private boolean createLdapInitContext(String username, Object credential) throws Exception {
		this.bindDN = (String) this.options.get("bindDN");
		this.bindCredential = (String) this.options.get("bindCredential");
		String securityDomain = (String) this.options.get("jaasSecurityDomain");
		if (securityDomain != null) {
			ObjectName serviceName = new ObjectName(securityDomain);
			char[] tmp = DecodeAction.decode(this.bindCredential, serviceName);
			this.bindCredential = new String(tmp);
		}

		this.baseDN = (String) this.options.get("baseCtxDN");
		this.baseFilter = (String) this.options.get("baseFilter");
		this.roleFilter = (String) this.options.get("roleFilter");
		this.roleAttributeID = (String) this.options.get("roleAttributeID");
		if (this.roleAttributeID == null) {
			this.roleAttributeID = "role";
		}

		String roleAttributeIsDNOption = (String) this.options.get("roleAttributeIsDN");
		this.roleAttributeIsDN = Boolean.valueOf(roleAttributeIsDNOption);
		this.roleNameAttributeID = (String) this.options.get("roleNameAttributeID");
		if (this.roleNameAttributeID == null) {
			this.roleNameAttributeID = "name";
		}

		String parseRoleNameFromDNOption = (String) this.options.get("parseRoleNameFromDN");
		this.parseRoleNameFromDN = Boolean.valueOf(parseRoleNameFromDNOption);
		this.rolesCtxDN = (String) this.options.get("rolesCtxDN");
		String strRecursion = (String) this.options.get("roleRecursion");

		try {
			this.recursion = Integer.parseInt(strRecursion);
		} catch (Exception var19) {
			if (this.trace) {
				this.log.trace("Failed to parse: " + strRecursion + ", disabling recursion");
			}

			this.recursion = 0;
		}

		String timeLimit = (String) this.options.get("searchTimeLimit");
		if (timeLimit != null) {
			try {
				this.searchTimeLimit = Integer.parseInt(timeLimit);
			} catch (NumberFormatException var18) {
				if (this.trace) {
					this.log.trace("Failed to parse: " + timeLimit + ", using searchTimeLimit=" + this.searchTimeLimit);
				}
			}
		}

		String scope = (String) this.options.get("searchScope");
		if ("OBJECT_SCOPE".equalsIgnoreCase(scope)) {
			this.searchScope = 0;
		} else if ("ONELEVEL_SCOPE".equalsIgnoreCase(scope)) {
			this.searchScope = 1;
		}

		if ("SUBTREE_SCOPE".equalsIgnoreCase(scope)) {
			this.searchScope = 2;
		}

		InitialLdapContext ctx = null;

		try {
			ctx = this.constructInitialLdapContext(this.bindDN, this.bindCredential);
			String userDN = this.bindDNAuthentication(ctx, username, credential, this.baseDN, this.baseFilter);
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(this.searchScope);
			constraints.setReturningAttributes(new String[0]);
			constraints.setTimeLimit(this.searchTimeLimit);
			this.rolesSearch(ctx, constraints, username, userDN, this.recursion, 0);
		} finally {
			if (ctx != null) {
				ctx.close();
			}

		}

		return true;
	}

	protected String bindDNAuthentication(InitialLdapContext ctx, String user, Object credential, String baseDN,
			String filter) throws NamingException {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(2);
		constraints.setReturningAttributes(new String[0]);
		constraints.setTimeLimit(this.searchTimeLimit);
		NamingEnumeration results = null;
		Object[] filterArgs = new Object[]{user};
		results = ctx.search(baseDN, filter, filterArgs, constraints);
		if (!results.hasMore()) {
			results.close();
			throw new NamingException("Search of baseDN(" + baseDN + ") found no matches");
		} else {
			SearchResult sr = (SearchResult) results.next();
			String name = sr.getName();
			String userDN = null;
			if (sr.isRelative()) {
				userDN = name + "," + baseDN;
				results.close();
				results = null;
				InitialLdapContext var12 = this.constructInitialLdapContext(userDN, credential);
				var12.close();
				return userDN;
			} else {
				throw new NamingException("Can't follow referal for authentication: " + name);
			}
		}
	}

	protected void rolesSearch(InitialLdapContext ctx, SearchControls constraints, String user, String userDN,
			int recursionMax, int nesting) throws NamingException {
		Object[] filterArgs = new Object[]{user, userDN};
		NamingEnumeration results = ctx.search(this.rolesCtxDN, this.roleFilter, filterArgs, constraints);

		try {
			while (results.hasMore()) {
				SearchResult sr = (SearchResult) results.next();
				String dn = this.canonicalize(sr.getName());
				String[] attrNames;
				Attributes result;
				Attribute roles2;
				int m;
				String roleName;
				if (nesting == 0 && this.roleAttributeIsDN && this.roleNameAttributeID != null) {
					if (this.parseRoleNameFromDN) {
						this.parseRole(dn);
					} else {
						attrNames = new String[]{this.roleNameAttributeID};
						result = ctx.getAttributes(dn, attrNames);
						roles2 = result.get(this.roleNameAttributeID);
						if (roles2 != null) {
							for (m = 0; m < roles2.size(); ++m) {
								roleName = (String) roles2.get(m);
								this.addRole(roleName);
							}
						}
					}
				}

				attrNames = new String[]{this.roleAttributeID};
				result = ctx.getAttributes(dn, attrNames);
				if (result != null && result.size() > 0) {
					roles2 = result.get(this.roleAttributeID);

					for (m = 0; m < roles2.size(); ++m) {
						roleName = (String) roles2.get(m);
						if (this.roleAttributeIsDN && this.parseRoleNameFromDN) {
							this.parseRole(roleName);
						} else if (this.roleAttributeIsDN) {
							String roleDN = roleName;
							String[] returnAttribute = new String[]{this.roleNameAttributeID};
							this.log.trace("Using roleDN: " + roleName);

							try {
								Attributes result2 = ctx.getAttributes(roleDN, returnAttribute);
								Attribute roles2B = result2.get(this.roleNameAttributeID);
								if (roles2B != null) {
									for (int m2 = 0; m2 < roles2B.size(); ++m2) {
										roleName = (String) roles2B.get(m2);
										this.addRole(roleName);
									}
								}
							} catch (NamingException var25) {
								this.log.trace("Failed to query roleNameAttrName", var25);
							}
						} else {
							this.addRole(roleName);
						}
					}
				}

				if (nesting < recursionMax) {
					this.rolesSearch(ctx, constraints, user, dn, recursionMax, nesting + 1);
				}
			}
		} finally {
			if (results != null) {
				results.close();
			}

		}

	}

	private InitialLdapContext constructInitialLdapContext(String dn, Object credential) throws NamingException {
		Properties env = new Properties();
		Iterator iter = this.options.entrySet().iterator();

		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			env.put(entry.getKey(), entry.getValue());
		}

		String factoryName = env.getProperty("java.naming.factory.initial");
		if (factoryName == null) {
			factoryName = "com.sun.jndi.ldap.LdapCtxFactory";
			env.setProperty("java.naming.factory.initial", factoryName);
		}

		String authType = env.getProperty("java.naming.security.authentication");
		if (authType == null) {
			env.setProperty("java.naming.security.authentication", "simple");
		}

		String protocol = env.getProperty("java.naming.security.protocol");
		String providerURL = (String) this.options.get("java.naming.provider.url");
		if (providerURL == null) {
			providerURL = "ldap://localhost:" + (protocol != null && protocol.equals("ssl") ? "636" : "389");
		}

		env.setProperty("java.naming.provider.url", providerURL);
		if (dn != null) {
			env.setProperty("java.naming.security.principal", dn);
		}

		if (credential != null) {
			env.put("java.naming.security.credentials", credential);
		}

		this.traceLdapEnv(env);
		return new InitialLdapContext(env, (Control[]) null);
	}

	private void traceLdapEnv(Properties env) {
		if (this.trace) {
			Properties tmp = new Properties();
			tmp.putAll(env);
			if (tmp.containsKey("bindCredential")) {
				tmp.setProperty("bindCredential", "***");
			}

			if (tmp.containsKey("java.naming.security.credentials")) {
				tmp.setProperty("java.naming.security.credentials", "***");
			}

			this.log.trace("Logging into LDAP server, env=" + tmp.toString());
		}

	}

	private String canonicalize(String searchResult) {
		int len = searchResult.length();
		String result;
		if (searchResult.endsWith("\"")) {
			result = searchResult.substring(0, len - 1) + "," + this.rolesCtxDN + "\"";
		} else {
			result = searchResult + "," + this.rolesCtxDN;
		}

		return result;
	}

	private void addRole(String roleName) {
		if (roleName != null) {
			try {
				Principal p = super.createIdentity(roleName);
				this.log.trace("Assign user to role " + roleName);
				this.userRoles.addMember(p);
			} catch (Exception var3) {
				this.log.debug("Failed to create principal: " + roleName, var3);
			}
		}

	}

	private void parseRole(String dn) {
		StringTokenizer st = new StringTokenizer(dn, ",");

		while (st != null && st.hasMoreTokens()) {
			String keyVal = st.nextToken();
			if (keyVal.indexOf(this.roleNameAttributeID) > -1) {
				StringTokenizer kst = new StringTokenizer(keyVal, "=");
				kst.nextToken();
				this.addRole(kst.nextToken());
			}
		}

	}
}