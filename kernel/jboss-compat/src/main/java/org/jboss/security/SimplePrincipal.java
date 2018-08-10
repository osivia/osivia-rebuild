package org.jboss.security;

import java.io.Serializable;
import java.security.Principal;

public class SimplePrincipal implements Principal, Serializable {
	static final long serialVersionUID = 7701951188631723261L;
	private String name;

	public SimplePrincipal(String name) {
		this.name = name;
	}

	public boolean equals(Object another) {
		if (!(another instanceof Principal)) {
			return false;
		} else {
			String anotherName = ((Principal) another).getName();
			boolean equals = false;
			if (this.name == null) {
				equals = anotherName == null;
			} else {
				equals = this.name.equals(anotherName);
			}

			return equals;
		}
	}

	public int hashCode() {
		return this.name == null ? 0 : this.name.hashCode();
	}

	public String toString() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}
}