package org.jboss.security.auth.login;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PolicyConfig {
	Map config = Collections.synchronizedMap(new HashMap());

	public void add(AuthenticationInfo authInfo) {
		this.config.put(authInfo.getName(), authInfo);
	}

	public AuthenticationInfo get(String name) {
		AuthenticationInfo info = (AuthenticationInfo) this.config.get(name);
		return info;
	}

	public AuthenticationInfo remove(String name) {
		AuthenticationInfo info = (AuthenticationInfo) this.config.remove(name);
		return info;
	}

	public void clear() {
		this.config.clear();
	}

	public Set getConfigNames() {
		return this.config.keySet();
	}

	public int size() {
		return this.config.size();
	}

	public boolean containsKey(String name) {
		return this.config.containsKey(name);
	}

	public void copy(PolicyConfig pc) {
		this.config.putAll(pc.config);
	}
}