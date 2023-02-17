package org.osivia.portal.core.layouts;

import java.util.Set;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;

public interface IDynamicLayoutService {
    
    String getLayoutCode(ControllerContext context, Page page) throws ControllerException ;

    DynamicLayoutInfos getLayoutInfos(ControllerContext context, Page page) throws ControllerException ;
}
