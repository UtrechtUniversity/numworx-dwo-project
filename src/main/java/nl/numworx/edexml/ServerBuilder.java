package nl.numworx.edexml;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminSchoolManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountLoginsManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class ServerBuilder implements Builder {

    private static final Logger LOG = Logger.getLogger(ServerBuilder.class.getName());
	SecureSchoolAdminSchoolClassManager schoolClassManager;
	
	Map<String, DomUserFull> students = new TreeMap<>();
	Map<String, DomUserFull> teachers = new TreeMap<>();
	Map<String, DomSchoolClassFull> classes = new TreeMap<>();
	Map<String, Collection<String>> memberships = new TreeMap<>();
	private DomSchoolsRolesAndClassesV2 logins;
	
	public void setSource(String username, String password, URL base) throws Dwo2Exception {
		schoolClassManager = new SecureSchoolAdminSchoolClassManager();
		StoredRestManager.getInstance().setBasicAuthString(username, password, null);
		StoredRestManager.getInstance().getAuthenticator().setServerUrlPath(base);
		logins = SecureUserAccountLoginsManager.getSchoolLogins();
		DomContext context = new DomContext();
		DomRole role = logins.getActiveSchoolRoleAndClass().getRole();
		if (role.getRoleName().equals(RoleType.SCHOOLADMIN.name())) {
			context.setDomHasRole(logins.getActiveSchoolRoleAndClass().getHasRole());
			StoredRestManager.getInstance().getAuthenticator().setContext(context);
		} else 
			throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthorizationError, "Wrong role");
	}

	public void setSource(String realm, StoredRestManager instance) throws Dwo2Exception {
		schoolClassManager = new SecureSchoolAdminSchoolClassManager(instance);
		logins = SecureUserAccountLoginsManager.getSchoolLogins();
		DomContext context = new DomContext();
		context.setRealm(realm);
		DomRole role = logins.getActiveSchoolRoleAndClass().getRole();
		if (role.getRoleName().equals(RoleType.SCHOOLADMIN.name())) {
			context.setDomHasRole(logins.getActiveSchoolRoleAndClass().getHasRole());
			instance.getAuthenticator().setContext(context);
		} else 
			throw new Dwo2Exception(Dwo2ExceptionCode.User_AuthorizationError, "Wrong role");
	}

	public void setRealm(String realm) {
		StoredRestManager.getInstance().getAuthenticator().getContext().setRealm(realm);
	}
	
	public DomSchool getSchool() {
		return logins.getActiveSchoolRoleAndClass().getSchool();
	}

	@Override
	public Map<String, DomUserFull> parseLeerlingen() {
		try {
			List<DomStudent> list = schoolClassManager.getStudentsInSchool();
			putUsers(list, students);
		} catch (Dwo2Exception e) {
			e.printStackTrace();
		}
		return students;
	}


	private void putUsers(List<? extends DomUser> list, Map<String, DomUserFull> map) {
		for(DomUser student: list) {
			String id = student.getId().getIdString();
			if (map.containsKey(id)) continue;
			DomUserFull user = new DomUserFull();
			user.setFamilyName(student.getFamilyName());
			user.setGivenName(student.getGivenName());
			user.setInsertion(student.getInsertion());
			user.setId(student.getId());
			user.setSingleSchool(student.getSingleSchool());
			user.setUserName(student.getUserName());
			map.put(id, user);
		}
	}

	@Override
	public Map<String, DomSchoolClassFull> parseGroepen() {
		try {
			List<DomSchoolClass> list = schoolClassManager.getSchoolClasses();
			for (DomSchoolClass item: list) {
				String id = item.getId().getIdString();
				if (classes.containsKey(id)) continue;
				DomSchoolClassFull klas = new DomSchoolClassFull();
				klas.setId(item.getId());
				klas.setSchoolClassName(item.getSchoolClassName());
				klas.setIconizer(item.getIconizer());
				klas.setHasRegKey(item.getHasRegKey());
				classes.put(id, klas);
			}
		} catch (Dwo2Exception e) {
			LOG.log(Level.WARNING, "parseGroepen", e);
		}
		return classes;
	}

	private void addMember(String student, String klas) {
		Collection<String> member = memberships.get(student);
		if (member == null) {
			member = new HashSet<>();
			memberships.put(student, member);
		}
		member.add(klas);
	}

	@Override
	public Map<String, Collection<String>> memberships() {		
		try {
			List<DomSchoolClass> list = schoolClassManager.getSchoolClasses();
			for (DomSchoolClass item: list) {
				List<DomStudent> s = schoolClassManager.getStudentsInSchoolClass(item);
				s.forEach(i -> addMember(i.getId().getIdString(), item.getId().getIdString()));
				List<DomTeacher> t  = schoolClassManager.getTeachersInSchoolClass(item);
				t.forEach(i -> addMember(i.getId().getIdString(), item.getId().getIdString()));
			}
		} catch (Dwo2Exception e) {
			LOG.log(Level.WARNING, "memberships", e);
		}
		return memberships;
	}

	@Override
	public Map<String, DomUserFull> parseLeerkrachten() {
		try {
			List<DomTeacher> list = schoolClassManager.getTeachersInSchool();
			putUsers(list, teachers);
		} catch (Dwo2Exception e) {
			LOG.log(Level.WARNING, "parseLeerkrachten", e);
		}
		return teachers;
	}

	// Add schoolclass (if not exists)
	public void addSchoolClasses(Map<String, DomSchoolClassFull> set) {
	  Set<String> names = parseGroepen().values().stream().map(DomSchoolClassFull::getSchoolClassName).collect(Collectors.toSet());
	  for(Map.Entry<String, DomSchoolClassFull> item:set.entrySet()) {
	    String key = item.getKey();
	    DomSchoolClassFull schoolClass = item.getValue();
	    if (names.contains(schoolClass.getSchoolClassName())) {
	      // existing class
	    } else {
	      try {
	    	  schoolClassManager.submitSchoolClass(schoolClass);
        } catch (Dwo2Exception e) {
          LOG.log(Level.WARNING, "addschoolclasses", e);
        }
	    }
	  }
	  Map<String,DomSchoolClassFull> byName = byName(parseGroepen());
	  for (Map.Entry<String, DomSchoolClassFull> item: set.entrySet()) {
	    DomSchoolClassFull value = item.getValue();
	    item.setValue(byName.getOrDefault(value.getSchoolClassName(), value));
	  }
	  
	} 
	private Map<String, DomSchoolClassFull> byName(Map<String, DomSchoolClassFull> groepen) {
    Map<String, DomSchoolClassFull> result = new TreeMap<>();
    groepen.values().forEach(i -> result.put(i.getSchoolClassName(), i));
    return result;
  }


  // Single school students
  public void addStudents (Map<String, DomUserFull> users, Map<String,Collection<String>>members, Map<String, DomSchoolClassFull> classes) {  
	DomContext context = RestAuthenticator.getInstance().getContext();   	  // XXX beter SecureSchoolAdminSchoolClassManager.getContext();
	String realm = context.getRealm();
	for (Map.Entry<String, DomUserFull> item: users.entrySet()) {
	  
	  String key = item.getKey();
	  DomSingleSchoolStudent domSingleSchoolStudent = new DomSingleSchoolStudent(item.getValue());
	  if (domSingleSchoolStudent.getUserName() == null || domSingleSchoolStudent.getUserName().isEmpty()) {
	    domSingleSchoolStudent.setUserName(key); // FIXME why
	    item.getValue().setUserName(key);
	  }
	  if (domSingleSchoolStudent.getEmail() == null || domSingleSchoolStudent.getEmail().isEmpty())
	    domSingleSchoolStudent.setEmail("noreply@numworx.nl");
	  
	  
	  Collection<String> collection = members.getOrDefault(key,Collections.emptySet());
      Iterator<String> iterator = collection.iterator();
      if (!iterator.hasNext()) continue; // or iterator is backup class
      try {
      DomSchoolClass domSchoolClass = classes.get(iterator.next());
	  DomNewSingleSchoolStudent submit = new DomNewSingleSchoolStudent();
	  submit.setDomSchoolClass(domSchoolClass);
	  submit.setDomSingleSchoolStudent(domSingleSchoolStudent);
	  String userName = submit.getDomSingleSchoolStudent().getUserName();
      try {
		if (userName.contains("@")) {
    		  int i = userName.lastIndexOf('@');
    		  submit.getDomSingleSchoolStudent().setUserName(userName.substring(0,i));
    		  context.setRealm(userName.substring(i));
    	  } else {
    		  context.setRealm(realm);
    	  }
    	  
		  schoolClassManager.submitSingleSchoolStudent(submit);
    	  submit.getDomSingleSchoolStudent().setUserName(userName); // restore.
      }
      catch (Dwo2Exception e) {
    	  submit.getDomSingleSchoolStudent().setUserName(userName); // restore.
          context.setRealm(realm); // realm of login
        Dwo2ExceptionCode code = e.getDwo2Code();
        //code = Dwo2ExceptionCode.Rest_Registration_UserName_exists;
        if (code == Dwo2ExceptionCode.Rest_Registration_UserName_exists) {
          iterator = collection.iterator();
          List<DomStudent> allStudents = schoolClassManager.getStudentsInSchool();
          allStudents.forEach(i -> { 
            if (i.getUserName().equals(item.getValue().getUserName()))
                item.getValue().setId(i.getId());
          });
         } else {
           LOG.log(Level.WARNING, "submit student", e);
          continue;
         }
      }
      context.setRealm(realm); // realm of login
      // find persistenceid of student by name.
      List<DomStudent> list = schoolClassManager.getStudentsInSchoolClass(domSchoolClass);
      list.forEach(i -> { 
          if (i.getUserName().equals(item.getValue().getUserName()))
              item.getValue().setId(i.getId());
        });
      
      while (iterator.hasNext()) {
        DomSubmitStudentToSchoolClass s = new DomSubmitStudentToSchoolClass();
        DomSchoolClass schoolClassTo = classes.get(iterator.next());
        s.setSchoolClassTo(schoolClassTo);
        s.setStudent(new DomStudent(item.getValue()));
        schoolClassManager.submitStudentToSchoolClass(s);
      }
      } catch (Dwo2Exception e) {
        LOG.log(Level.WARNING, "submit",e);
        continue;
      }      
	}
  }
  
  public void addTeachers(Map<String, DomUserFull> users, Map<String,Collection<String>>members, Map<String, DomSchoolClassFull> classes) {  
	  for (Map.Entry<String, DomUserFull> item: users.entrySet()) {    
      String key = item.getKey();
      DomUserFull value = item.getValue();
      if (value.getUserName().isEmpty()) {
        value.setUserName(key);
      }
      try {
        SecureSchoolAdminSchoolManager.submitTeacher(value); // FIXME duplicate in schoolclassmanager
      } catch (Dwo2Exception e) {
        LOG.log(Level.WARNING, "submit Teacher", e);
      }      
    }
    Collection<DomUserFull> allTeachers = parseLeerkrachten().values();
    for(Map.Entry<String,DomUserFull> item: users.entrySet()) {
      for (DomUserFull t: allTeachers) {
        if (t.getUserName().equals(item.getValue().getUserName())) {
          item.setValue(t);
          break;
        }
      }
    } 
    for(Map.Entry<String,DomUserFull> item: users.entrySet()) {
      String key = item.getKey();
      Collection<String> collection = members.getOrDefault(key, Collections.emptySet());
      DomSubmitTeacherToSchoolClass submit;
      submit = new DomSubmitTeacherToSchoolClass();
      submit.setTeacher(new DomTeacher(item.getValue()));
      for(String id: collection) {
        submit.setSchoolClass(classes.get(id));
        try {
        	schoolClassManager.submitTeacherToSchoolClass(submit);
        } catch (Dwo2Exception e) {
          LOG.log(Level.WARNING, "Teacher to class",e);
        }
      }
    }
  }
  
}
