package org.osivia.portal.kernel.common.portal;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 * Prepare response servlet output stream.
 *
 * @author Cédric Krommenhoek
 * @see ServletOutputStream
 */
public class PrepareResponseServletOutputStream extends ServletOutputStream {

    /**
     * Constructor.
     */
    public PrepareResponseServletOutputStream() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int arg0) throws IOException {
        // Do nothing
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReady() {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setWriteListener(WriteListener writeListener) {
        // Do nothing
    }

}
