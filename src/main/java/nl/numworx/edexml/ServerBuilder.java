package nl.numworx.edexml;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureSchoolAdminSchoolClassManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountLoginsManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureUserAccountManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

public class ServerBuilder implements Builder {

	
	SecureSchoolAdminSchoolClassManager schoolClassManager;
	
	Map<String, DomUserFull> students = new TreeMap<>();
	Map<String, DomUserFull> teachers = new TreeMap<>();
	Map<String, DomSchoolClassFull> classes = new TreeMap<>();
	Map<String, Collection<String>> memberships = new TreeMap<>();
	
	public void setSource(String username, String password, URL base) throws Dwo2Exception {
		schoolClassManager = new SecureSchoolAdminSchoolClassManager();
		StoredRestManager.getInstance().setBasicAuthString(username, password, null);
		StoredRestManager.getInstance().getAuthenticator().setServerUrlPath(base);
		DomSchoolsRolesAndClassesV2 logins = SecureUserAccountLoginsManager.getSchoolLogins();
		DomContext context = new DomContext();
		DomRole role = logins.getActiveSchoolRoleAndClass().getRole();
		if (role.getRoleName().equals(RoleType.SCHOOLADMIN.name())) {
			context.setDomHasRole(logins.getActiveSchoolRoleAndClass().getHasRole());
			StoredRestManager.getInstance().getAuthenticator().setContext(context);
		} else 
			throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "Wrong role");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return memberships;
	}

	@Override
	public Map<String, DomUserFull> parseLeerkrachten() {
		try {
			List<DomTeacher> list = schoolClassManager.getTeachersInSchool();
			putUsers(list, teachers);
		} catch (Dwo2Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return teachers;
	}

}
