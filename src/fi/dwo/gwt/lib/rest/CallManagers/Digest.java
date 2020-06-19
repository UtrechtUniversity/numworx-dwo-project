package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.TreeSet;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Digest  {

  public String digest(String value) {
    JSONValue v = JSONParser.parseLenient(value);
    return digest(v);
  }
  
  public String digest(JSONValue value) {
    StringBuilder sb = new StringBuilder();
    hash(value, sb);
    return MD5.md5(sb.toString());
  }

  NumberFormat nf = NumberFormat.getFormat("000000000000E0");
  void hash(JSONValue value, StringBuilder sb) {
    if (value.isString() != null) {
      sb.append(value.isString().stringValue());
    } else if (value.isBoolean() != null) {
      sb.append(value.isBoolean().booleanValue()?'T':'F');
    } else if (value.isNumber() != null) {
      sb.append(nf.format(value.isNumber().doubleValue()));     
    } else if (value.isArray() != null) {
      JSONArray a = value.isArray();
      int size = a.size();
      sb.append(size);
      for(int i = 0; i  < size; i++) {
        hash(a.get(i), sb);
      }
    } else if (value.isObject() != null) {
      JSONObject o = value.isObject();
      TreeSet<String> keys = new TreeSet<>(o.keySet());
      for (String k : keys) {
        sb.append(k);
        hash(o.get(k), sb);
      }
    }
    
  }
}
