package nl.uu.fi.dwo.rest.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategoryScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObjectiveScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;

public class StudentModelUtil {


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
			items = items.stream().filter(this.items::containsKey).collect(Collectors.toSet());
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
	private DomStudentModelObjectiveScore calculate(DomStudentModelObjectiveScore org) {
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
				count = objectives.stream().filter(this::isCorrect).count();
				gs = 0.5 + (gs-0.5) * count / size;
			}
		}
		result.setScore(gs, gc, rs, rc, t);
		return result;		
	}
	
	boolean isCorrect(String id) {
		return scores.get(id).getScore() > 0.75;
	}
}
