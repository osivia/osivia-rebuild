package org.osivia.portal.kernel.portal;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

/**
 * Portal prepare response.
 *
 * @author Cédric Krommenhoek
 * @see PortalResponse
 */
public class PortalPrepareResponse extends PortalResponse {

    /** Print writer. */
    private PrintWriter writer;
    /** Servlet output stream. */
    private ServletOutputStream outputStream;
    /** Content type. */
    private String contentType;
    /** Character encoding. */
    private String characterEncoding;
    /** Locale. */
    private Locale locale;
    /** Buffer size. */
    private int bufferSize;

    /** Window definitions. */
    private Map<String, WindowDef> windowDefs;
    /** Page parameter definitions. */
    private Map<QName, PageParameterDef> paramDefs;


    /**
     * Constructor.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     */
    public PortalPrepareResponse(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);

        this.windowDefs = new HashMap<String, WindowDef>();
        this.paramDefs = new HashMap<QName, PageParameterDef>();
    }


    /**
     * Get window identifiers.
     *
     * @return window identifiers
     */
    public Set<String> getWindowIds() {
        return this.windowDefs.keySet();
    }


    /**
     * Get window definition.
     *
     * @param windowId window identifier
     * @return window definition
     */
    public WindowDef getWindowDef(String windowId) {
        return this.windowDefs.get(windowId);
    }


    /**
     * Add window definition.
     *
     * @param windowId window identifier
     * @param portlet portlet
     */
    public void addWindowDef(String windowId, WindowDef portlet) {
        this.windowDefs.put(windowId, portlet);
    }


    /**
     * Get page parameter names;
     *
     * @return page parameter names
     */
    public Set<QName> getPageParameterNames() {
        return this.paramDefs.keySet();
    }


    /**
     * Get page parameter definition.
     *
     * @param name page parameter name
     * @return page parameter definition
     */
    public PageParameterDef getPageParameterDef(QName name) {
        return this.paramDefs.get(name);
    }


    /**
     * Set page parameter definition.
     * 
     * @param paramDef page parameter definition
     */
    public void setPageParameterDef(PageParameterDef paramDef) {
        this.paramDefs.put(paramDef.getName(), paramDef);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.writer != null) {
            throw new IllegalStateException();
        }
        if (this.outputStream == null) {
            this.outputStream = new PrepareResponseServletOutputStream();
        }
        return this.outputStream;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        if (this.outputStream != null) {
            throw new IllegalStateException();
        }
        if (this.writer == null) {
            Writer temporaryWriter = new PrepareResponseWriter();
            this.writer = new PrintWriter(temporaryWriter);
        }
        return this.writer;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void flushBuffer() throws IOException {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCommitted() {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void resetBuffer() {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendError(int sc, String msg) throws IOException {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendError(int sc) throws IOException {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendRedirect(String location) throws IOException {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addCookie(Cookie cookie) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setDateHeader(String name, long date) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addDateHeader(String name, long date) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeader(String name, String value) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addHeader(String name, String value) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentType(String type) {
        this.contentType = type;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentLength(int len) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setIntHeader(String name, int value) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addIntHeader(String name, int value) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(int sc) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(int sc, String sm) {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocale(Locale loc) {
        this.locale = loc;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        return this.contentType;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getLocale() {
        return this.locale;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setBufferSize(int size) {
        this.bufferSize = size;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getBufferSize() {
        return this.bufferSize;
    }

}
