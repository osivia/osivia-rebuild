package org.jboss.security.auth.spi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;



public abstract class UsernamePasswordLoginModule extends AbstractServerLoginModule {
	private Principal identity;
	private char[] credential;
	private String hashAlgorithm = null;
	private String hashCharset = null;
	private String hashEncoding = null;
	private boolean ignorePasswordCase;
	private boolean hashStorePassword;
	private boolean hashUserPassword = true;
	private boolean legacyCreatePasswordHash;
	private Throwable validateError;

	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		this.hashAlgorithm = (String) options.get("hashAlgorithm");
		if (this.hashAlgorithm != null) {
			this.hashEncoding = (String) options.get("hashEncoding");
			if (this.hashEncoding == null) {
				this.hashEncoding = "BASE64";
			}

			this.hashCharset = (String) options.get("hashCharset");
			if (this.log.isTraceEnabled()) {
				this.log.trace("Password hashing activated: algorithm = " + this.hashAlgorithm + ", encoding = "
						+ this.hashEncoding + ", charset = "
						+ (this.hashCharset == null ? "{default}" : this.hashCharset) + ", callback = "
						+ options.get("digestCallback") + ", storeCallback = " + options.get("storeDigestCallback"));
			}
		}

		String flag = (String) options.get("ignorePasswordCase");
		this.ignorePasswordCase = Boolean.valueOf(flag);
		flag = (String) options.get("hashStorePassword");
		this.hashStorePassword = Boolean.valueOf(flag);
		flag = (String) options.get("hashUserPassword");
		if (flag != null) {
			this.hashUserPassword = Boolean.valueOf(flag);
		}

		flag = (String) options.get("legacyCreatePasswordHash");
		if (flag != null) {
			this.legacyCreatePasswordHash = Boolean.valueOf(flag);
		}

	}

	public boolean login() throws LoginException {
		return true;
	}

	protected Principal getIdentity() {
		return this.identity;
	}

	protected Principal getUnauthenticatedIdentity() {
		return this.unauthenticatedIdentity;
	}

	protected Object getCredentials() {
		return this.credential;
	}

	protected String getUsername() {
		String username = null;
		if (this.getIdentity() != null) {
			username = this.getIdentity().getName();
		}

		return username;
	}

	protected String[] getUsernameAndPassword() throws LoginException {
		String[] info = new String[]{null, null};
		if (this.callbackHandler == null) {
			throw new LoginException("Error: no CallbackHandler available to collect authentication information");
		} else {
			NameCallback nc = new NameCallback("User name: ", "guest");
			PasswordCallback pc = new PasswordCallback("Password: ", false);
			Callback[] callbacks = new Callback[]{nc, pc};
			String username = null;
			String password = null;

			LoginException le;
			try {
				this.callbackHandler.handle(callbacks);
				username = nc.getName();
				char[] tmpPassword = pc.getPassword();
				if (tmpPassword != null) {
					this.credential = new char[tmpPassword.length];
					System.arraycopy(tmpPassword, 0, this.credential, 0, tmpPassword.length);
					pc.clearPassword();
					password = new String(this.credential);
				}
			} catch (IOException var9) {
				le = new LoginException("Failed to get username/password");
				le.initCause(var9);
				throw le;
			} catch (UnsupportedCallbackException var10) {
				le = new LoginException("CallbackHandler does not support: " + var10.getCallback());
				le.initCause(var10);
				throw le;
			}

			info[0] = username;
			info[1] = password;
			return info;
		}
	}

	protected String createPasswordHash(String username, String password, String digestOption) throws LoginException {
		return null;
	}

	protected Throwable getValidateError() {
		return this.validateError;
	}

	protected void setValidateError(Throwable validateError) {
		this.validateError = validateError;
	}

	protected boolean validatePassword(String inputPassword, String expectedPassword) {
		if (inputPassword != null && expectedPassword != null) {
			boolean valid = false;
			if (this.ignorePasswordCase) {
				valid = inputPassword.equalsIgnoreCase(expectedPassword);
			} else {
				valid = inputPassword.equals(expectedPassword);
			}

			return valid;
		} else {
			return false;
		}
	}

	protected abstract String getUsersPassword() throws LoginException;
}