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
import org.jboss.crypto.digest.DigestCallback;
import org.jboss.security.Util;

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
		String username;
		String password;
		if (super.login()) {
			Object username = this.sharedState.get("javax.security.auth.login.name");
			if (username instanceof Principal) {
				this.identity = (Principal) username;
			} else {
				username = username.toString();

				try {
					this.identity = this.createIdentity(username);
				} catch (Exception var7) {
					this.log.debug("Failed to create principal", var7);
					throw new LoginException("Failed to create principal: " + var7.getMessage());
				}
			}

			Object password = this.sharedState.get("javax.security.auth.login.password");
			if (password instanceof char[]) {
				this.credential = (char[]) ((char[]) password);
			} else if (password != null) {
				password = password.toString();
				this.credential = password.toCharArray();
			}

			return true;
		} else {
			super.loginOk = false;
			String[] info = this.getUsernameAndPassword();
			username = info[0];
			password = info[1];
			if (username == null && password == null) {
				this.identity = this.unauthenticatedIdentity;
				super.log.trace("Authenticating as unauthenticatedIdentity=" + this.identity);
			}

			if (this.identity == null) {
				try {
					this.identity = this.createIdentity(username);
				} catch (Exception var8) {
					this.log.debug("Failed to create principal", var8);
					throw new LoginException("Failed to create principal: " + var8.getMessage());
				}

				if (this.hashAlgorithm != null && this.hashUserPassword) {
					password = this.createPasswordHash(username, password, "digestCallback");
				}

				String expectedPassword = this.getUsersPassword();
				if (this.hashAlgorithm != null && this.hashStorePassword) {
					expectedPassword = this.createPasswordHash(username, expectedPassword, "storeDigestCallback");
				}

				if (!this.validatePassword(password, expectedPassword)) {
					Throwable ex = this.getValidateError();
					FailedLoginException fle = new FailedLoginException("Password Incorrect/Password Required");
					if (ex != null) {
						this.log.debug("Bad password for username=" + username, ex);
						fle.initCause(ex);
					} else {
						this.log.debug("Bad password for username=" + username);
					}

					throw fle;
				}
			}

			if (this.getUseFirstPass()) {
				this.sharedState.put("javax.security.auth.login.name", username);
				this.sharedState.put("javax.security.auth.login.password", this.credential);
			}

			super.loginOk = true;
			super.log.trace("User '" + this.identity + "' authenticated, loginOk=" + this.loginOk);
			return true;
		}
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
		if (this.legacyCreatePasswordHash) {
			LoginException le;
			try {
				Class[] sig = new Class[]{String.class, String.class};
				Method createPasswordHash = this.getClass().getMethod("createPasswordHash", sig);
				Object[] args = new Object[]{username, password};
				String passwordHash = (String) createPasswordHash.invoke(this, args);
				return passwordHash;
			} catch (InvocationTargetException var10) {
				le = new LoginException("Failed to delegate createPasswordHash");
				le.initCause(var10.getTargetException());
				throw le;
			} catch (Exception var11) {
				le = new LoginException("Failed to delegate createPasswordHash");
				le.initCause(var11);
				throw le;
			}
		} else {
			DigestCallback callback = null;
			String callbackClassName = (String) this.options.get(digestOption);
			if (callbackClassName != null) {
				try {
					ClassLoader loader = Thread.currentThread().getContextClassLoader();
					Class callbackClass = loader.loadClass(callbackClassName);
					callback = (DigestCallback) callbackClass.newInstance();
					if (this.log.isTraceEnabled()) {
						this.log.trace("Created DigestCallback: " + callback);
					}
				} catch (Exception var14) {
					if (this.log.isTraceEnabled()) {
						this.log.trace("Failed to load DigestCallback", var14);
					}

					SecurityException ex = new SecurityException("Failed to load DigestCallback");
					ex.initCause(var14);
					throw ex;
				}

				Map tmp = new HashMap();
				tmp.putAll(this.options);
				tmp.put("javax.security.auth.login.name", username);
				tmp.put("javax.security.auth.login.password", password);
				callback.init(tmp);
				Callback[] callbacks = (Callback[]) ((Callback[]) tmp.get("callbacks"));
				if (callbacks != null) {
					LoginException le;
					try {
						this.callbackHandler.handle(callbacks);
					} catch (IOException var12) {
						le = new LoginException(digestOption + " callback failed");
						le.initCause(var12);
						throw le;
					} catch (UnsupportedCallbackException var13) {
						le = new LoginException(digestOption + " callback failed");
						le.initCause(var13);
						throw le;
					}
				}
			}

			String passwordHash = Util.createPasswordHash(this.hashAlgorithm, this.hashEncoding, this.hashCharset,
					username, password, callback);
			return passwordHash;
		}
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