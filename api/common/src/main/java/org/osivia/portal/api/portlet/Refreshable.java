package org.osivia.portal.api.portlet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Refreshable.
 * Enables the refresh of a session Bean
 * 
 * @author Cédric Krommenhoek
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Refreshable {

}
