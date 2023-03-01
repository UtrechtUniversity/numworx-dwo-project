/*
 * $Id: CharacterEncoder.java,v 1.2 1997/06/19 08:33:17 ak Exp $
 * 
 * Copyright 1997 Hewlett-Packard Company
 * 
 * This file may be copied, modified and distributed only in
 * accordance with the terms of the limited licence contained
 * in the accompanying file LICENSE.TXT.
 */
package hplb.misc;

import java.io.*;

/**
 * Common superclass for a variety of encoders.
 *
 * @author Anders Kristensen
 */
public abstract class CharacterEncoder {

    /**
     * Encode the InputStream and write the encoded form to the OutputStream.
     *
     * @param out
     * @throws IOException if an I/O error has occurred.
     */
    public abstract void encodeBuffer(InputStream in,
            OutputStream out) throws IOException;

    /**
     * Encode the contents of the buffer and write the encoded form to the
     * OutputStream.
     *
     * @param out
     * @throws IOException if an I/O error has occurred.
     */
    public void encodeBuffer(byte[] buf, OutputStream out)
            throws IOException {
        ByteArrayInputStream bin = new ByteArrayInputStream(buf);
        encodeBuffer(bin, out);
    }

    /**
     * Encode the contents of the buffer and return it as a String.
     *
     * @return
     */
    public final String encodeBuffer(byte[] buf) {
        // The encoded form is approximately 33 % bigger than the unencoded form
        ByteArrayInputStream bin = new ByteArrayInputStream(buf);
        ByteArrayOutputStream bout = new ByteArrayOutputStream((int) (buf.length * 1.4));
        try {
            encodeBuffer(bin, bout);
        }
        catch (IOException e) {
        }
        return new String(bout.toByteArray(), 0);
    }
}
