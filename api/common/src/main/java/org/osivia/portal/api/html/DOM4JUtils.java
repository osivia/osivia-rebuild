package org.osivia.portal.api.html;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;

/**
 * Utility class with null-safe methods for DOM4J usage.
 *
 * @author Cédric Krommenhoek
 * @see Element
 * @see QName
 */
public final class DOM4JUtils {

    /**
     * Private constructor : prevent instantiation.
     */
    private DOM4JUtils() {
        throw new AssertionError();
    }


    /**
     * Generate DOM element.
     *
     * @param name DOM element name
     * @param htmlClass HTML class, may be null
     * @param text DOM element inner text, may be null
     * @param glyphicon glyphicon name, may be null
     * @param role accessibility role, may be null
     * @return DOM element, or null if DOM element name is blank
     */
    public static Element generateElement(String name, String htmlClass, String text, String glyphicon, AccessibilityRoles role) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        Element element = new DOMElement(QName.get(name));
        addAttribute(element, HTMLConstants.CLASS, htmlClass);
        addGlyphiconText(element, glyphicon, text);

        if (role != null) {
            addAttribute(element, HTMLConstants.ROLE, role.getValue());
        }

        return element;
    }


    /**
     * Generate DOM element.
     *
     * @param name DOM element name
     * @param htmlClass HTML class, may be null
     * @param text DOM element inner text, may be null
     * @return DOM element, or null if element name is blank
     */
    public static Element generateElement(String name, String htmlClass, String text) {
        return generateElement(name, htmlClass, text, null, null);
    }


    /**
     * Generate HTML "div" DOM element.
     *
     * @param htmlClass HTML class, may be null
     * @param role accessibility role, may be null
     * @return HTML "div" DOM element
     */
    public static Element generateDivElement(String htmlClass, AccessibilityRoles role) {
        return generateElement(HTMLConstants.DIV, htmlClass, StringUtils.EMPTY, null, role);
    }


    /**
     * Generate HTML "div" DOM element.
     *
     * @param htmlClass HTML class, may be null
     * @return HTML "div" DOM element
     */
    public static Element generateDivElement(String htmlClass) {
        return generateDivElement(htmlClass, null);
    }


    /**
     * Generate HTML "a" link DOM element.
     *
     * @param href link URL
     * @param target link target, may be null
     * @param onclick link onclick action, may be null
     * @param htmlClass HTML class, may be null
     * @param text link inner text
     * @param glyphicon glyphicon name, may be null
     * @param role accessibility role, may be null
     * @return HTML "a" link DOM element
     */
    public static Element generateLinkElement(String href, String target, String onclick, String htmlClass, String text, String glyphicon,
            AccessibilityRoles role) {
        Element link = generateElement(HTMLConstants.A, htmlClass, text, glyphicon, role);
        addAttribute(link, HTMLConstants.TARGET, target);
        addAttribute(link, HTMLConstants.ONCLICK, onclick);

        if (href == null) {
            addAttribute(link, HTMLConstants.HREF, HTMLConstants.A_HREF_DEFAULT);
        } else {
            addAttribute(link, HTMLConstants.HREF, href);
        }

        return link;
    }


    /**
     * Generate HTML "a" link DOM element.
     *
     * @param href link URL
     * @param target link target, may be null
     * @param onclick link onclick action, may be null
     * @param htmlClass HTML class, may be null
     * @param text link inner text
     * @param glyphicon glyphicon name, may be null
     * @return HTML "a" link DOM element
     */
    public static Element generateLinkElement(String href, String target, String onclick, String htmlClass, String text, String glyphicon) {
        return generateLinkElement(href, target, onclick, htmlClass, text, glyphicon, null);
    }


    /**
     * Generate HTML "a" link DOM element.
     *
     * @param href link URL
     * @param target link target, may be null
     * @param onclick link onclick action, may be null
     * @param htmlClass HTML class, may be null
     * @param text link inner text
     * @return HTML "a" link DOM element
     */
    public static Element generateLinkElement(String href, String target, String onclick, String htmlClass, String text) {
        return generateLinkElement(href, target, onclick, htmlClass, text, null, null);
    }


    /**
     * Add attribute to an existing DOM element.
     *
     * @param element DOM element
     * @param name attribute name
     * @param value attribute value
     */
    public static void addAttribute(Element element, String name, String value) {
        if ((element == null) || StringUtils.isBlank(name)) {
            return;
        }

        if (value != null) {
            element.addAttribute(QName.get(name), value);
        }
    }


    /**
     * Add data attribute to an existing DOM element.
     * 
     * @param element DOM element
     * @param name data attribute name
     * @param value data attribute value
     */
    public static void addDataAttribute(Element element, String name, String value) {
        addAttribute(element, "data-" + name, value);
    }


    /**
     * Add ARIA attribute to an existing DOM element.
     * 
     * @param element DOM element
     * @param name ARIA attribute name
     * @param value ARIA attribute value
     */
    public static void addAriaAttribute(Element element, String name, String value) {
        addAttribute(element, "aria-" + name, value);
    }


    /**
     * Add text to an existing DOM element.
     *
     * @param element DOM element
     * @param text element inner text
     */
    public static void addText(Element element, String text) {
        if (element == null) {
            return;
        }

        if (text != null) {
            element.addText(text);
        }
    }


    /**
     * Add glyphicon and text to an existing DOM element.
     *
     * @param element DOM element
     * @param glyphicon glyphicon name, may be null
     * @param text DOM element inner text
     */
    public static void addGlyphiconText(Element element, String glyphicon, String text) {
        if (element == null) {
            return;
        }

        if (glyphicon != null) {
            Element glyph = generateElement(HTMLConstants.I, glyphicon, StringUtils.EMPTY);
            element.add(glyph);

            if (text != null) {
                String htmlClass = null;
                String elementHTMLClass = element.attributeValue(QName.get(HTMLConstants.CLASS));
                if ((elementHTMLClass != null) && Arrays.asList(StringUtils.split(elementHTMLClass)).contains("btn")) {
                    // Button text with glyphicon is hidden for extra-small screens
                    htmlClass = "hidden-xs";
                }

                Element textSpan = generateElement(HTMLConstants.SPAN, htmlClass, text);
                element.add(textSpan);
            }
        } else {
            addText(element, text);
        }
    }


    /**
     * Add tooltip to DOM element.
     *
     * @param element DOM element
     * @param title tooltip title
     */
    public static void addTooltip(Element element, String title) {
        if (element == null) {
            return;
        }

        if (StringUtils.isNotBlank(title)) {
            addAttribute(element, HTMLConstants.TITLE, title);
            addAttribute(element, HTMLConstants.DATA_TOGGLE, "tooltip");
            addAttribute(element, HTMLConstants.DATA_PLACEMENT, "bottom");
        }
    }


    /**
     * Write HTML content.
     *
     * @param element DOM element
     * @return HTML content
     */
    public static String write(Element element) {
        String html;
        try {
            StringWriter stringWriter = new StringWriter();
            HTMLWriter htmlWriter = new HTMLWriter(stringWriter);
            htmlWriter.setEscapeText(false);
            htmlWriter.write(element);
            htmlWriter.flush();
            html = stringWriter.toString();
        } catch (IOException e) {
            html = StringUtils.EMPTY;
        }
        return html;
    }


    /**
     * Write compact HTML content.
     *
     * @param element DOM element
     * @return HTML content
     */
    public static String writeCompact(Element element) {
        String html;
        try {
            StringWriter stringWriter = new StringWriter();
            HTMLWriter htmlWriter = new HTMLWriter(stringWriter, OutputFormat.createCompactFormat());
            htmlWriter.setEscapeText(false);
            htmlWriter.write(element);
            htmlWriter.flush();
            html = stringWriter.toString();
        } catch (IOException e) {
            html = StringUtils.EMPTY;
        }
        return html;
    }


    /**
     * Write HTML content in provided writer.
     * 
     * @param writer writer
     * @param element DOM element
     */
    public static void write(Writer writer, Element element) {
        HTMLWriter htmlWriter = new HTMLWriter(writer);
        htmlWriter.setEscapeText(false);
        try {
            htmlWriter.write(element);
            htmlWriter.flush();
        } catch (IOException e) {
            // Do nothing
        }
    }

}
