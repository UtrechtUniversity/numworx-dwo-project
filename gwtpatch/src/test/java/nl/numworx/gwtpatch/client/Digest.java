package nl.numworx.gwtpatch.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * The server-side implementation of the RPC service.
 */
public class Digest  {

  public StringBuilder sb = new StringBuilder();
 
  public String digest(JsonObject value) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      digest(value, md5);
      byte[] bytes = md5.digest();
      return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    } catch (NoSuchAlgorithmException e) {
    }
    return null;
  }
  
  private void digest(JsonObject value, MessageDigest md5) {
    SortedSet<String> keys = new TreeSet<>(value.keySet());
    for(String key: keys) {
      hash(key, md5);
      JsonValue v = value.get(key);
      digest(v, md5);
    }    
  }

  private void digest(JsonValue v, MessageDigest md5) {
    switch (v.getValueType()) {
      case ARRAY:
        JsonArray array = v.asJsonArray();
        int size = array.size();
        hash(Integer.toString(size), md5);
        for(int i = 0; i < size; i++) {
          digest(array.get(i), md5);
        }
        break;
      case FALSE:
        hash("F", md5);
        break;
      case NULL:
        break;
      case NUMBER:
        JsonNumber n = (JsonNumber) v;
        hash(n.doubleValue(), md5);
        break;
      case OBJECT:
        digest(v.asJsonObject(), md5);
        break;
      case STRING:
        JsonString s = (JsonString) v;
        hash(s.getString(), md5);
        break;
      case TRUE:
        hash("T", md5);
        break;
      default:
        break;
      
    }
    
  }

  public String digest(Object value) throws NoSuchAlgorithmException {
    
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    hash(value, md5);
    byte[] bytes = md5.digest();
    return DatatypeConverter.printHexBinary(bytes).toLowerCase();
  }

  @SuppressWarnings("unchecked")
  private void hash(Object value, MessageDigest md5) {
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
  
  private void hash(String str, MessageDigest md5) {
	sb.append(str);
    byte bytes[] = str.getBytes(StandardCharsets.UTF_8);
    md5.update(bytes);
  }
  
  private DecimalFormat df = new DecimalFormat("000000000000E0");
  private void hash(Number n, MessageDigest md5) {
    hash( df.format(n.doubleValue()), md5);
  }
  
}
