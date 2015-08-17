package org.osivia.portal.kernel.portal;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

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

}
