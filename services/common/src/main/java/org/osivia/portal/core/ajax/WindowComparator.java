package org.osivia.portal.core.ajax;

import java.util.Comparator;

import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.taskbar.TaskbarItem;

/**
 * Window comparator.
 *

 */
public class WindowComparator implements Comparator<Window> {

    /**
     * Constructor.
     */
    public WindowComparator() {
        super();
    }


    /**
     * {@inheritDoc}
     */


    @Override
    public int compare(Window w1, Window w2) {
        int result;
        
        Integer i1;
        
        try { 
            i1 = Integer.parseInt( w1.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER));
        } catch(Exception e)    {
            i1 = null;
        }
        
        Integer i2;
        
        try { 
            i2 = Integer.parseInt( w2.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER));
        } catch(Exception e)    {
            i2 = null;
        }
        
        if (i1 == null) {
            result = -1;
        } else if (i2 == null) {
            result = 1;
        } else {

            result = i1.compareTo(i2);


        }
        return result;
        
    }

}
