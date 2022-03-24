/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.internationalization;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.customization.ICustomizationService;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.taglib.portal.tag.FormatRelativeDateTag;
import org.springframework.context.ApplicationContext;
import org.springframework.context.NoSuchMessageException;

/**
 * Internationalization service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IInternationalizationService
 */
public class InternationalizationService implements IInternationalizationService {

    /** Customization service. */
    private ICustomizationService customizationService;

    /** Class loader. */
    private final ClassLoader classLoader;


    /**
     * Constructor.
     */
    public InternationalizationService() {
        super();
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }


    @Override
    public IBundleFactory getBundleFactory(ClassLoader classLoader) {
        return new BundleFactory(this, classLoader);
    }


    @Override
    public IBundleFactory getBundleFactory(ClassLoader classLoader, ApplicationContext applicationContext) {
        return new BundleFactory(this, classLoader, applicationContext);
    }


    @Override
    public String getString(String key, Locale locale, Object... args) {
        return this.getString(key, locale, null, null, null, args);
    }


    @Override
    public String getString(String key, Locale locale, ClassLoader classLoader, Object... args) {
        return this.getString(key, locale, classLoader, null, null, args);
    }


    @Override
    public String getString(String key, Locale locale, ClassLoader classLoader, ClassLoader customizedClassLoader, Object... args) {
        return this.getString(key, locale, classLoader, customizedClassLoader, null, args);
    }

    @Override
    public String getString(String key, Locale locale, ClassLoader classLoader, ApplicationContext applicationContext, Object... args) {
        return this.getString(key, locale, classLoader, null, applicationContext, args);
    }


    @Override
    public String getString(String key, Locale locale, ClassLoader classLoader, ClassLoader customizedClassLoader, ApplicationContext applicationContext,
                            Object... args) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_KEY, key);
        attributes.put(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_LOCALE, locale);
        CustomizationContext context = new CustomizationContext(attributes);

        // Customizer invocation
        this.customizationService.customize(IInternationalizationService.CUSTOMIZER_ID, context);

        String pattern = null;
        if (attributes.containsKey(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_RESULT)) {
            // Custom result
            pattern = (String) attributes.get(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_RESULT);
        } else {
            if (applicationContext != null) {
                // Application context message source
                try {
                    pattern = applicationContext.getMessage(key, null, locale);
                } catch (NoSuchMessageException e) {
                    // Do nothing
                }
            }

            if (pattern == null) {
                pattern = this.getPattern(key, locale, customizedClassLoader);
            }

            if (pattern == null) {
                pattern = this.getPattern(key, locale, classLoader);
            }


            if (pattern == null) {
                // Saved class loader
                ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();

                // Portal default result
                Thread.currentThread().setContextClassLoader(this.classLoader);

                try {
                    // Resource bundle
                    ResourceBundle resourceBundle = ResourceBundle.getBundle(InternationalizationConstants.RESOURCE_BUNDLE_NAME, locale);
                    pattern = resourceBundle.getString(key);
                } catch (MissingResourceException e) {
                    // Do nothing
                } finally {
                    Thread.currentThread().setContextClassLoader(savedClassLoader);
                }
            }
            
            
            if (pattern == null) {
            	// Portal Resources that have be renamed not to be overloaded
            	pattern = this.getPattern(key, locale, "tag", classLoader);
            }
        }


        // Result
        String result;

        if (pattern == null) {
            result = "[Missing resource: " + key + "]";
        } else {
            
            // display message key
            String prefix = "";
            if( PageProperties.getProperties().getPagePropertiesMap() != null) {
                String messageKey = PageProperties.getProperties().getPagePropertiesMap().get("messageKey");
                if(messageKey != null)
                    prefix = "["+key + "]";
            }
                
            
            Object[] formattedArguments = this.formatArguments(args, locale);
            result = prefix + MessageFormat.format(pattern, formattedArguments);
        }

        return result;
    }


    /**
     * Get pattern.
     *
     * @param key         internationalization key
     * @param locale      locale
     * @param classLoader class loader
     * @return pattern
     */
    private String getPattern(String key, Locale locale, ClassLoader classLoader) {
        return getPattern(key, locale, null, classLoader);
    }


    /**
     * Get pattern.
     *
     * @param key         internationalization key
     * @param locale      locale
     * @param classLoader class loader
     * @return pattern
     */
    private String getPattern(String key, Locale locale, String nameSuffixe, ClassLoader classLoader) {
        String pattern = null;

        if (classLoader != null) {
        	String resourceName = InternationalizationConstants.RESOURCE_BUNDLE_NAME;
        	if( StringUtils.isNotEmpty(nameSuffixe))	{
        		resourceName += "-" + nameSuffixe;
        	}
        	
            // Class loader resource bundle
            ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceName, locale, classLoader);
            if (resourceBundle != null) {
                try {
                    pattern = resourceBundle.getString(key);
                } catch (MissingResourceException e) {
                    // Do nothing
                }
            }
        }

        return pattern;
    }
    
    
    /**
     * Utility method used to format arguments.
     *
     * @param args   arguments
     * @param locale locale
     * @return formatted arguments
     */
    private Object[] formatArguments(Object[] args, Locale locale) {
        if (args == null) {
            return null;
        }

        List<Object> formattedArguments = new ArrayList<Object>(args.length);
        for (Object arg : args) {
            if (arg == null) {
                formattedArguments.add(String.valueOf(arg));
            } else if (NumberUtils.isNumber(arg.toString()) && !NumberUtils.isDigits(args.toString())) {
                // Decimal number
                double value = NumberUtils.createDouble(arg.toString());
                String display = NumberFormat.getNumberInstance(locale).format(value);
                formattedArguments.add(display);
            } else if (arg instanceof Date) {
                // Date
                String date = DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(arg);
                formattedArguments.add(date);
            } else {
                // Default : text
                formattedArguments.add(arg);
            }
        }

        return formattedArguments.toArray();
    }


    /**
     * Getter for customizationService.
     *
     * @return the customizationService
     */
    public ICustomizationService getCustomizationService() {
        return this.customizationService;
    }

    /**
     * Setter for customizationService.
     *
     * @param customizationService the customizationService to set
     */
    public void setCustomizationService(ICustomizationService customizationService) {
        this.customizationService = customizationService;
    }

}
