package org.osivia.portal.kernel.common.spring;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.osivia.portal.kernel.common.configuration.PortalConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Portal configuration JUnit test case.
 *
 * @author Cédric Krommenhoek
 */
public class PortalConfigurationTest {

    /** Application context. */
    private static ApplicationContext applicationContext;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        applicationContext = new AnnotationConfigApplicationContext(PortalConfiguration.class);
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }


    // @Test
    // public void testApplicationContext() {
    // PortletInvokerInterceptor consumerPortletInvoker = (PortletInvokerInterceptor) applicationContext.getBean("ConsumerPortletInvoker",
    // PortletInvoker.class);
    // assertNotNull(consumerPortletInvoker);
    //
    // ConsumerCacheInterceptor consumerCacheInterceptor = (ConsumerCacheInterceptor) consumerPortletInvoker.getNext();
    // assertNotNull(consumerCacheInterceptor);
    //
    //
    // PortalControllerContextFactory factory = applicationContext.getBean(PortalControllerContextFactory.class);
    // assertNotNull(factory);
    // }

}
