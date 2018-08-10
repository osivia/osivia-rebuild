package org.jboss.security.auth.login;

import javax.management.ObjectName;
import org.jboss.system.ServiceMBean;

public interface DynamicLoginConfigMBean extends ServiceMBean {
	PolicyConfig getPolicyConfig();

	void setPolicyConfig(PolicyConfig var1);

	String getAuthConfig();

	void setAuthConfig(String var1);

	ObjectName getLoginConfigService();

	void setLoginConfigService(ObjectName var1);

	void flushAuthenticationCaches() throws Exception;

	ObjectName getSecurityManagerService();

	void setSecurityManagerService(ObjectName var1);
}