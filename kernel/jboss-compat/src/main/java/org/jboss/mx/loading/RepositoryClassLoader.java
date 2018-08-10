package org.jboss.mx.loading;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;
import EDU.oswego.cs.dl.util.concurrent.ReentrantLock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.jboss.logging.Logger;
import org.jboss.util.collection.SoftSet;
import org.jboss.util.loading.Translator;

public abstract class RepositoryClassLoader extends URLClassLoader {
	private static final Logger log = Logger.getLogger(RepositoryClassLoader.class);
	private static final URL[] EMPTY_URL_ARRAY = new URL[0];
	protected LoaderRepository repository = null;
	protected Exception unregisterTrace;
	private int addedOrder;
	protected ClassLoader parent = null;
	private Set classBlackList = Collections.synchronizedSet(new HashSet());
	private Set resourceBlackList = Collections.synchronizedSet(new HashSet());
	private ConcurrentReaderHashMap resourceCache = new ConcurrentReaderHashMap();
	protected ReentrantLock loadLock = new ReentrantLock();
	private int loadClassDepth;

	protected RepositoryClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		this.parent = parent;
		String mode = ClassToStringAction.getProperty("org.jboss.mx.loading.blacklistMode", (String) null);
		if (mode != null && !mode.equalsIgnoreCase("HashSet")) {
			if (mode.equalsIgnoreCase("SoftSet")) {
				this.classBlackList = Collections.synchronizedSet(new SoftSet());
				this.resourceBlackList = Collections.synchronizedSet(new SoftSet());
			}
		} else {
			this.classBlackList = Collections.synchronizedSet(new HashSet());
			this.resourceBlackList = Collections.synchronizedSet(new HashSet());
		}

	}

	public abstract ObjectName getObjectName() throws MalformedObjectNameException;

	public LoaderRepository getLoaderRepository() {
		return this.repository;
	}

	public void setRepository(LoaderRepository repository) {
		log.debug("setRepository, repository=" + repository + ", cl=" + this);
		this.repository = repository;
	}

	public int getAddedOrder() {
		return this.addedOrder;
	}

	public void setAddedOrder(int addedOrder) {
		this.addedOrder = addedOrder;
	}

	public Class loadClassLocally(String name, boolean resolve) throws ClassNotFoundException {
		boolean trace = log.isTraceEnabled();
		if (trace) {
			log.trace("loadClassLocally, " + this + " name=" + name);
		}

		if (name != null && name.length() != 0) {
			Class result = null;

			Class var6;
			try {
				if (this.isClassBlackListed(name)) {
					if (trace) {
						log.trace("Class in blacklist, name=" + name);
					}

					throw new ClassNotFoundException("Class Not Found(blacklist): " + name);
				}

				if (name.startsWith("java.")) {
					ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
					var6 = systemClassLoader.loadClass(name);
					return var6;
				}

				try {
					result = super.loadClass(name, resolve);
					Class var5 = result;
					return var5;
				} catch (ClassNotFoundException var10) {
					this.addToClassBlackList(name);
					if (name.charAt(0) != '[') {
						if (trace) {
							log.trace("CFNE: Adding to blacklist: " + name);
						}

						throw var10;
					}
				}

				result = Class.forName(name, true, this);
				this.removeFromClassBlackList(name);
				var6 = result;
			} finally {
				if (trace) {
					if (result != null) {
						log.trace("loadClassLocally, " + this + " name=" + name + " class=" + result + " cl="
								+ result.getClassLoader());
					} else {
						log.trace("loadClassLocally, " + this + " name=" + name + " not found");
					}
				}

			}

			return var6;
		} else {
			throw new ClassNotFoundException("Null or empty class name");
		}
	}

	public URL getResourceLocally(String name) {
		URL resURL = (URL) this.resourceCache.get(name);
		if (resURL != null) {
			return resURL;
		} else if (this.isResourceBlackListed(name)) {
			return null;
		} else {
			resURL = super.getResource(name);
			if (log.isTraceEnabled()) {
				log.trace("getResourceLocally(" + this + "), name=" + name + ", resURL:" + resURL);
			}

			if (resURL == null) {
				this.addToResourceBlackList(name);
			} else {
				this.resourceCache.put(name, resURL);
			}

			return resURL;
		}
	}

	public URL getURL() {
		URL[] urls = super.getURLs();
		return urls.length > 0 ? urls[0] : null;
	}

	public void unregister() {
		log.debug("Unregistering cl=" + this);
		if (this.repository != null) {
			this.repository.removeClassLoader(this);
		}

		this.clearBlackLists();
		this.resourceCache.clear();
		this.repository = null;
		this.unregisterTrace = new Exception();
	}

	public URL[] getClasspath() {
		return super.getURLs();
	}

	public URL[] getAllURLs() {
		return this.repository.getURLs();
	}

	public void addToClassBlackList(String name) {
		this.classBlackList.add(name);
	}

	public void removeFromClassBlackList(String name) {
		this.classBlackList.remove(name);
	}

	public boolean isClassBlackListed(String name) {
		return this.classBlackList.contains(name);
	}

	public void clearClassBlackList() {
		this.classBlackList.clear();
	}

	public void addToResourceBlackList(String name) {
		this.resourceBlackList.add(name);
	}

	public void removeFromResourceBlackList(String name) {
		this.resourceBlackList.remove(name);
	}

	public boolean isResourceBlackListed(String name) {
		return this.resourceBlackList.contains(name);
	}

	public void clearResourceBlackList() {
		this.resourceBlackList.clear();
	}

	public void clearBlackLists() {
		this.clearClassBlackList();
		this.clearResourceBlackList();
	}

	public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		boolean trace = log.isTraceEnabled();
		if (trace) {
			log.trace("loadClass " + this + " name=" + name + ", loadClassDepth=" + this.loadClassDepth);
		}

		Class clazz = null;

		Class var5;
		try {
			if (this.repository != null) {
				clazz = this.repository.getCachedClass(name);
				if (clazz != null) {
					if (log.isTraceEnabled()) {
						StringBuffer buffer = new StringBuffer("Loaded class from cache, ");
						ClassToStringAction.toString(clazz, buffer);
						log.trace(buffer.toString());
					}

					var5 = clazz;
					return var5;
				}
			}

			clazz = this.loadClassImpl(name, resolve, Integer.MAX_VALUE);
			var5 = clazz;
		} finally {
			if (trace) {
				if (clazz != null) {
					log.trace("loadClass " + this + " name=" + name + " class=" + clazz + " cl="
							+ clazz.getClassLoader());
				} else {
					log.trace("loadClass " + this + " name=" + name + " not found");
				}
			}

		}

		return var5;
	}

	public Class loadClassBefore(String name) throws ClassNotFoundException {
		boolean trace = log.isTraceEnabled();
		if (trace) {
			log.trace("loadClassBefore " + this + " name=" + name);
		}

		Class clazz = null;

		Class var4;
		try {
			clazz = this.loadClassImpl(name, false, this.addedOrder);
			var4 = clazz;
		} finally {
			if (trace) {
				if (clazz != null) {
					log.trace("loadClassBefore " + this + " name=" + name + " class=" + clazz + " cl="
							+ clazz.getClassLoader());
				} else {
					log.trace("loadClassBefore " + this + " name=" + name + " not found");
				}
			}

		}

		return var4;
	}

	public synchronized Class loadClassImpl(String name, boolean resolve, int stopAt) throws ClassNotFoundException {
		++this.loadClassDepth;
		boolean trace = log.isTraceEnabled();
		if (trace) {
			log.trace("loadClassImpl, name=" + name + ", resolve=" + resolve);
		}

		if (this.repository == null) {
			try {
				return super.loadClass(name, resolve);
			} catch (ClassNotFoundException var15) {
				String msg = "Invalid use of destroyed classloader, UCL destroyed at:";
				throw new ClassNotFoundException(msg, this.unregisterTrace);
			}
		} else {
			for (boolean acquired = this.attempt(1L); !acquired; acquired = this.attempt(1L)) {
				try {
					if (trace) {
						log.trace("Waiting for loadClass lock");
					}

					this.wait();
				} catch (InterruptedException var16) {
					;
				}
			}

			ClassLoadingTask task = null;

			try {
				Thread t = Thread.currentThread();
				if (this.loadLock.holds() == 1L) {
					LoadMgr3.registerLoaderThread(this, t);
				}

				task = new ClassLoadingTask(name, this, t, stopAt);
				UnifiedLoaderRepository3 ulr3 = (UnifiedLoaderRepository3) this.repository;
				if (!LoadMgr3.beginLoadTask(task, ulr3)) {
					while (task.threadTaskCount != 0) {
						try {
							LoadMgr3.nextTask(t, task, ulr3);
						} catch (InterruptedException var17) {
							break;
						}
					}
				}
			} finally {
				if (this.loadLock.holds() == 1L) {
					LoadMgr3.endLoadTask(task);
				}

				this.release();
				this.notifyAll();
				--this.loadClassDepth;
			}

			if (task.loadedClass == null) {
				if (task.loadException instanceof ClassNotFoundException) {
					throw (ClassNotFoundException) task.loadException;
				} else if (task.loadException instanceof NoClassDefFoundError) {
					throw (NoClassDefFoundError) task.loadException;
				} else if (task.loadException != null) {
					if (log.isTraceEnabled()) {
						log.trace("Unexpected error during load of:" + name, task.loadException);
					}

					String msg = "Unexpected error during load of: " + name + ", msg="
							+ task.loadException.getMessage();
					ClassNotFoundException cnfe = new ClassNotFoundException(msg, task.loadException);
					throw cnfe;
				} else {
					throw new IllegalStateException("ClassLoadingTask.loadedTask is null, name: " + name);
				}
			} else {
				return task.loadedClass;
			}
		}
	}

	public URL getResource(String name) {
		return this.repository != null ? this.repository.getResource(name, this) : null;
	}

	public Enumeration findResources(String name) throws IOException {
		Vector resURLs = new Vector();
		if (this.repository == null) {
			String msg = "Invalid use of destroyed classloader, UCL destroyed at:";
			IOException e = new IOException(msg);
			e.initCause(this.unregisterTrace);
			throw e;
		} else {
			this.repository.getResources(name, this, resURLs);
			return resURLs.elements();
		}
	}

	public Enumeration findResourcesLocally(String name) throws IOException {
		return super.findResources(name);
	}

	protected Class findClass(String name) throws ClassNotFoundException {
		if (this.repository == null) {
			String msg = "Invalid use of destroyed classloader for " + name + ", UCL destroyed at:";
			ClassNotFoundException e = new ClassNotFoundException(msg);
			e.initCause(this.unregisterTrace);
			throw e;
		} else {
			boolean trace = log.isTraceEnabled();
			if (trace) {
				log.trace("findClass, name=" + name);
			}

			if (this.isClassBlackListed(name)) {
				if (trace) {
					log.trace("Class in blacklist, name=" + name);
				}

				throw new ClassNotFoundException("Class Not Found(blacklist): " + name);
			} else {
				Translator translator = this.repository.getTranslator();
				if (translator != null) {
					try {
						URL classUrl = this.getClassURL(name);
						byte[] rawcode = this.loadByteCode(classUrl);
						URL codeSourceUrl = this.getCodeSourceURL(name, classUrl);
						ProtectionDomain pd = this.getProtectionDomain(codeSourceUrl);
						byte[] bytecode = translator.transform(this, name, (Class) null, pd, rawcode);
						if (bytecode == null) {
							bytecode = rawcode;
						}

						this.definePackage(name);
						return this.defineClass(name, bytecode, 0, bytecode.length, pd);
					} catch (ClassNotFoundException var9) {
						throw var9;
					} catch (Throwable var10) {
						throw new ClassNotFoundException(name, var10);
					}
				} else {
					Class clazz = null;

					try {
						clazz = this.findClassLocally(name);
						return clazz;
					} catch (ClassNotFoundException var11) {
						if (trace) {
							log.trace("CFNE: Adding to blacklist: " + name);
						}

						this.addToClassBlackList(name);
						throw var11;
					}
				}
			}
		}
	}

	protected Class findClassLocally(String name) throws ClassNotFoundException {
		return super.findClass(name);
	}

	protected void definePackage(String className) {
		int i = className.lastIndexOf(46);
		if (i != -1) {
			try {
				this.definePackage(className.substring(0, i), (String) null, (String) null, (String) null,
						(String) null, (String) null, (String) null, (URL) null);
			} catch (IllegalArgumentException var4) {
				;
			}

		}
	}

	public void addURL(URL url) {
		if (url == null) {
			throw new IllegalArgumentException("url cannot be null");
		} else {
			if (this.repository.addClassLoaderURL(this, url)) {
				log.debug("Added url: " + url + ", to ucl: " + this);
				String query = url.getQuery();
				if (query != null) {
					String ext = url.toExternalForm();
					String ext2 = ext.substring(0, ext.length() - query.length() - 1);

					try {
						url = new URL(ext2);
					} catch (MalformedURLException var6) {
						log.warn("Failed to strip query from: " + url, var6);
					}
				}

				super.addURL(url);
				this.clearBlackLists();
			} else if (log.isTraceEnabled()) {
				log.trace("Ignoring duplicate url: " + url + ", for ucl: " + this);
			}

		}
	}

	public URL[] getURLs() {
		return EMPTY_URL_ARRAY;
	}

	public Package getPackage(String name) {
		return super.getPackage(name);
	}

	public Package[] getPackages() {
		return super.getPackages();
	}

	public final boolean equals(Object other) {
		return super.equals(other);
	}

	public final int hashCode() {
		return super.hashCode();
	}

	public String toString() {
		return super.toString() + "{ url=" + this.getURL() + " }";
	}

	protected boolean attempt(long waitMS) {
		boolean acquired = false;
		boolean trace = log.isTraceEnabled();
		boolean threadWasInterrupted = Thread.interrupted();

		try {
			acquired = this.loadLock.attempt(waitMS);
		} catch (InterruptedException var10) {
			;
		} finally {
			if (threadWasInterrupted) {
				Thread.currentThread().interrupt();
			}

		}

		if (trace) {
			log.trace("attempt(" + this.loadLock.holds() + ") was: " + acquired + " for :" + this);
		}

		return acquired;
	}

	protected void acquire() {
		boolean threadWasInterrupted = Thread.interrupted();

		try {
			this.loadLock.acquire();
		} catch (InterruptedException var6) {
			;
		} finally {
			if (threadWasInterrupted) {
				Thread.currentThread().interrupt();
			}

		}

		if (log.isTraceEnabled()) {
			log.trace("acquired(" + this.loadLock.holds() + ") for :" + this);
		}

	}

	protected void release() {
		if (log.isTraceEnabled()) {
			log.trace("release(" + this.loadLock.holds() + ") for :" + this);
		}

		this.loadLock.release();
		if (log.isTraceEnabled()) {
			log.trace("released, holds: " + this.loadLock.holds());
		}

	}

	protected byte[] loadByteCode(String classname) throws ClassNotFoundException, IOException {
		byte[] bytecode = null;
		URL classURL = this.getClassURL(classname);
		InputStream is = null;

		try {
			is = classURL.openStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] tmp = new byte[1024];
			boolean var7 = false;

			int read;
			while ((read = is.read(tmp)) > 0) {
				baos.write(tmp, 0, read);
			}

			byte[] bytecode = baos.toByteArray();
			return bytecode;
		} finally {
			if (is != null) {
				is.close();
			}

		}
	}

	protected byte[] loadByteCode(URL classURL) throws ClassNotFoundException, IOException {
		byte[] bytecode = null;
		InputStream is = null;

		try {
			is = classURL.openStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] tmp = new byte[1024];
			boolean var6 = false;

			int read;
			while ((read = is.read(tmp)) > 0) {
				baos.write(tmp, 0, read);
			}

			byte[] bytecode = baos.toByteArray();
			return bytecode;
		} finally {
			if (is != null) {
				is.close();
			}

		}
	}

	protected ProtectionDomain getProtectionDomain(URL codesourceUrl) {
		Certificate[] certs = null;
		CodeSource cs = new CodeSource(codesourceUrl, (Certificate[]) certs);
		PermissionCollection permissions = Policy.getPolicy().getPermissions(cs);
		if (log.isTraceEnabled()) {
			log.trace(
					"getProtectionDomain, url=" + codesourceUrl + " codeSource=" + cs + " permissions=" + permissions);
		}

		return new ProtectionDomain(cs, permissions);
	}

	private URL getCodeSourceURL(String classname, URL classURL) throws MalformedURLException {
		String classRsrcName = classname.replace('.', '/') + ".class";
		String urlAsString = classURL.toString();
		int idx = urlAsString.indexOf(classRsrcName);
		if (idx == -1) {
			return classURL;
		} else {
			urlAsString = urlAsString.substring(0, idx);
			return new URL(urlAsString);
		}
	}

	private URL getClassURL(String classname) throws ClassNotFoundException {
		String classRsrcName = classname.replace('.', '/') + ".class";
		URL classURL = this.getResourceLocally(classRsrcName);
		if (classURL == null) {
			String msg = "Failed to find: " + classname + " as resource: " + classRsrcName;
			throw new ClassNotFoundException(msg);
		} else {
			return classURL;
		}
	}
}