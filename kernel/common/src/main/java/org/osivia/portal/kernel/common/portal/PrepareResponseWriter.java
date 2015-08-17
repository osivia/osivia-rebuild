package org.osivia.portal.kernel.common.portal;

import java.io.IOException;
import java.io.Writer;

/**
 * Prepare response writer.
 *
 * @author Cédric Krommenhoek
 * @see Writer
 */
public class PrepareResponseWriter extends Writer {

    /**
     * Constructor.
     */
    public PrepareResponseWriter() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void write(char[] arg0, int arg1, int arg2) throws IOException {
        // Do nothing
    }

}
