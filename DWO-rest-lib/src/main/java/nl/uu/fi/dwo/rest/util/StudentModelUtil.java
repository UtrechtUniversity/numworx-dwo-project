package nl.uu.fi.dwo.rest.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelVariant;

public class StudentModelUtil {
	public static String MASTERY = "⌈MASTERY⌉"; // reserved variant: score without ceiling

	Map<String, DomStudentModelObj> items;
	Map<String, Collection<String>> foreknowledge;
	Map<String, DomStudentModelObjectiveScore> scores;
	DomStudentModelStructureScore structure;
		
	public StudentModelUtil() {
		items = new HashMap<>();
		foreknowledge = new HashMap<>();
		scores = new HashMap<>();
	}
	
	public void setStudentModelStructure(DomStudentModelStructure structure) {
		items.clear();
		foreknowledge.clear();
		for( DomStudentModelCategory cat: structure.getCategories()) {
			for (DomStudentModelObj obj: cat.getObjectives()) {
				addObjective(obj);
			}			
		}		
		items.values().forEach(this::addForeKnowledge);
// closure
		boolean changed;
		do {
			changed = false;
			for (Collection<String> set: foreknowledge.values()) {
				Collection<String> add;
				add = set.stream().flatMap(this::streamofknowledge).collect(Collectors.toSet());
				if ( !add.isEmpty() && set.addAll(add) )
					changed = true;
			}
		} while(changed);
	}

	private Stream<String> streamofknowledge(String id) {
		return foreknowledge.getOrDefault(id, Collections.emptySet()).stream();
	}

	private void addObjective(DomStudentModelObj obj) {
		List<DomStudentModelObj> list = obj.getObjectives();
		if (list != null) 
			list.forEach(this::addObjective);
		else {
			String id = obj.getInfo().getId();
			if (id != null) items.put(id, obj);
		}
	}

	private void addForeKnowledge(DomStudentModelObj obj) {
		DomStudentModelContextInfo info = obj.getInfo();
		String id = info.getId();
		Collection<String> items = info.getVoorkennis();
		if (items != null)
			items = items.stream()
			.map(s -> s.split("/")[0])
			.filter(this.items::containsKey).collect(Collectors.toSet());
		Collection<String> set = foreknowledge.computeIfAbsent(id, key -> new HashSet<String>());
		if (items != null) set.addAll(items);
	}
	
	public void setStudentModelScore(DomStudentModelStructureScore score) {
		scores.clear();
		structure = score;
		for( DomStudentModelCategoryScore cat: score.getCategories()) {
			for (DomStudentModelObjectiveScore s: cat.getObjectives()) {
				addObjectiveScore(s);
			}
		}
		
	}

	private void addObjectiveScore(DomStudentModelObjectiveScore s) {
		List<DomStudentModelObjectiveScore> children = s.getChildren();
		if (children != null)
			children.forEach(this::addObjectiveScore);
		else {
			scores.put(s.getId(), s);
		}
	}
	
	public DomStudentModelStructureScore calculate() {
		DomStudentModelStructureScore result = new DomStudentModelStructureScore();
		result.setId(structure.getId());
		result.setCategories(structure.getCategories().stream().map(this::copy).collect(Collectors.toList()));
		result.recalculateAncestors();
		return result;
	}
	
	private DomStudentModelCategoryScore copy (DomStudentModelCategoryScore org) {
		DomStudentModelCategoryScore result = new DomStudentModelCategoryScore();
		result.setId(org.getId());
		result.setObjectives(org.getObjectives().stream().map(this::calculate).collect(Collectors.toList()));		
		return result;
	}
	
	public static String strip(String id) {
		return id.split("/",2)[0];
	}

	public static Set<String> strip(Collection<String> set) {
		return set.stream().map(StudentModelUtil::strip).collect(Collectors.toSet()); 
	}
	
	
	DomStudentModelObjectiveScore calculate(DomStudentModelObjectiveScore org) {
		DomStudentModelObjectiveScore result = new DomStudentModelObjectiveScore();
		String id = org.getId();
		result.setId(id);
		List<DomStudentModelObjectiveScore> list = org.getChildren();
		if (list != null) {
			result.setChildren(list.stream().map(this::calculate).collect(Collectors.toList()));
		} else {
			result.setChildren(null);
		}
		
		long gc = org.getGreenCount();
		double gs = org.getGreenScore();
		long rc = org.getRedCount();
		double rs = org.getRedScore();
		long t = org.getTotalCount();
		if (gc > 0L) {
			Collection<String> objectives = foreknowledge.getOrDefault(id, Collections.emptyList());
			int size = objectives.size();
			long count;
			if (size != 0) {
				Stream<String> filter = objectives.stream().filter(this::isCorrect);
				List<String> filtered = filter.collect(Collectors.toList());
				count = filtered.size();
				DomStudentModelObj obj = items.get(id);
				List<DomStudentModelVariant> varlist = obj.getInfo().getVariants();
				if (varlist	!= null) {
					for(DomStudentModelVariant var: varlist) {
						if (var.getName() != null) {
							Set<String> desel = strip(var.getDeselections());
							if (desel != null  && !desel.isEmpty()) {
								Predicate<String> f = key -> !desel.contains(key);								
								long s = objectives.stream().filter(f).count();
								long c = filtered.stream().filter(f).count();
								if (s == 0L) { s = 1L; c = 1L; }
								result.getVariants().put(var.getName(), 0.5 + (gs-0.5) * c / s);
							}
						}
					}
				}
				result.getVariants().put(MASTERY, gs);
				gs = 0.5 + (gs-0.5) * count / size;
			} else {
				result.getVariants().put(MASTERY, gs);				
			}
		}
		result.setScore(gs, gc, rs, rc, t);
		return result;		
	}
	
	boolean isCorrect(String id) {
		return scores.get(id).getScore() > 0.75;
	}
	
//    private static List<String> strip(Collection<String> list) {
//    	return list.stream().map(s -> s.split("/")[0]).collect(Collectors.toList());
//    }

}
