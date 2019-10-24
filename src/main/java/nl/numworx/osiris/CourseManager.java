package nl.numworx.osiris;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVRecord;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.PublicProfileManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserCourseManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecuredTeacherCourseManager;
import nl.uu.fi.dwo.rest.dom.entities.DomACL;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class CourseManager {
	
	enum Col {
		FACULTEIT,
		COLLEGEJAAR,
		CURSUS,
		AANVANGSBLOK,
		KORTE_NAAM_NL,
		TOETS,
		VOLTIJD_DEELTIJD,
		BLOK,
		GELEGENHEID,
		OMSCHRIJVING
	}

	SecuredTeacherCourseManager updater;
	SecureUserCourseManager getter;
	
	PublicProfileManager profiles;
	
	DomDwoProfile profile;
	
	Map<PersistenceId, List<DomCourseStudent>> children;
	DomSchool school;
	DomACL schoolright;
	private Map<String, DomSchoolClassFull> groepen;
	
	public CourseManager(String name, DomSchool school, Map<String, DomSchoolClassFull> groepen) throws Dwo2Exception {
		
		profile = PublicProfileManager.get(name);
		getter = new SecureUserCourseManager();
		updater = new SecuredTeacherCourseManager();
		children = new HashMap<>();
		this.groepen = groepen;
		this.school = school;
		schoolright = new DomACL();
		schoolright.setAccess(ACL.READ);
		schoolright.setEntity(school.getId());
	}
	
	Collection<String> names(Collection<? extends DomCourse> list) {
		return list.stream().map(DomCourse::getName).collect(Collectors.toSet());
	}
	
	void createToets(CSVRecord record) throws Dwo2Exception {
		String faculteit =  record.get(Col.FACULTEIT);		
		List<DomCourseStudent> courses;
		
		courses = children.get(null);
		if (courses == null) {
			courses = SecureUserCourseManager.getCoursesSchool(profile);
			children.put(null, courses);
		}
		DomCourse root = optionalCreateMap(faculteit, courses, null, "Faculteit " + faculteit);
		
		courses = getChildren(root);
		String year = record.get(Col.COLLEGEJAAR);
		root = optionalCreateMap(year, courses, root, "Jaar " + year);
		
		String course = record.get(Col.CURSUS) + " - " + record.get(Col.AANVANGSBLOK) + " - " + record.get(Col.KORTE_NAAM_NL);
		courses = getChildren(root);
		root = optionalCreateMap(course, courses, root, record.get(Col.KORTE_NAAM_NL));
		
		String toets = record.get(Col.TOETS) 
				+ " - " + record.get(Col.VOLTIJD_DEELTIJD)
				+ " - " + record.get(Col.BLOK) 
				+ " - " + record.get(Col.GELEGENHEID)
 				+ " - " + record.get(Col.OMSCHRIJVING);
		toets = trunk40(toets);
		courses = getChildren(root);
		if ( ! names(courses).contains(toets) ) {
			DomCourseStudent c = createMap(root, toets, Boolean.FALSE, record.get(Col.OMSCHRIJVING));
			DomCourseFull d = new DomCourseFull();
			d.setId(c.getId());
			d.setNotVisible(c.isNotVisible());
			DomACL acl = new DomACL();
			acl.setAccess(ACL.WRITE);
			String groepNaam = trunk100(year + " - " + course);
			PersistenceId klas = school.getId();
			if (groepen.containsKey(groepNaam))    
			  klas =  groepen.get(groepNaam).getId();
			acl.setEntity(klas);
			d.setAcls(Collections.singletonList(acl));
			updater.update(d);
			courses.add(c);
		}
		
 	}

	
	private String trunk100(String string) {
		  string = string.trim();
	      return string.length()>100?string.substring(0,100):string;
	  }

	private List<DomCourseStudent> getChildren(DomCourse root) throws Dwo2Exception {
		List<DomCourseStudent> courses;
		courses = children.get(root.getId());
		if (courses == null) {
			courses = SecureUserCourseManager.getCourses(root, profile);
			children.put(root.getId(), courses);
		}
		return courses;
	}

	private DomCourse optionalCreateMap(String name, List<DomCourseStudent> children, DomCourse parent, String description)
			throws Dwo2Exception {
	    name = trunk40(name);
	    String sname =  name;
		DomCourse root;
		Collection<String> set = names(children);
		if (! set.contains(sname)) {
			DomCourseStudent s = createMap(parent, sname, Boolean.TRUE, description);
			children.add(s);
			root = s;
		} else {
			root = children.stream().filter(i -> sname.equals(i.getName())).findAny().get();
		}
		return root;
	}

  String trunk40(String name) {
    if (name.length() > 40) {
      name = name.substring(0,40);
    }
    return name;
  }

	
	private DomCourseStudent createMap(DomCourse parent, String name, Boolean map, String description) throws Dwo2Exception {
		DomCourseFull edit = new DomCourseFull();
		edit.setName(name);
		edit.setDescription(description == null ?"": description);
		edit.setDwoProfileId(profile.getId());
		edit.setParentID(parent == null? null : parent.getId());
		edit.setSchoolId(school.getId());
		edit.setWithChildren(map);
		edit.setExport(Boolean.FALSE);		
		edit = updater.add(edit);
		edit.setAcls(Collections.singletonList(schoolright));
		edit = updater.update(edit);
		return edit;
	}
		
	
	
}
