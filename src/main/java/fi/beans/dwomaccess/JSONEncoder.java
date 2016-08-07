package fi.beans.dwomaccess;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import fi.beans.private_base64code.StringCodeObject;

public class JSONEncoder {

    @SuppressWarnings("rawtypes")
    @Deprecated
    private static void encode(Map map, Writer out) throws IOException {
        map = transformMap(map, null);
        JSONObject.writeJSONString(map, out);
    }

    /**
     * @param value
     * @return
     */
    private static List<Object> toArrayList(Object value) {
        int length = Array.getLength(value);
        List<Object> list = new ArrayList<Object>(length);
        for (int i = 0; i < length; i++) {
            list.add(Array.get(value, i));
        }
        return list;
    }

    private static Object transformTypes(Object value, ClassLoader cl) {
        if (value instanceof byte[]) {
            value = ByteArray.newInstance((byte[]) value);
        }

        if (value instanceof Collection) {
            Collection<?> c = (Collection<?>) value;
            value = c.toArray();
        }

        if (value instanceof Map) {
            return transformMap((Map<?, ?>) value, cl);
        }

        if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            return transformArray(array, cl);
        }

        if (value == null || value instanceof Number || value instanceof Boolean || value instanceof String
                // equivalent of the above.
                || value instanceof JSONAware || value instanceof JSONStreamAware) {
            return value;
        }
// Primitive Array
        if (value.getClass().isArray()) {
            return toArrayList(value);
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
            Map<String, String> r = new HashMap<String, String>();
            r.put("@type", "dwomaccess:ByteArray");
            r.put("string", b.getString());
            return r;
        }
        if (value instanceof URL) {
            URL u = (URL) value;
            Map<String, String> r = new HashMap<String, String>();
            r.put("@type", "java:URL");
            r.put("@value", u.toExternalForm());
            return r;
        }
        if (value instanceof URI) {
            URI u = (URI) value;
            Map<String, String> r = new HashMap<String, String>();
            r.put("@type", "java:URI");
            r.put("@value", u.toString());
            return r;

        }
// leftovers
        return Collections.singletonMap("@type", "javaclass:" + value.getClass().getName());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map transformMap(Map map, ClassLoader cl) {
        Map result = map;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object value = entry.getValue();
            if (value instanceof String && value.toString().startsWith("H4sIA")) {
                value = StringCodeObject.decodeStringToObject(value.toString(),cl);
                if (value != null) {
                    if (result == map) {
                        result = new JSONObject(map);
                    }
                    result.put(entry.getKey(), value);
                }
            }

            Object transformed = transformTypes(value, cl);
            if (transformed != value) {
                if (result == map) {
                    result = new JSONObject(map);
                }
                result.put(entry.getKey(), transformed);
            }
        }
        return result;
    }

    private static List<Object> transformArray(Object[] array, ClassLoader cl) {
        Object[] result = array;
        for (int i = 0; i < array.length; i++) {
            Object value = array[i];
            value = transformTypes(value, cl);

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
        return Arrays.asList(result);
    }

	public static void encode(Map map, Writer out, ClassLoader cl) throws IOException {
        map = transformMap(map, cl);
        JSONObject.writeJSONString(map, out);
	}

}
