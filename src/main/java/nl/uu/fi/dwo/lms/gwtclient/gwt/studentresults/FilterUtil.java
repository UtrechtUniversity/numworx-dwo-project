package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Map;
import java.util.Set;

public class FilterUtil {

	private FilterUtil() {}

	public static String setFilter(Map<String, Map<String, Set<Integer>>> map) {
		if (map.size() == 1) {
			String methode = map.keySet().iterator().next();
			if (methode.isEmpty()) return FilterTitle.ALLE_LEERDOELEN;
			Map<String, Set<Integer>> m = map.get(methode);
			if (m.size() == 1) {
				String boek = m.keySet().iterator().next();
				Set<Integer> hfstk = m.get(boek);
				return (methode + " > " + boek + " >" + h(hfstk));
			} else {
				StringBuilder sb = new StringBuilder(methode).append(" > ");
				m.keySet().stream().sorted().forEach(k -> {
					Set<Integer> hfstk = m.get(k);
					sb.append(k).append("-h");
					hfstk.stream().sorted().forEach(i -> sb.append(i).append(',') );
					sb.deleteCharAt(sb.length()-1).append(" ; ");
				});
				return (sb.substring(0, sb.length()-3));
			}
		}
		return FilterTitle.ALLE_LEERDOELEN;
	}

	public static String h(Set<Integer> hfstk ) {
		StringBuilder sb = new StringBuilder(" ");
		hfstk.stream().sorted().forEach(i -> sb.append('h').append(i).append(','));
		return sb.deleteCharAt(sb.length()-1).toString();
	}

	
}
