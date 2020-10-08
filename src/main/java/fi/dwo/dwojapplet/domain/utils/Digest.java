package fi.dwo.dwojapplet.domain.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * The java client-side implementation.
 */
public class Digest  {

  public static String digest(Object value) throws NoSuchAlgorithmException {
    
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    hash(value, md5);
    byte[] bytes = md5.digest();
    return javax.xml.bind.DatatypeConverter.printHexBinary(bytes).toLowerCase();
  }

  @SuppressWarnings("unchecked")
  private static void hash(Object value, MessageDigest md5) {
    if (value instanceof String)
       hash((String)value, md5);
    else if (value instanceof Number) {
      hash ((Number) value, md5);
    } else if (value instanceof Boolean) {
      hash ( ((Boolean) value).booleanValue() ? "T" : "F", md5);
    } else if ( value instanceof Collection) {
      hash( Integer.toString(((Collection<?>) value).size()), md5);
      for (Object v: (Collection<Object>)value) {
        hash (v, md5);
      }
    } else if ( value instanceof Map) {
      Map<String, Object> m = (Map<String, Object>) value;
      SortedSet<String> keys = new TreeSet<String>( m.keySet());
      for ( String k: keys) {
        hash(k, md5);
        hash(m.get(k), md5);
      }
    }
  }
  
  private static void hash(String str, MessageDigest md5) {
    byte bytes[] = str.getBytes(StandardCharsets.UTF_8);
    md5.update(bytes);
  }
  
  private static final DecimalFormat df = new DecimalFormat("000000000000E0");
  private static void hash(Number n, MessageDigest md5) {
    hash( df.format(n.doubleValue()), md5);
  }
  
}
