package org.jboss.security;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

public class SimpleGroup extends SimplePrincipal implements Group, Cloneable {
	private static final long serialVersionUID = 6051859639378507247L;
	private HashMap members = new HashMap(3);

	public SimpleGroup(String groupName) {
		super(groupName);
	}

	public boolean addMember(Principal user) {
		boolean isMember = this.members.containsKey(user);
		if (!isMember) {
			this.members.put(user, user);
		}

		return !isMember;
	}

	public boolean isMember(Principal member) {
		boolean isMember = this.members.containsKey(member);
		if (!isMember) {
			isMember = member instanceof AnybodyPrincipal;
			if (!isMember && member instanceof NobodyPrincipal) {
				return false;
			}
		}

		if (!isMember) {
			Collection values = this.members.values();
			Iterator iter = values.iterator();

			while (!isMember && iter.hasNext()) {
				Object next = iter.next();
				if (next instanceof Group) {
					Group group = (Group) next;
					isMember = group.isMember(member);
				}
			}
		}

		return isMember;
	}

	public Enumeration members() {
		return Collections.enumeration(this.members.values());
	}

	public boolean removeMember(Principal user) {
		Object prev = this.members.remove(user);
		return prev != null;
	}

	public String toString() {
		StringBuffer tmp = new StringBuffer(this.getName());
		tmp.append("(members:");
		Iterator iter = this.members.keySet().iterator();

		while (iter.hasNext()) {
			tmp.append(iter.next());
			tmp.append(',');
		}

		tmp.setCharAt(tmp.length() - 1, ')');
		return tmp.toString();
	}

	public synchronized Object clone() throws CloneNotSupportedException {
		SimpleGroup clone = (SimpleGroup) super.clone();
		if (clone != null) {
			clone.members = (HashMap) this.members.clone();
		}

		return clone;
	}
}