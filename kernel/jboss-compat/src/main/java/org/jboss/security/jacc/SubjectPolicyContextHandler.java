package org.jboss.security.jacc;

import java.security.AccessController;
import java.util.HashSet;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContextException;
import javax.security.jacc.PolicyContextHandler;
import org.jboss.security.jacc.SubjectPolicyContextHandler.GetSubjectAction;

public class SubjectPolicyContextHandler implements PolicyContextHandler {
	public static final String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
	public static final HashSet EMPTY_SET = new HashSet();

	public Object getContext(String key, Object data) throws PolicyContextException {
		if (!key.equalsIgnoreCase("javax.security.auth.Subject.container")) {
			return null;
		} else {
			Subject subject = (Subject) AccessController.doPrivileged(GetSubjectAction.ACTION);
			return subject;
		}
	}

	public String[] getKeys() throws PolicyContextException {
		String[] keys = new String[]{"javax.security.auth.Subject.container"};
		return keys;
	}

	public boolean supports(String key) throws PolicyContextException {
		return key.equalsIgnoreCase("javax.security.auth.Subject.container");
	}
}