package fi.beans.dwomaccess;

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

import fi.beans.base64code.StringCodeObject;

public class JSONEncoder {
	
	public static void encode(Map map, Writer out) throws IOException {
		map = mapWalker(map);
		JSONObject.writeJSONString(map, out);
	}

	private static Map mapWalker(Map map) {
		transform(map);
		transformTypes(map);
		return map;
	}

	static Object transformTypes(Object value) {
		if (value instanceof Collection ) {
			Collection c = (Collection) value;
			value = c.toArray();
		}
		if(value instanceof Map) {
			transformTypes((Map) value);
			return value;
		} 
		if(value instanceof Object[]) {
			Object[] array = (Object[]) value;
			Object[] out = new Object[array.length];
			for (int i = 0; i < array.length; i++) {
				out[i] = transformTypes(array[i]);
			}
			value = out;
		} 
		if(value == null || value instanceof Number || value instanceof Boolean || value instanceof String || value.getClass().isArray()
// equivalent of the above.
				|| value instanceof JSONAware || value instanceof JSONStreamAware
		)
		{
			return value;
		}
// De moeilijke gevallen:		
		if(value instanceof Color) {
			Color c = (Color) value;
			Map r = new HashMap();
			r.put("@type", "java:Color");
			r.put("red", c.getRed());
			r.put("green", c.getGreen());
			r.put("blue", c.getBlue());
			if(c.getAlpha() != 255) r.put("alpha", c.getAlpha());
			return r;
		} 
		if(value instanceof Font) {
			Font f = (Font)value;
			Map r = new HashMap();
			r.put("@type", "java:Font");
			r.put("family", f.getFamily());
			r.put("style", f.getStyle()); // Number/Name?
			r.put("size", f.getSize());
			return r;
		}
		if(value instanceof ByteArray ) {
			ByteArray b = (ByteArray) value;
			Map r = new HashMap();
			r.put("@type", "dwomaccess:ByteArray");
			r.put("string", b.getString());
			return r;
		}
		if(value instanceof URL) {
			URL u = (URL)value;
			Map r = new HashMap();
			r.put("@type", "java:URL");
			r.put("@value", u.toExternalForm());
			return r;
		} 
		if(value instanceof URI) {
			URI u = (URI)value;
			Map r = new HashMap();
			r.put("@type", "java:URI");
			r.put("@value", u.toString());
			return r;
			
		}
// leftovers
		return Collections.singletonMap("@type", "javaclass:" + value.getClass().getName());
	}
	
	
	static void transformTypes(Map map) {
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object value = entry.getValue();
			entry.setValue(transformTypes(value));
		}
		
	}
	public static void transform(Map map) {
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object value = entry.getValue();
			if(value instanceof String && value.toString().startsWith("H4sIA")) {
				value = StringCodeObject.decodeStringToObject(value.toString());
				if(value != null)
				{	
					entry.setValue(value);
				}
			}

			if(value instanceof Map) {
				transform((Map) value);
			}
			else if(value instanceof byte[]) {
				ByteArray ba = ByteArray.newInstance((byte[]) value);
				entry.setValue(ba);
			}
			

//			if(value instanceof Font) {
//				value = value.toString();
//				entry.setValue(value);
//			}
//			if(value instanceof java.awt.Color) {
//				value = value.toString();
//				entry.setValue(value);
//			}
// arraytypes TODO List.
			else if (value instanceof Object[]) {
				Object[] array = (Object[])value;
				entry.setValue(transform(array));
			} 
		}
		
	}

	private static Object transform(Object[] array) {
		for (int i = 0; i < array.length; i++) {
			Object value = array[i];
			if(value instanceof Map) 
				transform( (Map) value);
			if(value instanceof Object[]) 
				value = transform((Object[])value);
			array[i] = value;
		}
		return array;
	}


}
