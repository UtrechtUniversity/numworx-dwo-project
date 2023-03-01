package fi.beans.dwomaccess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class Compressor implements Predicate<String> {
	
	private static final char SUFFIX = '\f';
  private static final String IMAGES = "$IMAGE$MAP$";
	private Map<String,Object> images;
	private Set<String> keys;
	private static boolean skip = true;
	
	private static Logger LOG = Logger.getLogger(Compressor.class.getName());
	
	
  @SuppressWarnings("unchecked")
  Map<String, Object> compress(Map<String, Object> map) {
    if (skip) return map;
    removeTemplates(map.get("instellingen"));
    keys = new TreeSet<>();
    images = Collections.EMPTY_MAP;
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (IMAGES.equals(entry.getKey())) {
        images = (Map<String, Object>) entry.getValue();
      } else if (entry.getKey().startsWith("opdracht_")) {
        tag(entry.getValue());
      }
    }
    retainImages();
    return map;
  }

  private static final Collection<String> templateSet =
      Arrays.asList("TekstVakPanelTemplatePages", "TekstVakPanelTemplateComponents",
          "TekstVakPanelTemplatePagesKeys", "TekstVakPanelTemplateComponentsKeys");

  private void removeTemplates(Object object) {
    if (object instanceof Map) {
      Map<?, ?> map = (Map<?, ?>) object;
      map.keySet().removeAll(templateSet);
    }

  }

  private void retainImages() {
		Set<String> set = images.keySet();
		int size = set.size();
		set.removeIf(this);
		LOG.info("compression from "+size+" to "+ set.size() + " items");
	}

	@SuppressWarnings("rawtypes")
	private void tag(Object value) {
		if(value instanceof String) {
			tagString( (String) value);
		} else
		if(value instanceof Object[]) {
			Object[] array = (Object[]) value;
			for(Object elem: array) tag(elem);
		} else
		if (value instanceof Collection) {
			Collection collection = (Collection) value;
			for(Object elem: collection) tag(elem);
		} else
		if (value instanceof Map) {
			Map map = (Map) value;
			tagMap(map);
		}
		
	}

	@SuppressWarnings("rawtypes")
	private void tagMap(Map map) {
		if( !recognizeMap(map)) {
			for(Object elem: map.values()) tag(elem);
		}
	}

	private boolean recognizeMap(Map map) {
		if(map.containsKey("@type")) return true; // recognize @type=java:XXXX maps

		tagValue("knopImageString", map);
		tagValue("knopImageString1", map);
		tagValue("knopImageString2", map);
		tagValue("popupImageString", map);
		
		// TODO Auto-generated method stub
		return false;
	}

	private boolean tagValue(String key, Map map) {
		Object value = map.get(key);
		if(value != null && !"".equals(value)) {
			key = value.toString();
            keys.add(key);
            int k = key.indexOf(SUFFIX);
            if (k > 0) 
              keys.add(key.substring(0,k));         
			return true;
		}
		return false;
	}

	private void tagString(String value) {
		int i = value.indexOf("$I");
		while(i >= 0) {
			int j = value.indexOf('@', i);
			if(j < 0) j = i;
			String key = value.substring(i+2,j);
			keys.add(key);
			int k = key.indexOf(SUFFIX);
			if (k > 0) 
			  keys.add(key.substring(0,k));			
			i = value.indexOf("$I", j+1);
		}
	}

	@Override
	public boolean test(String t) {
		int slash = t.indexOf('/'); 
		if( slash >= 0 ) t = t.substring(0,slash);
		return ! keys.contains(t);
	}

	/**
	 * @param skip the skip to set
	 */
	public static void setSkip(boolean skip) {
		LOG.info("compression json set skip to " + skip);
		Compressor.skip = skip;
	}

	/**
	 * @return the skip
	 */
	public static boolean isSkip() {
		return skip;
	}
}
