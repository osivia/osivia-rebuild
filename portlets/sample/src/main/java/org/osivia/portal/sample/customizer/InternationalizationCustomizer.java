package org.osivia.portal.sample.customizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.customization.CustomizationModuleMetadatas;
import org.osivia.portal.api.customization.ICustomizationModule;
import org.osivia.portal.api.customization.ICustomizationModulesRepository;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;

/**
 * Internationalization customizer.
 *
 * @author Cédric Krommenhoek
 * @see GenericPortlet
 * @see ICustomizationModule
 */
public class InternationalizationCustomizer extends GenericPortlet implements ICustomizationModule {

    /** Customizer name. */
    private static final String CUSTOMIZER_NAME = "cloud-ens.sample.customizer.internationalization";
    /** Customization modules repository attribute name. */
    private static final String ATTRIBUTE_CUSTOMIZATION_MODULES_REPOSITORY = "CustomizationModulesRepository";
    /** Custom resource bundle name. */
    private static final String RESOURCE_BUNDLE_NAME = "SampleCustomizer";


    /** Customization module metadatas. */
    private final CustomizationModuleMetadatas metadatas;


    /** Customization modules repository. */
    private ICustomizationModulesRepository repository;

    /** External file */
    ClassLoader fileClassLoader;
    
    /** UTF decoder */
    UTF8Control _UTF8Control;

    /** External bundleName **/

    /** Log. */
    private final Log log;

    /**
     * Constructor.
     */
    public InternationalizationCustomizer() {
        super();
        this.metadatas = this.generateMetadatas();
        this.log = LogFactory.getLog(this.getClass());
    }


    /**
     * Generate customization module metadatas.
     *
     * @return metadatas
     */
    private final CustomizationModuleMetadatas generateMetadatas() {
        CustomizationModuleMetadatas metadatas = new CustomizationModuleMetadatas();
        metadatas.setName(CUSTOMIZER_NAME);
        metadatas.setModule(this);
        metadatas.setCustomizationIDs(Arrays.asList(IInternationalizationService.CUSTOMIZER_ID));
        metadatas.setOrder(9999);
        return metadatas;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws PortletException {
        super.init();
        this.repository = Locator.getService("osivia:service=CustomizationService", ICustomizationModulesRepository.class);
        this.repository.register(this.metadatas);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        super.destroy();
        this.repository.unregister(this.metadatas);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void customize(CustomizationContext customizationContext) {
        Map<String, Object> attributes = customizationContext.getAttributes();
        String key = (String) attributes.get(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_KEY);
        Locale locale = (Locale) attributes.get(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_LOCALE);

        if (StringUtils.isNotBlank(key) && (locale != null)) {
            try {


                    ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);
                    String result = resourceBundle.getString(key);

                attributes.put(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_RESULT, result);
            } catch (MissingResourceException e) {
                // Do nothing
            }
        }
    }
    
    /**
     * Decode the resources according to utf-8 decoding
     * 
     * @author Jean-Sébastien
     */
    public class UTF8Control extends ResourceBundle.Control {
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
                                        boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            // The below is a copy of the default implementation.
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    // Only this line is changed to make it to read properties files as UTF-8.
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }

}
