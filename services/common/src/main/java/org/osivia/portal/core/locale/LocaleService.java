package org.osivia.portal.core.locale;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locale.ILocaleService;
import org.springframework.stereotype.Service;

@Service
public class LocaleService implements ILocaleService {

    @Override
    public void setLocale(PortalControllerContext portalCtx, Locale locale) throws PortalException {
        HttpServletRequest mainRequest = (HttpServletRequest) portalCtx.getHttpServletRequest();
        mainRequest.getSession().setAttribute("osivia.locale", locale);

    }

    @Override
    public Locale getLocale(PortalControllerContext portalCtx) throws PortalException {
        HttpServletRequest mainRequest = (HttpServletRequest) portalCtx.getHttpServletRequest();
        Locale locale = (Locale) mainRequest.getSession().getAttribute("osivia.locale");
        if (locale == null)
            locale = Locale.FRENCH;

        return locale;
    }


}
