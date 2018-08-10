package org.jboss.security;

import java.security.Principal;

public class AnybodyPrincipal implements Comparable, Principal {
	public static final String ANYBODY = "<ANYBODY>";
	public static final AnybodyPrincipal ANYBODY_PRINCIPAL = new AnybodyPrincipal();

	public int hashCode() {
		return "<ANYBODY>".hashCode();
	}

	public String getName() {
		return "<ANYBODY>";
	}

	public String toString() {
		return "<ANYBODY>";
	}

	public boolean equals(Object another) {
		return true;
	}

	public int compareTo(Object o) {
		return 0;
	}
}