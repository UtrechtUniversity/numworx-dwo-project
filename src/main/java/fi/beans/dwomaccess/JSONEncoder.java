package fi.beans.dwomaccess;

import fi.beans.base64code.StringCodeObject;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

public class JSONEncoder {

    public static void encode(Map map, Writer out) throws IOException {
        map = transformMap(map);
        JSONObject.writeJSONString(map, out);
    }

    private static Object transformTypes(Object value) {

        if (value instanceof Collection) {
            Collection c = (Collection) value;
            value = c.toArray();
        }

        if (value instanceof Map) {
            return transformMap((Map) value);
        }

        if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            return transformArray(array);
        }

        if (value instanceof byte[]) {
            value = ByteArray.newInstance((byte[]) value);
        }

        if (value == null || value instanceof Number || value instanceof Boolean || value instanceof String || value.getClass().isArray()
                // equivalent of the above.
                || value instanceof JSONAware || value instanceof JSONStreamAware) {
            return value;
        }
// De moeilijke gevallen:		
        if (value instanceof Color) {
            Color c = (Color) value;
            Map r = new HashMap();
            r.put("@type", "java:Color");
            r.put("red", c.getRed());
            r.put("green", c.getGreen());
            r.put("blue", c.getBlue());
            if (c.getAlpha() != 255) {
                r.put("alpha", c.getAlpha());
            }
            return r;
        }
        if (value instanceof Font) {
            Font f = (Font) value;
            Map r = new HashMap();
            r.put("@type", "java:Font");
            r.put("family", f.getFamily());
            r.put("style", f.getStyle()); // Number/Name?
            r.put("size", f.getSize());
            return r;
        }
        if (value instanceof ByteArray) {
            ByteArray b = (ByteArray) value;
            Map r = new HashMap();
            r.put("@type", "dwomaccess:ByteArray");
            r.put("string", b.getString());
            return r;
        }
        if (value instanceof URL) {
            URL u = (URL) value;
            Map r = new HashMap();
            r.put("@type", "java:URL");
            r.put("@value", u.toExternalForm());
            return r;
        }
        if (value instanceof URI) {
            URI u = (URI) value;
            Map r = new HashMap();
            r.put("@type", "java:URI");
            r.put("@value", u.toString());
            return r;

        }
// leftovers
        return Collections.singletonMap("@type", "javaclass:" + value.getClass().getName());
    }

    private static Map transformMap(Map map) {
        Map result = map;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object value = entry.getValue();
            if (value instanceof String && value.toString().startsWith("H4sIA")) {
                value = StringCodeObject.decodeStringToObject(value.toString());
                if (value != null) {
                    if (result == map) {
                        result = new JSONObject(map);
                    }
                    result.put(entry.getKey(), value);
                }
            }

            if (value instanceof Map) {
                Map transformed = transformMap((Map) value);
                if (transformed != value) {
                    if (result == map) {
                        result = new JSONObject(map);
                    }
                    result.put(entry.getKey(), transformed);
                }
            } else if (value instanceof byte[]) {
                ByteArray transformed = ByteArray.newInstance((byte[]) value);
                if (result == map) {
                    result = new JSONObject(map);
                }
                result.put(entry.getKey(), transformTypes(transformed));
            } // arraytypes TODO List.
            else if (value instanceof Object[]) {
                Object[] array = (Object[]) value;
                Object transformed = transformArray(array);
                if (transformed != value) {
                    if (result == map) {
                        result = new JSONObject(map);
                    }
                    result.put(entry.getKey(), transformed);
                }
            } else {
                Object transformed = transformTypes(value);
                if (transformed != value) {
                    if (result == map) {
                        result = new JSONObject(map);
                    }
                    result.put(entry.getKey(), transformed);
                }

            }
        }
        return result;
    }

    private static Object[] transformArray(Object[] array) {
        Object[] result = array;
        for (int i = 0; i < array.length; i++) {
            Object value = array[i];
            if (value instanceof Map) {
                value = transformMap((Map) value);
            } else if (value instanceof Object[]) {
                value = transformArray((Object[]) value);
            } else {
                value = transformTypes(value);
            }

            if (value != array[i]) {
                if (result == array) {
                    result = new Object[array.length];
                    if (i > 0) {
                        System.arraycopy(array, 0, result, 0, i);
                    }
                }
            }
            result[i] = value;
        }
        return result;
    }

}
