package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.uu.fi.dwo.rest.dom.entities.DomMethod;

public class FilterUtil {

	private FilterUtil() {}

	public static String setFilter(Map<String, Map<String, Set<Integer>>> map, DomMethod method) {
		if (map.size() == 1) {
			String methode = map.keySet().iterator().next();
			if (methode.isEmpty()) return FilterTitle.ALLE_LEERDOELEN;
			Map<String, Set<Integer>> m = map.get(methode); // KEY
			final boolean keyOkay = methode.equals(method.key());
			if (keyOkay) methode = method.getMethod(); // TITLE
			if (m.size() == 1) {
				String boek = m.keySet().iterator().next();
				List<String> chapters = null;
				for(int i = 0; i < method.books.size(); i++) {
					if (keyOkay && boek.equals(method.books.get(i))) chapters = method.chapters.get(i); // CHAPTER
				}
				Set<Integer> hfstk = m.get(boek);
				return (methode + " > " + boek + " >" + h(hfstk, chapters));
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

	public static String h(Set<Integer> hfstk, List<String> chapters) {
		StringBuilder sb = new StringBuilder(" ");
		if (chapters != null && hfstk.size() == 1) {
			int index = hfstk.stream().findAny().get().intValue()-1;
			if (index < chapters.size()) 
				return sb.append(chapters.get(index)).toString();
		}
		hfstk.stream().sorted().forEach(i -> sb.append('h').append(i).append(','));
		return sb.deleteCharAt(sb.length()-1).toString();
	}

	
}
