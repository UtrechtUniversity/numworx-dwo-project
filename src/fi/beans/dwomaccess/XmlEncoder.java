package fi.beans.dwomaccess;

import java.beans.XMLEncoder;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import fi.beans.base64code.StringCodeObject;
import fi.beans.dwomaccess.ByteArray;


/**
 * Utility class voor XML encoding van een Launchdata hashtable.
 * @author velth101
 * @see java.beans.XMLEncoder
 */

public class XmlEncoder {
	
	private XmlEncoder() {}
	
	/** 
	 * Encode a LaunchData Hashtable 
	 * @param map
	 * @param out
	 */
	public static void encode(Hashtable map, OutputStream out) {
		transform(map);
		XMLEncoder encoder = new XMLEncoder(out);
		ByteArray.installDelegate(encoder);
		encoder.writeObject(map);
		encoder.close();
	}

	private static void transform(Map map) {
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
