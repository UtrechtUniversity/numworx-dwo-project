/*
 * $Id: CharacterDecoder.java,v 1.2 1997/06/19 08:33:16 ak Exp $
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
 * Common superclass for a variety of decoders.
 * @author      Anders Kristensen
 */
public abstract class CharacterDecoder {
  /**
   * Decodes the specified InputStream and writes result onto
   * the OutputStream.
   * @param in                  encoded input
   * @param out                 decoded output
   * @throws IOException        if an I/O error has occurred.
   */
  public abstract void decodeBuffer(InputStream in,
                                    OutputStream out) throws IOException;

  /** Decode the specified String and return result as byte array. */
  public byte[] decodeBuffer(String s) {
    byte[] buf = ByteArray.getBytes(s);
    ByteArrayInputStream bin = new ByteArrayInputStream(buf);
    ByteArrayOutputStream bout = new ByteArrayOutputStream(buf.length);
    try { decodeBuffer(bin, bout); } catch (IOException e) {}
    return bout.toByteArray();
  }

  /**
   * Decode the InputStream and return result as byte array.
   * @throws IOException        if an I/O error has occurred.
   */
  public byte[] decodeBuffer(InputStream in) throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
    decodeBuffer(in, bout);
    return bout.toByteArray();
  }
}
