package org.jboss.mx.server;

public interface ServerConstants {
	String JMI_DOMAIN = "JMImplementation";
	String MBEAN_SERVER_DELEGATE = "JMImplementation:type=MBeanServerDelegate";
	String MBEAN_REGISTRY = "JMImplementation:type=MBeanRegistry";
	String MBEAN_SERVER_CONFIGURATION = "JMImplementation:type=MBeanServerConfiguration";
	String DEFAULT_DOMAIN = "DefaultDomain";
	String SPECIFICATION_NAME = "Java Management Extensions Instrumentation and Agent Specification";
	String SPECIFICATION_VERSION = "1.2 Maintenance Release";
	String SPECIFICATION_VENDOR = "Sun Microsystems, Inc.";
	String IMPLEMENTATION_NAME = "JBossMX";
	String IMPLEMENTATION_VERSION = "4.0.0";
	String IMPLEMENTATION_VENDOR = "JBoss Organization";
	String REQUIRED_MODELMBEAN_CLASS_PROPERTY = "jbossmx.required.modelmbean.class";
	String DEFAULT_REQUIRED_MODELMBEAN_CLASS = "org.jboss.mx.modelmbean.XMBean";
	String LOADER_REPOSITORY_CLASS_PROPERTY = "jbossmx.loader.repository.class";
	String DEFAULT_LOADER_REPOSITORY_CLASS = "org.jboss.mx.loading.UnifiedLoaderRepository3";
	String UNIFIED_LOADER_REPOSITORY_CLASS = "org.jboss.mx.loading.UnifiedLoaderRepository3";
	String DEFAULT_SCOPED_REPOSITORY_CLASS = "org.jboss.mx.loading.HeirarchicalLoaderRepository3";
	String DEFAULT_SCOPED_REPOSITORY_PARSER_CLASS = "org.jboss.mx.loading.HeirarchicalLoaderRepository3ConfigParser";
	String MBEAN_REGISTRY_CLASS_PROPERTY = "jbossmx.mbean.registry.class";
	String DEFAULT_MBEAN_REGISTRY_CLASS = "org.jboss.mx.server.registry.BasicMBeanRegistry";
	String MBEAN_SERVER_BUILDER_CLASS_PROPERTY = "javax.management.builder.initial";
	String DEFAULT_MBEAN_SERVER_BUILDER_CLASS = "org.jboss.mx.server.MBeanServerBuilderImpl";
	String OPTIMIZE_REFLECTED_DISPATCHER = "jbossmx.optimized.dispatcher";
	String DEFAULT_LOADER_NAME = "JMImplementation:service=LoaderRepository,name=Default";
	String CLASSLOADER_ADDED = "jboss.mx.classloader.added";
	String CLASSLOADER_REMOVED = "jboss.mx.classloader.removed";
	String CLASS_REMOVED = "jboss.mx.class.removed";
	String CLASSLOADER = "org.jboss.mx.classloader";
}