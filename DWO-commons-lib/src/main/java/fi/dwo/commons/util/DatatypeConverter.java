package fi.dwo.commons.util;


/**
 * replacement for javax.xml.bind.DatatypeConverter.
 * Missing in openjdk 11+. 
 * @author peterboon
 *
 */

public class DatatypeConverter {

  private DatatypeConverter() {}
  private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

  /**
   * <p>
   * Converts an array of bytes into a string.
   * @param val
   *     An array of bytes
   * @return
   *     A string containing a lexical representation of xsd:hexBinary
   * @throws IllegalArgumentException if <tt>val</tt> is null.
   */

  public static String printHexBinary(byte[] data) {
      StringBuilder r = new StringBuilder(data.length * 2);
      for (byte b : data) {
          r.append(hexCode[(b >> 4) & 0xF]);
          r.append(hexCode[(b & 0xF)]);
      }
      return r.toString();
  }

}
